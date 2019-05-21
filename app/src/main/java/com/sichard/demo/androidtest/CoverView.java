package com.sichard.demo.androidtest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sichard.demo.R;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2019-2-14
 */
public class CoverView extends View {
    private Bitmap mBitmap, mCoverBitmap;
    private Paint mPaint;
    private PorterDuffXfermode mXfermode;
    private int mLeft;
    private Runnable mAction;
    private int mInstant = -3;
    private Bitmap mBitmap1;

    public CoverView(Context context) {
        super(context);
    }

    public CoverView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CoverView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        /**
         * 开启硬件离屏缓存
         */
//        setLayerType(LAYER_TYPE_HARDWARE, null);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);

        mBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.particle);
        mCoverBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.cover);

        mAction = new Runnable() {
            @Override
            public void run() {
                mLeft += mInstant;
                if (mLeft <= -100) {
                    mInstant = 3;
                } else if (mLeft >= 0) {
                    mInstant = -3;
                }
                invalidate();
                postDelayed(mAction, 16);
            }
        };
        postDelayed(mAction, 50);


        mBitmap1 = drawableToBitmap(getResources().getDrawable(R.drawable.bg_float_expand_layout));
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);

        return bitmap;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawARGB(255, 139, 197, 186);
//        int i = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
//        canvas.drawBitmap(mBitmap, 0, 0, mPaint);
//        mPaint.setXfermode(mXfermode);
//        canvas.drawBitmap(mCoverBitmap, mLeft, 0, mPaint);
//        mPaint.setXfermode(null);
//        canvas.restoreToCount(i);
        canvas.drawBitmap(mBitmap1, 40, getHeight() - 40, null);
    }

}
