package com.sichard.demo.animation;

import android.animation.IntEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述:属性动画
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-18</b>
 */

public class ValueAnimationActivity extends BaseActivity implements View.OnClickListener {
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_value_animation);
        mTextView = findViewById(R.id.value_test_text);

        findViewById(R.id.value_test_button_scale).setOnClickListener(this);
        findViewById(R.id.value_test_button_object).setOnClickListener(this);
        findViewById(R.id.value_test_button_value).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.value_test_button_scale:
                performScaleAnimation();
                break;
            case R.id.value_test_button_object:
                performObjectAnimator();
                break;
            case R.id.value_test_button_value:
                performAnimate();
                break;
        }
    }

    private void performScaleAnimation() {
        ScaleAnimation animation = new ScaleAnimation(1, 2, 1, 1, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(2500);
        mTextView.startAnimation(animation);
    }

    private void performObjectAnimator() {
        ObjectAnimator objectAnimator = new ObjectAnimator();
        objectAnimator.setFloatValues(1, 2, 1);
        objectAnimator.setTarget(mTextView);
        objectAnimator.setPropertyName("scaleX");
        objectAnimator.setDuration(2500);
        objectAnimator.start();
    }

    private void performAnimate() {
        final int start = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        final int end = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        mTextView.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, getResources().getDisplayMetrics());
        mTextView.requestLayout();

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            //持有一个IntEvaluator对象，方便下面估值的时候使用
            private IntEvaluator mEvaluator = new IntEvaluator();

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                float fraction = (float) animator.getAnimatedValue();

                //直接调用整型估值器通过比例计算出宽度，然后再设给Button
                if (fraction < 0.5f) {
                    mTextView.getLayoutParams().width = mEvaluator.evaluate(fraction * 2, start, end);
                } else {
                    mTextView.getLayoutParams().width = mEvaluator.evaluate((fraction - 0.5f) * 2, end, start);
                }
                mTextView.requestLayout();
            }
        });

        valueAnimator.setDuration(2500).start();
    }

}
