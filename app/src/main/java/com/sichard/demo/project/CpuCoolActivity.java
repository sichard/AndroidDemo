package com.sichard.demo.project;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述:CPU降温的界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-16</b>
 */
public class CpuCoolActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cpu_cool);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        findViewById(R.id.cpu_cool_snow).clearAnimation();
    }
}
