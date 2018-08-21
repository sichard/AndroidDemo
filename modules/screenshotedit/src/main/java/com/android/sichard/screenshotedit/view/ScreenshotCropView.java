package com.android.sichard.screenshotedit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.android.sichard.screenshotedit.IScreenshotEditView;


/**
 * <br>类描述:截屏裁剪view
 * <br>详细描述:
 * <br><mBottom>Author sichard</mBottom>
 * <br><mBottom>Date 18-8-3</mBottom>
 */
public class ScreenshotCropView extends AppCompatImageView implements IScreenshotEditView {
    private Bitmap mBitmap;
    /** 裁剪框的Paint */
    private Paint mFramePaint;
    private float mFramePaintWidth;
    /** 裁剪框四个角的Paint */
    private Paint mFrameCornerPaint;
    private float mFrameCornerPaintWidth;
    private float mFrameCornerPaintHalfWidth;
    private float mFrameCornerLength;
    /** 未选中区域颜色 */
    private Paint mFrameOutPaint;

    private int mLeft = 0;
    private int mTop = 0;
    private int mRight = 0;
    private int mBottom = 0;

    private int mPreX;
    private int mPreY;

    /** 触点位置的标志位 */
    private byte mFlag;
    private static final byte mFlagIn = 1;
    private static final byte mFlagL = 2;
    private static final byte mFlagT = 3;
    private static final byte mFlagR = 4;
    private static final byte mFlagB = 5;
    private static final byte mFlagLT = 6;
    private static final byte mFlagRT = 7;
    private static final byte mFlagRB = 8;
    private static final byte mFlagLB = 9;
    private static final byte mFlagOut = 10;

    /** 手指触点的范围值 */
    private int LOCATION;
    /** 边框接近的范围 */
    private int RANGE;

    /** 裁剪框的宽度 */
    private int mFrameWidth;
    /** 裁剪框的高度 */
    private int mFrameHeight;
    /** 裁剪框的左上边界值 */
    private int mLeftTopBoundary;
    /** 裁剪框的底部边界值 */
    private int mRightBoundary;
    /** 裁剪框的右边界值 */
    private int mBottomBoundary;
    /** 当前的view的layout_padding */
    private int mPadding;

    public ScreenshotCropView(Context context) {
        super(context);
        init();
    }

    public ScreenshotCropView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScreenshotCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void init() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        mFramePaintWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, displayMetrics);
        mFrameCornerPaintWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, displayMetrics);
        mFrameCornerPaintHalfWidth = mFrameCornerPaintWidth / 2;
        mFrameCornerLength = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, displayMetrics);

        LOCATION = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, displayMetrics);
        RANGE = LOCATION * 3;

        mFramePaint = new Paint();
        mFramePaint.setStyle(Paint.Style.STROKE);
        mFramePaint.setStrokeMiter(6);
        mFramePaint.setStrokeWidth(mFramePaintWidth);
        mFramePaint.setColor(0xFF33FFE2);

        mFrameCornerPaint = new Paint();
        mFrameCornerPaint.setStyle(Paint.Style.STROKE);
        mFrameCornerPaint.setStrokeMiter(6);
        mFrameCornerPaint.setStrokeWidth(mFrameCornerPaintWidth);
        mFrameCornerPaint.setColor(0xFF33FFE2);

        mFrameOutPaint = new Paint();
        mFrameOutPaint.setColor(Color.BLACK);
        mFrameOutPaint.setAlpha(100);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mPadding = getPaddingLeft();
        mLeftTopBoundary = mPadding;
        mRightBoundary = getWidth() - mPadding;
        mBottomBoundary = getHeight() - mPadding;

        mLeft = mLeftTopBoundary;
        mTop = mLeftTopBoundary;
        mRight = mRightBoundary;
        mBottom = mBottomBoundary;
    }

    /**
     * 设置裁剪view的bitmap，这里对裁剪的view进行了缩放，保证裁剪view刚好包裹Bitmap，同时填充满父容器
     *
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
        View parent = (View) getParent();
        int height = parent.getHeight();
        int width = parent.getWidth();
        float rate = height * 1.0f / width;

        float bitmapRate = bitmap.getHeight() * 1.0f / bitmap.getWidth();
        if (bitmapRate > rate) {
            width = (int) (height * 1.0f / bitmap.getHeight() * bitmap.getWidth());
        } else {
            height = (int) (width * 1.0f / bitmap.getWidth() * bitmap.getHeight());
        }
        setImageBitmap(Bitmap.createScaledBitmap(bitmap, width, height, true));
        disableClipOnParents(this);
    }

    /**
     * 让父容器允许子view绘制在子view的边界之外
     *
     * @param v
     */
    private void disableClipOnParents(View v) {
        if (v.getParent() == null) {
            return;
        }

        if (v instanceof ViewGroup) {
            ((ViewGroup) v).setClipChildren(false);
        }

        if (v.getParent() instanceof View) {
            disableClipOnParents((View) v.getParent());
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPreX = (int) event.getX();
                mPreY = (int) event.getY();
                // 判断点击点是否在矩形框内
                if (mPreX > mLeft + LOCATION && mPreY > mTop + LOCATION
                        && mPreX < mRight - LOCATION && mPreY < mBottom - LOCATION) {
                    mFlag = mFlagIn;
                    mFrameWidth = mRight - mLeft;
                    mFrameHeight = mBottom - mTop;
                }
                // 判断是否左边框
                else if (mPreX > mLeft - LOCATION && mPreY > mTop + LOCATION
                        && mPreX < mLeft + LOCATION && mPreY < mBottom - LOCATION) {
                    mFlag = mFlagL;
                }
                // 判断是否上边框
                else if (mPreX > mLeft + LOCATION && mPreY > mTop - LOCATION
                        && mPreX < mRight - LOCATION && mPreY < mTop + LOCATION) {
                    mFlag = mFlagT;
                }
                // 判断是否右边框
                else if (mPreX > mRight - LOCATION && mPreY > mTop + LOCATION
                        && mPreX < mRight + LOCATION && mPreY < mBottom - LOCATION) {
                    mFlag = mFlagR;
                }
                // 判断是否下边框
                else if (mPreX > mLeft + LOCATION && mPreY > mBottom - LOCATION
                        && mPreX < mRight - LOCATION && mPreY < mBottom + LOCATION) {
                    mFlag = mFlagB;
                }
                // 判断是否左上角
                else if (mPreX > mLeft - LOCATION && mPreY > mTop - LOCATION
                        && mPreX < mLeft + LOCATION && mPreY < mTop + LOCATION) {
                    mFlag = mFlagLT;
                }
                // 判断是否右上角
                else if (mPreX > mRight - LOCATION && mPreY > mTop - LOCATION
                        && mPreX < mRight + LOCATION && mPreY < mTop + LOCATION) {
                    mFlag = mFlagRT;
                }
                // 判断是否右下角
                else if (mPreX > mRight - LOCATION && mPreY > mBottom - LOCATION
                        && mPreX < mRight + LOCATION && mPreY < mBottom + LOCATION) {
                    mFlag = mFlagRB;
                }
                // 判断是否左下角
                else if (mPreX > mLeft - LOCATION && mPreY > mBottom - LOCATION
                        && mPreX < mLeft + LOCATION && mPreY < mBottom + LOCATION) {
                    mFlag = mFlagLB;
                } else {
                    mFlag = mFlagOut;
                }

            case MotionEvent.ACTION_MOVE:
                int curX = (int) event.getX();
                int curY = (int) event.getY();

                switch (mFlag) {
                    //如果在边框内
                    case mFlagIn:

                        mLeft = mLeft + curX - mPreX;
                        mTop = mTop + curY - mPreY;
                        mRight = mRight + curX - mPreX;
                        mBottom = mBottom + curY - mPreY;

                        // 如果碰到左边框
                        if (mLeft < mLeftTopBoundary) {
                            mLeft = mLeftTopBoundary;
                            mRight = mLeft + mFrameWidth;
                        }
                        // 如果碰到上边框
                        if (mTop < mLeftTopBoundary) {
                            mTop = mLeftTopBoundary;
                            mBottom = mTop + mFrameHeight;
                        }
                        // 如果碰到右边框
                        if (mRight > mRightBoundary) {
                            mRight = mRightBoundary;
                            mLeft = mRight - mFrameWidth;
                        }
                        // 如果碰到下边框
                        if (mBottom > mBottomBoundary) {
                            mBottom = mBottomBoundary;
                            mTop = mBottom - mFrameHeight;
                        }

                        // 注意移动后,要把新坐标赋值给之前的坐标
                        mPreX = curX;
                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是左边框
                    case mFlagL:
                        mLeft = mLeft + curX - mPreX;

                        // 判断是否接近右边框
                        if (mRight - mLeft < RANGE) {
                            mLeft = mRight - RANGE;
                            curX = mLeft;
                        }
                        // 判断触点是否越出当前view左边界
                        if (mLeft < mLeftTopBoundary) {
                            mLeft = mLeftTopBoundary;
                            curX = mLeftTopBoundary;
                        }

                        mPreX = curX;
                        invalidate();
                        break;
                    //如果是上边框
                    case mFlagT:
                        mTop = mTop + curY - mPreY;

                        // 判断是否接近下边框
                        if (mBottom - mTop < RANGE) {
                            mTop = mBottom - RANGE;
                            curY = mTop;
                        }
                        // 判断触点是否越出当前View的上边界
                        if (mTop < mLeftTopBoundary) {
                            mTop = mLeftTopBoundary;
                            curY = mLeftTopBoundary;
                        }

                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是右边框
                    case mFlagR:
                        mRight = mRight + curX - mPreX;

                        // 判断是否接近左边框
                        if (mRight - mLeft < RANGE) {
                            mRight = mLeft + RANGE;
                            curX = mRight;
                        }
                        // 判断触点是否越出当前View的右边界
                        if (mRight > mRightBoundary) {
                            mRight = mRightBoundary;
                            curX = mRightBoundary;
                        }
                        mPreX = curX;
                        invalidate();
                        break;
                    //如果是下边框
                    case mFlagB:
                        mBottom = mBottom + curY - mPreY;

                        // 判断是否接近上边框
                        if (mBottom - mTop < RANGE) {
                            mBottom = mTop + RANGE;
                            curY = mBottom;
                        }
                        // 判断触点是否越出当前View的下边界
                        if (mBottom > mBottomBoundary) {
                            mBottom = mBottomBoundary;
                            curY = mBottomBoundary;
                        }
                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是左上角
                    case mFlagLT:
                        mLeft = mLeft + curX - mPreX;
                        mTop = mTop + curY - mPreY;

                        // 判断是否接近右边框
                        if (mRight - mLeft < RANGE) {
                            mLeft = mRight - RANGE;
                            curX = mLeft;
                        }
                        // 判断是否接近下边框
                        if (mBottom - mTop < RANGE) {
                            mTop = mBottom - RANGE;
                            curY = mTop;
                        }
                        // 判断触点是否越出当前view的左边界
                        if (mLeft < mLeftTopBoundary) {
                            mLeft = mLeftTopBoundary;
                            curX = mLeftTopBoundary;
                        }
                        // 判断触点是否越出当前view的上边界
                        if (mTop < mLeftTopBoundary) {
                            mTop = mLeftTopBoundary;
                            curY = mLeftTopBoundary;
                        }

                        mPreX = curX;
                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是右上角
                    case mFlagRT:
                        mRight = mRight + curX - mPreX;
                        mTop = mTop + curY - mPreY;

                        // 判断是否接近左边框
                        if (mRight - mLeft < RANGE) {
                            mRight = mLeft + RANGE;
                            curX = mRight;
                        }
                        // 判断是否接近下边框
                        if (mBottom - mTop < RANGE) {
                            mTop = mBottom - RANGE;
                            curY = mTop;
                        }
                        // 判断触点是否越出当前view的右边界
                        if (mRight > mRightBoundary) {
                            mRight = mRightBoundary;
                            curX = mRightBoundary;
                        }
                        // 判断触点是否越出当前view的上边界
                        if (mTop < mLeftTopBoundary) {
                            mTop = mLeftTopBoundary;
                            curY = mLeftTopBoundary;
                        }

                        mPreX = curX;
                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是右下角
                    case mFlagRB:
                        mRight = mRight + curX - mPreX;
                        mBottom = mBottom + curY - mPreY;

                        // 判断是否接近左边框
                        if (mRight - mLeft < RANGE) {
                            mRight = mLeft + RANGE;
                            curX = mRight;
                        }
                        // 判断是否接近上边框
                        if (mBottom - mTop < RANGE) {
                            mBottom = mTop + RANGE;
                            curY = mBottom;
                        }
                        // 判断触点是否越出当前view的右边界
                        if (mRight > mRightBoundary) {
                            mRight = mRightBoundary;
                            curX = mRightBoundary;
                        }
                        // 判断触点是否越出当前view的下边界
                        if (mBottom > mBottomBoundary) {
                            mBottom = mBottomBoundary;
                            curY = mBottomBoundary;
                        }

                        mPreX = curX;
                        mPreY = curY;
                        invalidate();
                        break;
                    //如果是左下角
                    case mFlagLB:
                        mLeft = mLeft + curX - mPreX;
                        mBottom = mBottom + curY - mPreY;

                        // 判断是否接近右边框
                        if (mRight - mLeft < RANGE) {
                            mLeft = mRight - RANGE;
                            curX = mLeft;
                        }
                        // 判断是否接近上边框
                        if (mBottom - mTop < RANGE) {
                            mBottom = mTop + RANGE;
                            curY = mBottom;
                        }
                        // 判断触点是否越出当前view的左边界
                        if (mLeft < mLeftTopBoundary) {
                            mLeft = mLeftTopBoundary;
                            curX = mLeftTopBoundary;
                        }
                        // 判断触点是否越出当前view的xia边界
                        if (mBottom > mBottomBoundary) {
                            mBottom = mBottomBoundary;
                            curY = mBottomBoundary;
                        }

                        mPreX = curX;
                        mPreY = curY;
                        invalidate();
                        break;
                    case mFlagOut:

                        break;
                    default:
                        break;
                }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRect(canvas);
    }

    /**
     * 画出矩形
     *
     * @param canvas 传入画布
     */
    private void drawRect(Canvas canvas) {

        canvas.drawRect(mLeft, mTop, mRight, mBottom, mFramePaint);

        // 绘制未选中区域的阴影
        canvas.drawRect(mPadding, mPadding, mRightBoundary, mTop, mFrameOutPaint);
        canvas.drawRect(mPadding, mBottom, mRightBoundary, mBottomBoundary, mFrameOutPaint);
        canvas.drawRect(mPadding, mTop, mLeft, mBottom, mFrameOutPaint);
        canvas.drawRect(mRight, mTop, mRightBoundary, mBottom, mFrameOutPaint);

        // 边角线共用的x,y坐标
        float leftX = mLeft - mFrameCornerPaintHalfWidth;
        float topY = mTop - mFrameCornerPaintHalfWidth;
        float rightX = mRight + mFrameCornerPaintHalfWidth;
        float bottomY = mBottom + mFrameCornerPaintHalfWidth;

        // 绘制左上边角
        canvas.drawLine(mLeft - mFrameCornerPaintWidth, topY, mLeft + mFrameCornerLength, topY, mFrameCornerPaint);
        canvas.drawLine(leftX, mTop - mFrameCornerPaintWidth, leftX, mTop + mFrameCornerLength, mFrameCornerPaint);

        // 绘制右上边角
        canvas.drawLine(mRight + mFrameCornerPaintWidth, topY, mRight - mFrameCornerLength, topY, mFrameCornerPaint);
        canvas.drawLine(rightX, mTop - mFrameCornerPaintWidth, rightX, mTop + mFrameCornerLength, mFrameCornerPaint);

        // 绘制右下边角
        canvas.drawLine(mRight + mFrameCornerPaintWidth, bottomY, mRight - mFrameCornerLength, bottomY, mFrameCornerPaint);
        canvas.drawLine(rightX, mBottom + mFrameCornerPaintWidth, rightX, mBottom - mFrameCornerLength, mFrameCornerPaint);

        // 绘制左下边角
        canvas.drawLine(mLeft - mFrameCornerPaintWidth, bottomY, mLeft + mFrameCornerLength, bottomY, mFrameCornerPaint);
        canvas.drawLine(leftX, mBottom + mFramePaintWidth, leftX, mBottom - mFrameCornerLength, mFrameCornerPaint);

        //边线中点坐标
        int middleY = (mBottom + mTop) / 2;
        int middleX = (mRight + mLeft) / 2;
        float middleCornerLength = mFrameCornerLength / 2 + mFrameCornerPaintWidth;
        // 绘制左中线
        canvas.drawLine(leftX, middleY - middleCornerLength, leftX, middleY + middleCornerLength, mFrameCornerPaint);

        // 绘制上中线
        canvas.drawLine(middleX - middleCornerLength, topY, middleX + middleCornerLength, topY, mFrameCornerPaint);

        // 绘制右中线
        canvas.drawLine(rightX, middleY - middleCornerLength, rightX, middleY + middleCornerLength, mFrameCornerPaint);

        // 绘制下中线
        canvas.drawLine(middleX - middleCornerLength, bottomY, middleX + middleCornerLength, bottomY, mFrameCornerPaint);

        //绘制边角圆圈
//        canvas.drawCircle(mLeft, mTop, RADIUS, mFramePaint);
//        canvas.drawCircle(mRight, mTop, RADIUS, mFramePaint);
//        canvas.drawCircle(mRight, mBottom, RADIUS, mFramePaint);
//        canvas.drawCircle(mLeft, mBottom, RADIUS, mFramePaint);
//        canvas.drawCircle(mLeft, mTop +(mBottom - mTop)/2, RADIUS, mFramePaint);
//        canvas.drawCircle(mLeft +(mRight - mLeft)/2, mTop, RADIUS, mFramePaint);
//        canvas.drawCircle(mRight, mTop +(mBottom - mTop)/2, RADIUS, mFramePaint);
//        canvas.drawCircle(mLeft +(mRight - mLeft)/2, mBottom, RADIUS, mFramePaint);
    }

    public Bitmap getBitmap() {
        int bitmapWidth = mBitmap.getWidth();
        int bitmapHeight = mBitmap.getHeight();
        float widthRate = bitmapWidth * 1.0f / (getWidth() - 2 * mPadding);
        float heightRate = bitmapHeight * 1.0f / (getHeight() - 2 * mPadding);
        int x = (int) ((mLeft - mPadding) * widthRate);
        int y = (int) ((mTop - mPadding) * heightRate);

        int width = (int) ((mRight - mLeft) * widthRate);
        int height = (int) ((mBottom - mTop) * heightRate);

        try {
            return Bitmap.createBitmap(mBitmap, x, y, width, height);
        } catch (Exception e) {
            e.printStackTrace();
            return Bitmap.createBitmap(mBitmap, 0, 0, bitmapWidth, bitmapHeight);
        }
    }
}
