package com.tcl.launcherpro.search.theme;

import android.content.ComponentName;
import android.graphics.drawable.Drawable;

/**
 * 主题工具
 * Created by lunou on 2016/12/27.
 */
public interface ThemeTools {
    /**
     * 生成主题图标
     * @param componentName
     * @param drawable
     * @return
     */
    public Drawable createIcon(ComponentName componentName, Drawable drawable);
}
