package com.sichard.demo.view.listview;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 *<br>类描述：可滑动的ListView的Item
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2017/3/25</b>
 */
public class SlideItemLayout extends LinearLayout {

    // content View
    private View mContentView;
    // menu View
    private View mMenuView;
    // content View的布局参数对象
    private LayoutParams mContentLayoutParams;
    // 菜单是否打开
    private boolean mIsMenuOpen;
    // contentView最小的leftMargin
    private int minLeftMargin;
    // contentView最大的leftMargin
    private int maxLeftMargin = 0;
    // 滑动类
    private Scroller mScroller = null;

    public SlideItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContentLayoutParams = new LayoutParams(getScreenWidth(), LayoutParams.MATCH_PARENT);
        mScroller = new Scroller(context);
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            setLeftMargin(mScroller.getCurrX());
            postInvalidate();
        }
    }

    /**
     * Scroller平滑打开Menu
     */
    public void smoothOpenMenu() {
        mIsMenuOpen = true;
        mScroller.startScroll(mContentLayoutParams.leftMargin, 0, minLeftMargin - mContentLayoutParams.leftMargin, 0, 350);
        postInvalidate();
    }

    /**
     * Scroller平滑关闭Menu
     */
    public void smoothCloseMenu() {
        mIsMenuOpen = false;
        mScroller.startScroll(mContentLayoutParams.leftMargin, 0, maxLeftMargin - mContentLayoutParams.leftMargin, 0, 350);
        postInvalidate();
    }

    /**
     * 立即关闭Menu
     */
    public void closeMenu() {
        mIsMenuOpen = false;
        mScroller.startScroll(mContentLayoutParams.leftMargin, 0, maxLeftMargin - mContentLayoutParams.leftMargin, 0, 0);
        postInvalidate();
    }

    /**
     * 在布局inflate完成后调用
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        // 第一个孩子是contentView
        mContentView = getChildAt(0);
        // 第二个孩子是MenuView
        mMenuView = getChildAt(1);
        // 最小的leftMargin为负的menuView宽度
        ViewGroup.LayoutParams lp = mMenuView.getLayoutParams();
        minLeftMargin = -lp.width;
    }

    /**
     * 获取屏幕宽度
     * @return
     */
    private int getScreenWidth() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 给contentView设置leftMargin
     * @param leftMargin
     */
    public void setLeftMargin(int leftMargin) {
        // 控制leftMargin不越界
        if (leftMargin > maxLeftMargin) {
            leftMargin = maxLeftMargin;
        }
        if (leftMargin < minLeftMargin) {
            leftMargin = minLeftMargin;
        }
        mContentLayoutParams.leftMargin = leftMargin;
        // 通过设置leftMargin，达到menu显示的效果
        mContentView.setLayoutParams(mContentLayoutParams);
    }

    /**
     * 获取menuView宽度
     * @return
     */
    public int getMenuWidth() {
        return -minLeftMargin;
    }

    /**
     * Menu是否打开
     * @return
     */
    public boolean isMenuOpen() {
        return mIsMenuOpen;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getContext().getResources().getDisplayMetrics());
    }
}
