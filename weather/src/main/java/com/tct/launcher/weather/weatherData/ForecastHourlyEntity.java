package com.tct.launcher.weather.weatherData;


/**
 [
 {
 "DateTime": "2014-03-24T13:00:00-04:00",
 "EpochDateTime": 1395680400,
 "WeatherIcon": 2,
 "IconPhrase": "Mostly sunny",
 "IsDaylight": true,
 "Temperature": {
 "Value": 26.0,
 "Unit": "F",
 "UnitType": 18
 },
 "PrecipitationProbability": 0,
 "MobileLink": "http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=13&lang=en-us",
 "Link": "http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=13&lang=en-us"
 },
 {
 "DateTime": "2014-03-24T14:00:00-04:00",
 "EpochDateTime": 1395684000,
 "WeatherIcon": 4,
 "IconPhrase": "Intermittent clouds",
 "IsDaylight": true,
 "Temperature": {
 "Value": 28.0,
 "Unit": "F",
 "UnitType": 18
 },
 "PrecipitationProbability": 0,
 "MobileLink": "http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=14&lang=en-us",
 "Link": "http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=14&lang=en-us"
 },
 {
 "DateTime": "2014-03-24T15:00:00-04:00",
 "EpochDateTime": 1395687600,
 "WeatherIcon": 3,
 "IconPhrase": "Partly sunny",
 "IsDaylight": true,
 "Temperature": {
 "Value": 31.0,
 "Unit": "F",
 "UnitType": 18
 },
 "PrecipitationProbability": 0,
 "MobileLink": "http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=15&lang=en-us",
 "Link": "http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=15&lang=en-us"
 },
 {
 "DateTime": "2014-03-24T16:00:00-04:00",
 "EpochDateTime": 1395691200,
 "WeatherIcon": 3,
 "IconPhrase": "Partly sunny",
 "IsDaylight": true,
 "Temperature": {
 "Value": 32.0,
 "Unit": "F",
 "UnitType": 18
 },
 "PrecipitationProbability": 0,
 "MobileLink": "http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=16&lang=en-us",
 "Link": "http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=16&lang=en-us"
 },
 {
 "DateTime": "2014-03-24T17:00:00-04:00",
 "EpochDateTime": 1395694800,
 "WeatherIcon": 3,
 "IconPhrase": "Partly sunny",
 "IsDaylight": true,
 "Temperature": {
 "Value": 32.0,
 "Unit": "F",
 "UnitType": 18
 },
 "PrecipitationProbability": 0,
 "MobileLink": "http://m.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=17&lang=en-us",
 "Link": "http://www.accuweather.com/en/us/state-college-pa/16801/hourly-weather-forecast/335315?day=1&hbhhour=17&lang=en-us"
 },
 ...
 }
 ]
 */

import com.tct.launcher.weather.Interface.NotProGuard;

/**
 * <br>类描述:根据locationkey查询得到小时的预测信息实体
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/21</b>
 */
public class ForecastHourlyEntity implements NotProGuard {
    public String DateTime;
    public String EpochDateTime;
    public String WeatherIcon;
    public Temperature Temperature;

    public ForecastHourlyEntity() {
    }

    public static class Temperature {
        public float Value;
        public String Unit;
        public int UnitType;
    }

}
