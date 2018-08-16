package com.android.sichard.lockscreen.view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br>类描述：气泡动画view
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/2/22</b>
 */
@SuppressWarnings("ALL")
public class BubbleAnimationView extends View implements ValueAnimator.AnimatorUpdateListener, Animator.AnimatorListener {

    /**
     * 动画改变的属性值
     */
    private float phase1 = 0f;
    private float phase2 = 0f;
    private float phase3 = 0f;
    private float phase4 = 0f;

    /**
     * 气泡集合
     */
    private Bubble bubble1 = new Bubble();
    private Bubble bubble2 = new Bubble();
    private Bubble bubble3 = new Bubble();
    private Bubble bubble4 = new Bubble();
    /**
     * 宽度
     */
    private int width = 0;
    /**
     * 高度
     */
    private int height = 0;

    /**
     * 曲线高度个数分割
     */
    private int quadCount = 5;
    /**
     * 曲度
     */
    private float intensity = 0.2f;

    /**
     * 动画播放的时间
     */
    private int time = 6000;
    /**
     * 动画间隔
     */
    private final int delay = 2500;

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 测量路径的坐标位置
     */
    private PathMeasure pathMeasure = null;

    /**
     * 曲线摇摆的幅度
     */
    private int range = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics());
    /**
     * 高度往上偏移量,把开始点移出屏幕顶部
     */
    private float dy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
    private ObjectAnimator animator1;
    private ObjectAnimator animator2;
    private ObjectAnimator animator3;
    private ObjectAnimator animator4;

    public BubbleAnimationView(Context context) {
        super(context);
        init(context);
    }

    public BubbleAnimationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(Color.WHITE);
        mPaint.setAlpha(76);
        mPaint.setStyle(Paint.Style.STROKE);

        pathMeasure = new PathMeasure();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        height = bottom - top;
        width = right - left;
        if (height > 0 && width > 0) {
            builderFollower(bubble1);
            builderFollower(bubble2);
            builderFollower(bubble3);
            builderFollower(bubble4);
        }
    }

    /**
     * 创建气泡
     */
    private void builderFollower(Bubble bubble) {
        Path path = new Path();
        CPoint CPoint = new CPoint(width / 2, height + dy);
        List<CPoint> points = builderPath(CPoint);
        drawBubblePath(path, points);
        bubble.setPath(path);
        int max = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        int min = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics());
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        bubble.setRadius(s);
    }

    /**
     * 画曲线
     *
     * @param path
     * @param points
     */
    private void drawBubblePath(Path path, List<CPoint> points) {
        if (points.size() > 1) {
            for (int j = 0; j < points.size(); j++) {

                CPoint point = points.get(j);

                if (j == 0) {
                    CPoint next = points.get(j + 1);
                    point.dx = ((next.x - point.x) * intensity);
                    point.dy = ((next.y - point.y) * intensity);
                } else if (j == points.size() - 1) {
                    CPoint prev = points.get(j - 1);
                    point.dx = ((point.x - prev.x) * intensity);
                    point.dy = ((point.y - prev.y) * intensity);
                } else {
                    CPoint next = points.get(j + 1);
                    CPoint prev = points.get(j - 1);
                    point.dx = ((next.x - prev.x) * intensity);
                    point.dy = ((next.y - prev.y) * intensity);
                }

                // create the cubic-spline path
                if (j == 0) {
                    path.moveTo(point.x, point.y);
                } else {
                    CPoint prev = points.get(j - 1);
                    path.cubicTo(prev.x + prev.dx, (prev.y + prev.dy),
                            point.x - point.dx, (point.y - point.dy),
                            point.x, point.y);
                }
            }
        }
    }

    /**
     * 画路径
     *
     * @param point
     * @return
     */
    private List<CPoint> builderPath(CPoint point) {
        List<CPoint> points = new ArrayList<CPoint>();
        Random random = new Random();
        for (int i = 0; i < quadCount + 1; i++) {
            if (i == 0) {
                points.add(point);
            } else {
                CPoint tmp = new CPoint(0, 0);
                if (random.nextInt(100) % 2 == 0) {
                    tmp.x = point.x + random.nextInt(range);
                } else {
                    tmp.x = point.x - random.nextInt(range);
                }
                tmp.y = (int) (height - height / (float) quadCount * i) - dy;
                points.add(tmp);
            }
        }
        return points;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawBubble(canvas, bubble1);
        drawBubble(canvas, bubble2);
        drawBubble(canvas, bubble3);
        drawBubble(canvas, bubble4);
    }

    /**
     * @param canvas
     * @param bubble
     */
    private void drawBubble(Canvas canvas, Bubble bubble) {
        float[] pos = new float[2];
//		canvas.drawPath(bubble.getPath(), mPaint); //绘制路径
        pathMeasure.setPath(bubble.getPath(), false);
        pathMeasure.getPosTan(pathMeasure.getLength() * bubble.getValue(), pos, null);
        canvas.clipRect(getLeft(), 30, getRight(), getBottom());
        canvas.drawCircle(pos[0], pos[1], bubble.radius, mPaint);
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
//			canvas.drawBitmap(bitmap, pos[0], pos[1] - dy, null);
//			bitmap.recycle();
    }

    public void startAnimation() {
        animator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
        animator1.setDuration(time);
        animator1.addUpdateListener(this);
        animator1.addListener(this);
        animator1.setRepeatCount(ValueAnimator.INFINITE);
        animator1.setRepeatMode(ValueAnimator.RESTART);
//        animator1.setInterpolator(new AccelerateInterpolator(1));
        animator1.start();

        animator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
        animator2.setDuration(time);
        animator2.addUpdateListener(this);
        animator2.addListener(this);
        animator2.setRepeatCount(ValueAnimator.INFINITE);
        animator2.setRepeatMode(ValueAnimator.RESTART);
        animator2.setStartDelay(delay);
//        animator2.setInterpolator(new AccelerateInterpolator());
        animator2.start();

        animator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
        animator3.setDuration(time);
        animator3.addUpdateListener(this);
        animator3.addListener(this);
        animator3.setRepeatCount(ValueAnimator.INFINITE);
        animator3.setRepeatMode(ValueAnimator.RESTART);
        animator3.setStartDelay(2 * delay);
//        animator3.setInterpolator(new DecelerateInterpolator(1));
        animator3.start();

        animator4 = ObjectAnimator.ofFloat(this, "phase4", 0f, 1f);
        animator4.setDuration(time);
        animator4.addUpdateListener(this);
        animator4.addListener(this);
        animator4.setRepeatCount(ValueAnimator.INFINITE);
        animator4.setRepeatMode(ValueAnimator.RESTART);
        animator4.setStartDelay(3 * delay); //减去10ms是为了防止气泡最后会跳动
//        animator4.setInterpolator(new AnticipateOvershootInterpolator(1));
        animator4.start();
    }

    public void cleanAnimation() {
        if (animator1 != null) {
            animator1.cancel();
        }
        if (animator2 != null) {
            animator2.cancel();
        }
        if (animator3 != null) {
            animator3.cancel();
        }
        if (animator4 != null) {
            animator4.cancel();
        }
    }

    /**
     * 跟新小球的位置
     *
     * @param value
     * @param bubble
     */
    private void updateValue(float value, Bubble bubble) {
        bubble.setValue(value);
    }

    /**
     * 动画改变回调
     */
    @Override
    public void onAnimationUpdate(ValueAnimator animator) {
        final int startDelay = (int) animator.getStartDelay();
        switch (startDelay) {
            case 0:
                updateValue(phase1, bubble1);
                break;
            case delay:
                updateValue(phase2, bubble2);
                break;
            case delay * 2:
                updateValue(phase3, bubble3);
                break;
            case delay * 3:
                updateValue(phase4, bubble4);
                break;
        }
        invalidate();
    }

    /**
     * 属性动画必须实现的方法
     * @param phase1
     */
    public void setPhase1(float phase1) {
        this.phase1 = phase1;
    }

    /**
     * 属性动画必须实现的方法
     * @param phase2
     */
    public void setPhase2(float phase2) {
        this.phase2 = phase2;
    }

    /**
     * 属性动画必须实现的方法
     * @param phase3
     */
    public void setPhase3(float phase3) {
        this.phase3 = phase3;
    }

    /**
     * 属性动画必须实现的方法
     * @param phase4
     */
    public void setPhase4(float phase4) {
        this.phase4 = phase4;
    }



    private String tag = this.getClass().getSimpleName();

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {
        final int startDelay = (int) animation.getStartDelay();
        switch (startDelay) {
            case 0:
                builderFollower(bubble1);
                break;
            case delay:
                builderFollower(bubble2);
                break;
            case delay * 2:
                builderFollower(bubble3);
                break;
            case delay * 3:
                builderFollower(bubble4);
                break;
        }
    }

    private class CPoint {

        public float x = 0f;
        public float y = 0f;

        /**
         * x-axis distance
         */
        public float dx = 0f;

        /**
         * y-axis distance
         */
        public float dy = 0f;

        public CPoint(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private class Bubble implements Serializable {
        private int radius;
        private Path path;
        private float value;

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public float getValue() {
            return value;
        }

        public void setValue(float value) {
            this.value = value;
        }

        public void setRadius(int radius) {
            this.radius = radius;
        }

        public int getRadius() {
            return radius;
        }

        @Override
        public String toString() {
            return "Bubble [path=" + path + ", value=" + value + "]";
        }
    }

}
