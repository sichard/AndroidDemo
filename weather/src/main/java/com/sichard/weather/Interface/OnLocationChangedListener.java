package com.sichard.weather.Interface;

/**
 * Created by jinhui.shao on 2016/8/1.
 */
public interface OnLocationChangedListener {

    void onLocationStart();

    void onLocationTimeout();

    void onLocationFail();
    /**
     * GPS或辅助定位成功
     */
    void onLocationSuccess(double latitude, double longitude);

}
