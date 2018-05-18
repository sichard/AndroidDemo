package com.tcl.launcherpro.search.data.searchInWeb;

import android.graphics.drawable.Drawable;

/**
 * AppCenter搜索项
 * Created by lunou on 2016/12/29.
 */
public class SearchInWebInfo extends ISearhInWebIml {
    // 搜索内容
    private String mSearchContent;
    // 图标
    private Drawable mIcon;
    private Runnable mAction;

    public SearchInWebInfo(Drawable icon, String searchContent) {
        mType = SearchInWebTitle.TYPE_ITEM;
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

    public void setAction(Runnable action) {
        mAction = action;
    }

    public Runnable getAction() {
        return mAction;
    }
}
