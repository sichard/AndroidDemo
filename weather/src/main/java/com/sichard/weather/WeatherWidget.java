package com.sichard.weather;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sichard.weather.activity.CityActivity;
import com.sichard.weather.activity.WeatherCityActivity;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <br> 桌面时间和天气的widget View
 * Created by jinhui.shao on 2016/8/8.
 */
public class WeatherWidget extends LinearLayout implements View.OnClickListener {
    private Context mContext;

    private ImageView mWeatherIcon;
    private TextView mTempView;
    private TextView mLocationView;
    private ImageView mLocationLogo;
    private LinearLayout mTempText;
    private LinearLayout mLocationArea;
    private LinearLayout mRefreshArea;
    private TextView mWind, mHumidity, mVisibility, mTempMin, mTempMax;
    private final RotateAnimation loadingAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
            0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
    private TextView mUpdateTime;
    private LinearLayout mWidgetWeather;
    private long mLastRefreshTime;
    private WeatherDataEntity mWeatherDataEntity;

    public WeatherWidget(Context context) {
        super(context);
        mContext = context;
    }

    public WeatherWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
        WeatherWidgetManager.getsInstance().addWeatherWidget(this);   // 将每个创建的天气widget加入管理
    }

    private void initView(){

        mWeatherIcon = (ImageView) findViewById(R.id.weather_icon);
        mTempView = (TextView) findViewById(R.id.temp);
        mLocationView = (TextView) findViewById(R.id.location);
        mLocationLogo = (ImageView) findViewById(R.id.location_logo);
        mTempText = (LinearLayout) findViewById(R.id.temp_and_text);
        mLocationArea = (LinearLayout) findViewById(R.id.location_area);
        mRefreshArea = (LinearLayout) findViewById(R.id.refresh_area);
        mWind = (TextView) findViewById(R.id.wind);
        mHumidity = (TextView) findViewById(R.id.humidity);
        mVisibility = (TextView) findViewById(R.id.visibility);
        mTempMin = (TextView) findViewById(R.id.temp_min);
        mTempMax = (TextView) findViewById(R.id.temp_max);
        mUpdateTime = (TextView) findViewById(R.id.updated_time);

        mWidgetWeather = (LinearLayout) findViewById(R.id.widget_weather);
        mWidgetWeather.setOnClickListener(this);
        mLocationArea.setOnClickListener(this);
        mRefreshArea.setOnClickListener(this);
    }

    public void startLoadingAnimation(){
        Log.i("sjh5", " start Loading Animation " );
        showWeatherData(false);
        mWeatherIcon.setImageResource(R.drawable.loading);
        loadingAnimation.reset();
        loadingAnimation.setDuration(3000);
        loadingAnimation.setRepeatCount(Animation.INFINITE);
        mWeatherIcon.setAnimation(loadingAnimation);
        loadingAnimation.start();
        mWidgetWeather.setVisibility(INVISIBLE);
    }

    /**
     * 请求数据失败更新界面
     */
    public void requestFailed() {
        mUpdateTime.setText(R.string.update_failed);
        mWidgetWeather.setVisibility(VISIBLE);
    }

    /**
     * @param isVisible 请求成功or读取缓存成功时为true.
     */
    public void showWeatherData(boolean isVisible){
        Log.i("sjh5", " showWeatherData isVisible = " + isVisible);
        if(isVisible){
            mTempText.setVisibility(View.VISIBLE);
            mLocationArea.setVisibility(View.VISIBLE);
            mWidgetWeather.setVisibility(VISIBLE);
        }
    }

    /**
     * 根据天气实体信息刷新widget界面
     * @param weatherDataEntity
     */
    public void updateWeatherView(WeatherDataEntity weatherDataEntity) {
        if (weatherDataEntity == null) {
            return;
        }
        mWeatherDataEntity = weatherDataEntity;
        mWidgetWeather.setVisibility(VISIBLE);
        String LocalizedName = "";
        String EnglishName = "";
        if (weatherDataEntity.mLocationEntity != null) {
            LocalizedName = weatherDataEntity.mLocationEntity.LocalizedName;
            EnglishName = weatherDataEntity.mLocationEntity.EnglishName;
        }

        String WeatherIcon = null;
        String WeatherText = null;
        String Value = null;
        String wind = null;
        String humidity = null;
        String visibility = null;
        String tempMin = null;
        String tempMax = null;
        if (weatherDataEntity.mCurrentConditionEntity != null && weatherDataEntity.mForecastDailyEntity != null) {
            WeatherIcon = weatherDataEntity.mCurrentConditionEntity.WeatherIcon;
            WeatherText = weatherDataEntity.mCurrentConditionEntity.WeatherText;
            Value = weatherDataEntity.mCurrentConditionEntity.temperature.metric.Value;
            wind = mContext.getString(R.string.wind) + " " + weatherDataEntity.mCurrentConditionEntity.wind.speed.metric.Value + weatherDataEntity.mCurrentConditionEntity.wind.speed.metric.Unit;
            humidity = mContext.getString(R.string.humidity) + " " + weatherDataEntity.mCurrentConditionEntity.RelativeHumidity + "%";
            visibility = mContext.getString(R.string.visibility) + " " + weatherDataEntity.mCurrentConditionEntity.visibility.metric.Value + weatherDataEntity.mCurrentConditionEntity.visibility.metric.Unit;
            tempMin = WeatherUtils.getCurrentCTemp(weatherDataEntity.mForecastDailyEntity.dailyForecasts[0].temperature.minimum.Value, WeatherUtils.F_TEMP);
            tempMax = WeatherUtils.getCurrentCTemp(weatherDataEntity.mForecastDailyEntity.dailyForecasts[0].temperature.maximum.Value, WeatherUtils.F_TEMP);
        }
        mLocationLogo.setImageResource(R.drawable.ic_widget_location);
        showWeatherData(true);

        if (!TextUtils.isEmpty(LocalizedName)) {
            mLocationView.setText(LocalizedName);
        } else {
            mLocationView.setText(EnglishName);
        }
        if (!TextUtils.isEmpty(WeatherIcon) && !TextUtils.isEmpty(WeatherText) && !TextUtils.isEmpty(Value)) {
            mWeatherIcon.setImageResource(WeatherUtils.getWeatherIconId(mContext, WeatherIcon));
            mTempView.setText(WeatherUtils.getCurrentCTemp(Value, WeatherUtils.C_TEMP));
            mWind.setText(wind);
            mHumidity.setText(humidity);
            mVisibility.setText(visibility);
            mTempMin.setText(tempMin);
            mTempMax.setText(tempMax);
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            mUpdateTime.setText(mContext.getString(R.string.update_time) + " " + df.format(new Date()));
            Log.i("sjh5", " a widget updateWeatherViewByShared  ");
        }

    }

    @Override
    public void onClick(View v) {
        if (v == mLocationArea) {
            Intent intent = new Intent(mContext, CityActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("IS_AUTO_LOCATION", true);
            mContext.startActivity(intent);
        } else if (v == mRefreshArea) {
            long now = System.currentTimeMillis();
            if (now - mLastRefreshTime > 5000) {
                mUpdateTime.setText(R.string.weather_update);
                if (mWeatherDataEntity != null && mWeatherDataEntity.mLocationEntity != null) {
                    WeatherWidgetManager.getsInstance().requestSelectedCityWeather(mWeatherDataEntity.mLocationEntity.Key);
                } else {
                    WeatherWidgetManager.getsInstance().requestLocationAndGetWeather();
                }
                mLastRefreshTime = now;
            }
        } else if (v == mWidgetWeather) {
            if (WeatherDataManager.getInstance().isHaveData()) {
                Intent intent = new Intent(mContext, WeatherCityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else {
                Intent intent = new Intent(mContext, CityActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("IS_AUTO_LOCATION", true);
                mContext.startActivity(intent);
            }
        }
    }
}