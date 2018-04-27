package com.sichard.demo.specialeffects;

import android.os.Bundle;
import android.view.View;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

public class PathAnimationActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_path_animation);
        final FlowerAnimationView flowerAnimationView = findViewById(R.id.path_animation_view);

        findViewById(R.id.but_run).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flowerAnimationView.startAnimation();
            }
        });
    }
}
