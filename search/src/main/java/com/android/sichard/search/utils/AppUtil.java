package com.android.sichard.search.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.android.sichard.search.SearchSDK;
import com.android.sichard.search.data.App.AppInfo;

import junit.framework.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述：App相关的工具方法
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2016/7/7</b>
 */
public final class AppUtil {

    private static final String LOG_TAG = "AppUtil";

    public static List<AppInfo> getInstalledAppList(Context context) {
        List<ResolveInfo> appList = getAppList(context);
        List<AppInfo> appInfoList = new ArrayList<>(appList.size());
        for (ResolveInfo appResolveInfo : appList) {
            AppInfo appInfo = createAppInfo(context, appResolveInfo);
            if (null != appInfo) {
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    public static List<AppInfo> getAppList(Context context, List<ResolveInfo> resolveInfos, List<Drawable> icons) {
        List<AppInfo> appInfoList = new ArrayList<>();
        for (int i = 0; i < resolveInfos.size(); i++) {
            ResolveInfo appResolveInfo = resolveInfos.get(i);
            AppInfo appInfo = createAppInfo(context, appResolveInfo, icons.get(i));
            if (null != appInfo) {
                appInfoList.add(appInfo);
            }
        }
        return appInfoList;
    }

    /**
     * <br>功能简述:获取系统所有启动app
     * <br>功能详细描述:通过PackageManager查询获取
     * <br>注意:
     * @param context
     * @return
     */
    public static List<ResolveInfo> getAppList(Context context) {
        PackageManager pkgManager = context.getPackageManager();
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // ResolveInfo类:根据<intent>节点来获取其上一层目录的信息，通常是<activity>、<receiver>、<service>节点信息。
        List<ResolveInfo> appList = pkgManager.queryIntentActivities(mainIntent, 0);
        return appList;
    }

    /**
     * <br>功能简述:跟据 app 的 ResolveInfo信息构建一个AppInfo对象
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param appResolveInfo
     * @return
     */
    public static AppInfo createAppInfo(Context context, ResolveInfo appResolveInfo) {
        if (null == appResolveInfo) {
            return null;
        }

        ApplicationInfo applicationInfo = appResolveInfo.activityInfo.applicationInfo;
        // 如果是桌面自身就过滤掉，此处如果用包名去过滤，主题也会被过滤掉，故用类名
        if (appResolveInfo.activityInfo.name.equals("com.tcl.launcherpro.MainActivity")) {
            return null;
        }
        ComponentName componentName = new ComponentName(applicationInfo.packageName,
                appResolveInfo.activityInfo.name);
        Intent launcherIntent = createLaunchIntent(componentName);

        PackageManager pkgManager = context.getPackageManager();
        String title = appResolveInfo.loadLabel(pkgManager).toString();
        if (null == title) {
            title = appResolveInfo.activityInfo.name;
        }

        Drawable drawable = appResolveInfo.loadIcon(pkgManager);
        if (drawable == null) {
            drawable = pkgManager.getDefaultActivityIcon();
        }
        AppInfo appInfo = new AppInfo();
        appInfo.setIcon(drawable);
        appInfo.setSystemIcon(drawable);
        appInfo.setTitle(title);
        appInfo.setIntent(launcherIntent);
        appInfo.setComponentName(componentName);
        appInfo.setPackageName(applicationInfo.packageName);
        return appInfo;
    }

    /**
     * <br>功能简述:跟据 app 的 ResolveInfo信息构建一个AppInfo对象
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param appResolveInfo
     * @return
     */
    public static AppInfo createAppInfo(Context context, ResolveInfo appResolveInfo, Drawable icon) {
        if (null == appResolveInfo) {
            return null;
        }

        ApplicationInfo applicationInfo = appResolveInfo.activityInfo.applicationInfo;
        // 如果是桌面自身就过滤掉，此处如果用包名去过滤，主题也会被过滤掉，故用类名
        if (appResolveInfo.activityInfo.name.equals("com.tcl.launcherpro.MainActivity")) {
            return null;
        }
        ComponentName componentName = new ComponentName(applicationInfo.packageName,
                appResolveInfo.activityInfo.name);
        Intent launcherIntent = createLaunchIntent(componentName);

        PackageManager pkgManager = context.getPackageManager();
        String title = appResolveInfo.loadLabel(pkgManager).toString();
        if (null == title) {
            title = appResolveInfo.activityInfo.name;
        }

        Drawable drawable = icon;
        AppInfo appInfo = new AppInfo();
        appInfo.setIcon(drawable);
        appInfo.setSystemIcon(drawable);
        appInfo.setTitle(title);
        appInfo.setIntent(launcherIntent);
        appInfo.setComponentName(componentName);
        appInfo.setPackageName(applicationInfo.packageName);
        return appInfo;
    }

    /**
     * <br>功能简述:
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param componentName
     * @return
     */
    public static Intent createLaunchIntent(ComponentName componentName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        return intent;
    }

    /**
     * <br>功能简述:获取应用名称
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param appResolveInfo
     * @return
     */
    public static String getAppTitle(Context context, ResolveInfo appResolveInfo) {
        if (context == null || appResolveInfo == null) {
            return null;
        }

        PackageManager pkgManager = context.getPackageManager();
        String title = appResolveInfo.loadLabel(pkgManager).toString();
        if (null == title) {
            title = appResolveInfo.activityInfo.name;
        }
        return title;
    }

    public static Drawable getAppDefIcon(Context context) {
        if (context == null) {
            context = SearchSDK.getContext();
        }
        PackageManager pkgManager = context.getPackageManager();
        return pkgManager.getDefaultActivityIcon();
    }

    public static Drawable getAppFullResIcon(Context context, ComponentName componentName) {
        Assert.assertNotNull(componentName);

        PackageManager pkgManager = context.getPackageManager();
        Drawable appIcon = null;
        try {
            appIcon = pkgManager.getActivityIcon(componentName);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }

        //没有获取到图标，获取默认app图标
        if (null == appIcon) {
            appIcon = getAppDefIcon(context);
        }
        return appIcon;
    }

    /**
     * <br>功能简述:获取apk包的标题
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param context
     * @param pkgName
     * @return
     */
    public static String getPkgTitle(Context context, String pkgName) {
        Assert.assertNotNull(pkgName);

        String title = null;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo;
            packageInfo = packageManager.getPackageInfo(pkgName, PackageManager.GET_ACTIVITIES);
            title = (String) packageInfo.applicationInfo.loadLabel(packageManager);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return title;
    }
}
