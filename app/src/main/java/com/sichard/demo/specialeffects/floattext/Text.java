package com.sichard.demo.specialeffects.floattext;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.SystemClock;

/**
 * 浮动的Text
 */
public class Text {
    private int mOriginRadius;
    private String mText;
    private int mOriginX, mOriginY;
    private float mNewX, mNewY;
    private float mPreX, mPreY;
    private float mCurX, mCurY;
    private long mDuration;
    private long mStartTime;
    private Paint mPaint;
    private Bitmap mBitmap;

    Text(int originX, int originY, int originRadius, int duration) {
        mOriginX = originX;
        mOriginY = originY;
        mPreX = originX;
        mPreY = originY;
        mOriginRadius = originRadius;
        mDuration = duration;
        mPaint = new Paint();
        mPaint.setColor(0xffffffff);
        mPaint.setTextSize(50);
        mPaint.setAntiAlias(true);

        int radius = (int) (Math.random() * mOriginRadius);
        double degree = Math.random() * 2 * Math.PI;
        mNewX = (float) (mPreX + radius * Math.cos(degree));
        mNewY = (float) (mPreY + radius * Math.sin(degree));
        mStartTime = SystemClock.uptimeMillis();
    }

    public void setText(String text) {
        mText = text;
    }

    public void draw(Canvas canvas) {
        long now = SystemClock.uptimeMillis();
        float fraction = ((float) (now - mStartTime)) / mDuration;
        if (fraction > 1) {
            float radius = (int) (Math.random() * mOriginRadius);
            double degree = Math.random() * 2 * Math.PI;
            // 记录起点
            mPreX = mNewX;
            mPreY = mNewY;
            // 计算下一次运动点坐标
            mNewX = (float) (mOriginX + radius * Math.cos(degree));
            mNewY = (float) (mOriginY + radius * Math.sin(degree));

            mStartTime = SystemClock.uptimeMillis();
            fraction = 0;
        }

        mCurX = mPreX + fraction * (mNewX - mPreX);
        mCurY = mPreY + fraction * (mNewY - mPreY);
        float textWidth = mPaint.measureText(mText);
        float textSize = mPaint.getTextSize();
        canvas.drawBitmap(mBitmap, mCurX, mCurY - textSize, null);
        canvas.drawText(mText, mCurX, mCurY, mPaint);
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }
}
