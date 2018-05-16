package com.tct.launcher.weather.weatherData;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import junit.framework.Assert;

/**
 * @author tyler.tang
 * @date 2016/6/30
 * @project LauncherPro
 */
public class WeatherPreferencesManager {

    /** 桌面 Preference配置文件名称 */
    public static final String DEFAULT_PREFERENCE_NAME = "com.tct.launcher.weather";
    /** preference配置对象 */
    private SharedPreferences mPreference = null;
    /** Editor */
    private Editor mEditor = null;

    private static WeatherPreferencesManager sInstance;

    public static WeatherPreferencesManager getInstance(Context context) {
        if (null == sInstance) {
            synchronized (WeatherPreferencesManager.class) {
                if (null == sInstance) {
                    sInstance = new WeatherPreferencesManager(context);
                }
            }
        }
        return sInstance;
    }

    private WeatherPreferencesManager(Context context) {
        mPreference = context.getSharedPreferences(DEFAULT_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Assert.assertNotNull(mPreference);

        mEditor = mPreference.edit();
        Assert.assertNotNull(mEditor);
    }

    public String getString(String key, String defValue) {
        return mPreference.getString(key, defValue);
    }

    public boolean putString(String key, String value) {
        mEditor.putString(key, value);
        return mEditor.commit();
    }


    public boolean getBoolean(String key, boolean defValue) {
        return mPreference.getBoolean(key, defValue);
    }

    /**
     * 存储 Boolean 类型的值到 SharedPreferences 文件里面
     *
     * @param key
     * @param value
     * @return
     */
    public boolean putBoolean(String key, boolean value) {
        mEditor.putBoolean(key, value);
        return mEditor.commit();
    }

    /**
     * 获取Int类型的值
     *
     * @param key
     * @param defValue
     * @return 是否成功
     */
    public int getInt(String key, int defValue) {
        return mPreference.getInt(key, defValue);
    }


    /**
     * 存储 Int 类型的值到 SharedPreferences 文件里面
     *
     * @param key
     * @param value
     * @return 是否成功
     */
    public boolean putInt(String key, int value) {
        mEditor.putInt(key, value);
        return mEditor.commit();
    }

    /**
     * 获取 Long 类型的值到 SharedPreferences 文件里面
     *
     * @param key
     * @param defValue
     * @return
     */
    public long getLong(String key, long defValue) {
        return mPreference.getLong(key, defValue);
    }

    /**
     * 存储 Long 类型的值到 SharedPreferences 文件里面
     *
     * @param key
     * @param value
     * @return 是否成功
     */
    public boolean putLong(String key, long value) {
        mEditor.putLong(key, value);
        return mEditor.commit();
    }

    /**
     * 清除SharedPreferences中保存的内容
     * @param key
     * @return 是否成功
     */
    public boolean removeKey(String key) {
        mEditor.remove(key);
        return mEditor.commit();
    }

}
