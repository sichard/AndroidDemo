package com.tcl.launcherpro.search.data.hotSite;

import android.content.Context;
import android.graphics.drawable.Drawable;

/**
 * <br>类描述:热门网站项
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/7</b>
 */
public class HotSiteInfo {
    private Context mContext;
    public Drawable mIcon;
    public String mTitle;
    public String mUrl;

    public HotSiteInfo(Context context) {
        mContext = context;
    }

    public void setIcon(int resId) {
        this.mIcon = mContext.getResources().getDrawable(resId);
    }

    public void setTitle(int resId) {
        this.mTitle = mContext.getString(resId);
    }
}
