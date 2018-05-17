package com.android.sichard.battersaver;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br>类描述:省电旋转的碎片
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-2</b>
 */

public class BatterySaverCleanView extends View {

    private float mFraction;
    private Bitmap mCircle1, mCircle2, mCircle3;
    private Bitmap mSnow;
    private long mStartTime;
    private Paint mPaint;
    private RectF mRectF;
    private List<Snow> mSnows = new ArrayList<>();
    private BatterySaverProgressListener mProgressListener;
    private BatterySaverFinishListener mFinishListener;
    private int mCenterCircle;
    private int mWidth;
    private int mHeight;

    public BatterySaverCleanView(Context context) {
        super(context);
    }

    public BatterySaverCleanView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BatterySaverCleanView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mCircle1 = BitmapFactory.decodeResource(getResources(), R.drawable.battery_saver_circle1);
        mCircle2 = BitmapFactory.decodeResource(getResources(), R.drawable.battery_saver_circle2);
        mCircle3 = BitmapFactory.decodeResource(getResources(), R.drawable.battery_saver_circle3);
        mSnow = BitmapFactory.decodeResource(getResources(), R.drawable.battery_saver_snow);
        mPaint = new Paint();
        mRectF = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        mCenterCircle = getWidth() / 2;
        mWidth = getWidth();
        mHeight = getHeight();
    }


    public void startCleanAnim() {
        createSnow();
        mStartTime = System.currentTimeMillis();

        final ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(2500);
        animator.setRepeatMode(ValueAnimator.RESTART);
        animator.setInterpolator(new AccelerateInterpolator(2));
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFraction = (float) animation.getAnimatedValue();
                mProgressListener.batterySaverProgressChanged(mFraction);
                invalidate();
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            public void onAnimationStart(Animator animation) {

            }

            public void onAnimationEnd(Animator animation) {
                if (mFinishListener != null) {
                    mFinishListener.batterySaverFinish();
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    private void createSnow() {
        int width = getWidth() / 2;
        int count = 80;
        for (int i = 0; i < count; i++) {
            Snow snow = new Snow();
            float random = new Random().nextFloat();
            int R = (int) (0.9f * width + 0.25f * width * random);
            int r = (int) (0.8f * width + 0.1 * width * random);
            double radian = 2 * Math.PI * i / count + 2 * Math.PI * i / count * random;
            double cos = Math.cos(radian);
            double sin = Math.sin(radian);
            snow.mStartX = (int) (R * cos + width);
            snow.mStartY = (int) (R * sin + width);
            snow.mEndX = (int) (r * cos + width);
            snow.mEndY = (int) (r * sin + width);
            long SNOW_DURATION = 1200;
            snow.mDuration = (int) (SNOW_DURATION - SNOW_DURATION * random / 2);
            snow.mAlpha = (int) (255 - 255 * random / 2);
            snow.mRotate = 10 * random;
            snow.mSize = mSnow.getWidth() + mSnow.getWidth() * random;
            mSnows.add(snow);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float scale = 1 - mFraction;
        if (mFraction < 0.13f) {
            long last = System.currentTimeMillis() - mStartTime;
            for (Snow snow : mSnows) {
                snow.draw(canvas, mSnow, mPaint, last);
            }

            canvas.save();
            canvas.rotate(180 * (mFraction / 0.1f), mCenterCircle, mCenterCircle);
            canvas.scale(scale, scale, mCenterCircle, mCenterCircle);
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mCircle2, null, mRectF, null);
            canvas.restore();

            canvas.save();
            canvas.rotate(360 * (mFraction / 0.1f), mCenterCircle, mCenterCircle);
            canvas.scale(scale, scale, mCenterCircle, mCenterCircle);
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mCircle1, null, mRectF, null);
            canvas.restore();
        } else if (mFraction < 0.3f) {
            canvas.save();
            canvas.rotate(4000 * mFraction, mCenterCircle, mCenterCircle);
            canvas.scale(scale, scale, mCenterCircle, mCenterCircle);
            mPaint.setAlpha((int) (255 * mFraction));
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mCircle3, null, mRectF, mPaint);
            canvas.drawBitmap(mCircle2, null, mRectF, null);
            canvas.drawBitmap(mCircle1, null, mRectF, null);
            canvas.restore();
        } else {
            canvas.save();
            canvas.rotate(4000 * mFraction, mCenterCircle, mCenterCircle);
            canvas.scale(scale, scale, mCenterCircle, mCenterCircle);
            mPaint.setAlpha((int) (255 * mFraction));
            mRectF.set(0, 0, mWidth, mHeight);
            canvas.drawBitmap(mCircle3, null, mRectF, mPaint);
            canvas.drawBitmap(mCircle2, null, mRectF, null);
            canvas.drawBitmap(mCircle1, null, mRectF, null);
            canvas.restore();
        }
    }

    public void setBatterySaverProgressListener(BatterySaverProgressListener batterySaverProgressListener) {
        this.mProgressListener = batterySaverProgressListener;
    }

    public void setBatterySaverFinishListener(BatterySaverFinishListener finishListener) {
        mFinishListener = finishListener;
    }

    public void onDestroy() {
        if (mCircle1 != null && !mCircle1.isRecycled()) {
            mCircle1.recycle();
            mCircle1 = null;
        }
        if (mCircle2 != null && !mCircle2.isRecycled()) {
            mCircle2.recycle();
            mCircle2 = null;
        }
        if (mCircle3 != null && !mCircle3.isRecycled()) {
            mCircle3.recycle();
            mCircle3 = null;
        }
    }

    class Snow {
        int mDuration;
        int mAlpha;
        int mStartX;
        int mStartY;
        int mEndX;
        int mEndY;
        float mSize;
        float mRotate;

        void draw(Canvas canvas, Bitmap bitmap, Paint paint, long last) {
            float fraction = (float) last % mDuration / mDuration;
            int x = (int) (mStartX + (mEndX - mStartX) * fraction);
            int y = (int) (mStartY + (mEndY - mStartY) * fraction);
            int alpha = (int) (mAlpha - mAlpha * fraction);
            canvas.save();
            canvas.rotate(mRotate * fraction, getWidth() / 2, getWidth() / 2);
            paint.setAlpha(alpha);
            mRectF.set(x, y, x + mSize, y + mSize);
            canvas.drawBitmap(bitmap, null, mRectF, paint);
            canvas.restore();
        }
    }

    interface BatterySaverProgressListener {
        void batterySaverProgressChanged(float progress);
    }

    public interface BatterySaverFinishListener {
        void batterySaverFinish();
    }
}
