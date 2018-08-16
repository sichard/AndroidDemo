package com.sichard.weather;

import android.content.Context;

/**
 * <br> 天气工具类
 * Created by jinhui.shao on 2016/8/3.
 */
public class WeatherUtils {
    public static final String C_TEMP = "17";   //摄氏度
    public static final String F_TEMP = "18";   //华氏度
    public static final int C_TEMP_INT = 17;   //摄氏度
    public static final int F_TEMP_INT = 18;   //华氏度

    /**
     * 获取详情页面的天气图标
     * @param context
     * @param icon
     * @return
     */
    public static int getWeatherDetailIconId(Context context, String icon) {
        try {
            int resId = context.getResources().getIdentifier("weather_detail_icon_" + icon, "drawable", context.getPackageName());
            if(resId == 0){
                return R.drawable.weather_icon_7;
            }
            else{
                return resId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.weather_icon_7;
        }
    }

    /**
     * 获取桌面widget的天气图标
     * @param context
     * @param icon
     * @return
     */
    public static int getWeatherIconId(Context context, String icon) {
        try {
            int resId = context.getResources().getIdentifier("weather_icon_" + icon, "drawable", context.getPackageName());
            if(resId == 0){
                return R.drawable.weather_icon_7;
            }
            else{
                return resId;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return R.drawable.weather_icon_7;
        }
    }

    /**
     * 因为天气源来的温度数据有小数点，过滤小数点之后的数据.
     * @param tempValue 摄氏度or华氏度的值
     * @param unitType 天气的单位
     * @return 摄氏度（带单位）
     */
    public static String getCurrentCTemp(String tempValue, String unitType){
        try {
            int t;
            float cTemp;
            float temp = Float.parseFloat(tempValue);
            if(C_TEMP.equals(unitType)){
                t = Math.round(temp);
            }
            else if(F_TEMP.equals(unitType)){
                cTemp = ftempToCtemp(temp);
                t = Math.round(cTemp);
            }
            else{
                return tempValue;
            }
            return t + "°";
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return tempValue;
    }

    /**
     * 因为天气源来的温度数据有小数点，过滤小数点之后的数据.
     * @param value 摄氏度or华氏度的值
     * @param unitType 天气的单位
     * @return 摄氏度（带单位）
     */
    public static int getCurrentCTemp(float value, int unitType) {
        try {
            int t;
            float cTemp;
            float temp = value;
            if (C_TEMP_INT == unitType) {
                t = Math.round(temp);
            } else if (F_TEMP_INT == unitType) {
                cTemp = ftempToCtemp(temp);
                t = Math.round(cTemp);
            } else {
                return (int) value;
            }
            return t;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return (int) value;
    }

    /**
     * 摄氏度(°C) = ((华氏度°F) - 32) * 5 / 9
     * @param fTemp 华氏度
     * @return
     */
    public static float ftempToCtemp(float fTemp) {
        float cTemp = (fTemp - 32) * 5 / 9;
        return cTemp;
    }

    /**
     * 华氏度°F = ((摄氏度(°C)) * 9) / 5 + 32
     * @param ctemp 摄氏度
     * @return
     */
    public static float ctempToFtemp(float ctemp) {
        return (ctemp * 9) / 5 + 32;
    }
}
