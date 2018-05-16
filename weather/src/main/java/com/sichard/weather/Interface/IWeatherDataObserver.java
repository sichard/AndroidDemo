package com.tct.launcher.weather.Interface;


import com.sichard.weather.WeatherDataChangeEvent;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.util.List;

/**
 * <br>类描述:天气城市列表的数据观察者需要是实现该接口
 * <br>详细描述:当天气城市列表数据发生改变时，将会通知该观察者
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/26</b>
 */

public interface IWeatherDataObserver {
    /**
     * @param event
     * @param chgDataList 改变后的城市天气实体的列表
     */
    void onDataChanged(WeatherDataChangeEvent event, List<WeatherDataEntity> chgDataList);
}
