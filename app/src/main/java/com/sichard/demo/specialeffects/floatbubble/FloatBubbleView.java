package com.sichard.demo.specialeffects.floatbubble;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.animation.AnimationUtils;

/**
 * <br>类描述：用圆形浮动气泡填充的View
 * <br>详细描述：因为气泡需要不断绘制 所以防止阻塞UI线程 需要继承 SurfaceView 开启线程更新 并实现回调类
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-13</b>
 */
public class FloatBubbleView extends SurfaceView implements SurfaceHolder.Callback {

    /** 绘制线程 */
    private final DrawThread mDrawThread = new DrawThread();
    private BubbleDrawer mPreDrawer;
    /** 当前绘制对象 */
    private BubbleDrawer mCurDrawer;
    /** 当前透明度 (范围为0f~1f，因为 CircleBubble 中 convertAlphaColor 方法已经处理过了) */
    private float curDrawerAlpha = 0f;
    //当前屏幕宽高
    private int mWidth, mHeight;
    private long now;
    private int framesCount;
    private int framesCountAvg;
    private Paint paint;

    public FloatBubbleView(Context context) {
        super(context);
        initThreadAndHolder();
    }

    public FloatBubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initThreadAndHolder();
    }

    public FloatBubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initThreadAndHolder();
    }

    /**
     * 初始化绘制线程和 SurfaceHolder
     *
     */
    private void initThreadAndHolder() {
        SurfaceHolder surfaceHolder = getHolder();
        //添加回调
        surfaceHolder.addCallback(this);
        //渐变效果 就是显示SurfaceView的时候从暗到明
        surfaceHolder.setFormat(PixelFormat.RGBA_8888);
        //开启绘制线程
        mDrawThread.start();
    }

    /**
     * 当view的大小发生变化时触发
     *
     * @param w    当前宽度
     * @param h    当前高度
     * @param oldw 变化前宽度
     * @param oldh 变化前高度
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    /**
     * 设置绘制者
     *
     * @param bubbleDrawer 气泡绘制
     */
    public void setDrawer(BubbleDrawer bubbleDrawer) {
        //防空保护
        if (bubbleDrawer == null) {
            return;
        }
        //完全透明
        curDrawerAlpha = 0f;
        //如果当前有正在绘制的对象 直接设置为前一次绘制对象
        if (this.mCurDrawer != null) {
            this.mPreDrawer = mCurDrawer;
        }
        //当前绘制对象 为设置的对象
        this.mCurDrawer = bubbleDrawer;
    }

    /**
     * 绘制线程 必须开启子线程绘制 防止出现阻塞主线程的情况
     */
    private class DrawThread extends Thread {
        SurfaceHolder mSurface;
        /** 三种状态 */
        boolean mRunning, mActive, mQuit;
        Canvas mCanvas;
        private long framesTimer;

        @Override
        public void run() {
            //一直循环 不断绘制
            while (true) {
                synchronized (this) {
                    //根据返回值 判断是否直接返回 不进行绘制
                    if (!processDrawThreadState()) {
                        return;
                    }
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
         * 处理绘制线程的状态问题
         *
         * @return true：不结束继续绘制 false：结束且不绘制
         */
        private boolean processDrawThreadState() {
            //处理没有运行 或者 Holder 为 null 的情况
            while (mSurface == null || !mRunning) {
                if (mActive) {
                    mActive = false;
                    notify();   //唤醒
                }
                if (mQuit) {
                    return false;
                }
                try {
                    wait();     //等待
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //其他情况肯定是活动状态
            if (!mActive) {
                mActive = true;
                notify();       //唤醒
            }
            return true;
        }

        /**
         * 处理画布与绘制过程 要注意一定要保证是同步锁中才能执行 否则会出现
         *
         * @param mCanvas 画布
         */
        private void processDrawCanvas(Canvas mCanvas) {
            try {
                //加锁画布
                mCanvas = mSurface.lockCanvas();
                //防空保护
                if (mCanvas != null) {
                    //清屏操作
                    mCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                    //真正开始画 SurfaceView 的地方
                    drawSurface(mCanvas);
                    now = System.currentTimeMillis();
                    mCanvas.drawText(framesCountAvg+" fps", 40, 70, paint);
                    framesCount++;
                    if (now - framesTimer > 1000) {
                        framesTimer = now;
                        framesCountAvg = framesCount;
                        framesCount = 0;
                    }
                }
            } catch (Exception ignored) {

            } finally {
                if (mCanvas != null) {
                    //释放canvas锁，并显示视图
                    mSurface.unlockCanvasAndPost(mCanvas);
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
                return;
            }

            //如果前一次绘制对象不为空 且 当前绘制者有透明效果的话 绘制前一次的对象即可
            if (mPreDrawer != null && curDrawerAlpha < 1f) {
                mPreDrawer.setViewSize(mWidth, mHeight);
                mPreDrawer.drawBgAndBubble(canvas, 1f - curDrawerAlpha);
            }

            //直到当前绘制完全不透明时将上一次绘制的置空
            if (curDrawerAlpha < 1f) {
                curDrawerAlpha += 0.6f;
                if (curDrawerAlpha > 1f) {
                    curDrawerAlpha = 1f;
                    mPreDrawer = null;
                }
            }

            //如果当前有绘制对象 直接绘制即可 先设置绘制宽高再绘制气泡
            if (mCurDrawer != null) {
                mCurDrawer.setViewSize(mWidth, mHeight);
                mCurDrawer.drawBgAndBubble(canvas, curDrawerAlpha);
            }
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
                    Thread.sleep(needSleepTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**========== Surface 回调方法 需要加同步锁 防止阻塞 START==========*/

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        synchronized (mDrawThread) {
            mDrawThread.mSurface = holder;
            //唤醒
            mDrawThread.notify();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        synchronized (mDrawThread) {
            mDrawThread.mSurface = holder;
            //唤醒
            mDrawThread.notify();
            while (mDrawThread.mActive) {
                try {
                    //等待
                    mDrawThread.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        holder.removeCallback(this);
    }
    /**========== Surface 回调方法 需要加同步锁 防止阻塞 END==========*/

    /**========== 处理与 Activity 生命周期相关方法 需要加同步锁 防止阻塞 START==========*/
    public void onDrawResume() {
        synchronized (mDrawThread) {
            //运行状态
            mDrawThread.mRunning = true;
            //唤醒线程
            mDrawThread.notify();
        }
    }

    public void onDrawPause() {
        synchronized (mDrawThread) {
            //不运行状态
            mDrawThread.mRunning = false;
            //唤醒线程
            mDrawThread.notify();
        }
    }

    public void onDrawDestroy() {
        synchronized (mDrawThread) {
            //退出状态
            mDrawThread.mQuit = true;
            //唤醒线程
            mDrawThread.notify();
        }
    }
    /*========== 处理与 Activity 生命周期相关方法 需要加同步锁 防止阻塞 END==========*/
}