package com.sichard.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sichard.weather.view.WeatherDailyItemView;
import com.sichard.weather.view.WeatherDetailScrollView;
import com.sichard.weather.view.WeatherHourItemView;
import com.sichard.weather.view.WeatherTemperatureDiagram;
import com.sichard.weather.weatherData.CurrentConditionEntity;
import com.sichard.weather.weatherData.ForecastDailyEntity;
import com.sichard.weather.weatherData.ForecastHourlyEntity;
import com.sichard.weather.weatherData.LocationEntity;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * <br>类描述:天气Fragment
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/15</b>
 */

public class WeatherFragment extends Fragment {

    private Context mContext;
    private WeatherDetailScrollView mView;
    private TextView mLocationView;
    private TextView mWeatherView;
    private TextView mTemperatureView;
    private TextView mWeekView, mTemperatureMinView, mTemperatureMaxView;
    /** 未来24小时天气列表 */
    private LinearLayout mWeatherHourListView;
    /** 未来5天天气列表 */
    private LinearLayout mWeatherDateListView;
    /** 温度曲线图 */
    private WeatherTemperatureDiagram mTemperatureDiagram;
    private WeatherDataEntity mWeatherDataEntity;
    private LoadForecastDateTask mLoadForecastDataTask;
    private LinearLayout mNavigateBar;
    private LinearLayout mCurrentLocationLayout;
    /** 是否已经重新加载数据，每次切换Fragment时，会重新加载数据，所以用标志位来控制数据的加载 */
    private boolean mIsReloaded = false;
    /** 是否定位城市 */
    private boolean mIsFixedPositionCity;
    /** 是否上报过广告展示数据 */
    private boolean mIsUploadStatisticsData;
    /** 是否获取天气数据失败 */
    private boolean mIsRequestFailed;

    public WeatherFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        mContext = getActivity();
        mView = (WeatherDetailScrollView) inflater.inflate(R.layout.weather_fragment, container, false);
        mView.setVerticalScrollBarEnabled(false);
        mView.setNavigateBar(mNavigateBar);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        reloadData();
    }

    private void initView() {
        mLocationView = (TextView) mView.findViewById(R.id.weather_current_location);
        mWeatherView = (TextView) mView.findViewById(R.id.weather_current_text);
        mTemperatureView = (TextView) mView.findViewById(R.id.weather_current_temperature);
        mWeekView = (TextView) mView.findViewById(R.id.weather_current_week);
        mTemperatureMaxView = (TextView) mView.findViewById(R.id.weather_current_max_temperature);
        mTemperatureMinView = (TextView) mView.findViewById(R.id.weather_current_min_temperature);
        mWeatherHourListView = (LinearLayout) mView.findViewById(R.id.weather_hour_list);
        mWeatherDateListView = (LinearLayout) mView.findViewById(R.id.weather_date_list);
        mTemperatureDiagram = (WeatherTemperatureDiagram) mView.findViewById(R.id.weather_temperature_diagram);
        mCurrentLocationLayout = (LinearLayout) mView.findViewById(R.id.weather_location_layout);

        updateView();
    }

    private void reloadData() {
        if (!mIsReloaded) {
            String language = mContext.getResources().getConfiguration().locale.toString();
            mLoadForecastDataTask = new LoadForecastDateTask(mWeatherDataEntity, language);
            mLoadForecastDataTask.execute();
        }
    }

    public void setLocationEntity(WeatherDataEntity weatherDataEntity) {
        this.mWeatherDataEntity = weatherDataEntity;
    }

    private void updateView() {
        if (mWeatherDataEntity == null) {
            return;
        }
        // 设置天气当前状态
        if (mWeatherDataEntity.mLocationEntity != null) {
            mLocationView.setText(mWeatherDataEntity.mLocationEntity.LocalizedName);
        }
        if (mWeatherDataEntity.mCurrentConditionEntity == null) {
            return;
        }
        mWeatherView.setText(mWeatherDataEntity.mCurrentConditionEntity.WeatherText);
        mTemperatureView.setText(WeatherUtils.getCurrentCTemp(mWeatherDataEntity.mCurrentConditionEntity.temperature.metric.Value,
                mWeatherDataEntity.mCurrentConditionEntity.temperature.metric.UnitType));
        final int weatherIcon = Integer.valueOf(mWeatherDataEntity.mCurrentConditionEntity.WeatherIcon);
        setWeatherBackground(weatherIcon);

        if (mWeatherDataEntity.mForecastDailyEntity == null) {
            return;
        }
        SimpleDateFormat format = new SimpleDateFormat("E");
        ForecastDailyEntity.DailyForecasts dailyData = mWeatherDataEntity.mForecastDailyEntity.dailyForecasts[0];
        mWeekView.setText(format.format(new Date(Long.valueOf(dailyData.EpochDate + "000"))));
        mTemperatureMaxView.setText(WeatherUtils.getCurrentCTemp(dailyData.temperature.maximum.Value, dailyData.temperature.maximum.UnitType));
        mTemperatureMinView.setText(WeatherUtils.getCurrentCTemp(dailyData.temperature.minimum.Value, dailyData.temperature.minimum.UnitType));

        mWeatherHourListView.removeAllViews();
        mWeatherDateListView.removeAllViews();

        if (mWeatherDataEntity.mForecastHourlyEntityList == null) {
            return;
        }
        // 设置未来24小时预报
        int minTemp = Integer.MAX_VALUE, maxTemp = Integer.MIN_VALUE;
        // 添加当前时间的天气
        final WeatherHourItemView weatherCurrentHourItem = (WeatherHourItemView) LayoutInflater.from(mContext).inflate(R.layout.weather_hour_item, mWeatherHourListView, false);
        weatherCurrentHourItem.setData(mWeatherDataEntity.mCurrentConditionEntity.WeatherIcon);
        mWeatherHourListView.addView(weatherCurrentHourItem);
        final int currentTemp = WeatherUtils.getCurrentCTemp(Float.valueOf(mWeatherDataEntity.mCurrentConditionEntity.temperature.metric.Value),
                Integer.valueOf(mWeatherDataEntity.mCurrentConditionEntity.temperature.metric.UnitType));
        minTemp = Math.min(minTemp, currentTemp);
        maxTemp = Math.max(maxTemp, currentTemp);
        // 添加未来二十四小时的天气
        for (int i = 0; i < mWeatherDataEntity.mForecastHourlyEntityList.size(); i++) {
            final ForecastHourlyEntity forecastHourlyEntity = mWeatherDataEntity.mForecastHourlyEntityList.get(i);
            final WeatherHourItemView weatherHourItem = (WeatherHourItemView) LayoutInflater.from(mContext).inflate(R.layout.weather_hour_item, mWeatherHourListView, false);
            weatherHourItem.setData(forecastHourlyEntity);
            mWeatherHourListView.addView(weatherHourItem);
            final int currentCTemp = WeatherUtils.getCurrentCTemp(forecastHourlyEntity.Temperature.Value, forecastHourlyEntity.Temperature.UnitType);
            minTemp = Math.min(minTemp, currentCTemp);
            maxTemp = Math.max(maxTemp, currentCTemp);
        }

        mTemperatureDiagram.setData(minTemp, maxTemp, currentTemp, mWeatherDataEntity.mForecastHourlyEntityList);
        mTemperatureDiagram.setWeatherIconNumber(weatherIcon);

        // 设置未来4天天气
        for (int i = 0; i < mWeatherDataEntity.mForecastDailyEntity.dailyForecasts.length; i++) {
            final WeatherDailyItemView weatherDailyItemView = (WeatherDailyItemView) LayoutInflater.from(mContext).inflate(R.layout.weather_date_item, mWeatherDateListView, false);
            weatherDailyItemView.setData(mWeatherDataEntity.mForecastDailyEntity.dailyForecasts[i]);
            mWeatherDateListView.addView(weatherDailyItemView);
        }
    }

    /**
     * 根据天气设置详情页面背景
     * @param weatherIcon 天气id
     */
    private void setWeatherBackground(int weatherIcon) {
        int drawableId = R.drawable.bg_weather_cloudy;
        switch (weatherIcon) {
            case 33:
            case 34:
            case 35:
            case 36:
            case 37:
            case 38:
                drawableId = R.drawable.bg_weather_night;
                break;
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 30:
                drawableId = R.drawable.bg_weather_sunny;
                break;
            case 22:
            case 23:
            case 24:
            case 31:
            case 43:
            case 44:
                drawableId = R.drawable.bg_weather_snow;
                break;
            case 6:
            case 7:
            case 8:
            case 32:
            case 11:
                drawableId = R.drawable.bg_weather_cloudy;
                break;
            case 12:
            case 13:
            case 14:
            case 16:
            case 17:
            case 18:
            case 19:
            case 20:
            case 21:
            case 25:
            case 26:
            case 29:
            case 39:
            case 40:
            case 41:
                drawableId = R.drawable.bg_weather_rain;
                break;
            case 15:
            case 42:
                drawableId = R.drawable.bg_weather_thunderstorm;
        }
//        final Resources resources = mContext.getResources();
//        final int width =resources.getDisplayMetrics().widthPixels;
//        final int height = resources.getDimensionPixelSize(R.dimen.weather_detail_height);

        mCurrentLocationLayout.setBackgroundDrawable(mContext.getResources().getDrawable(drawableId));
    }

    /**
     * 获取天气详情的历史数据
     * @return
     */
    public WeatherDataEntity getWeatherDataEntity() {
        return mWeatherDataEntity;
    }

    /**
     * 设置详情界面的NavigateBar,主要是为了和ScrollView一起纵向滚动
     * @param navigateBar
     */
    public void setNavigateBar(LinearLayout navigateBar) {
        this.mNavigateBar = navigateBar;
    }

    /**
     * 设定是否自动定位的城市
     * @param isFixedPosition
     */
    public void setFixedPosition(boolean isFixedPosition) {
        this.mIsFixedPositionCity = isFixedPosition;
    }

    /**
     *<br>类描述：加载天气信息的任务
     *<br>详细描述：
     *<br><b>Author sichard</b>
     *<br><b>Date 2017/3/23</b>
     */
    private class LoadForecastDateTask extends AsyncTask<Void, Void, WeatherDataEntity> {
        private WeatherDataEntity weatherDataEntity;
        private String language;
        public LoadForecastDateTask(WeatherDataEntity weatherDataEntity, String language) {
            this.weatherDataEntity = weatherDataEntity;
            this.language = language;
        }

        @Override
        protected WeatherDataEntity doInBackground(Void... params) {
            try {
                if (mWeatherDataEntity.mLocationEntity == null) {
                    this.cancel(true);
                    return null;
                }
                final AccuWeather accuWeather = AccuWeather.getInstance(mContext.getApplicationContext());
                final String locationKey = weatherDataEntity.mLocationEntity.Key;
                final LocationEntity locationEntity = accuWeather.getCityByLocationKey(locationKey, language);
                ForecastDailyEntity dailyEntity = accuWeather.getCurrentForecast(locationKey, language);
                CurrentConditionEntity currentCondition = null;
                final List<CurrentConditionEntity> conditionEntityList = accuWeather.getCurrentCondition(locationKey, language);
                if (conditionEntityList != null && conditionEntityList.size() > 0) {
                    currentCondition = conditionEntityList.get(0);
                }
                List<ForecastHourlyEntity> hourlyEntityList = AccuWeather.getInstance(mContext.getApplicationContext()).getForecastHourlyEntity(locationKey);

                if (locationEntity != null) {
                    weatherDataEntity.mLocationEntity = locationEntity;
                    // 注意此处不更改城市的Key,因为切换语言时同一个城市对应的Key不一样。但无论用那种语言的Key获取的天气数据是相同的，所以此处不更改Key的值
                    // 如果更改Key,会重新存储一份数据，导致详情页面多出一份相同城市的数据。
                    weatherDataEntity.mLocationEntity.Key = locationKey;
                }
                weatherDataEntity.mCurrentConditionEntity = currentCondition;
                weatherDataEntity.mForecastDailyEntity = dailyEntity;
                weatherDataEntity.mForecastHourlyEntityList = hourlyEntityList;
            } catch (Exception e) {
                e.printStackTrace();
                mIsRequestFailed = true;
            }
            return weatherDataEntity;
        }

        @Override
        protected void onPostExecute(WeatherDataEntity weatherDataEntity) {
            super.onPostExecute(weatherDataEntity);
            if (mIsRequestFailed) {
                return;
            }
            updateView();
            if (mIsFixedPositionCity) {
                WeatherDataManager.getInstance().saveFixedPositionData(mWeatherDataEntity);
            } else {
                // 保存获取到的天气信息
                WeatherDataManager.getInstance().saveWeatherDataEntity(mWeatherDataEntity);
                mIsReloaded = true;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLoadForecastDataTask != null) {
            mLoadForecastDataTask.cancel(true);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
