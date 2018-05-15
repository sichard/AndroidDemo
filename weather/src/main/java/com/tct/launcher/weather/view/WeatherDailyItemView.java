package com.tct.launcher.weather.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tct.launcher.weather.R;
import com.tct.launcher.weather.WeatherUtils;
import com.tct.launcher.weather.weatherData.ForecastDailyEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br>类描述:天气页面，显示未来天气的View
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/22</b>
 */

public class WeatherDailyItemView extends LinearLayout {
    private TextView mWeek, mMinTemp, mMaxTemp;
    private ImageView mWeatherImage;

    public WeatherDailyItemView(Context context) {
        super(context);
    }

    public WeatherDailyItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherDailyItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mWeek = (TextView) findViewById(R.id.weather_daily_week);
        mMinTemp = (TextView) findViewById(R.id.weather_daily_min_temperature);
        mMaxTemp = (TextView) findViewById(R.id.weather_daily_max_temperature);
        mWeatherImage = (ImageView) findViewById(R.id.weather_daily_image);
    }

    public void setData(ForecastDailyEntity.DailyForecasts data) {
        SimpleDateFormat format = new SimpleDateFormat("E");
        mWeek.setText(format.format(new Date(Long.valueOf(data.EpochDate + "000"))));
        if (data.day != null) {
            mWeatherImage.setImageResource(WeatherUtils.getWeatherDetailIconId(getContext(), data.day.Icon));
        }
        if (data.temperature.minimum.Value != null) {
            mMinTemp.setText(WeatherUtils.getCurrentCTemp(data.temperature.minimum.Value, data.temperature.minimum.UnitType));
        }
        if (data.temperature.maximum.Value != null) {
            mMaxTemp.setText(WeatherUtils.getCurrentCTemp(data.temperature.maximum.Value, data.temperature.maximum.UnitType));
        }
    }
}
