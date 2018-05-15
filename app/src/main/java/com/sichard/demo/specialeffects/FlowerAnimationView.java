package com.sichard.demo.specialeffects;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <br>类描述：路径动画的View
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-27</b>
 * <br>用到的知识点：
 * 1、android属性动画
 * 2、Path路径绘制
 * 3、贝塞尔曲线
 */
public class FlowerAnimationView extends View implements ValueAnimator.AnimatorUpdateListener {

    /**
     * 动画改变的属性值
     */
    private float phase1 = 0f;
    private float phase2 = 0f;
    private float phase3 = 0f;

    /**
     * 小球集合
     */
    private List<Flower> flowers1 = new ArrayList<>();
    private List<Flower> flowers2 = new ArrayList<>();
    private List<Flower> flowers3 = new ArrayList<>();

    /**
     * 动画播放的时间
     */
    private int time = 4000;
    /**
     * 动画间隔
     */
    private int delay = 500;

    /**
     * 资源ID
     */
//	private int resId = R.drawable.flower_love;
    public FlowerAnimationView(Context context) {
        super(context);
        init(context);
//		this.resId = resId;
    }

    public FlowerAnimationView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @SuppressWarnings("deprecation")
    private void init(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = (int) (wm.getDefaultDisplay().getHeight() * 3 / 2f);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
        mPaint.setColor(Color.BLUE);
        mPaint.setStyle(Style.STROKE);

        pathMeasure = new PathMeasure();

        builderFlower(flowerCount, flowers1);
        builderFlower(flowerCount, flowers2);
        builderFlower(flowerCount, flowers3);

    }

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
    private int quadCount = 10;
    /**
     * 曲度
     */
    private float intensity = 0.2f;

    /**
     * 第一批个数
     */
    private int flowerCount = 4;

    /**
     * 创建花
     */
    private void builderFlower(int count, List<Flower> flowers) {

        int max = (int) (width * 3 / 4f);
        int min = (int) (width / 4f);
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            int s = random.nextInt(max) % (max - min + 1) + min;
            Path path = new Path();
            CPoint CPoint = new CPoint(s, 0);
            List<CPoint> points = builderPath(CPoint);
            drawFlowerPath(path, points);
            Flower flower = new Flower();
            flower.setPath(path);
            flowers.add(flower);
        }

    }

    /**
     * 画曲线
     *
     * @param path
     * @param points
     */
    private void drawFlowerPath(Path path, List<CPoint> points) {
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
     * 曲线摇摆的幅度
     */
    private int range = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 70, getResources().getDisplayMetrics());

    /**
     * 画路径
     *
     * @param point
     * @return
     */
    private List<CPoint> builderPath(CPoint point) {
        List<CPoint> points = new ArrayList<CPoint>();
        Random random = new Random();
        for (int i = 0; i < quadCount; i++) {
            if (i == 0) {
                points.add(point);
            } else {
                CPoint tmp = new CPoint(0, 0);
                if (random.nextInt(100) % 2 == 0) {
                    tmp.x = point.x + random.nextInt(range);
                } else {
                    tmp.x = point.x - random.nextInt(range);
                }
                tmp.y = (int) (height / (float) quadCount * i);
                points.add(tmp);
            }
        }
        return points;
    }

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 测量路径的坐标位置
     */
    private PathMeasure pathMeasure = null;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawFlower(canvas, flowers1);
        drawFlower(canvas, flowers2);
        drawFlower(canvas, flowers3);

    }

    /**
     * 高度往上偏移量,把开始点移出屏幕顶部
     */
    private float dy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());

    /**
     * @param canvas
     * @param flowers
     */
    private void drawFlower(Canvas canvas, List<Flower> flowers) {
        for (Flower flower : flowers) {
            float[] pos = new float[2];
            canvas.drawPath(flower.getPath(), mPaint);
            pathMeasure.setPath(flower.getPath(), false);
            pathMeasure.getPosTan(height * flower.getValue(), pos, null);
            canvas.drawCircle(pos[0], pos[1], 10, mPaint);
//			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resId);
//			canvas.drawBitmap(bitmap, pos[0], pos[1] - dy, null);
//			bitmap.recycle();
        }
    }

    public void startAnimation() {
        ObjectAnimator mAnimator1 = ObjectAnimator.ofFloat(this, "phase1", 0f, 1f);
        mAnimator1.setDuration(time);
        mAnimator1.addUpdateListener(this);
        mAnimator1.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator1.start();

        ObjectAnimator mAnimator2 = ObjectAnimator.ofFloat(this, "phase2", 0f, 1f);
        mAnimator2.setDuration(time);
        mAnimator2.addUpdateListener(this);
        mAnimator2.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator2.setStartDelay(delay);
        mAnimator2.start();

        ObjectAnimator mAnimator3 = ObjectAnimator.ofFloat(this, "phase3", 0f, 1f);
        mAnimator3.setDuration(time);
        mAnimator3.addUpdateListener(this);
        mAnimator3.setInterpolator(new AccelerateInterpolator(1f));
        mAnimator3.setStartDelay(delay * 2);
        mAnimator3.start();
    }

    /**
     * 更新小球的位置
     *
     * @param value
     * @param flowers
     */
    private void updateValue(float value, List<Flower> flowers) {
        for (Flower flower : flowers) {
            flower.setValue(value);
        }
    }

    /**
     * 动画改变回调
     */
    @Override
    public void onAnimationUpdate(ValueAnimator arg0) {

        updateValue(getPhase1(), flowers1);
        updateValue(getPhase2(), flowers2);
        updateValue(getPhase3(), flowers3);
        Log.i(tag, getPhase1() + "");
        invalidate();
    }

    public float getPhase1() {
        return phase1;
    }

    public void setPhase1(float phase1) {
        this.phase1 = phase1;
    }

    public float getPhase2() {
        return phase2;
    }

    public void setPhase2(float phase2) {
        this.phase2 = phase2;
    }

    public float getPhase3() {
        return phase3;
    }

    public void setPhase3(float phase3) {
        this.phase3 = phase3;
    }

    private String tag = this.getClass().getSimpleName();

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

}
