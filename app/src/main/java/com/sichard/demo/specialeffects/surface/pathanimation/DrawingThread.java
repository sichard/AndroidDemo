package com.sichard.demo.specialeffects.surface.pathanimation;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.SurfaceHolder;
import android.view.animation.AnimationUtils;

/**
 * <br>类描述：绘制线程
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-24</b>
 */
public class DrawingThread extends Thread {
    /** 三种状态 */
    boolean mRunning, mQuit = false;
    private SurfaceHolder mSurfaceHolder;
    private PathParam mPathParam;
    /** 每帧的时间 */
    private static final int FRAME_TIME = 20;

    @Override
    public void run() {
        while (true) {
            Canvas canvas = null;
            synchronized (this) {
                try {
                    if (mQuit) {
                        return;
                    }
                    canvas = mSurfaceHolder.lockCanvas();
                    if (canvas != null) {
                        //动画开始时间
                        final long startTime = AnimationUtils.currentAnimationTimeMillis();
                        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                        //处理画布并进行绘制
                        mPathParam.draw(canvas);
                        //绘制时间
                        final long drawTime = AnimationUtils.currentAnimationTimeMillis() - startTime;
                        //处理一下线程需要的睡眠时间
                        processDrawThreadSleep(drawTime);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (canvas != null) {
                        mSurfaceHolder.unlockCanvasAndPost(canvas);
                    }
                }
            }
        }
    }

//    /**
//     * 处理绘制线程的状态问题
//     *
//     * @return true：不结束继续绘制 false：结束且不绘制
//     */
//    private boolean processDrawThreadState() {
//        //处理没有运行 或者 Holder 为 null 的情况
//        while (mSurfaceHolder == null || !mRunning) {
//            if (mQuit) {
//                return false;
//            }
//            try {
//                Log.i("sichardcao", "DrawingThread|processDrawThreadState:" + "===wait");
//                wait();     //等待
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        return true;
//    }

    /**
     * 处理线程需要的睡眠时间
     * View通过刷新来重绘视图，在一些需要频繁刷新或执行大量逻辑操作时，超过16ms就会导致明显卡顿
     *
     * @param drawTime 绘制时间
     */
    private void processDrawThreadSleep(long drawTime) {
        //需要睡眠时间
        final long needSleepTime = FRAME_TIME - drawTime;

        if (needSleepTime > 0) {
            try {
                Thread.sleep(needSleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void setSurfaceHolder(SurfaceHolder surfaceHolder) {
        mSurfaceHolder = surfaceHolder;
    }

    public void setPathParam(PathParam pathParam) {
        mPathParam = pathParam;
    }

}
