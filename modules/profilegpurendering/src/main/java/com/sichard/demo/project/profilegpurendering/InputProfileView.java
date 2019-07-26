package com.sichard.demo.project.profilegpurendering;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.sichard.common.rx.Rx2Util;
import com.android.sichard.common.rx.Source;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2019-7-18
 */
public class InputProfileView extends View {
    private boolean mIsEnableInput;
    private boolean mIsEnableMeasure;
    private boolean mIsEnableDraw;
    private boolean mIsEnableUpload;
    private boolean mIsEnableIssue;
    private boolean mIsEnableMiscellaneous;
    private boolean mIsEnableDrawPoint;

    private String mCurrentTime;
    private Paint mPaint;
    private Bitmap mBitmap, mBitmap1, mBitmap2, mBitmap3, mBitmap4, mBitmap5;
    private Random mRandomX, mRandomY;
    private int mTop = 300;
    private float[] mPoints;

    public InputProfileView(Context context) {
        super(context);
    }

    public InputProfileView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public InputProfileView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mPaint = new Paint();
        mPaint.setColor(0xFF000000);
        mPaint.setTextSize(48);
        mPaint.setStrokeWidth(6);
        mPaint.setAntiAlias(true);
        mCurrentTime = "当前时间：0";
        mRandomX = new Random();
        mRandomY = new Random();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            Thread.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsEnableDraw) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (mIsEnableUpload && mBitmap != null && !mBitmap.isRecycled()) {
            canvas.drawBitmap(mBitmap, 0, 0, null);
            canvas.drawBitmap(mBitmap1, 0, mTop, null);
            canvas.drawBitmap(mBitmap2, 0, mTop * 2, null);
            canvas.drawBitmap(mBitmap3, 0, mTop * 3, null);
            canvas.drawBitmap(mBitmap4, 0, mTop * 4, null);
            canvas.drawBitmap(mBitmap5, 0, mTop * 5, null);
        }

        if (mIsEnableIssue) {
            for(int i = 0; i < 300; i++) {
                mPaint.setColor(mRandomX.nextInt(268435455) | 0xff000000);
                canvas.drawText(String.valueOf(i % 10), i * 30 % 1080, 500 + (i / 36) * 36, mPaint);
            }
        }

        if (mIsEnableDrawPoint) {
//            canvas.drawPoints(mPoints, mPaint);
            for (int i = 0; i < mPoints.length; i = i + 2) {
                canvas.drawPoint(mPoints[i], mPoints[i + 1], mPaint);
            }
        }
        canvas.drawText(mCurrentTime, 0, 50, mPaint);
        
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        refreshCurrentTime();
        if (mIsEnableInput) {
            try {
                Thread.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        invalidate();
        return true;
    }

    public void refreshCurrentTime() {
        mCurrentTime = "当前时间：" + System.currentTimeMillis();

        if (mIsEnableMeasure) {
            requestLayout();
        }
    }

    public void enableInput(boolean isInputEnable) {
        mIsEnableInput = isInputEnable;
        invalidate();
    }

    public void enableMeasure(boolean isMeasureEnable) {
        mIsEnableMeasure = isMeasureEnable;
        invalidate();
    }

    public void enableDraw(boolean isDraw) {
        mIsEnableDraw = isDraw;
        invalidate();
    }

    public void enableIssue(boolean isIssue) {
        mIsEnableIssue = isIssue;
        invalidate();
    }

    public void enableMiscellaneous(boolean isMiscellaneous) {
        mIsEnableMiscellaneous = isMiscellaneous;
        invalidate();
    }

    public void enableDrawPoint(boolean isDrawPoint) {
        mIsEnableDrawPoint = isDrawPoint;
        if (mIsEnableDrawPoint && mPoints == null) {
            mPoints = new float[1000];
            for (int i = 0; i < mPoints.length; i++) {
                if (i % 2 == 0) {
                    mPoints[i] = mRandomX.nextInt(1080);
                } else {
                    mPoints[i] = 120 + mRandomY.nextInt(60);
                }
            }
        } else {
            mPoints = null;
        }
        invalidate();
    }

    @SuppressLint("CheckResult")
    public void enableUpload(boolean isSynced) {
        mIsEnableUpload = isSynced;
        final ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (isSynced) {
            layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
            if (mIsEnableMiscellaneous) {
                loadBitmap();
                invalidate();
            } else {
                Rx2Util
                        .getObservableOnIo(new Source<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                loadBitmap();
                                return true;
                            }
                        })
                        .delay(1, TimeUnit.SECONDS)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Boolean>() {
                            @Override
                            public void accept(Boolean b) throws Exception {
                                invalidate();
                            }
                        });
            }
        } else {
            Rx2Util.getObservableOnIo(new Source<Object>() {
                @Override
                public Object call() throws Exception {
                    recycleBitmap();
                    return null;
                }
            });
            invalidate();
        }
    }

    private void recycleBitmap() {
        recycle(mBitmap);
        recycle(mBitmap1);
        recycle(mBitmap2);
        recycle(mBitmap3);
        recycle(mBitmap4);
        recycle(mBitmap5);
    }

    private void recycle(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    private void loadBitmap() {
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_thunderstorm);
        mBitmap1 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_rain);
        mBitmap2 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_night);
        mBitmap3 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_snow);
        mBitmap4 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_sunny);
        mBitmap5 = BitmapFactory.decodeResource(getResources(), R.drawable.bg_weather_cloudy);
    }
}
