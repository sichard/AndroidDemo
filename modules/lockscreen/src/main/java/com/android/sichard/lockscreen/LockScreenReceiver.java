package com.android.sichard.lockscreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.android.sichard.lockscreen.utils.BatteryInfo;

/**
 * <br>类描述：监听锁屏事件以及是否充电事件的Receiver
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/6</b>
 */
public class LockScreenReceiver extends BroadcastReceiver {
    /**
     * <br>功能简述:是否正在充电
     *
     * @return true,正在充电；false，不在充电
     */
    private boolean isCharging(Context context) {
        return BatteryInfo.isElectricity(context);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (!TextUtils.isEmpty(action)) {
            if (action.equals(Intent.ACTION_SCREEN_OFF) || action.equals(Intent.ACTION_POWER_CONNECTED)) {
                if ( isCharging(context)) {
                    // 隐式启动锁屏页
                    final Intent intent1 = new Intent("android.intent.action.LOCK_SCREEN");
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent1.putExtra(LockScreenActivity.START_LEVEL, BatteryInfo.getLevelPercent(context));
                    context.startActivity(intent1);
                }
            }
        }
    }
}
