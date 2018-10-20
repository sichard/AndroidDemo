package com.sichard.demo.specialeffects.surface.pathanimation;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * <br>类描述：基于SurfaceView的路径动画
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-24</b>
 */
public class PathAnimationSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    private PathParam mPathParam;
    private DrawingThread mThread;

    public PathAnimationSurfaceView(Context context) {
        this(context, null);
    }

    public PathAnimationSurfaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //透明背景
        getHolder().setFormat(PixelFormat.TRANSPARENT);
        getHolder().addCallback(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("sichardcao", "PathAnimationSurfaceView|surfaceCreated:" + "===created");

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        Log.i("sichardcao", "PathAnimationSurfaceView|surfaceChanged:" + "===changed");
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        holder.removeCallback(this);
    }

    public void setPathParam(PathParam pathParam) {
        Log.i("sichardcao", "PathAnimationSurfaceView|setPathParam:" + "====setParam");
        mPathParam = pathParam;
    }

    public void startAnimation() {

        if (mThread != null && mThread.isAlive()) {
            mThread.mQuit = true;
            mThread = null;
        }
        mThread = new DrawingThread();
        mThread.setSurfaceHolder(getHolder());
        mPathParam.setStartTime(System.currentTimeMillis());
        mThread.setPathParam(mPathParam);
        mPathParam.attach(mThread);
        mThread.start();
    }
}
