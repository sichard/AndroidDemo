package com.tct.launcher.weather.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ScrollView;

/**
 * <br>类描述:天气详情界面的ScrollView
 * <br>详细描述:为了让详情界面的NavigateBar界面跟着详情界面一起滚动，加入的封装View
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/28</b>
 */

public class WeatherDetailScrollView extends ScrollView {
    private LinearLayout mNavigateBar;

    public WeatherDetailScrollView(Context context) {
        super(context);
    }

    public WeatherDetailScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherDetailScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        // 让NavigateBar和ScrollView一起滚动
        if (mNavigateBar != null) {
            mNavigateBar.scrollTo(l,t);
        }
    }

    public void setNavigateBar(LinearLayout navigateBar) {
        this.mNavigateBar = navigateBar;
    }
}
