package com.sichard.demo.animation;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

import java.util.Locale;

/**
 * 类描述：Animation & Animator
 *
 * @author caosc
 * @date 2018-12-27
 */
public class AnimationAnimatorActivity extends BaseActivity implements View.OnClickListener {

    private View mTextView;
    private Button mButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation_animator);
        mButton = findViewById(R.id.animation_animator_button);
        mButton.setOnClickListener(this);
        mTextView = findViewById(R.id.animation_animator_text);
        mTextView.setOnTouchListener(new View.OnTouchListener() {

            int lastX, lastY;
            Toast toast = Toast.makeText(AnimationAnimatorActivity.this, "", Toast.LENGTH_SHORT);

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();

                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    //Toolbar和状态栏的高度
                    int toolbarHeight = (getWindow().getDecorView().getHeight() - findViewById(R.id.root_view).getHeight());
                    int widthOffset = mTextView.getWidth() / 2;
                    int heightOffset = mTextView.getHeight() / 2;

                    mTextView.setTranslationX(x - mTextView.getLeft() - widthOffset);
                    mTextView.setTranslationY(y - mTextView.getTop() - heightOffset - toolbarHeight / 2);

                    toast.setText(String.format(Locale.CHINA,"left: %d, top: %d, right: %d, bottom: %d",
                            mTextView.getLeft(), mTextView.getTop(), mTextView.getRight(), mTextView.getBottom()));
                    toast.show();
                }
                lastX = x;
                lastY = y;
                return false;
            }
        });

        mTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mButton) {
            mTextView.animate().translationX(150).translationY(300).setDuration(500).start();
//            TranslateAnimation translateAnimation = new TranslateAnimation(0, 150, 0, 300);
//            translateAnimation.setDuration(500);
//            translateAnimation.setFillAfter(true);
//            mTextView.startAnimation(translateAnimation);
        } else {
            Toast.makeText(AnimationAnimatorActivity.this, "onClick", Toast.LENGTH_SHORT).show();
        }
    }
}
