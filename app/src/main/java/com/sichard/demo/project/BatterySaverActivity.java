package com.sichard.demo.project;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.battersaver.BatterySaverCleanView;
import com.android.sichard.battersaver.BatterySaverView;
import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述:省电主界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-17</b>
 */
public class BatterySaverActivity extends BaseActivity implements BatterySaverCleanView.BatterySaverFinishListener {

    private BatterySaverView mBatterySaverView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery_saver);
        mBatterySaverView = findViewById(R.id.battery_saver_view);
        mBatterySaverView.setBatterySaverFinishListener(this);
    }

    @Override
    public void batterySaverFinish() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBatterySaverView.onDestroy();
    }
}
