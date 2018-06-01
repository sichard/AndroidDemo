package com.sichard.demo.view.waveview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.android.sichard.search.common.TaskManager;
import com.sichard.demo.R;

import static java.lang.Thread.sleep;

/**
 * <br>类描述:贝塞尔曲线实现的波浪效果View
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-31</b>
 */
public class BesselWaveView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private static final int RED_IMG_LINE = 14;
    private static final int YELLOW_IMG_LINE = 60;
    private static final int DEFAULT_ABOVE_WAVE_ALPHA = 128;

    private Bitmap mBackground;
    private SurfaceHolder mSurfaceHolder;
    /** 总共平移的间隔 */
    private int totalWidth = 0;
    private Path mEdgePath;
    private Paint mPaint;
    private float mProgress;

    private static final int RED_COLOR = Color.parseColor("#E31100");
    private static final int GREEN_COLOR = Color.parseColor("#00bcd4");
    private static final int YELLOW_COLOR = Color.parseColor("#D5C113");

    Path mAbovePath = new Path();
    Path mBelowPath = new Path();


    public BesselWaveView(Context context) {
        super(context);
    }

    public BesselWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // 注意不设置下面这句，clip后，canvas会有黑边
        setZOrderOnTop(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.setFormat(PixelFormat.TRANSLUCENT);
        mSurfaceHolder.addCallback(this);
        mEdgePath = new Path();
        mPaint = new Paint();
        mPaint.setColor(Color.BLUE);
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public BesselWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mBackground = BitmapFactory.decodeResource(getResources(), R.mipmap.bg_wave);
        TaskManager.execWorkTask(this);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        TaskManager.removeWorkTask(this);
    }

    @Override
    public void run() {
        // 修改波浪的宽度
        int width = getMeasuredWidth();
        // 修改波浪的高度
        int height = getMeasuredHeight();
        Rect rect = new Rect(0, 0, width, height);
        // 振幅(值越大，振幅越大)
        int amplitude = height / 7;
        // 频率(值越大，频率越高)
        int frequency = 15;
        // 波长(值越大波长越爱)
        int wavelength = width * 2;
        // x轴偏移量(两个波之间的偏移量)
        int offsetX = wavelength / 4;

        while (true) {
            try {
                sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Canvas canvas = mSurfaceHolder.lockCanvas();

            if (canvas == null) {
                return;
            }
            try {
                mEdgePath.reset();
                mEdgePath.addCircle(width / 2, height / 2, width / 2, Path.Direction.CW);
                canvas.clipPath(mEdgePath, Region.Op.INTERSECT);
                canvas.drawColor(Color.WHITE);
                canvas.drawBitmap(mBackground, null, rect, null);

                // y轴偏移量
                float offset = (1 - mProgress) * height;
                mAbovePath.reset();
                mAbovePath.moveTo(-wavelength + totalWidth, offset);
                mAbovePath.quadTo(-wavelength * 3 / 4 + totalWidth, amplitude + offset, -wavelength / 2 + totalWidth, offset);
                mAbovePath.quadTo(-wavelength / 4 + totalWidth, -amplitude + offset, totalWidth, offset);
                mAbovePath.quadTo(wavelength / 4 + totalWidth, amplitude + offset, wavelength / 2 + totalWidth, offset);
                mAbovePath.quadTo(wavelength * 3 / 4 + totalWidth, -amplitude + offset, wavelength + totalWidth, offset);

                mAbovePath.lineTo(wavelength + totalWidth, height);
                mAbovePath.lineTo(-wavelength + totalWidth, height);
                mAbovePath.close();

                mBelowPath.reset();
                mBelowPath.addPath(mAbovePath, -offsetX, 0);

                canvas.drawPath(mAbovePath, mPaint);
                canvas.drawPath(mBelowPath, mPaint);

                totalWidth += frequency;

                if (totalWidth > wavelength) {
                    totalWidth = 0;
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mSurfaceHolder.unlockCanvasAndPost(canvas);
            }
        }
    }

    /**
     *
     * @param progress
     */
    public void setProgress(float progress) {
        if (progress <= RED_IMG_LINE) {
            mPaint.setColor(RED_COLOR);
        } else if (progress >= RED_IMG_LINE && progress <= YELLOW_IMG_LINE) {
            mPaint.setColor(YELLOW_COLOR);
        } else {
            mPaint.setColor(GREEN_COLOR);
        }
        mPaint.setAlpha(DEFAULT_ABOVE_WAVE_ALPHA);
        progress = progress * 0.01f;
        this.mProgress = progress > 1 ? 1 : progress;
    }
}
