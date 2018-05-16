package com.sichard.weather;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sichard.weather.Interface.IWeatherDataObservable;
import com.sichard.weather.weatherData.LocationEntity;
import com.sichard.weather.weatherData.WeatherDataEntity;
import com.tct.launcher.weather.Interface.IWeatherDataObserver;
import com.tct.launcher.weather.weatherData.WeatherPreferencesManager;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:天气数据管理者
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/20</b>
 */

public class WeatherDataManager implements IWeatherDataObservable {
    /** 天气详情页面是否有数据 */
    private static final String IS_HAVE_DATA = "is_have_data";
    /** 定位城市 */
    private static final String FIXED_POSITION_CITY_ENTITY = "fixed_position_city_entity";
    private static Context sContext;
    private final String LOCATION_ENTITY_ARRAY = "location_entity_array";
    private static WeatherDataManager sInstance;
    private WeatherPreferencesManager mPreferencesManager;
    /** 天气观察者列表 */
    private List<IWeatherDataObserver> mDataObserver = new ArrayList<IWeatherDataObserver>();

    public WeatherDataManager() {
        mPreferencesManager = WeatherPreferencesManager.getInstance(sContext);
    }

    public static WeatherDataManager getInstance() {
        if (sContext == null) {
            throw new RuntimeException("init() must be called first!!!");
        }
        if (sInstance == null) {
            sInstance = new WeatherDataManager();
        }
        return sInstance;
    }

    public static void init(Context context) {
        sContext = context.getApplicationContext();
        getInstance();
    }

    /**
     * 保存定位城市的天气数据
     * @param weatherDataEntity
     */
    public void saveFixedPositionData(WeatherDataEntity weatherDataEntity) {
        if (weatherDataEntity != null) {
            // 如果没有数据直接存储，并发出FIXED_POSITION_ADD事件通知
            if (TextUtils.isEmpty(mPreferencesManager.getString(FIXED_POSITION_CITY_ENTITY, ""))) {
                if (mPreferencesManager.putString(FIXED_POSITION_CITY_ENTITY, JSON.toJSONString(weatherDataEntity))) {
                    notifyDataChanged(WeatherDataChangeEvent.FIXED_POSITION_ADD, weatherDataEntity);
                }
            } else {
                final WeatherDataEntity fixedPositionData = getFixedPositionData();
                // 如果存在数据，且两次数据的LocationEntity.Key相同则认为是同一城市，保存数据并发出FIXED_POSITION_UPDATE通知，否则发出城市改变通知FIXED_POSITION_CHANGE
                if (fixedPositionData.mLocationEntity.Key.equals(weatherDataEntity.mLocationEntity.Key)) {
                    if (mPreferencesManager.putString(FIXED_POSITION_CITY_ENTITY, JSON.toJSONString(weatherDataEntity))) {
                        notifyDataChanged(WeatherDataChangeEvent.FIXED_POSITION_UPDATE, weatherDataEntity);
                    }
                } else {
                    if (mPreferencesManager.putString(FIXED_POSITION_CITY_ENTITY, JSON.toJSONString(weatherDataEntity))) {
                        notifyDataChanged(WeatherDataChangeEvent.FIXED_POSITION_CHANGE, weatherDataEntity);
                    }
                }
            }
        }
    }

    /**
     * 获取自动定位城市的天气数据
     * @return
     */
    public WeatherDataEntity getFixedPositionData() {
        final String jsonStr = mPreferencesManager.getString(FIXED_POSITION_CITY_ENTITY, "");
        if (!TextUtils.isEmpty(jsonStr)) {
            return JSONObject.parseObject(jsonStr, WeatherDataEntity.class);
        }
        return null;
    }

    /**
     * 获取本地保存的WeatherDataEntity列表
     *
     * @return
     */
    public List<WeatherDataEntity> getSavedWeatherDataEntityList() {
        final String string = mPreferencesManager.getString(LOCATION_ENTITY_ARRAY, "");
        List<WeatherDataEntity> weatherDataEntityList = JSONArray.parseArray(string, WeatherDataEntity.class);
        if (weatherDataEntityList == null) {
            weatherDataEntityList = new ArrayList<>();
        }
        return weatherDataEntityList;
    }

    /**
     * 获取默认城市的WeatherDataEntity
     * @return WeatherDataEntity 天气实体数据
     */
    public WeatherDataEntity getDefaultWeatherDataEntity() {
        final List<WeatherDataEntity> savedWeatherDataEntityList = getSavedWeatherDataEntityList();
        if (savedWeatherDataEntityList != null && savedWeatherDataEntityList.size() > 0) {
            return savedWeatherDataEntityList.get(0);
        } else {
            return null;
        }
    }

    /**
     * 保存天气信息
     *
     * @param weatherDataEntity
     */
    public void saveWeatherDataEntity(WeatherDataEntity weatherDataEntity) {
        if (weatherDataEntity == null || weatherDataEntity.mLocationEntity == null) {
            return;
        }
//        Log.i("csc", "saveWeatherDataEntity:" + JSON.toJSONString(weatherDataEntity));
        final String string = mPreferencesManager.getString(LOCATION_ENTITY_ARRAY, "");
        JSONArray jsonArray = JSONArray.parseArray(string);
        if (jsonArray == null || jsonArray.size() == 0) {
            jsonArray = new JSONArray();
            jsonArray.add(JSON.toJSON(weatherDataEntity));
            if (mPreferencesManager.putString(LOCATION_ENTITY_ARRAY, jsonArray.toJSONString())) {
                notifyDataChanged(WeatherDataChangeEvent.ADD, weatherDataEntity);
            }
            return;
        }
        boolean isNeedReplace = false;
        int index = -1;
        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject o = jsonArray.getJSONObject(i);
            final WeatherDataEntity weatherDataEntity1 = JSON.toJavaObject(o, WeatherDataEntity.class);
            if (weatherDataEntity.mLocationEntity.Key.equals(weatherDataEntity1.mLocationEntity.Key)) {
                isNeedReplace = true;
                index = i;
            }
        }
        if (isNeedReplace) {
            jsonArray.remove(index);
            jsonArray.add(index, JSON.toJSON(weatherDataEntity));
        } else {
            jsonArray.add(JSON.toJSON(weatherDataEntity));
        }
        final boolean isSaveSuccess = mPreferencesManager.putString(LOCATION_ENTITY_ARRAY, jsonArray.toJSONString());
        if (isSaveSuccess) {
            if (isNeedReplace) {
                notifyDataChanged(WeatherDataChangeEvent.UPDATE, weatherDataEntity);
            } else {
                notifyDataChanged(WeatherDataChangeEvent.ADD, weatherDataEntity);
            }
        }
    }

    /**
     * 根据位置实体信息删除天气详情实体
     * @param locationEntity
     */
    public void removeWeatherData(LocationEntity locationEntity) {
        final String string = mPreferencesManager.getString(LOCATION_ENTITY_ARRAY, "");
        JSONArray jsonArray = JSONArray.parseArray(string);
        if (jsonArray == null || jsonArray.size() == 0) {
            return;
        }

        for (int i = 0; i < jsonArray.size(); i++) {
            final JSONObject o = jsonArray.getJSONObject(i);
            final WeatherDataEntity weatherDataEntity = JSON.toJavaObject(o, WeatherDataEntity.class);
            if (locationEntity.Key.equals(weatherDataEntity.mLocationEntity.Key)) {
                jsonArray.remove(i);
                if (mPreferencesManager.putString(LOCATION_ENTITY_ARRAY, jsonArray.toJSONString())) {
                    notifyDataChanged(WeatherDataChangeEvent.DELETE, weatherDataEntity);
                }
            }
        }
    }

    /**
     * 更新列表数据(这里是指拖动城市列表交换列表数据)
     * @param cityList
     */
    public void updateWeatherData(List<WeatherDataEntity> cityList) {
        if (cityList == null || cityList.size() <= 0) {
            return;
        }
        JSONArray jsonArray = new JSONArray();
        for(WeatherDataEntity weatherDataEntity : cityList) {
            jsonArray.add(JSON.toJSON(weatherDataEntity));
        }
        if (mPreferencesManager.putString(LOCATION_ENTITY_ARRAY, jsonArray.toJSONString())) {
            notifyDataChanged(WeatherDataChangeEvent.SWAP, null);
        }
    }

    /**
     * 获取是否有天气数据
     * @return
     */
    public boolean isHaveData() {
        final String stringCityArray = mPreferencesManager.getString(LOCATION_ENTITY_ARRAY, "");
        final String stringFixedPositionCity = mPreferencesManager.getString(FIXED_POSITION_CITY_ENTITY, "");
        return !(TextUtils.isEmpty(stringCityArray) && TextUtils.isEmpty(stringFixedPositionCity));
    }

    @Override
    public void register(IWeatherDataObserver observer) {
        if (null == observer) {
            return;
        }
        if (mDataObserver.contains(observer)) {
            return;
        }
        mDataObserver.add(observer);
    }

    @Override
    public void unregister(IWeatherDataObserver observer) {
        if (null == observer) {
            return;
        }
        mDataObserver.remove(observer);
    }

    @Override
    public void notifyDataChanged(WeatherDataChangeEvent event, WeatherDataEntity weatherDataEntity) {
        List<WeatherDataEntity> weatherDataEntityList = new ArrayList<>();
        if (event == WeatherDataChangeEvent.SWAP) {
            weatherDataEntityList = getSavedWeatherDataEntityList();
        } else {
            weatherDataEntityList.add(weatherDataEntity);
        }
        for (IWeatherDataObserver observer : mDataObserver) {
            observer.onDataChanged(event, weatherDataEntityList);
        }
    }
}
