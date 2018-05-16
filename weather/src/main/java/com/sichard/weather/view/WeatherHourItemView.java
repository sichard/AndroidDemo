package com.sichard.weather.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sichard.weather.R;
import com.sichard.weather.WeatherUtils;
import com.sichard.weather.weatherData.ForecastHourlyEntity;


/**
 * <br>类描述:天气页面，显示小时天气的View
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/22</b>
 */

public class WeatherHourItemView extends LinearLayout {

    private TextView mTime;
    private ImageView mWeatherImage;

    public WeatherHourItemView(Context context) {
        super(context);
    }

    public WeatherHourItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WeatherHourItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mTime = (TextView) findViewById(R.id.weather_hour_time);
        mWeatherImage = (ImageView) findViewById(R.id.weather_hour_image);
    }

    public void setData(ForecastHourlyEntity data) {
        mTime.setText(data.DateTime.substring(11, 16));
        mWeatherImage.setImageResource(WeatherUtils.getWeatherDetailIconId(getContext(), data.WeatherIcon));
    }

    public void setData(String weatherIcon) {
        mTime.setText("now");
        mWeatherImage.setImageResource(WeatherUtils.getWeatherDetailIconId(getContext(), weatherIcon));
    }
}
