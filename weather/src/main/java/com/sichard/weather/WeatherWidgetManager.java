package com.sichard.weather;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.sichard.weather.Interface.OnLocationChangedListener;
import com.sichard.weather.utils.DeviceUtil;
import com.sichard.weather.utils.TimeConstant;
import com.sichard.weather.weatherData.CurrentConditionEntity;
import com.sichard.weather.weatherData.ForecastDailyEntity;
import com.sichard.weather.weatherData.ForecastHourlyEntity;
import com.sichard.weather.weatherData.LocationEntity;
import com.sichard.weather.weatherData.WeatherDataEntity;
import com.tct.launcher.weather.Interface.IWeatherDataObserver;
import com.tct.launcher.weather.weatherData.WeatherPreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * <br>类描述:天气widget管理者
 * <br>详细描述:负责天气widget添加或移动后，刷新数据(重构了原来请求数据的代码，故代码有点乱)
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/31</b>
 */

public class WeatherWidgetManager implements IWeatherDataObserver, OnLocationChangedListener {
    private static final String TAG = "csc";
    private static final int GET_CITY_TIMEOUT = 1 * 60 * 1000;// 获取天气信息的任务超时时间

    private static Context sContext;
    private static WeatherWidgetManager sInstance;
    private WeatherLocationManager mLocationManger;
    private GetCityTask mCurrentGetCityTask;
    private Handler mHandler;

    /** 获得城市天气的时间，单位是秒(该时间主要是为了确定是否需要更新天气信息) */
    private static final String EPOCH_TIME = "epoch_time";
    /** 桌面所有天气widget的列表 */
    private List<WeatherWidget> mWidgetList = new ArrayList<>();
    /** 是否有天气widget正在运行(桌面是否启动的标志位) */
    private boolean mWidgetIsRunning = false;
    /** 是否正在加载widget数据 */
    private boolean mIsLoading = false;
    /** 当前用户的的语言 */
    private String mCurrentLanguage;
    /** 获取指定城市天气数据失败的次数 */
    private int mGetCityDataFailTimes;
    /** 定时器，用于两个小时刷新一次数据 */
    private Timer timer;
    private TimerTask timerTask;
    /** 当前的天气信息实体 */
    private WeatherDataEntity mWeatherDataEntity = new WeatherDataEntity();

    /**
     * 确保调用init()方法后，再调用该方法。{@link #init(Context)}
     * @return
     */
    public static WeatherWidgetManager getsInstance() {
        if (sInstance == null) {
            sInstance = new WeatherWidgetManager();
        }
        return sInstance;
    }

    private WeatherWidgetManager() {
        if (sContext == null) {
            throw new RuntimeException("init() must be called first!!!");
        }
        WeatherDataManager.getInstance().register(this);
        mHandler = new Handler(Looper.getMainLooper());
        mLocationManger = WeatherLocationManager.getInstance();
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        WeatherDataManager.init(context);
        WeatherLocationManager.init(context);
        getsInstance();
    }

    /**
     * 定时约2小时后再更新天气
     */
    private void runBackgroundTimer(){
        Log.i(TAG, "加入定时器，两小时后重新获取数据");
        long detaMs = 2 * TimeConstant.ONE_HOUR;
        if (timerTask != null) {
            timerTask.cancel();
        }
        timerTask = new TimerTask() {
            @Override
            public void run() {
                Log.i(TAG, " timerTask run. ");
                requestLocationAndGetWeather();
            }
        };

        if (timer != null) {
            timer.cancel();
        }
        timer = new Timer();
        timer.schedule(timerTask, detaMs);
    }

    /**
     * 每一个添加的天气widget都要加入WeatherViewManager的管理。这是添加天气widget的入口。
     *
     * @param weatherWidget
     */
    public void addWeatherWidget(WeatherWidget weatherWidget) {
        if (!mWidgetList.contains(weatherWidget)) {
            mWidgetList.add(weatherWidget);
            // 桌面第一次启动或者添加第一个天气widget
            if (mWidgetList.size() == 1 && !mWidgetIsRunning) {
                updateWeatherViewByShared(true);
                mWidgetIsRunning = true;
                mCurrentLanguage = sContext.getResources().getConfiguration().locale.toString();
                runBackgroundTimer();
            } else {
                // 先用保存的天气数据更新桌面天气widget
                updateWeatherViewByCurrentData();

                String language = sContext.getResources().getConfiguration().locale.toString();
                // 如果更改了语言，根据语言重新请求天气数据
                if (!language.equals(mCurrentLanguage)) {
                    mCurrentLanguage = language;
                    // 根据语言更新天气数据
                    requestLocationAndGetWeather();
                }
            }
        }
    }

    /**
     * 读取SharedPreferences天气数据，若无则请求网络数据
     * @param hasLoading true,天气widget做loading动画；false，不显示loading动画，直接更新数据
     */
    private void updateWeatherViewByShared(boolean hasLoading){
        if(mIsLoading){
            return;
        }
        String weatherIcon = "";
        String weatherText = "";
        String value = "";
        Long epochTime = 0L;
        String entityKey = "";

        epochTime = WeatherPreferencesManager.getInstance(sContext).getLong(EPOCH_TIME, 0);
        if (epochTime != 0) {
            mWeatherDataEntity = WeatherDataManager.getInstance().getFixedPositionData();
            // 此处如果获取自动定位城市为空，则取用户手动添加的第一个城市作为桌面widget显示的城市
            if (mWeatherDataEntity == null) {
                mWeatherDataEntity = WeatherDataManager.getInstance().getDefaultWeatherDataEntity();
            }
            if (mWeatherDataEntity != null) {
                if (mWeatherDataEntity.mLocationEntity != null) {
                    entityKey = mWeatherDataEntity.mLocationEntity.Key;
                }
                if (mWeatherDataEntity.mCurrentConditionEntity != null) {
                    weatherIcon = mWeatherDataEntity.mCurrentConditionEntity.WeatherIcon;
                    weatherText = mWeatherDataEntity.mCurrentConditionEntity.WeatherText;
                    value = mWeatherDataEntity.mCurrentConditionEntity.temperature.metric.Value;
                }
            }
        }

        if (epochTime == 0 || TextUtils.isEmpty(weatherIcon) || TextUtils.isEmpty(weatherText)
                || TextUtils.isEmpty(value)) {
            if (hasLoading) {
            } else {
                updateWeatherViewByCurrentData();
            }
            requestLocationAndGetWeather();
            return;
        }

        long detaMs = System.currentTimeMillis() - epochTime * 1000;
        if (detaMs > 0 && detaMs < 6 * TimeConstant.ONE_HOUR) {
            for (WeatherWidget weatherWidget : mWidgetList) {
                weatherWidget.updateWeatherView(mWeatherDataEntity);
            }
        } else if (detaMs > 6 * TimeConstant.ONE_HOUR && detaMs < 24 * TimeConstant.ONE_HOUR) {
            for (WeatherWidget weatherWidget : mWidgetList) {
                weatherWidget.updateWeatherView(mWeatherDataEntity);
            }
            if (TextUtils.isEmpty(entityKey)) {
                requestLocationAndGetWeather();
            } else {
                requestSelectedCityWeather(entityKey);
            }
        } else {  // 包含用户改成以往的时间.
            if (TextUtils.isEmpty(entityKey)) {
                requestLocationAndGetWeather();
            } else {
                requestSelectedCityWeather(entityKey);
            }
        }
    }

    /**
     * 请求位置并获取天气信息
     */
    public void requestLocationAndGetWeather(){
        mLocationManger.requestLocation();
        mLocationManger.addOnLocationChangedListener(this);
    }

    /**
     * 根据指定的entityKey获取天气信息
     * @param entityKey
     */
    public void requestSelectedCityWeather(String entityKey){
        String language = sContext.getResources().getConfiguration().locale.toString();
        mHandler.postDelayed(mGetWeatherTimeoutRunnable, GET_CITY_TIMEOUT);
        new GetCityTask(0, 0, language, entityKey).execute();
    }

    private void requestFailed() {
        for (WeatherWidget weatherWidget : mWidgetList) {
            weatherWidget.requestFailed();
        }
    }

    /**
     * 用本地存储的数据来更新天气widget信息
     */
    private void updateWeatherViewByCurrentData() {
        if(mIsLoading){
            if(mWidgetList.size() > 0){
                mWidgetList.get(mWidgetList.size() - 1).startLoadingAnimation();
            }
            return;
        }
        for (WeatherWidget weatherWidget : mWidgetList) {
            weatherWidget.updateWeatherView(mWeatherDataEntity);
        }
    }


    @Override
    public void onLocationStart() {

    }

    @Override
    public void onLocationTimeout() {
        mLocationManger.removeOnLocationListener(this);
        requestFailed();
    }

    @Override
    public void onLocationFail() {
        mLocationManger.removeOnLocationListener(this);
        Log.d(TAG, "定位失败");
        requestFailed();
    }

    @Override
    public void onLocationSuccess(double latitude, double longitude){
        mLocationManger.removeOnLocationListener(this);
        final double[] locations = new double[2];
        locations[0] = latitude;
        locations[1] = longitude;
        onStopLocationAndQueryCity(locations, true);
    }

    /**
     * 停止定位并根据经纬度查询城市，并清理相关listener，runnable，AsyncTask.
     * @param locations 经纬度坐标，可能为null.
     * @param isAdjustedByCanonicalKey 是否使用CanonicalKey来校正得到的城市（再次定位城市）
     */
    private void onStopLocationAndQueryCity(double[] locations, boolean isAdjustedByCanonicalKey) {
        // 停止定位，根据新的位置信息获取城市信息
        if (locations != null) {
            String language = sContext.getResources().getConfiguration().locale.toString();
            Log.d(TAG, "language = " + language + " latitude = " + locations[0] + " longitude = " + locations[1]);

            mHandler.postDelayed(mGetWeatherTimeoutRunnable, GET_CITY_TIMEOUT);
            new GetCityTask(locations[0], locations[1], language, null).execute();
        }
    }

    public class GetCityTask extends AsyncTask<Void, Void, WeatherDataEntity> {
        private double latitude;
        private double longitude;
        private String language;
        private String locationEntityKey;

        public GetCityTask(double latitude, double longitude, String language, String locationEntityKey) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.language = language;
            this.locationEntityKey = locationEntityKey;
            if (mCurrentGetCityTask != null) {
                mCurrentGetCityTask.cancel(true);
            }
            mCurrentGetCityTask = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.d(TAG, "onPreExecute");
            if (!DeviceUtil.isNetworkOK(sContext)) {
                Log.d(TAG, "无可用网络 GetCityTask 或 requestEntityList 空");
                if (mCurrentGetCityTask != null) {
                    mCurrentGetCityTask.cancel(true);
                    mCurrentGetCityTask = null;
                }
                onGetCityDataFail();
            }
        }

        @Override
        protected WeatherDataEntity doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground ");
            if (mCurrentGetCityTask == null) {
                return null;
            }
            String entityKey = locationEntityKey;
            LocationEntity entity = null;
            if (TextUtils.isEmpty(locationEntityKey)) {
                try {
                    entity = AccuWeather.getInstance(sContext).getCityByCoordinate(latitude, longitude, language);
                } catch (AccuWeather.NetworkRequestException e) {
                    e.printStackTrace();
                    redoGetCityTask(latitude, longitude, language, locationEntityKey);
                    return null;
                }
                if (entity == null) Log.e(TAG, " LocationEntity null! ");
                // 服务器定位，获取粗粒度地理位置
                try {   // 开了gps定位得到的坐标得到深圳南山也要跑这调整到地区的上一级深圳.实质再次请求的目的是调整城市到上一级.而且SupplementalAdminAreas（内含深圳）可能为空.
                    if (entity != null && entity.Key != null && entity.Details != null && entity.Details.CanonicalLocationKey != null
                            && !entity.Key.equals(entity.Details.CanonicalLocationKey)) {
                        Log.i(TAG, "即将要定位的城市, key = " + entity.Details.CanonicalLocationKey);
                        entity = AccuWeather.getInstance(sContext).getCityByLocationKey(entity.Details.CanonicalLocationKey, language);
                    } else if (entity != null && TextUtils.isEmpty(entity.LocalizedName)) {//zh_tw定位到香港的时候有且仅有LocalizedName为空，改成zh即可.
                        String lang = language;
                        if (language != null && language.contains("_")) {
                            lang = language.substring(0, language.indexOf("_"));
                        }
                        Log.w(TAG, "即将要定位的城市, lang = " + lang);
                        entity = AccuWeather.getInstance(sContext).getCityByLocationKey(entity.Details.CanonicalLocationKey, lang);
                    } else {
                        Log.i(TAG, " GetCityTask entity may be null.");
                    }
                } catch (AccuWeather.NetworkRequestException e) {
                    e.printStackTrace();
                    redoGetCityTask(latitude, longitude, language, locationEntityKey);
                    return null;
                } catch (Throwable e) {
                    Log.e(TAG, " GetCityTask doInBackground error : " + e.getMessage());
                }
            } else {  // 更新用户点击的城市天气
                try {
                    entity = AccuWeather.getInstance(sContext).getCityByLocationKey(locationEntityKey, language);
                } catch (AccuWeather.NetworkRequestException e) {
                    e.printStackTrace();
                    redoGetCityTask(latitude, longitude, language, locationEntityKey);
                    return null;
                }
            }
            if (mWeatherDataEntity == null) {
                mWeatherDataEntity = new WeatherDataEntity();
            }
            if (entity != null && entity.Key != null) {
                mWeatherDataEntity.mLocationEntity = entity;
                entityKey = entity.Key;
            }

            try {
                final List<CurrentConditionEntity> conditionEntityList = AccuWeather.getInstance(sContext).getCurrentCondition(entityKey, language);
                if (conditionEntityList != null && conditionEntityList.size() > 0) {
                    CurrentConditionEntity currentConditionEntity = conditionEntityList.get(0);
                    mWeatherDataEntity.mCurrentConditionEntity = currentConditionEntity;
                }
                ForecastDailyEntity forecastDailyEntity = AccuWeather.getInstance(sContext).getCurrentForecast(entityKey, language);
                mWeatherDataEntity.mForecastDailyEntity = forecastDailyEntity;
                List<ForecastHourlyEntity> forecastHourlyEntityList = AccuWeather.getInstance(sContext).getForecastHourlyEntity(entityKey);
                mWeatherDataEntity.mForecastHourlyEntityList = forecastHourlyEntityList;
            } catch (Exception e) {
                e.printStackTrace();
                redoGetCityTask(latitude, longitude, language, entityKey);
                return null;
            }

            return mWeatherDataEntity;
        }

        /***
         * AccuWeather返回的所有数据汇总
         * @param weatherDataEntity
         */
        @Override
        protected void onPostExecute(WeatherDataEntity weatherDataEntity) {
            super.onPostExecute(weatherDataEntity);
            // 天气设置页面自动定位的城市.
            if (weatherDataEntity != null && weatherDataEntity.mLocationEntity != null) {
                onGetCityDataSuccess();
                updateWeatherViewAndSave(weatherDataEntity);
            } else {
                Log.e(TAG, "get city data fail. ");
                onGetCityDataFail();
            }
            // 获取数据后，定时2小时重新获取数据
            runBackgroundTimer();

            mCurrentGetCityTask = null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }

    /**
     *  更新天气数据并写入SharedPreferences
     */
    private void updateWeatherViewAndSave(WeatherDataEntity weatherDataEntity) {
        // 需要请求的数据请求结果都不为null时继续
        if (weatherDataEntity == null || weatherDataEntity.mCurrentConditionEntity == null) {
            return;
        }

        WeatherDataManager.getInstance().saveFixedPositionData(weatherDataEntity);

        for (WeatherWidget weatherWidget : mWidgetList) {
            weatherWidget.updateWeatherView(weatherDataEntity);
        }

        // 存储数据
        WeatherPreferencesManager.getInstance(sContext).putLong(EPOCH_TIME, Long.parseLong(weatherDataEntity.mCurrentConditionEntity.EpochTime));
    }

    /**
     * 网络协议原因请求失败时，取消当前的获取AccuWeather任务并重新请求.
     */
    private void redoGetCityTask(final double latitude, final double longitude, final String language, final String locationEntityKey) {
        Log.i(TAG, "redoGetCityTask mGetCityDataFailTimes = " + String.valueOf(mGetCityDataFailTimes + 1));
        if(mCurrentGetCityTask == null){
            return;
        }
        final int detaSecond;
        mGetCityDataFailTimes++;
        if(mGetCityDataFailTimes == 1){
            detaSecond = 2;
        }
        else if(mGetCityDataFailTimes == 2){
            detaSecond = 6;
        }
        else if(mGetCityDataFailTimes == 3){
            detaSecond = 10;
        }
        else if(mGetCityDataFailTimes == 4){
            detaSecond = 20;
        }
        else{
            mGetCityDataFailTimes = 0;
            return;
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                new GetCityTask(latitude, longitude, language, locationEntityKey).execute();
            }
        }, detaSecond * 1000);

        if (mCurrentGetCityTask != null) {
            mCurrentGetCityTask.cancel(true);
            mCurrentGetCityTask = null;
        }
    }

    private Runnable mGetWeatherTimeoutRunnable = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "获取天气信息超时");
            onGetCityDataTimeout();
        }
    };

    private void onGetCityDataSuccess() {
        mGetCityDataFailTimes = 0;
        mHandler.removeCallbacks(mGetWeatherTimeoutRunnable);
    }

    private void onGetCityDataTimeout() {
        mGetCityDataFailTimes = 0;
        if (mCurrentGetCityTask != null) {
            mCurrentGetCityTask.cancel(true);
            mCurrentGetCityTask = null;
        }
        Log.i(TAG, " 获取天气的请求任务超时, 延时请求. onLocationTimeout. mGetCityDataFailTimes = " + mGetCityDataFailTimes);
        requestFailed();
    }

    private void onGetCityDataFail() {
        Log.d(TAG, "获取城市信息失败");
        mHandler.removeCallbacks(mGetWeatherTimeoutRunnable);
        requestFailed();
    }

    @Override
    public void onDataChanged(WeatherDataChangeEvent event, List<WeatherDataEntity> chgDataList) {
        if ((event == WeatherDataChangeEvent.FIXED_POSITION_CHANGE || event == WeatherDataChangeEvent.FIXED_POSITION_UPDATE)
                && chgDataList != null && chgDataList.size() > 0) {
            mWeatherDataEntity = chgDataList.get(0);
            for (WeatherWidget weatherWidget : mWidgetList) {
                weatherWidget.updateWeatherView(mWeatherDataEntity);
            }
        }
    }

    /**
     * @param weatherWidget
     * @param realDelete    默认为false，手动移除控件时为true.
     */
    public void deleteWeatherWidget(WeatherWidget weatherWidget, boolean realDelete) {
        // 处理多余的detach。remove最后一个widget后，因为执行了detach的WeatherViewManager.getInstance(sContext).deleteWeatherWidget(this, false)所以要清理掉.
        if (mWidgetList.size() == 0 && !realDelete) {
            sInstance = null;
        } else if (mWidgetList.contains(weatherWidget)) {
            mWidgetList.remove(weatherWidget);
            if (mWidgetList.size() == 0 && realDelete) {
                release();
            }
        }
    }

    /**
     * 终止所有天气请求有关的任务并清理占有资源.
     */
    public void release(){
        if (mCurrentGetCityTask != null) {
            mCurrentGetCityTask.cancel(true);
            mCurrentGetCityTask = null;
        }

        if (timer != null) {
            timer.cancel();
        }

        mWidgetIsRunning = false;
        mHandler.removeCallbacks(mGetWeatherTimeoutRunnable);

        mLocationManger.release();
        sInstance = null;

    }
}
