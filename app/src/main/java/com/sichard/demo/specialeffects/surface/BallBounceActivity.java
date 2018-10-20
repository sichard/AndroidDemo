package com.sichard.demo.specialeffects.surface;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;

/**
 * <br>类描述：跳动小球
 * <br>详细描述：跳动小球的Activity,该示例主要用于展示SurfaceVew的用法
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-15</b>
 */
public class BallBounceActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new BallBounceView(this));
    }
}
