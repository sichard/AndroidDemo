package com.android.sichard.lockscreen.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * <br>类描述:带闪动效果的TextView
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/2/23</b>
 */

public class FlickerTextView extends TextView {
    private int mViewWidth;
    private TextPaint mPaint;
    private LinearGradient mLinearGradient;
    private Matrix mGradientMatrix;
    private int mTranslate ;
    private boolean mIsFirst = true;

    public FlickerTextView(Context context) {
        super(context);
    }

    public FlickerTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlickerTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                mLinearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                        new int[]{Color.GRAY, 0xffffffff, Color.GRAY}, null, Shader.TileMode.CLAMP);
                mPaint.setShader(mLinearGradient);
                mGradientMatrix = new Matrix();
                mTranslate = -mViewWidth;
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mGradientMatrix != null) {
            mTranslate += mViewWidth / 5;
            if (mTranslate > 2 * mViewWidth) {
                mTranslate = -mViewWidth;
            }
            mGradientMatrix.setTranslate(mTranslate, 0);
            mLinearGradient.setLocalMatrix(mGradientMatrix);
            if (mIsFirst) {
                postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mIsFirst = false;
                        postInvalidateDelayed(150);
                    }
                }, 100);
            } else {
                postInvalidateDelayed(150);
            }

        }
    }
}
