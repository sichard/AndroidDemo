package com.sichard.weather.Interface;

import com.sichard.weather.WeatherDataChangeEvent;
import com.sichard.weather.weatherData.WeatherDataEntity;

/**
 * <br>类描述:天气城市列表被观察者接口
 * <br>详细描述:天气城市列表数据源被观察时实现该接口
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/26</b>
 */

public interface IWeatherDataObservable {
    /**
     * <br>功能简述:注册一个数据变更观察者
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param observer
     */
    public void register(IWeatherDataObserver observer);

    /**
     * <br>功能简述:注销一个数据变更观察者
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param observer
     */
    public void unregister(IWeatherDataObserver observer);

    /**
     * <br>功能简述:通知所有数据观察者数据发生了变更
     * <br>功能详细描述:
     * <br>注意:
     *
     * @param event
     * @param weatherDataEntity
     */
    public void notifyDataChanged(WeatherDataChangeEvent event, WeatherDataEntity weatherDataEntity);
}
