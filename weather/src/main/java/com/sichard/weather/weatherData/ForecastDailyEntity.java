package com.sichard.weather.weatherData;

import com.sichard.weather.Interface.NotProGuard;

import java.util.Arrays;
/**
 * {
 * Headline:
 * {
 * EffectiveDate: "2013-01-30T07:00:00-05:00",
 * EffectiveEpochDate: 1359547200,
 * Severity: 2,
 * Text: "Rain and thunderstorms Wednesday",
 * MobileLink: "http://m.accuweather.com/en/us/state-college-pa/16801/extended-weather-forecast/335315?lang=en-us",
 * Link: "http://www.accuweather.com/en/us/state-college-pa/16801/extended-weather-forecast/335315?lang=en-us"
 * },
 * DailyForecasts:
 * [
 * {
 * Date: "2013-01-28T07:00:00-05:00",
 * EpochDate: 1359374400,
 * Temperature:
 * {
 * Minimum:
 * {
 * Value: 34,
 * Unit: "F",
 * UnitType: 18
 * },
 * Maximum:
 * {
 * Value: 35,
 * Unit: "F",
 * UnitType: 18
 * }
 * },
 * Day:
 * {
 * Icon: 12,
 * IconPhrase: "Showers"
 * },
 * Night:
 * {
 * Icon: 18,
 * IconPhrase: "Rain"
 * },
 * MobileLink: "http://m.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=1&lang=en-us",
 * Link: "http://www.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=1&lang=en-us"
 * },
 * {
 * Date: "2013-01-29T07:00:00-05:00",
 * EpochDate: 1359460800,
 * Temperature:
 * {
 * Minimum:
 * {
 * Value: 46,
 * Unit: "F",
 * UnitType: 18
 * },
 * Maximum:
 * {
 * Value: 46,
 * Unit: "F",
 * UnitType: 18
 * }
 * },
 * Day:
 * {
 * Icon: 12,
 * IconPhrase: "Showers"
 * },
 * Night:
 * {
 * Icon: 11,
 * IconPhrase: "Fog"
 * },
 * MobileLink: "http://m.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=2&lang=en-us",
 * Link: "http://www.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=2&lang=en-us"
 * },
 * {
 * Date: "2013-01-30T07:00:00-05:00",
 * EpochDate: 1359547200,
 * Temperature:
 * {
 * Minimum:
 * {
 * Value: 33,
 * Unit: "F",
 * UnitType: 18
 * },
 * Maximum:
 * {
 * Value: 57,
 * Unit: "F",
 * UnitType: 18
 * }
 * },
 * Day:
 * {
 * Icon: 18,
 * IconPhrase: "Rain"
 * },
 * Night:
 * {
 * Icon: 7,
 * IconPhrase: "Cloudy"
 * },
 * MobileLink: "http://m.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=3&lang=en-us",
 * Link: "http://www.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=3&lang=en-us"
 * },
 * {
 * Date: "2013-01-31T07:00:00-05:00",
 * EpochDate: 1359633600,
 * Temperature:
 * {
 * Minimum:
 * {
 * Value: 18,
 * Unit: "F",
 * UnitType: 18
 * },
 * Maximum:
 * {
 * Value: 36,
 * Unit: "F",
 * UnitType: 18
 * }
 * },
 * Day:
 * {
 * Icon: 6,
 * IconPhrase: "Mostly Cloudy"
 * },
 * Night:
 * {
 * Icon: 35,
 * IconPhrase: "Partly Cloudy"
 * },
 * MobileLink: "http://m.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=4&lang=en-us",
 * Link: "http://www.accuweather.com/en/us/state-college-pa/16801/daily-weather-forecast/335315?day=4&lang=en-us"
 * },
 * {
 * Date: "2013-02-02T07:00:00-04:00",
 * EpochDate: 1359802800,
 * Temperature:
 * {
 * Minimum:
 * {
 * Value: 41,
 * Unit: "F",
 * UnitType: 18
 * },
 * Maximum:
 * {
 * Value: 65,
 * Unit: "F",
 * UnitType: 18
 * }
 * },
 * Day:
 * {
 * Icon: 15,
 * IconPhrase: "Thunderstorms"
 * },
 * Night:
 * {
 * Icon: 15,
 * IconPhrase: "Thunderstorms"
 * },
 * MobileLink: "http://m.accuweather.com/en/bo/santiago-de-machaca/33531/daily-weather-forecast/33531?day=6&lang=en-us",
 * Link: "http://www.accuweather.com/en/bo/santiago-de-machaca/33531/daily-weather-forecast/33531?day=6&lang=en-us"
 * },
 * ...
 * }
 * ]
 * }
 */

/**
 * Created by jinhui.shao on 2016/7/29.
 *
 * 根据locationkey查询得到的预测信息实体
 * <br><b>注意此处是以天为单位的预测信息实体</b>
 */
public class ForecastDailyEntity implements NotProGuard {
    private static final String TAG = "ForecastDailyEntity";
    public Headline headline;
    public DailyForecasts[] dailyForecasts;

    public ForecastDailyEntity() {
    }

    public static class Headline {
        public String EffectiveEpochDate;
        public String Text;
        public String Category;

        @Override
        public String toString() {
            return "Headline [EffectiveEpochDate=" + EffectiveEpochDate + ", Text=" + Text + ",Category=" + Category + "]";
        }
    }

    public static class DailyForecasts {
        public String Date;
        public String EpochDate;
        public Temperature temperature;
        public Day day;
        public Night night;

        public static class Temperature {
            public Minimum minimum;
            public Maximum maximum;

            public static class Minimum {
                public String Value;
                public String Unit;
                public String UnitType;

                @Override
                public String toString() {
                    return "Minimum [Value=" + Value + ", Unit=" + Unit + ", UnitType=" + UnitType + "]";
                }

            }

            public static class Maximum {
                public String Value;
                public String Unit;
                public String UnitType;

                @Override
                public String toString() {
                    return "Maximum [Value=" + Value + ", Unit=" + Unit + ", UnitType=" + UnitType + "]";
                }
            }

            @Override
            public String toString() {
                return "Temperature [minimum=" + minimum + ", maximum=" + maximum + "]";
            }
        }

        public static class Day {
            public String Icon;
            public String IconPhrase;

            @Override
            public String toString() {
                return "Day [Icon=" + Icon + ", IconPhrase=" + IconPhrase + "]";
            }
        }

        public static class Night {
            public String Icon;
            public String IconPhrase;

            @Override
            public String toString() {
                return "Night [Icon=" + Icon + ", IconPhrase=" + IconPhrase + "]";
            }
        }

        @Override
        public String toString() {
            return "DailyForecasts [Date=" + Date + ",EpochDate=" + EpochDate
                    + ",Temperature=" + temperature
                    + ",Day=" + day + ",Night=" + night
                    + "]";
        }

    }

    @Override
    public String toString() {
        return "ForecastDailyEntity [headline=" + headline + ",dailyForecasts=" + Arrays.toString(dailyForecasts) + "]"; // 内部实现等价于getDailyForecastsStr
    }

    // get and set begin
    public Headline getHeadline() {
        return headline;
    }

    public void setHeadline(Headline headline) {
        this.headline = headline;
    }

    public ForecastDailyEntity.DailyForecasts[] getDailyForecasts() {
        return dailyForecasts;
    }

    public void setDailyForecasts(ForecastDailyEntity.DailyForecasts[] dailyForecasts) {
        this.dailyForecasts = dailyForecasts;
    }

    private String getDailyForecastsStr() {
        if (dailyForecasts == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < dailyForecasts.length; i++) {
            sb.append(dailyForecasts[i]);
            sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}
