package com.tcl.launcherpro.search;

import android.content.ComponentName;

/**
 * app过滤
 * Created by lunou on 2016/12/27.
 */
public interface AppsFilter {

    /**
     * 应用顾虑
     * @param pkg
     * @return
     */
    public boolean isFilter(String pkg);

}
