package com.sichard.demo.animation;

import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;
import com.sichard.demo.animation.view.ValueAnimationView;

/**
 * <br>类描述:自定义属性的动画
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-25</b>
 */
public class CustomValueAnimationActivity extends BaseActivity {

    private ObjectAnimator mAnimator;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_value_animation);
        ValueAnimationView view = findViewById(R.id.custom_value_animation_view);
        mAnimator = ObjectAnimator.ofObject(view, "color", new ColorEvaluator(), Color.RED, Color.BLUE, Color.GREEN);
        mAnimator.setDuration(6000);
        mAnimator.setRepeatCount(Animation.INFINITE);
        mAnimator.setRepeatMode(ValueAnimator.REVERSE);
        mAnimator.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAnimator != null) {
            mAnimator.cancel();
        }
    }

    class ColorEvaluator implements TypeEvaluator {

        private IntEvaluator evaluator = new IntEvaluator();
        @Override
        public Integer evaluate(float fraction, Object start, Object end) {

            int startColor = (int) start;
            int endColor = (int) end;
            // 截取颜色分为RGB三个部分，并将RGB的值转换成十六进制数字
            // 那么每个颜色的取值范围就是0-255
            int startRed = (startColor & 0xFF0000) >> 16;
            int startGreen = (startColor & 0x00FF00) >> 8;
            int startBlue = (startColor & 0x0000FF);

            int endRed = (endColor & 0xFF0000) >> 16;
            int endGreen = (endColor & 0x00FF00) >> 8;
            int endBlue = (endColor & 0x0000FF);

            int mCurrentRed = evaluator.evaluate(fraction, startRed, endRed) << 16;
            int mCurrentGreen = evaluator.evaluate(fraction, startGreen, endGreen) << 8;
            int mCurrentBlue = evaluator.evaluate(fraction, startBlue, endBlue);
            // 注意这里要加上alpha的不透明值,否则将显示为完全透明
            int mCurrentColor = 0xFF000000 + mCurrentRed + mCurrentGreen + mCurrentBlue;
            return mCurrentColor;
        }
    }
}
