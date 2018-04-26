package com.sichard.demo.animation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;


public class AnimationActivity extends BaseActivity implements OnClickListener {
    private ImageView mImageView;
    private Button mTranslate, mScale, mRotate, mAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);
        initView();
    }

    private void initView() {
        mImageView = findViewById(R.id.imageView);
        mTranslate = findViewById(R.id.translate);
        mTranslate.setOnClickListener(this);
        mScale = findViewById(R.id.scale);
        mScale.setOnClickListener(this);
        mRotate = findViewById(R.id.rotate);
        mRotate.setOnClickListener(this);
        mAll = findViewById(R.id.all);
        mAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        //**     注意以下坐标值，在没有明却指定坐标原点的情况下，都是以ImageView(要做动画的View)的左上角为(0,0)开始结算     ***/

        switch (v.getId()) {
            case R.id.translate:
                // 相对对于原来，起点坐标(300,500)，然后沿x轴平移出去
//				 TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 300f, Animation.RELATIVE_TO_SELF, 1.0f, Animation.ABSOLUTE, 500f, Animation.ABSOLUTE,500f);
                // 相对对于原来，起点坐标(300,500)，然后沿y轴平移出去
//				 TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 300f,Animation.ABSOLUTE, 300f, Animation.ABSOLUTE, 500f, Animation.RELATIVE_TO_SELF,1.0f);
                // 相对对于原来，起点坐标(300,500)，然后沿x,y轴平移回原始位置
                TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 300f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 500f, Animation.RELATIVE_TO_SELF, 0f);
                // 相对对于原来，起点坐标(300,500)，然后沿x,y轴平移出屏幕
//				 TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 300f,Animation.RELATIVE_TO_SELF, 1.0f, Animation.ABSOLUTE, 500f, Animation.RELATIVE_TO_SELF, 1.0f);
                // 不指定平移类型时都是绝对平移，从坐标(20,30)平移到坐标(300,500)
//				TranslateAnimation translateAnimation = new TranslateAnimation(20, 300, 30, 500);
                translateAnimation.setFillAfter(true);
                translateAnimation.setDuration(1500);
                mImageView.startAnimation(translateAnimation);
//				mView.startAnimation(translateAnimation);
                break;
            case R.id.scale:
                // 以(0,0)点为中心点开始缩放,相当于ABSOLUTE
                // ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1.0f, 0.5f, 1.0f);

                // 以(300,500)点为中心点开始缩放
                ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.3f, 1.0f, 0.3f, 300f, 500f);
//				 ScaleAnimation scaleAnimation = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, 300f, 500f);

//				ScaleAnimation scaleAnimation = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

                scaleAnimation.setFillAfter(true);
                scaleAnimation.setDuration(1500);
                mImageView.startAnimation(scaleAnimation);
                break;
            case R.id.rotate:
                // 以(0,0)为轴进行旋转
                // RotateAnimation rotateAnimation = new RotateAnimation(0, 90);
                // 以(300,500)为轴进行旋转
                // RotateAnimation rotateAnimation = new RotateAnimation(0, 90, 300, 500);
                // 以自身的(0.2,0.3)处为轴进行旋转
//				RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.RELATIVE_TO_SELF, 0.2f, Animation.RELATIVE_TO_SELF, 0.3f);

                RotateAnimation rotateAnimation = new RotateAnimation(0, 90, Animation.ABSOLUTE, 300f, Animation.ABSOLUTE, 500f);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setDuration(1500);
                mImageView.startAnimation(rotateAnimation);
                break;
            case R.id.all:
                AnimationSet set = new AnimationSet(true);
                set.setDuration(1500);
                set.setFillAfter(true);
                TranslateAnimation translateAnimation2 = new TranslateAnimation(Animation.ABSOLUTE, 300f, Animation.RELATIVE_TO_SELF, 0f, Animation.ABSOLUTE, 500f, Animation.RELATIVE_TO_SELF, 0f);
                RotateAnimation rotateAnimation2 = new RotateAnimation(0, 90, Animation.ABSOLUTE, 300f, Animation.ABSOLUTE, 500f);
//				TranslateAnimation translateAnimation2 = new TranslateAnimation(0f, 300f, 0f, 500f);
//				ScaleAnimation scaleAnimation2 = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
//				ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 0.5f, 1.0f, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                ScaleAnimation scaleAnimation2 = new ScaleAnimation(1.0f, 0.2f, 1.0f, 0.2f, 300f, 500f);
//				ScaleAnimation scaleAnimation2 = new ScaleAnimation(0.2f, 1.0f, 0.2f, 1.0f, 300f, 500f);
                set.addAnimation(translateAnimation2);
                set.addAnimation(scaleAnimation2);
//				set.addAnimation(rotateAnimation2);
                mImageView.startAnimation(set);
                break;
            default:
                break;
        }

    }
}
