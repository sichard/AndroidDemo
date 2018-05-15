package com.tct.launcher.weather.weatherData;

import com.tct.launcher.weather.Interface.NotProGuard;

/**
 * Created by jinhui.shao on 2016/7/28.
 * 根据locationkey查询得到的现状信息实体
 */
/*
Current Conditions By LocationKey：
http://api.accuweather.com/currentconditions/v1/58194.json?apikey=af7408e9f4d34fa6a411dd92028d4630    &details=true
[
    {
        "LocalObservationDateTime": "2016-07-27T16:03:00+08:00",
        "EpochTime": 1469606580,
        "WeatherText": "Sunny",
        "WeatherIcon": 1,
        "LocalSource": {
            "Id": 7,
            "Name": "Huafeng",
            "WeatherCode": "00"
        },
        "IsDayTime": true,
        "Temperature": {
            "Metric": {
                "Value": 32.8,
                "Unit": "C",
                "UnitType": 17
            },
            "Imperial": {
                "Value": 91,
                "Unit": "F",
                "UnitType": 18
            }
        },
        "MobileLink": "http://m.accuweather.com/en/cn/shenzhen/58194/current-weather/58194?lang=en-us",
        "Link": "http://www.accuweather.com/en/cn/shenzhen/58194/current-weather/58194?lang=en-us"
    }
]
 */
public class CurrentConditionEntity implements NotProGuard {
    private static final String TAG = "CurrentConditionEntity";
    public String EpochTime;
    public String WeatherText;
    public String WeatherIcon;
    public Temperature temperature;
    public boolean IsDayTime;
    public Wind wind;
    public int RelativeHumidity;
    public Visibility visibility;

    public CurrentConditionEntity() {
    }

    public static class Temperature {
        public Metric metric;
        public Imperial imperial;

        @Override
        public String toString() {
            return "Temperature [metric=" + metric + ", imperial=" + imperial + "]";
        }
    }

    public static class Wind {
        public Speed speed;
        public static class Speed {
            public Metric metric;
            public Imperial imperial;

            @Override
            public String toString() {
                return "Speed [metric=" + metric + ", imperial=" + imperial + "]";
            }
        }

        @Override
        public String toString() {
            return "Wind [Speed = " + speed + "]";
        }
    }

    public static class Visibility {
        public Metric metric;
        public Imperial imperial;

        @Override
        public String toString() {
            return "Visibility [metric=" + metric + ", imperial=" + imperial + "]";
        }
    }

    public static class Metric {
        public String Value;
        public String Unit;
        public String UnitType;

        @Override
        public String toString() {
            return "Metric [Value=" + Value + ", Unit=" + Unit + ", UnitType=" + UnitType + "]";
        }

    }

    public static class Imperial {
        public String Value;
        public String Unit;
        public String UnitType;

        @Override
        public String toString() {
            return "Metric [Value=" + Value + ", Unit=" + Unit + ", UnitType=" + UnitType + "]";
        }
    }

    @Override
    public String toString() {
        return "CurrentConditionEntity [EpochTime=" + EpochTime + ", WeatherText=" + WeatherText
                + ", WeatherIcon=" + WeatherIcon + ", temperature=" +  temperature + ", wind = " + wind
                + ", RelativeHumidity = " + RelativeHumidity + ", Visibility = " + visibility
                + "]";
    }

    // get and set begin
    public String getEpochTime() {
        return EpochTime;
    }

    public void setEpochTime(String EpochTime) {
        this.EpochTime = EpochTime;
    }

    public String getWeatherText() {
        return WeatherText;
    }

    public void setWeatherText(String weatherText) {
        WeatherText = weatherText;
    }

    public String getWeatherIcon() {
        return WeatherIcon;
    }

    public void setWeatherIcon(String weatherIcon) {
        WeatherIcon = weatherIcon;
    }

    public Temperature getTemperature() {
        return temperature;
    }

    public void setTemperature(Temperature temperature) {
        this.temperature = temperature;
    }
}
