package com.tcl.launcherpro.search.data.appcenter;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * Created by lunou on 2016/12/30.
 */
public class AppCenterUtil {
    // searchSdk差异配置 （oneTouchLauncher:true）
    public static final boolean IS_USE_APPCENTER_SEARCH = true;

    public static AppCenterInfo getAppCenterInfo(Context context, String searchContent) {
        if (!IS_USE_APPCENTER_SEARCH) {
            return null;
        }
        PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
        Intent mainIntent = new Intent("com.tcl.action.apps.search", null);
        List<ResolveInfo> resolveInfos = pm
                .queryIntentActivities(mainIntent, PackageManager.MATCH_DEFAULT_ONLY);
        ResolveInfo resolveInfo = null;
        for (ResolveInfo reInfo : resolveInfos) {
            resolveInfo = reInfo;
            break;
        }

        if (resolveInfo != null) {
            Drawable icon = resolveInfo.loadIcon(pm); // 获得应用程序图标
            return new AppCenterInfo(icon, searchContent);
        }
        return null;
    }
}
