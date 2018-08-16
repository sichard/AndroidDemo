package com.android.sichard.search.recent;

import android.content.ComponentName;

import java.util.List;

/**
 * 最近使用
 * Created by lunou on 2017/1/3.
 */
public interface IRecentTask {
    /**
     * 添加一项最近使用
     * @param componentName
     */
    public void saveRecent(ComponentName componentName);

    /**
     * 获取最近使用
     * @return
     */
    public List<ComponentName> getRecentList();
}
