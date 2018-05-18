package com.tcl.launcherpro.search.data.App;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.tcl.launcherpro.search.common.MatchResult;

/**
 * <br>类描述:搜索应用信息
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-8</b>
 */
public class AppInfo implements IApp {
    private Drawable mSystemIcon = null;
    private Drawable mIcon = null;
    /** App标题 */
    private String mTitle = null;
    /** 启动应用的Intent */
    private Intent mIntent;
    /** 应用ComponentName */
    private ComponentName mComponentName;
    /** 包名 */
    private String mPackageName = null;
    /** 搜索结果匹配 */
    public MatchResult mMatchResult;

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }

    public void setSystemIcon(Drawable mIcon) {
        mSystemIcon = mIcon;
    };

    public Drawable getSystemIcon() {
        return mSystemIcon;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public ComponentName getComponentName() {
        return mComponentName;
    }

    public void setComponentName(ComponentName componentName) {
        this.mComponentName = componentName;
    }

    public Intent getIntent() {
        return mIntent;
    }

    public void setIntent(Intent intent) {
        this.mIntent = intent;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String mPackageName) {
        this.mPackageName = mPackageName;
    }
}
