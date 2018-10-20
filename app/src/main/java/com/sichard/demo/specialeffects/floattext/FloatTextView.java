package com.sichard.demo.specialeffects.floattext;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

import com.sichard.demo.R;

import java.util.ArrayList;

/**
 * <br>类描述：浮动的TextView
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-13</b>
 */
public class FloatTextView extends SurfaceView implements SurfaceHolder.Callback {

    /** 绘制线程 */
    private final DrawThread mDrawThread = new DrawThread();
    private TextDrawerManger mTextDrawerManger;
    //当前屏幕宽高
    private int mWidth, mHeight;
    private ArrayList<String> mTextList;
    private Bitmap mBgBitmap;

    public FloatTextView(Context context) {
        super(context);
        initThreadAndHolder();
    }

    public FloatTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThreadAndHolder();
    }

    public FloatTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThreadAndHolder();
    }

    /**
     * 初始化绘制线程和 SurfaceHolder
     *
     */
    private void initThreadAndHolder() {
        setZOrderOnTop(true);
        SurfaceHolder surfaceHolder = getHolder();
        //添加回调
        surfaceHolder.addCallback(this);
        //渐变效果 就是显示SurfaceView的时候从暗到明
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);

        mTextDrawerManger = new TextDrawerManger();
        mBgBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bitmap_float);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        holder.setFormat(PixelFormat.TRANSLUCENT);
        mDrawThread.mSurfaceHolder = holder;
        Log.i("sichardcao", "FloatTextView|surfaceCreated:" + "====create");
        //唤醒
//        mDrawThread.notify();
        //开启绘制线程
        mDrawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mWidth = width;
        mHeight = height;
        mTextDrawerManger.setViewSize(width, height);
        Log.i("sichardcao", "FloatTextView|surfaceChanged:" + "#########change");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mDrawThread) {
            mDrawThread.mSurfaceHolder = holder;
        }
        holder.removeCallback(this);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mTextDrawerManger.setViewSize(mWidth, mHeight);
        mTextDrawerManger.setData(mTextList, mBgBitmap);

        Log.i("sichardcao", "FloatTextView|onSizeChanged:" + "--------onSizeChanged---" + mWidth);
    }

    public void setTextList(ArrayList<String> textList) {
        mTextList = new ArrayList<>(textList);
    }

    /**
     * 绘制线程 必须开启子线程绘制 防止出现阻塞主线程的情况
     */
    private class DrawThread extends Thread {
        SurfaceHolder mSurfaceHolder;
        Canvas mCanvas;

        @Override
        public void run() {
            //一直循环 不断绘制
            while (true) {
                synchronized (this) {
                    //动画开始时间
                    final long startTime = AnimationUtils.currentAnimationTimeMillis();
                    //处理画布并进行绘制
                    processDrawCanvas(mCanvas);

                    //绘制时间
                    final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                    //处理一下线程需要的睡眠时间
                    processDrawThreadSleep(drawTime);
                }
            }
        }

        /**
         * 处理画布与绘制过程 要注意一定要保证是同步锁中才能执行 否则会出现
         *
         * @param mCanvas 画布
         */
        private void processDrawCanvas(Canvas mCanvas) {
            try {
                //加锁画布
                mCanvas = mSurfaceHolder.lockCanvas();
                //防空保护
                if (mCanvas != null) {
                    //清屏操作
                    mCanvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                    //真正开始画 SurfaceView 的地方
                    drawSurface(mCanvas);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            } finally {
                if (mCanvas != null) {
                    //释放canvas锁，并显示视图
                    mSurfaceHolder.unlockCanvasAndPost(mCanvas);
                }
            }
        }

        /**
         * 真正的绘制 SurfaceView
         *
         * @param canvas 画布
         */
        private void drawSurface(Canvas canvas) {

            //防空保护
            if (mWidth == 0 || mHeight == 0) {
                return ;
            }
            mTextDrawerManger.drawText(canvas);

        }

        /**
         * 处理线程需要的睡眠时间
         * View通过刷新来重绘视图，在一些需要频繁刷新或执行大量逻辑操作时，超过16ms就会导致明显卡顿
         *
         * @param drawTime 绘制时间
         */
        private void processDrawThreadSleep(long drawTime) {
            //需要睡眠时间
            final long needSleepTime = 16 - drawTime;

            if (needSleepTime > 0) {
                try {
                    Log.i("sichardcao", "DrawThread|processDrawThreadSleep:" + "==========sleep");
                    Thread.sleep(needSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
