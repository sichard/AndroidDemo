package com.android.sichard.search.utils;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * 渠道工具类
 * Created by lunou on 2016/12/29.
 */
public class ChannelUtiil {

    /**
     * 获取渠道号
     * @param context
     * @return
     */
    public static String getChannel(Context context) {
        String channel = "google";
        ApplicationInfo applicationInfo = null;
        try {
            applicationInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            if (applicationInfo != null) {
                channel = applicationInfo.metaData.getString("UMENG_CHANNEL");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return channel;
    }

    public static boolean isAppCenterChannel(Context context) {
        String channel = getChannel(context);
        if ("appcenter".equals(channel) || "appcenter_setdef".equals(channel) || "sdkupgrade".equals(channel)) {
            return true;
        }
        return false;
    }

    public static boolean isAppCenterCannelSetDef(Context context) {
        String channel = getChannel(context);
        if ("appcenter_setdef".equals(channel)) {
            return true;
        }
        return false;
    }
}
