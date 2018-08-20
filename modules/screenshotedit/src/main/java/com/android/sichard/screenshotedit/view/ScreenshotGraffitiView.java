package com.android.sichard.screenshotedit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * <br>类描述:截图编辑界面的涂鸦View
 * <br>详细描述:注意该View继承自ImageView，使用了adjustViewBounds属性，所以该View的大小由其bitmap的大小决定
 * <br><b>Author sichard</b>
 * <br><b>Date 18-8-10</b>
 */
public class ScreenshotGraffitiView extends ImageView {
    private Paint mTextPaint;
    private ArrayList<Path> mPathList = new ArrayList<>();
    private Path mPath;
    private Bitmap mBitmap;
    private boolean mIsSetBitmap;
    private int mWidth;
    private int mHeight;
    private HashMap<Path, Paint> mPathMap = new HashMap<>();

    public ScreenshotGraffitiView(Context context) {
        super(context);
        init();
    }

    public ScreenshotGraffitiView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenshotGraffitiView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

    }

    private void init() {
        float paintWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        mTextPaint = new Paint();
        mTextPaint.setStyle(Paint.Style.STROKE);
        mTextPaint.setColor(0xFFFF3232);
        mTextPaint.setStrokeWidth(paintWidth);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float preX = event.getX();
        float preY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                preX = event.getX();
                preY = event.getY();
                mPath = new Path();
                mPath.moveTo(preX, preY);
                break;
            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(preX, preY);
                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mPathList.add(mPath);
                mPathMap.put(mPath, new Paint(mTextPaint));
                break;
        }
        return true;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        if (!mIsSetBitmap) {
            mIsSetBitmap = true;
            View parent = (View) getParent();
            int height = parent.getHeight();
            int width = parent.getWidth();
            float rate = height * 1.0f / width;

            float bitmapRate = mBitmap.getHeight() * 1.0f / mBitmap.getWidth();
            if (bitmapRate > rate) {
                width = (int) (height * 1.0f / mBitmap.getHeight() * mBitmap.getWidth());
            } else {
                height = (int) (width * 1.0f / mBitmap.getWidth() * mBitmap.getHeight());
            }
            setImageBitmap(Bitmap.createScaledBitmap(mBitmap, width, height, true));
        }
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Path path : mPathList) {
            Paint paint = mPathMap.get(path);
            canvas.drawPath(path, paint);
        }
        // 绘制正在触发move事件的path
        if (mPath != null) {
            canvas.drawPath(mPath, mTextPaint);
        }
    }

    /**
     * 设置要显示bitmap
     * 注意：该方法中重新计算了bitmap的宽高，进而决定了ImageView的大小。当前view的大小由{@link #setBitmapSize(int, int)}决定
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        // 如果没有拿到父容器的宽高，就用bitmap的宽高来代替
        if (mHeight == 0 || mWidth == 0) {
            mHeight = bitmap.getHeight();
            mWidth = bitmap.getWidth();
        }
        float rate = mHeight * 1.0f / mWidth;
        float bitmapRate = mBitmap.getHeight() * 1.0f / mBitmap.getWidth();
        if (bitmapRate > rate) {
            mWidth = (int) (mHeight * 1.0f / mBitmap.getHeight() * mBitmap.getWidth());
        } else {
            mHeight = (int) (mWidth * 1.0f / mBitmap.getWidth() * mBitmap.getHeight());
        }
        setImageBitmap(Bitmap.createScaledBitmap(mBitmap, mWidth, mHeight, true));
    }

    /**
     * 获取编辑后的bitmap
     * @return
     */
    public Bitmap getBitmap() {
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        Bitmap drawingCache = getDrawingCache();
        Bitmap bitmap = Bitmap.createScaledBitmap(drawingCache, mBitmap.getWidth(), mBitmap.getHeight(), true);
        destroyDrawingCache();
        setDrawingCacheEnabled(false);
        // 这里需要回收传入的bitmap
        mBitmap.recycle();
        return bitmap;
    }

    /**
     * 设定Bitmap的大小，进而决定当前view的大小
     *
     * @param width
     * @param height
     */
    public void setBitmapSize(int width, int height) {
        mWidth = width;
        mHeight = height;
    }


    /**
     * 设置涂鸦颜色
     *
     * @param color
     */
    public void setColor(int color) {
        mTextPaint.setColor(color);
    }
}
