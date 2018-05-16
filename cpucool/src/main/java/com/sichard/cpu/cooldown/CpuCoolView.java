package com.sichard.cpu.cooldown;

import android.content.Context;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * <br>类描述:CPU降温的view
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-1-26</b>
 */

public class CpuCoolView extends FrameLayout {
    private SnowAnimationView mSnowAnimationView;
    private ImageView mPenguin;
    private ImageView mPenguinShadow;

    public CpuCoolView(@NonNull Context context) {
        super(context);
    }

    public CpuCoolView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CpuCoolView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mSnowAnimationView = findViewById(R.id.cpu_cool_snow);
        mSnowAnimationView.startAnimation();
        mPenguin = findViewById(R.id.cpu_cool_penguin);
        mPenguinShadow = findViewById(R.id.cpu_cool_penguin_shadow);

        RotateAnimation animation = new RotateAnimation(-30, 30, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1);
        animation.setDuration(2000);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);

        int range = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());
        TranslateAnimation translateAnimation = new TranslateAnimation(-range, range, 0, 0);
        translateAnimation.setDuration(2000);
        translateAnimation.setRepeatMode(Animation.REVERSE);
        translateAnimation.setRepeatCount(Animation.INFINITE);

        mPenguin.startAnimation(animation);
        mPenguinShadow.startAnimation(translateAnimation);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }

    public void onDestroy() {
        if (mSnowAnimationView != null) {
            mSnowAnimationView.cleanAnimation();
            mSnowAnimationView = null;
        }
    }
}
