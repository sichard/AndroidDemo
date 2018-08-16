package com.sichard.weather.weatherData;

import java.util.List;

/**
 * <br>类描述:天气数据实体，包括地区的LocationEntity、CurrentConditionEntity以及ForecastEntity
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/21</b>
 */

public class WeatherDataEntity {
    public LocationEntity mLocationEntity;
    public CurrentConditionEntity mCurrentConditionEntity;
    public ForecastDailyEntity mForecastDailyEntity;
    public List<ForecastHourlyEntity> mForecastHourlyEntityList;

    public WeatherDataEntity() {
    }

    public WeatherDataEntity(LocationEntity mLocationEntity) {
        this.mLocationEntity = mLocationEntity;
    }

    public WeatherDataEntity(LocationEntity mLocationEntity, CurrentConditionEntity mCurrentConditionEntity, ForecastDailyEntity mForecastDailyEntity, List<ForecastHourlyEntity> mForecastHourlyEntityList) {
        this.mLocationEntity = mLocationEntity;
        this.mCurrentConditionEntity = mCurrentConditionEntity;
        this.mForecastDailyEntity = mForecastDailyEntity;
        this.mForecastHourlyEntityList = mForecastHourlyEntityList;
    }
}
