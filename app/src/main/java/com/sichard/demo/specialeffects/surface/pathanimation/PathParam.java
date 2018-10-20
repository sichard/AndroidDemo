package com.sichard.demo.specialeffects.surface.pathanimation;

import android.animation.TimeInterpolator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

import com.sichard.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述：路径动画封装类
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-24</b>
 */
public class PathParam {
    /** 曲度 */
    private static final float CURVATURE = 0.2f;
    private static final int COUNT = 4;
    /** 动画的路径 */
    private Path mPath;
    private Path mRevertPath;
    private Bitmap mBitmap, mBitmapExit;
    private Point mStartPoint, mEndPoint;
    private int mStartSize, mEndSize;
    private long mStartTime;
    private int mDuration;
    private TimeInterpolator mInterpolator;
    private boolean mIsOpen;
    private PathMeasure mPathMeasure = new PathMeasure();
    private float[] mCoordinate = new float[2];
    private Rect mDstRect = new Rect();
    private AnimationEndListener mAnimationEndListener;
    private DrawingThread mDrawThread;
    private int mFrame;

    public PathParam(Context context) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        mBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_spirit, options);
        mBitmapExit = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_spirit_origin, options);
    }

    /**
     * 设置路径的动画的起点和终点
     *
     * @param origin      起点
     * @param destination 终点
     */
    public void setOriginDestination(Point origin, Point destination) {
        if (mStartPoint == null || mEndPoint == null) {
            mStartPoint = origin;
            mEndPoint = destination;
        } else {
            if (mStartPoint.equals(origin.x, origin.y) && mEndPoint.equals(destination.x, destination.y)) {
                return;
            }
        }

        float offsetX = (destination.x - origin.x) / (COUNT - 1f) / 2;
        float offsetY = (destination.y - origin.y) / (COUNT - 1f) / 2;
        createPathPoints(COUNT, origin, destination, offsetX, offsetY);
    }

    private void createPathPoints(int count, Point origin, Point destination, float offsetX, float offsetY) {
        int width = destination.x - origin.x;
        int height = destination.y - origin.y;


        List<CPoint> mPointList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            CPoint point = new CPoint();
            if (i == 0) {
                point.x = origin.x;
                point.y = origin.y;
            } else if (i < count - 1) {
                point.x = origin.x + (float) width / (count - 1) * i - offsetX;
                point.y = origin.y + (float) height / (count - 1) * i + offsetY;
            } else {
                point.x = destination.x;
                point.y = destination.y;
            }
            mPointList.add(point);
        }
        createPath(mPointList);
    }

    /**
     * 画曲线
     *
     * @param points 路径取样点
     */
    private void createPath(List<CPoint> points) {
        mPath = new Path();
        if (points.size() > 1) {
            for (int j = 0; j < points.size(); j++) {

                CPoint point = points.get(j);

                if (j == 0) {
                    CPoint next = points.get(j + 1);
                    point.dx = ((next.x - point.x) * CURVATURE);
                    point.dy = ((next.y - point.y) * CURVATURE);
                } else if (j == points.size() - 1) {
                    CPoint prev = points.get(j - 1);
                    point.dx = ((point.x - prev.x) * CURVATURE);
                    point.dy = ((point.y - prev.y) * CURVATURE);
                } else {
                    CPoint next = points.get(j + 1);
                    CPoint prev = points.get(j - 1);
                    point.dx = ((next.x - prev.x) * CURVATURE);
                    point.dy = ((next.y - prev.y) * CURVATURE);
                }

                // 创建cubic-spline路径
                if (j == 0) {
                    mPath.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j - 1);
                    mPath.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                            point.x - point.dx, (point.y - point.dy),
                            point.x, point.y);
                }
            }
        }

        mRevertPath = new Path();
        if (points.size() > 1) {
            for (int j = points.size() - 1; j >= 0; j--) {

                CPoint point = points.get(j);

                if (j == points.size() - 1) {
                    CPoint next = points.get(j - 1);
                    point.dx = ((next.x - point.x) * CURVATURE);
                    point.dy = ((next.y - point.y) * CURVATURE);
                } else if (j == 0) {
                    CPoint prev = points.get(j + 1);
                    point.dx = ((point.x - prev.x) * CURVATURE);
                    point.dy = ((point.y - prev.y) * CURVATURE);
                } else {
                    CPoint next = points.get(j - 1);
                    CPoint prev = points.get(j + 1);
                    point.dx = ((next.x - prev.x) * CURVATURE);
                    point.dy = ((next.y - prev.y) * CURVATURE);
                }

                // 创建cubic-spline路径
                if (j == points.size() - 1) {
                    mRevertPath.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j + 1);
                    mRevertPath.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                            point.x - point.dx, (point.y - point.dy),
                            point.x, point.y);
                }
            }
        }
    }

    public void setStartSize(int startSize) {
        mStartSize = startSize;
    }

    public void setEndSize(int endSize) {
        mEndSize = endSize;
    }

    public void draw(Canvas canvas) {
        long now = System.currentTimeMillis();
        float fraction = ((float) (now - mStartTime)) / mDuration;
        if (fraction >= 1) {
            fraction = 1;
        }
        if (mInterpolator != null) {
            fraction = mInterpolator.getInterpolation(fraction);
        }
        Log.i("sichardcao", "PathParam|draw:" + fraction);
        if (mIsOpen) {
            drawPathAnim(canvas, fraction);
        } else {
            drawRevertPathAnim(canvas, fraction);
        }
        mFrame++;
        if (fraction == 1) {
            Log.e("sichardcao", "PathParam|draw: " + mFrame);
            mFrame = 0;
            mDrawThread.mQuit = true;
            if (mAnimationEndListener != null) {
                mAnimationEndListener.onAnimationEnd();
            }
        }
    }

    private void drawPathAnim(Canvas canvas, float fraction) {
        mPathMeasure.setPath(mPath, false);
        mPathMeasure.getPosTan(mPathMeasure.getLength() * fraction, mCoordinate, null);
        int size = (int) (mStartSize + fraction * (mEndSize - mStartSize));
        mDstRect.left = (int) (mCoordinate[0] - size / 2);
        mDstRect.top = (int) (mCoordinate[1] - size / 2);
        mDstRect.right = (int) (mCoordinate[0] + size / 2);
        mDstRect.bottom = (int) (mCoordinate[1] + size / 2);
        canvas.drawBitmap(mBitmap, null, mDstRect, null);
        //绘制路径
//        canvas.drawPath(mPath, mPaint);
    }

    private void drawRevertPathAnim(Canvas canvas, float fraction) {
        mPathMeasure.setPath(mRevertPath, false);
        mPathMeasure.getPosTan(mPathMeasure.getLength() * fraction, mCoordinate, null);
        int size = (int) (mEndSize + fraction * (mStartSize - mEndSize));
        mDstRect.left = (int) (mCoordinate[0] - size / 2);
        mDstRect.top = (int) (mCoordinate[1] - size / 2);
        mDstRect.right = (int) (mCoordinate[0] + size / 2);
        mDstRect.bottom = (int) (mCoordinate[1] + size / 2);
        if (fraction > 0.7f) {
            canvas.drawBitmap(mBitmapExit, null, mDstRect, null);
        } else {
            canvas.drawBitmap(mBitmap, null, mDstRect, null);
        }
    }

    public void setStartTime(long startTime) {
        mStartTime = startTime;
    }

    public void setDuration(int duration) {
        mDuration = duration;
    }


    public void setInterpolator(TimeInterpolator interpolator) {
        mInterpolator = interpolator;
    }

    public void setIsOpenAI(boolean isOpen) {
        mIsOpen = isOpen;
    }

    public boolean isOpen() {
        return mIsOpen;
    }

    public void attach(DrawingThread thread) {
        mDrawThread = thread;
    }

    private class CPoint {

        float x = 0f;
        float y = 0f;

        /**
         * x-axis distance
         */
        float dx = 0f;

        /**
         * y-axis distance
         */
        float dy = 0f;

        CPoint() {
        }
    }

    public interface AnimationEndListener {
        /**
         * 动画结束回调
         */
        void onAnimationEnd();
    }

    public void setAnimationEndListener(AnimationEndListener listener) {
        this.mAnimationEndListener = listener;
    }
}
