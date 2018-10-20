package com.sichard.demo.view.XfermodeView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

/**
 * <br>类描述：PorterDuffXfermode的演示view
 * <br>详细描述：特别提示！！！！该view要关闭硬件加速{@link #setLayerType(int, Paint)}
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-20</b>
 */
public class XfermodeView extends View {
    private Paint mPaint = new Paint();
    private int mFlag;

    public XfermodeView(Context context) {
        super(context);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public XfermodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    public XfermodeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int radius = width / 3;

        canvas.drawARGB(255, 139, 197, 186);
        //绘制黄色圆形
        mPaint.setColor(0xFFFFCC44);
        canvas.drawCircle(radius, radius, radius, mPaint);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //绘制蓝色的矩形
        mPaint.setColor(0xFF66AAFF);
        canvas.drawRect(radius, radius, radius * 2.7f, radius * 2.7f, mPaint);
//        mPaint.setXfermode(null);
    }
}
