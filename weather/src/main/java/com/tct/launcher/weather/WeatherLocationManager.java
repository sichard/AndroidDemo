package com.tct.launcher.weather;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.tct.launcher.weather.Interface.OnLocationChangedListener;
import com.tct.launcher.weather.utils.AESUtil;
import com.tct.launcher.weather.utils.DeviceUtil;
import com.tct.launcher.weather.weatherData.ServerCoordinateEntity;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * <br>类描述:天气定位功能
 * <br>详细描述:该类用来返回定位的经纬度
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/31</b>
 */

public class WeatherLocationManager {
    private String TAG = "csc";
    private static Context mContext;
    private static WeatherLocationManager sInstance;
    /**
     * 定位超时时间
     */
    private static final int LOCATION_TIMEOUT = 1 * 80 * 1000;
    /**
     * GPS定位超时时间
     */
    private static final int GET_LOCATION_DELAYED = 25 * 1000;

    private LocationManager mLocationManager;
    private Handler mHandler;
    private Status mStatus = Status.init;
    /**
     * 当前ip定位的task
     */
    private GetCoordinateByServerTask mCurrentGetCoordinateByServerTask;
    private CopyOnWriteArrayList<OnLocationChangedListener> mOnLocationChangedListenerList = new CopyOnWriteArrayList<>();

    /**
     * 失败达到最高次数or成功则清零
     */
    private int mGetCoordinateFailTimes = 0;
    /** 此处指的是隐私协议中用户是否允许获取位置信息 */
    private boolean mHaveLocationPermission = true;

    /**
     * 确保调用init()方法后，再调用该方法。{@link #init(Context)}
     * @return
     */
    public static WeatherLocationManager getInstance() {
        if (mContext == null) {
            throw new RuntimeException("init() must be called first!!!");
        }
        if (sInstance == null) {
            sInstance = new WeatherLocationManager();
        }
        return sInstance;
    }

    private WeatherLocationManager() {
        mHandler = new Handler(Looper.getMainLooper());
        mLocationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
    }

    public void addOnLocationChangedListener(OnLocationChangedListener onLocationChangedListener) {
        if (onLocationChangedListener != null) {
            mOnLocationChangedListenerList.add(onLocationChangedListener);
        }
    }

    public void removeOnLocationListener(OnLocationChangedListener onLocationChangedListener) {
        if (onLocationChangedListener != null) {
            mOnLocationChangedListenerList.remove(onLocationChangedListener);
        }
    }

    public static void init(Context context) {
        mContext = context.getApplicationContext();
        getInstance();
    }

    private enum Status {
        locating, // 正在定位
        init// 初始状态
    }

    /**
     * 定位超时的runnable
     */
    private Runnable locationTimeoutRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "定位超时");
            stopLocation();
            synchronized (mOnLocationChangedListenerList) {
                for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                    listener.onLocationTimeout();
                }
            }
        }
    };


    /**
     * IP定位
     */
    private Runnable queryForCoordinateRunnable = new Runnable() {

        @Override
        public void run() {
            Log.d(TAG, "开始服务器IP辅助定位");
            new GetCoordinateByServerTask().execute();
        }
    };

    /**
     * ip辅助定位的task
     */
    private class GetCoordinateByServerTask extends AsyncTask<Void, Void, ServerCoordinateEntity> {

        public GetCoordinateByServerTask() {
            if (mCurrentGetCoordinateByServerTask != null) {
                mCurrentGetCoordinateByServerTask.cancel(true);
            }
            mCurrentGetCoordinateByServerTask = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!DeviceUtil.isNetworkOK(mContext)) {
                Log.d(TAG, "无可用网络");
                synchronized (mOnLocationChangedListenerList) {
                    for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                        listener.onLocationFail();
                    }
                }
                if (mCurrentGetCoordinateByServerTask != null) {
                    mCurrentGetCoordinateByServerTask.cancel(true);
                    mCurrentGetCoordinateByServerTask = null;
                }
            }
        }

        @Override
        protected ServerCoordinateEntity doInBackground(Void... voids) {
            if (mCurrentGetCoordinateByServerTask == null) {
                return null;
            }
            ServerCoordinateEntity entity = null;
            try {
                entity = requestForCoordinateJSON(BuildConfig.COORDINATE_URL, ServerCoordinateEntity.class);
            } catch (AccuWeather.NetworkRequestException e) {
                e.printStackTrace();
                redoGetCoordinateTask();
                return null;
            }
            return entity;
        }

        @Override
        protected void onPostExecute(ServerCoordinateEntity serverCoordinateEntity) {
            super.onPostExecute(serverCoordinateEntity);
            if (serverCoordinateEntity != null && serverCoordinateEntity.getStatus() == 0) {
                // 解密服务端返回的数据
                byte[] base64Byte = AESUtil.decodeBase64(serverCoordinateEntity.getData());
                byte[] decryptByte = new byte[0];
                try {
                    decryptByte = AESUtil.decrypt2(base64Byte, AESUtil.AES_DECRYPT_KEY);
                    String decryptStr = new String(decryptByte);
                    ServerCoordinateEntity.DataEntity dataEntity = JSON.parseObject(decryptStr, ServerCoordinateEntity.DataEntity.class);
                    serverCoordinateEntity.setDataEntity(dataEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "服务器辅助IP定位的经纬度获取并解析成功 : " + serverCoordinateEntity.toString());
                synchronized (mOnLocationChangedListenerList) {
                    for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                        listener.onLocationSuccess(serverCoordinateEntity.getDataEntity().latitude, serverCoordinateEntity.getDataEntity().longitude);
                    }
                }
                stopLocation();
            } else {
                synchronized (mOnLocationChangedListenerList) {
                    for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                        listener.onLocationFail();
                    }
                }
                Log.i(TAG, "serverCoordinateEntity null or request fail.(maybe no Internet) ");
            }

            mCurrentGetCoordinateByServerTask = null;
        }
    }

    /**
     * 网络协议请求偶尔失败时，取消当前的获取服务器任务并重新请求.（暂不算失败，不回调请求失败的代码）
     * tips:超出60s后运行的 queryForCoordinateRunnable 会被取消.
     */
    private void redoGetCoordinateTask() {
        if (mCurrentGetCoordinateByServerTask == null) {
            return;
        }
        Log.i(TAG, "redoGetCoordinateTask mGetCoordinateFailTimes = " + String.valueOf(mGetCoordinateFailTimes + 1));
        int detaSecond;
        mGetCoordinateFailTimes++;
        if (mGetCoordinateFailTimes == 1) {
            detaSecond = 1;
        } else if (mGetCoordinateFailTimes == 2) {
            detaSecond = 5;
        } else if (mGetCoordinateFailTimes == 3) {
            detaSecond = 15;
        } else if (mGetCoordinateFailTimes == 4) {
            detaSecond = 30;
        } else {
            mGetCoordinateFailTimes = 0;
            return;
        }

        mHandler.removeCallbacks(queryForCoordinateRunnable);
        mHandler.postDelayed(queryForCoordinateRunnable, detaSecond * 1000);

        if (mCurrentGetCoordinateByServerTask != null) {
            mCurrentGetCoordinateByServerTask.cancel(true);
            mCurrentGetCoordinateByServerTask = null;
        }
    }


    /**
     * 通过网络来返回定位信息
     *
     * @param url
     * @param t
     * @param <T>
     * @return
     * @throws AccuWeather.NetworkRequestException
     */
    private <T> T requestForCoordinateJSON(String url, Class<T> t) throws AccuWeather.NetworkRequestException {
        FormBody.Builder builder = new FormBody.Builder();
        //添加参数信息
        //builder.add(BaseParamKey.IMEI, configDataProvider.getIMEI());
        //builder.add(BaseParamKey.IMIS, configDataProvider.getIMSI());
        String versionName = "1.0";
        try {
             versionName = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        String network = networkInfo != null ? networkInfo.getTypeName().toUpperCase() : "UNKNOW";

        builder.add(BaseParamKey.LANGUAGE, mContext.getResources().getConfiguration().locale.toString());
        builder.add(BaseParamKey.MODEL, Build.MODEL);
        builder.add(BaseParamKey.OS_VERSION, Build.VERSION.RELEASE);
        builder.add(BaseParamKey.OS_VERSION_CODE, String.valueOf(Build.VERSION.SDK_INT));
        builder.add(BaseParamKey.SCREEN_SIZE, "1920#1080");
        builder.add(BaseParamKey.VERSION_NAME, versionName);
        builder.add(BaseParamKey.USER_INFO_ID, "");
        builder.add(BaseParamKey.REGION, "");
        builder.add(BaseParamKey.NETWORK, network);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).post(builder.build()).build();
        T entity = null;
        try {
            Response response = client.newCall(request).execute();
            if (response != null & response.isSuccessful()) {
                String json = response.body().string();
                entity = (T) JSON.parseObject(json, t);
                if (entity != null) {
                    Log.w("sjh5", " entity(Coordinate) = " + entity.toString());
                    return entity;
                } else {
                    Log.e("sjh5", "entity null ");
                }
            }
        } catch (IOException e) {
            Log.e("sjh5", "IOException (map be no Internet) (request For Coordinate) : " + e.toString());
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("thread interrupted")) {
                return null;
            }
            throw new AccuWeather.NetworkRequestException("100", e.getMessage());
        } catch (NetworkOnMainThreadException e) {
            Log.e("sjh5", "NetworkOnMainThreadException : " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 取消定位或定位成功后，清理相关runnable，listener，AsyncTask.
     */
    public void stopLocation() {
        mStatus = WeatherLocationManager.Status.init;
        if (mCurrentGetCoordinateByServerTask != null) {
            mCurrentGetCoordinateByServerTask.cancel(true);
            mCurrentGetCoordinateByServerTask = null;
        }
        mHandler.removeCallbacks(queryForCoordinateRunnable);
        mHandler.removeCallbacks(locationTimeoutRunnable);
        removeLocationUpdateListener();
    }

    /**
     * 移除系统定位监听
     */
    private void removeLocationUpdateListener() {
        Log.d(TAG, "removeLocationUpdateListener");
        try {
            mLocationManager.removeUpdates(mLocationListener);
        } catch (SecurityException e) {
            Log.e(TAG, "removeLocationUpdateListener error : " + e.getMessage());
        }
    }

    /**
     * 发起一次定位,如果已经在定位了，就再次设置timeout时间为{@link #LOCATION_TIMEOUT}
     */
    public void requestLocation() {
        if (!haveLocationPermission()) {
            return;
        }
        if (mStatus == Status.locating) return;
        Log.d(TAG, "开始定位");

        // 超时就取消定位
        mHandler.removeCallbacks(locationTimeoutRunnable);
        mHandler.postDelayed(locationTimeoutRunnable, LOCATION_TIMEOUT);

        // 30s内不能得到GPS定位结果就启动服务器IP辅助定位
        mHandler.removeCallbacks(queryForCoordinateRunnable);
        mHandler.postDelayed(queryForCoordinateRunnable, GET_LOCATION_DELAYED);

        if (mStatus == Status.locating) {
            return;
        }
        mStatus = Status.locating;

        mHandler.post(new Runnable() {
            @Override
            public void run() {
                synchronized (mOnLocationChangedListenerList) {
                    for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                        listener.onLocationStart();
                    }
                }

                try {
                    if (DeviceUtil.isNetworkOK(mContext)) {
                        if (mLocationManager.isProviderEnabled("fused")) {
                            mLocationManager.requestLocationUpdates("fused", 0, 1000 * 5, mLocationListener);
                            Log.d(TAG, "  FUSED_PROVIDER");
                        } else {
                            // 开启了wifi精度的GPS就可用，不管是否有网络
                            if (mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 1000 * 5, mLocationListener);
                                Log.d(TAG, "  NETWORK_PROVIDER");
                            }
                            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 1000 * 5, mLocationListener);
                                Log.d(TAG, "  GPS_PROVIDER");
                            }
                            if (!mLocationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                                    && !mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                                mHandler.removeCallbacks(queryForCoordinateRunnable);       //立即使用服务器IP辅助定位
                                mHandler.post(queryForCoordinateRunnable);
                            }
                        }
                    } else {
                        mHandler.removeCallbacks(queryForCoordinateRunnable);       //立即使用服务器IP辅助定位
                        mHandler.post(queryForCoordinateRunnable);
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "SecurityException requestLocationUpdates error : " + e.getMessage());
                    mHandler.removeCallbacks(queryForCoordinateRunnable);       //立即使用服务器IP辅助定位
                    mHandler.post(queryForCoordinateRunnable);
                }
            }
        });
    }

    /**
     * 系统Gps定位监听
     */
    LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.i(TAG, "GPS定位成功." + " latitude = " + location.getLatitude()
                        + " longitude = " + location.getLongitude());
                synchronized (mOnLocationChangedListenerList) {
                    for (OnLocationChangedListener listener : mOnLocationChangedListenerList) {
                        listener.onLocationSuccess(location.getLatitude(), location.getLongitude());
                    }
                }
            } else {
                Log.w(TAG, "location null ");
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.w(TAG, "onStatusChanged");  //Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.w(TAG, "onProviderEnabled");
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.w(TAG, "onProviderDisabled");
        }

    };

    public boolean haveLocationPermission() {
        return mHaveLocationPermission;
    }

    /**
     * 释放定位相关资源
     */
    public void release() {
        if (mCurrentGetCoordinateByServerTask != null) {
            mCurrentGetCoordinateByServerTask.cancel(true);
            mCurrentGetCoordinateByServerTask = null;
        }

        mHandler.removeCallbacks(queryForCoordinateRunnable);
        mHandler.removeCallbacks(locationTimeoutRunnable);

        removeLocationUpdateListener();
        sInstance = null;
    }

    public static class BaseParamKey {
        public static final String IMEI = "imei";
        public static final String USER_INFO_ID = "user_info_id";
        public static final String REGION = "region";
        public static final String VERSION_NAME = "version_name";
        public static final String NETWORK = "network";
        public static final String SCREEN_SIZE = "screen_size";
        public static final String LANGUAGE = "language";
        public static final String OS_VERSION = "os_version";
        public static final String IMIS = "imsi";
        public static final String OS_VERSION_CODE = "os_version_code";
        public static final String MODEL = "model";

        public BaseParamKey() {
        }
    }
}
