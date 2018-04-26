package com.sichard.demo.view.listview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import com.sichard.demo.R;

/**
 * <br>类描述：可拖拽的ListView
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-18</b>
 */
public class DragListView extends ListView {

    private final float mAlpha = 0.9f;
    //拖动的view
    private ImageView mDragView;
    private Context mContext;
    private WindowManager mWindowManager;
    private WindowManager.LayoutParams mLayoutParams;
    //开始拖动时的位置
    private int mDragStartPosition;
    //当前的位置
    private int mDragCurrentPostion;
    //在滑动的时候，手的移动要大于这个返回的距离值才开始移动控件
    private int mScaledTouchSlop;
    //当前位置距离边界的位置
    private int mDragOffsetX;
    private int mDragOffSetY;
    //移动的位置
    private int mDragPointX;
    private int mDragPointY;
    //边界
    private int mUpperBound;
    private int mLowerBound;
    private DropViewListener mDropViewListener;

    public DragListView(Context context) {
        super(context);
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    public DragListView(Context context, AttributeSet attr) {
        super(context, attr);
        mContext = context;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        mScaledTouchSlop = ViewConfiguration.get(mContext).getScaledTouchSlop();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        //ev.getX()相对于控件本身左上角，ev.getRawX()相对于容器圆点位置
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                final int x = (int) ev.getX();//相对于空间本身
                final int y = (int) ev.getY();
                final int itemNum = pointToPosition(x, y);
                if (itemNum == AdapterView.INVALID_POSITION) {
                    break;
                }
                final ViewGroup item = (ViewGroup) getChildAt(itemNum - getFirstVisiblePosition());
                mDragPointX = x - item.getLeft();
                mDragPointY = y - item.getTop();
                mDragOffsetX = ((int) ev.getRawX()) - x;
                mDragOffSetY = ((int) ev.getRawY()) - y;

                //长按
                item.setOnLongClickListener(new OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        //计算边界
                        final int height = getHeight();
                        mUpperBound = Math.min(y - mScaledTouchSlop, height / 3);
                        mLowerBound = Math.max(y + mScaledTouchSlop, height * 2 / 3);
                        mDragCurrentPostion = mDragStartPosition = itemNum;

                        item.setDrawingCacheEnabled(true);
                        Bitmap bitmap = Bitmap.createBitmap(item.getDrawingCache());
                        startDragging(bitmap, x, y);
                        return true;
                    }
                });
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mDragView != null && mDragCurrentPostion != INVALID_POSITION && mDropViewListener != null) {
            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                    //int y = (int) ev.getY();
                    stopDragging();
                    //数据交换
                    if (mDragCurrentPostion >= 0 && mDragCurrentPostion < getCount()) {
                        mDropViewListener.drop(mDragStartPosition, mDragCurrentPostion);
                    }
                    break;
                case MotionEvent.ACTION_MOVE:
                    int x = (int) ev.getX();
                    int y = (int) ev.getY();
                    dragView(x, y);
                    if (y >= getHeight() / 3) {
                        mUpperBound = getHeight() / 3;
                    }
                    if (y <= getHeight() * 2 / 3) {
                        mLowerBound = getHeight() * 2 / 3;
                    }
                    int speed = 0;
                    if (y > mLowerBound) {
                        if (getLastVisiblePosition() < getCount() - 1) {
                            speed = y > (getHeight() + mLowerBound) / 2 ? 16 : 4;
                        } else {
                            speed = 1;
                        }
                    } else if (y < mUpperBound) {
                        speed = y < mUpperBound / 2 ? -16 : -4;
                        if (getFirstVisiblePosition() == 0 && getChildAt(0).getTop() >= getPaddingTop()) {
                            speed = 0;
                        }
                    }
                    if (speed != 0) {
                        smoothScrollBy(speed, 30);
                    }
                    break;
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }

    /**
     * 开始拖动
     *
     * @param bitm
     * @param x
     * @param y
     */
    private void startDragging(Bitmap bitm, int x, int y) {
        stopDragging();

        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.gravity = Gravity.TOP | Gravity.LEFT;
        mLayoutParams.x = x - mDragPointX + mDragOffsetX;
        mLayoutParams.y = y - mDragPointY + mDragOffSetY;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        mLayoutParams.format = PixelFormat.TRANSLUCENT;
        mLayoutParams.windowAnimations = 0;

        ImageView imageView = new ImageView(mContext);
        imageView.setImageBitmap(bitm);
        imageView.setBackgroundResource(R.mipmap.tab_item_bg);
        imageView.setPadding(0, 0, 0, 0);
        mWindowManager.addView(imageView, mLayoutParams);
        mDragView = imageView;
    }

    /**
     * 回收资源
     */
    private void stopDragging() {
        if (mDragView != null) {
            mWindowManager.removeView(mDragView);
            mDragView.setImageDrawable(null);
            mDragView = null;
        }
    }

    /**
     * 拖拽
     *
     * @param x
     * @param y
     */
    private void dragView(int x, int y) {
        if (mDragView != null) {
            mLayoutParams.alpha = mAlpha;
            mLayoutParams.y = y - mDragPointY + mDragOffSetY;
            mLayoutParams.x = x - mDragPointX + mDragOffsetX;
            mWindowManager.updateViewLayout(mDragView, mLayoutParams);
        }
        int tempPosition = pointToPosition(0, y);
        if (tempPosition != INVALID_POSITION) {
            mDragCurrentPostion = tempPosition;
        }

        //滚动
        int scrollY = 0;
        if (y < mUpperBound) {
            scrollY = 8;
        } else if (y > mLowerBound) {
            scrollY = -8;
        }

        if (scrollY != 0) {
            int top = getChildAt(mDragCurrentPostion - getFirstVisiblePosition()).getTop();
            setSelectionFromTop(mDragCurrentPostion, top + scrollY);
        }
    }

    public void setDropViewListener(DropViewListener listener) {
        this.mDropViewListener = listener;
    }

    public interface DropViewListener {
        void drop(int from, int to);
    }
}
