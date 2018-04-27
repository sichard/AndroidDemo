package com.sichard.demo.animation.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 * <br>类描述:属性动画view
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-25</b>
 */
public class ValueAnimationView extends View {

    /** 圆的半径 = 100dp */
    private static final int RADIUS = 50;
    /** 圆的半径 */
    private float mRADIUS_PIX = 1;
    /** 绘图画笔 */
    private Paint mPaint;

    public ValueAnimationView(Context context) {
        super(context);
        initData();
    }

    public ValueAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initData();
    }

    public ValueAnimationView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData();
    }

    private void initData() {
        mRADIUS_PIX = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, RADIUS, getResources().getDisplayMetrics());
        mPaint = new Paint();
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        }, 1000);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, mRADIUS_PIX, mPaint);
    }

    /**
     * 属性动画必须设置的方法
     * @param color color值(对应属性动画的"color")
     */
    public void setColor(int color) {
        mPaint.setColor(color);
        invalidate();
    }
}
