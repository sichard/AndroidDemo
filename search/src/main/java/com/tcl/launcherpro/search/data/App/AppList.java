package com.tcl.launcherpro.search.data.App;

import com.tcl.launcherpro.search.data.ISearchItem;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:搜索应用列表封装类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-8</b>
 */
public class AppList extends IAppIml {
    /** 要展示app的列表 */
    public List<AppInfo> mAppList = new ArrayList<>();
    /** 是否有更多结果 */
    public boolean mIsMore = false;

    public AppList() {
        this.mType = ISearchItem.TYPE_ITEM;
    }
}
