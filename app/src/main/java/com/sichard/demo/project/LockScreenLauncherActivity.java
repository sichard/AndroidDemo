package com.sichard.demo.project;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;
import com.android.sichard.lockscreen.LockScreenReceiver;
import com.sichard.demo.R;

/**
 * <br>类描述:启动锁屏的Activity
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-22</b>
 */
public class LockScreenLauncherActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen_launcher);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_POWER_CONNECTED);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        LockScreenReceiver mLockScreenReceiver = new LockScreenReceiver();
        registerReceiver(mLockScreenReceiver, filter);
    }
}
