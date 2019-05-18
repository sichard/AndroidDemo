package com.sichard.weather.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.HeaderViewListAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;

import com.sichard.weather.WeatherDataManager;
import com.sichard.weather.WeatherTaskManager;
import com.sichard.weather.adapter.LocalCityAdapter;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.util.List;

/**
 * <br>类描述:可拖动和左滑可删除的ListView
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 17-5-26</b>
 */

public class DragSlideListView extends SlideListView {

    private WindowManager mWindowManager;
    /** item镜像的布局参数 */
    private WindowManager.LayoutParams mWindowLayoutParams;
    private WindowManager.LayoutParams mNewWindowLayoutParams;
    /** 选中的item的position */
    private int mSelectedPosition;
    /** 选中的item的View对象 */
    private View mItemView;
    /** 用于拖拽的镜像，这里直接用一个ImageView装载Bitmap */
    private ImageView mDragIV;
    /** 选中的item的镜像Bitmap */
    private Bitmap mBitmap;
    /** 按下的点到所在item的上边缘的距离 */
    private int mPoint2ItemTop;

    /** 按下的点到所在item的左边缘的距离 */
    private int mPoint2ItemLeft;

    /** CanDragListView距离屏幕顶部的偏移量 */
    private int mOffset2Top;
    /** CanDragListView自动向下滚动的边界值 */
    private int mDownScrollBorder;

    /** CanDragListView自动向上滚动的边界值 */
    private int mUpScrollBorder;
    /** CanDragListView自动滚动的速度 */
    private static final int speed = 20;

    /** CanDragListView距离屏幕左边的偏移量 */
    private int mOffset2Left;
    /** 状态栏的高度 */
    private int mStatusHeight;
    /** 默认长按事件时间是1000毫秒 */
    private long mLongClickTime = 600;
    /** 是否可拖拽，默认为false */
    private boolean isDrag = false;
    /** 是否交换了位置 */
    private boolean mIsSwap = false;
    /** 按下时的x坐标，move事件后会重新赋值 */
    private int mDownX;
    /** 按下时的y坐标，move事件后会重新赋值 */
    private int mDownY;
    /** 按下时的x坐标，ove事件后不会改变 */
    private int mOriginalDownX;

    /** item发生变化回调的接口 */
    private OnChangedListener onChangedListener;

    /**
     * 设置回调接口
     *
     * @param onChangedListener
     */
    public void setOnChangeListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    public DragSlideListView(Context context) {
        this(context, null);
    }

    public DragSlideListView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragSlideListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mStatusHeight = getStatusHeight(context); // 获取状态栏的高度
    }

    private Handler mHandler = new Handler();

    // 用来处理长按的Runnable
    private Runnable mLongClickRunnable = new Runnable() {

        @Override
        public void run() {
            isDrag = true; // 设置可以拖拽
            if (mItemView != null) {
                mItemView.setVisibility(View.INVISIBLE);// 隐藏该item
            }
            Log.i("DragDragListView", "**mLongClickRunnable**");
            // 根据我们按下的点显示item镜像
            createDragImage(mBitmap, mDownX, mDownY);
        }
    };

    /**
     * 当mDownY的值大于向上滚动的边界值，触发自动向上滚动 当mDownY的值小于向下滚动的边界值，触犯自动向下滚动 否则不进行滚动
     */
    private Runnable mScrollRunnable = new Runnable() {

        @Override
        public void run() {
            int scrollY;
            if (mDownY > mUpScrollBorder) {
                scrollY = speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else if (mDownY < mDownScrollBorder) {
                scrollY = -speed;
                mHandler.postDelayed(mScrollRunnable, 25);
            } else {
                scrollY = 0;
                mHandler.removeCallbacks(mScrollRunnable);
            }

            // 所以我们在这里调用下onSwapItem()方法来交换item
//            onSwapItem(mDownY, mDownY);

            smoothScrollBy(scrollY, 10);
        }
    };

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 如果左滑菜单处于打开状态，则touch事件交给SlideListView去处理
        if (mTouchView != null && mTouchView.isMenuOpen()) {
            return super.onTouchEvent(event);
        }
        // Log.i("CanDragListView", mSelectedPosition+"****"+mItemView);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mOriginalDownX = mDownX = (int) event.getX();
                mDownY = (int) event.getY();

                // 根据按下的坐标获取item对应的position
                mSelectedPosition = pointToPosition(mDownX, mDownY);
                // 如果是无效的position，即值为-1
                if (mSelectedPosition == AdapterView.INVALID_POSITION) {
                    return super.onTouchEvent(event);
                }
                // 根据position获取对应的item
                mItemView = getChildAt(mSelectedPosition - getFirstVisiblePosition());
                // 使用Handler延迟mLongClickTime执行mLongClickRunnable
                mHandler.postDelayed(mLongClickRunnable, mLongClickTime);
                if (mItemView != null) {
                    // 下面这几个距离大家可以参考我的博客上面的图来理解下
                    mPoint2ItemTop = mDownY - mItemView.getTop();
                    mPoint2ItemLeft = mDownX - mItemView.getLeft();

                    mOffset2Top = (int) (event.getRawY() - mDownY);
                    mOffset2Left = (int) (event.getRawX() - mDownX);

                    // 获取DragSlideListView自动向上滚动的偏移量，小于这个值，DragSlideListView向下滚动
                    mDownScrollBorder = getHeight() / 4;
                    // 获取DragSlideListView自动向下滚动的偏移量，大于这个值，DragSlideListView向上滚动
                    mUpScrollBorder = getHeight() * 3 / 4;

                    // 将该item进行绘图缓存
                    mItemView.setDrawingCacheEnabled(true);
                    // 从缓存中获取bitmap
                    mBitmap = Bitmap.createBitmap(mItemView.getDrawingCache());
                    // 释放绘图缓存，避免出现重复的缓存对象
                    mItemView.destroyDrawingCache();
                }

                // Log.i("DragSlideListView", "****"+isDrag);
                break;
            case MotionEvent.ACTION_MOVE:
                int moveX = (int) event.getX();
                int moveY = (int) event.getY();
                // 如果执行拖动前，x轴移动了一段距离，则认为是左滑删除操作，从而取消长安拖动的操作
                if (Math.abs(moveX - mOriginalDownX) > dp2px(2)) {
                    mHandler.removeCallbacks(mLongClickRunnable);
                }
                if (isDrag) {
                    if (!isOnTouchInItem(mItemView, moveX, moveY)) {
                        mHandler.removeCallbacks(mLongClickRunnable);
                    }
                    mDownX = moveX;
                    mDownY = moveY;
                    onDragItem(moveX, moveY);
                    // 如果拖动事件开始，则不再处理SlideListView的左滑事件
                    return true;
                }
                break;
            case MotionEvent.ACTION_UP:
                onStopDrag();
                mHandler.removeCallbacks(mLongClickRunnable);
                mHandler.removeCallbacks(mScrollRunnable);

                isDrag = false;
                mIsSwap = false;
                break;
            default:
                break;
        }

        return super.onTouchEvent(event);
    }

    /**
     * 判断手指按下的坐标是否在item范围内
     *
     * @param view
     * @param downX
     * @param downY
     * @return
     */
    private boolean isOnTouchInItem(View view, int downX, int downY) {
        if (view == null) {
            return false;
        }
        int leftX = view.getLeft();
        int topY = view.getTop();
        if (downX < leftX || downX > leftX + view.getWidth()) {
            return false;
        }
        if (downY < topY || downY > topY + view.getHeight()) {
            return false;
        }
        return true;
    }

    /**
     * 创建拖动的镜像
     *
     * @param bitmap
     * @param downX  按下的点相对父控件的X坐标
     * @param downY  按下的点相对父控件的X坐标
     */
    private void createDragImage(Bitmap bitmap, int downX, int downY) {
        mWindowLayoutParams = new WindowManager.LayoutParams();
        mWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 图片之外的其他地方透明
        mWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        mWindowLayoutParams.alpha = 0.55f; // 透明度
        mWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mNewWindowLayoutParams = new WindowManager.LayoutParams();
        mNewWindowLayoutParams.format = PixelFormat.TRANSLUCENT; // 图片之外的其他地方透明
        mNewWindowLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mNewWindowLayoutParams.x = downX - mPoint2ItemLeft + mOffset2Left;
        mNewWindowLayoutParams.y = downY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
        // mNewWindowLayoutParams.alpha = 0.55f; // 透明度
        mNewWindowLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mNewWindowLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mNewWindowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;

        mDragIV = new ImageView(getContext());
        mDragIV.setImageBitmap(bitmap);
        mWindowManager.addView(mDragIV, mWindowLayoutParams);
    }

    /**
     * 移除镜像
     */
    private void removeDragImage() {
        if (mDragIV != null) {
            mWindowManager.removeView(mDragIV);
            mDragIV = null;
        }
    }

    /**
     * 拖动item，在里面实现了item镜像的位置更新，item的相互交换以及ListView的自行滚动
     *
     * @param moveX
     * @param moveY
     */
    private void onDragItem(int moveX, int moveY) {
        if (mWindowLayoutParams != null && mDragIV != null) {
//            mWindowLayoutParams.x = moveX - mPoint2ItemLeft + mOffset2Left; // 拖动的view在x轴方向的移动
            mWindowLayoutParams.y = moveY - mPoint2ItemTop + mOffset2Top - mStatusHeight;
            // 下面两句限制移动的view只能在ListView的范围内
            mWindowLayoutParams.y = Math.max(mWindowLayoutParams.y, mOffset2Top - mStatusHeight);
            mWindowLayoutParams.y = Math.min(mWindowLayoutParams.y, mOffset2Top - mStatusHeight + getHeight() - 2 * mItemView.getHeight());
            mWindowManager.updateViewLayout(mDragIV, mWindowLayoutParams); // 更新镜像的位置
        }

        // 下面两句将moveY的值拦截在了ListView的范围内，由于加入了FooterView，为了不让拖动的view和FooterView交换位置，在ListView的高度上见去了2倍ItemView的高度
        moveY = Math.max(moveY, 0);
        moveY = Math.min(moveY, getHeight() - 2 * mItemView.getHeight());
        onSwapItem(moveX, moveY);
        // ListView自动滚动
        mHandler.post(mScrollRunnable);
    }

    /**
     * 交换item,并且控制item之间的显示与隐藏效果
     *
     * @param moveX
     * @param moveY
     */
    private void onSwapItem(int moveX, int moveY) {
        // 获取我们手指移动到的那个item的position
        int position = pointToPosition(moveX, moveY);
        // 假如tempPosition 改变了并且tempPosition不等于-1,则进行交换
        if (position != mSelectedPosition && position != AdapterView.INVALID_POSITION) {

            // mAdapter.getItem(mSelectedPosition);
            View newItem = getChildAt(position - getFirstVisiblePosition());
            View oldItem = getChildAt(mSelectedPosition - getFirstVisiblePosition());
            if (newItem != null && oldItem != null) {
                newItem.setVisibility(INVISIBLE);// 隐藏拖动到的位置的item
                oldItem.setVisibility(VISIBLE);//显示之前的
                if (onChangedListener != null) {
                    Log.i("DragSlideListView", "**onSwapItem**");
                    onChangedListener.onChange(mSelectedPosition, position);
                    mIsSwap = true;
                }
            }

            mSelectedPosition = position;
        }
    }

    /**
     * 停止拖拽我们将之前隐藏的item显示出来，并将镜像移除
     */
    private void onStopDrag() {
        View view = getChildAt(mSelectedPosition - getFirstVisiblePosition());
        if (view != null) {
            view.setVisibility(View.VISIBLE);
        }
        // ((DragAdapter)this.getAdapter()).setItemHide(-1);
        removeDragImage();

        // 如果拖动更改item位置，则更新存储数据
        if (isDrag && mIsSwap) {
            WeatherTaskManager.execWorkTask(new Runnable() {
                @Override
                public void run() {
                    final ListAdapter listAdapter = getAdapter();
                    // 由于加入了FooterView，Adapter默认转成了HeaderViewListAdapter，故此处需要转型
                    if (listAdapter != null && listAdapter instanceof HeaderViewListAdapter) {
                        final HeaderViewListAdapter headerViewListAdapter = (HeaderViewListAdapter) listAdapter;
                        final ListAdapter wrappedAdapter = headerViewListAdapter.getWrappedAdapter();
                        if (wrappedAdapter != null && wrappedAdapter instanceof LocalCityAdapter) {
                            final List<WeatherDataEntity> cityList = ((LocalCityAdapter) wrappedAdapter).getCityList();
                            WeatherDataManager.getInstance().updateWeatherData(cityList);
                        }
                    }
                }
            });
        }
    }

    /**
     * 获取状态栏的高度
     *
     * @param context
     * @return
     */
    private static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 监听数据拖拽的接口，用来更新数据显示
     */
    public interface OnChangedListener {

        /**
         * 当item交换位置的时候回调的方法，在此处实现数据的交换
         *
         * @param start 开始的position
         * @param to    拖拽到的position
         */
        public void onChange(int start, int to);
    }

}