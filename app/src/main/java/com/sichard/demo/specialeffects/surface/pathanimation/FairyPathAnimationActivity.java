package com.sichard.demo.specialeffects.surface.pathanimation;

import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述：路径动画activity
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-24</b>
 */
public class FairyPathAnimationActivity extends BaseActivity {

    private PathAnimationSurfaceView mPathAnimationSurfaceView;
    private PathParam pathParam;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fairy_path_animation);
        mPathAnimationSurfaceView = findViewById(R.id.fairy_view);
        pathParam = new PathParam(this);
        pathParam.setOriginDestination(new Point(80, 80), new Point(540, 680));
        pathParam.setStartSize(80);
        pathParam.setEndSize(960);
        pathParam.setDuration(1000);
        pathParam.setInterpolator(new DecelerateInterpolator());
        pathParam.setAnimationEndListener(new PathParam.AnimationEndListener() {
            @Override
            public void onAnimationEnd() {
                mPathAnimationSurfaceView.post(new Runnable() {
                    @Override
                    public void run() {

                    }
                });
            }
        });
        mPathAnimationSurfaceView.setPathParam(pathParam);
    }

    public void onClick(View view) {
        String tag = (String) view.getTag();
        if (tag.equals("start")) {
            pathParam.setIsOpenAI(true);
            mPathAnimationSurfaceView.startAnimation();
        } else if (tag.equals("end")) {
            mPathAnimationSurfaceView.setVisibility(View.VISIBLE);
            pathParam.setIsOpenAI(false);
            mPathAnimationSurfaceView.startAnimation();
        }
    }
}
