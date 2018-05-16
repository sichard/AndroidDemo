package com.sichard.demo;

import android.app.Application;

import com.sichard.weather.WeatherWidgetManager;

/**
 * <br>类描述:Demo的Application实例
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-15</b>
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        WeatherWidgetManager.init(this);
    }
}
