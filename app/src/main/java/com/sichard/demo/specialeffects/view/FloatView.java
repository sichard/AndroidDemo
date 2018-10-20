package com.sichard.demo.specialeffects.view;

import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sichard.demo.R;


/**
 * <br>类描述：浮动View
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-7</b>
 */
public class FloatView extends RelativeLayout {

    private TextView mSkill1, mSkill2, mSkill3, mSkill4, mSkill5;
    private int mWidth, mHeight;
    private int mStartX, mStartY;

    public FloatView(Context context) {
        super(context);
    }

    public FloatView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FloatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (mWidth == 0) {
            mWidth = r - l;
            mHeight = b - t;
            mStartX = mWidth / 2;
            mStartY = mHeight / 2;
            Log.i("sichardcao", "FloatView|onLayout:" + mStartX + "|" + mStartY);
            doAnimation();
        }

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSkill1 = findViewById(R.id.float_text1);
        mSkill2 = findViewById(R.id.float_text2);
        mSkill3 = findViewById(R.id.float_text3);
        mSkill4 = findViewById(R.id.float_text4);
        mSkill5 = findViewById(R.id.float_text5);

    }

    private void doAnimation() {
        AnimationSet set1 = new AnimationSet(true);
        set1.setDuration(500);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        ScaleAnimation scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_SELF, 0f);
//        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0f, Animation.RELATIVE_TO_PARENT, 0.3f, Animation.RELATIVE_TO_PARENT, 0f);
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, mStartX, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, -200, Animation.RELATIVE_TO_SELF, 0f);
        set1.addAnimation(alphaAnimation);
        set1.addAnimation(scaleAnimation);
        set1.addAnimation(translateAnimation);
        mSkill1.startAnimation(set1);

        AnimationSet set2 = new AnimationSet(true);
        set2.setDuration(500);
        alphaAnimation = new AlphaAnimation(0, 0.8f);
        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        set2.addAnimation(alphaAnimation);
        set2.addAnimation(scaleAnimation);
        set2.addAnimation(translateAnimation);
        set2.setFillAfter(true);
        set2.setStartOffset(100);
        mSkill2.startAnimation(set2);

        AnimationSet set3 = new AnimationSet(true);
        set3.setDuration(500);
        alphaAnimation = new AlphaAnimation(0, 0.6f);
        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        set3.addAnimation(alphaAnimation);
        set3.addAnimation(scaleAnimation);
        set3.addAnimation(translateAnimation);
        set3.setFillAfter(true);
        mSkill3.startAnimation(set3);

        AnimationSet set4 = new AnimationSet(true);
        set4.setDuration(500);
        alphaAnimation = new AlphaAnimation(0, 0.4f);
        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        set4.addAnimation(alphaAnimation);
        set4.addAnimation(scaleAnimation);
        set4.addAnimation(translateAnimation);
        set4.setFillAfter(true);
        mSkill4.startAnimation(set4);

        AnimationSet set5 = new AnimationSet(true);
        set5.setDuration(500);
        alphaAnimation = new AlphaAnimation(0, 0.2f);
        scaleAnimation = new ScaleAnimation(0, 1, 0, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        set5.addAnimation(alphaAnimation);
        set5.addAnimation(scaleAnimation);
        set5.addAnimation(translateAnimation);
        set5.setFillAfter(true);
        mSkill5.startAnimation(set5);

        bubbleFloat(mSkill1, 4000, 10, -1).start();
        bubbleFloat2(mSkill2, 4200, 5, -1).start();
        bubbleFloat3(mSkill3, 4500, 4, -1).start();
        bubbleFloat4(mSkill4, 4000, 6, -1).start();
        bubbleFloat5(mSkill5, 4800, 2, -1).start();
    }

    /**
     * 气泡漂浮动画
     *
     * @param view        动画view
     * @param duration    动画运行时间
     * @param offset      动画运行幅度
     * @param repeatCount 动画运行次数
     * @return
     */
    private ObjectAnimator bubbleFloat(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        Log.i("sichardcao", "FloatAnimationActivity|bubbleFloat:" + path);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset - path),
                Keyframe.ofFloat(2 / 12f, offset / 2),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(5 / 12f, offset + path),
                Keyframe.ofFloat(6 / 12f, offset * 2),
                Keyframe.ofFloat(7 / 12f, offset + path),
                Keyframe.ofFloat(8 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(9 / 12f, offset),
                Keyframe.ofFloat(10 / 12f, offset / 2),
                Keyframe.ofFloat(11 / 12f, offset - path),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofKeyframe(View.ROTATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateY = PropertyValuesHolder.ofKeyframe(View.ROTATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY/*, rotateX, rotateY*/).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    /**
     * 气泡漂浮动画
     *
     * @param view        动画view
     * @param duration    动画运行时间
     * @param offset      动画运行幅度
     * @param repeatCount 动画运行次数
     * @return
     */
    private ObjectAnimator bubbleFloat2(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        Log.i("sichardcao", "FloatAnimationActivity|bubbleFloat:" + path);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, 3),
                Keyframe.ofFloat(2 / 12f, 7),
                Keyframe.ofFloat(3 / 12f, 9),
                Keyframe.ofFloat(4 / 12f, 13),
                Keyframe.ofFloat(5 / 12f, 16),
                Keyframe.ofFloat(6 / 12f, 13),
                Keyframe.ofFloat(7 / 12f, 10),
                Keyframe.ofFloat(8 / 12f, 7),
                Keyframe.ofFloat(9 / 12f, 4),
                Keyframe.ofFloat(10 / 12f, 2),
                Keyframe.ofFloat(11 / 12f, 1),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofKeyframe(View.ROTATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateY = PropertyValuesHolder.ofKeyframe(View.ROTATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY/*, rotateX, rotateY*/).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    /**
     * 气泡漂浮动画
     *
     * @param view        动画view
     * @param duration    动画运行时间
     * @param offset      动画运行幅度
     * @param repeatCount 动画运行次数
     * @return
     */
    private ObjectAnimator bubbleFloat3(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        Log.i("sichardcao", "FloatAnimationActivity|bubbleFloat:" + path);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset - path),
                Keyframe.ofFloat(2 / 12f, offset / 2),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(5 / 12f, offset + path),
                Keyframe.ofFloat(6 / 12f, offset * 2),
                Keyframe.ofFloat(7 / 12f, offset + path),
                Keyframe.ofFloat(8 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(9 / 12f, offset),
                Keyframe.ofFloat(10 / 12f, offset / 2),
                Keyframe.ofFloat(11 / 12f, offset - path),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofKeyframe(View.ROTATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateY = PropertyValuesHolder.ofKeyframe(View.ROTATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY/*, rotateX, rotateY*/).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    /**
     * 气泡漂浮动画
     *
     * @param view        动画view
     * @param duration    动画运行时间
     * @param offset      动画运行幅度
     * @param repeatCount 动画运行次数
     * @return
     */
    private ObjectAnimator bubbleFloat4(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        Log.i("sichardcao", "FloatAnimationActivity|bubbleFloat:" + path);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset - path),
                Keyframe.ofFloat(2 / 12f, offset / 2),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(5 / 12f, offset + path),
                Keyframe.ofFloat(6 / 12f, offset * 2),
                Keyframe.ofFloat(7 / 12f, offset + path),
                Keyframe.ofFloat(8 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(9 / 12f, offset),
                Keyframe.ofFloat(10 / 12f, offset / 2),
                Keyframe.ofFloat(11 / 12f, offset - path),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofKeyframe(View.ROTATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateY = PropertyValuesHolder.ofKeyframe(View.ROTATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY/*, rotateX, rotateY*/).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }

    /**
     * 气泡漂浮动画
     *
     * @param view        动画view
     * @param duration    动画运行时间
     * @param offset      动画运行幅度
     * @param repeatCount 动画运行次数
     * @return
     */
    private ObjectAnimator bubbleFloat5(View view, int duration, int offset, int repeatCount) {
        float path = (float) (Math.sqrt(3) / 2 * offset);
        Log.i("sichardcao", "FloatAnimationActivity|bubbleFloat:" + path);
        PropertyValuesHolder translateX = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder translateY = PropertyValuesHolder.ofKeyframe(View.TRANSLATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset - path),
                Keyframe.ofFloat(2 / 12f, offset / 2),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(5 / 12f, offset + path),
                Keyframe.ofFloat(6 / 12f, offset * 2),
                Keyframe.ofFloat(7 / 12f, offset + path),
                Keyframe.ofFloat(8 / 12f, offset * 3 / 2),
                Keyframe.ofFloat(9 / 12f, offset),
                Keyframe.ofFloat(10 / 12f, offset / 2),
                Keyframe.ofFloat(11 / 12f, offset - path),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateX = PropertyValuesHolder.ofKeyframe(View.ROTATION_X,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        PropertyValuesHolder rotateY = PropertyValuesHolder.ofKeyframe(View.ROTATION_Y,
                Keyframe.ofFloat(0f, 0),
                Keyframe.ofFloat(1 / 12f, offset / 2),
                Keyframe.ofFloat(2 / 12f, path),
                Keyframe.ofFloat(3 / 12f, offset),
                Keyframe.ofFloat(4 / 12f, path),
                Keyframe.ofFloat(5 / 12f, offset / 2),
                Keyframe.ofFloat(6 / 12f, 0),
                Keyframe.ofFloat(7 / 12f, -offset / 2),
                Keyframe.ofFloat(8 / 12f, -path),
                Keyframe.ofFloat(9 / 12f, -offset),
                Keyframe.ofFloat(10 / 12f, -path),
                Keyframe.ofFloat(11 / 12f, -offset / 2),
                Keyframe.ofFloat(1f, 0)
        );

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(view, translateX, translateY/*, rotateX, rotateY*/).
                setDuration(duration);
        animator.setRepeatCount(repeatCount);
        animator.setInterpolator(new LinearInterpolator());
        return animator;
    }
}
