package com.android.sichard.search.data.appcenter;

import android.graphics.drawable.Drawable;

/**
 * AppCenter搜索项
 * Created by lunou on 2016/12/29.
 */
public class AppCenterInfo extends IAppCenterIml {
    // 搜索内容
    private String mSearchContent;
    // 图标
    private Drawable mIcon;

    public AppCenterInfo(Drawable icon, String searchContent) {
        mType = IAppCenterIml.TYPE_ITEM;
        mIcon = icon;
        mSearchContent = searchContent;
    }

    /**
     * 获取搜索内容
     * @return
     */
    public String getSearchContent() {
        return mSearchContent;
    }

    /**
     * 获取AppCenter图标
     * @return
     */
    public Drawable getIcon() {
        return mIcon;
    }
}
