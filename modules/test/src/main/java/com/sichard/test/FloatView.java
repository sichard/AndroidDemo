package com.sichard.test;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2020-11-17
 */
public class FloatView extends RelativeLayout {
    private float mLastY;
    private int mMaxTopMargin;
    private float mDownY;

    public FloatView(Context context) {
        super(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "点击", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        final float y = ev.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (getParent() instanceof CoordinatorLayout) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int height = ((CoordinatorLayout) (getParent())).getHeight();
                    mMaxTopMargin = height - getHeight();
                }
                mLastY = y;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
                layoutParams.topMargin = Math.round(layoutParams.topMargin + (y - mLastY));
                if (layoutParams.topMargin < 0) {
                    layoutParams.topMargin = 0;
                } else if (layoutParams.topMargin > mMaxTopMargin) {
                    layoutParams.topMargin = mMaxTopMargin;
                }
                setLayoutParams(layoutParams);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mDownY - mLastY) < 5) {
                    performClick();
                }
                break;

        }
        return true;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        final float y = event.getRawY();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (getParent() instanceof CoordinatorLayout) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                    int height = ((CoordinatorLayout) (getParent())).getHeight();
                    mMaxTopMargin = height - getHeight();
                }
                mLastY = y;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                CoordinatorLayout.LayoutParams layoutParams = (CoordinatorLayout.LayoutParams) getLayoutParams();
                // 这里不进行四舍五入的话，在拖动之后，手指开始的触点和移动后的触点会出现错位
                layoutParams.topMargin = Math.round(layoutParams.topMargin + (y - mLastY));
                if (layoutParams.topMargin < 0) {
                    layoutParams.topMargin = 0;
                } else if (layoutParams.topMargin > mMaxTopMargin) {
                    layoutParams.topMargin = mMaxTopMargin;
                }
                setLayoutParams(layoutParams);
                mLastY = y;
                break;
            case MotionEvent.ACTION_UP:
                if (Math.abs(mDownY - mLastY) < 5) {
                    performClick();
                }
                break;

        }
        return true;
    }
}