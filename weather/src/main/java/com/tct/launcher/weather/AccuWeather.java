package com.tct.launcher.weather;

import android.content.Context;
import android.os.NetworkOnMainThreadException;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.tct.launcher.weather.weatherData.CurrentConditionEntity;
import com.tct.launcher.weather.weatherData.ForecastDailyEntity;
import com.tct.launcher.weather.weatherData.ForecastHourlyEntity;
import com.tct.launcher.weather.weatherData.LocationEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by jinhui.shao on 2016/7/28.
 * 获取AccuWeather数据的常用方法
 */
public class AccuWeather {
    private static final String TAG = "AccuWeather";
    private static final String ACCUWEATHER_APIKEY = "af7408e9f4d34fa6a411dd92028d4630";
    private static final String SEARCH_CITY_BASE_URL = "http://api.accuweather.com/locations/v1/cities/search.json?";
    private static final String LOCATION_BASE_URL = "http://api.accuweather.com/locations/v1/cities/geoposition/search.json?";
    private static final String LOCATIONKEY_SEARCH_BASE_URL = "http://api.accuweather.com/locations/v1/";
    private static final String CURRENTCONDITION_BASE_URL = "http://api.accuweather.com/currentconditions/v1/";
    private static final String FORECAST_DAILY_BASE_URL = "http://api.accuweather.com/forecasts/v1/daily/5day/";
    private static final String FORECAST_HOURLY_BASE_URL = "http://api.accuweather.com/forecasts/v1/hourly/24hour/";
    private static final int LANGUAGE_ERROR = 400;
    private Context mContext;
    private String mFinalRequestLanguage;
    private static AccuWeather accuWeather;

    public static AccuWeather getInstance(Context context) {
        if (accuWeather == null) {
            accuWeather = new AccuWeather(context);
        }
        return accuWeather;
    }

    public AccuWeather(Context context) {
        mContext = context;
    }

    /**
     * 手动搜索城市。注意！！如果指定了language，搜索语言只能是该language，结果也是显示该language。如果没有指定language，搜索语言支持多语言搜索，但是结果只能显示英文
     *
     * @param query 搜索的字符串，支持多语言
     * @param lang
     * @return
     * @throws NetworkRequestException
     */

    public List<LocationEntity> getCityList(String query, String lang) throws NetworkRequestException {
        // http://api.accuweather.com/locations/v1/cities/search.json?q=gu&apikey=af7408e9f4d34fa6a411dd92028d4630
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();
        paramsMap.put("q", query);
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
//        paramsMap.put("alias", "always"); // 模糊搜索，但不清楚如何去重.
//        paramsMap.put("details", "true");
        paramsMap.put("language", language);

        List<LocationEntity> list = null;
        try {
            list = requestForJSONList(produceUrl(SEARCH_CITY_BASE_URL, paramsMap), LocationEntity.class);
        } catch (TaskException e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * 根据经纬度获取LocationEntity
     * <br><b>注意：此处返回的地区精确度很高，会精确到某市的某个区。比如：南山区
     * @param latitude
     * @param longitude
     * @param lang
     * @return
     * @throws NetworkRequestException
     */
    public LocationEntity getCityByCoordinate(double latitude, double longitude, String lang) throws NetworkRequestException {
        // http://api.accuweather.com/locations/v1/cities/geoposition/search.json?q=22.54,114.07&apikey=af7408e9f4d34fa6a411dd92028d4630&language=ZH
        // http://api.accuweather.com/locations/v1/cities/geoposition/search.json?q=22.56,113.91&apikey=af7408e9f4d34fa6a411dd92028d4630
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();

        // paramsMap.put("q", Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", latitude)) + "," + Double.parseDouble(String.format(Locale.ENGLISH, "%.2f", longitude)));
        paramsMap.put("q", latitude + "," + longitude);
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
        paramsMap.put("details", "true");
        paramsMap.put("language", language);

        LocationEntity entity = null;
        mFinalRequestLanguage = "";
        try {
            entity = requestForJSON(produceUrl(LOCATION_BASE_URL, paramsMap), LocationEntity.class);
        } catch (TaskException e) {
            if (String.valueOf(LANGUAGE_ERROR).equals(e.getCode())) { //取locale中的语言再次请求
                try {
                    entity = requestForJSON(produceUrl(LOCATION_BASE_URL, checkUnsupportedLanguage(paramsMap, false)), LocationEntity.class);
                    if (entity != null) {
                        mFinalRequestLanguage = (String) checkUnsupportedLanguage(paramsMap, false).get("language");
                    }
                } catch (TaskException e1) {
                    if (String.valueOf(LANGUAGE_ERROR).equals(e1.getCode())) { //用默认的英语再请求
                        try {
                            entity = requestForJSON(produceUrl(LOCATION_BASE_URL, checkUnsupportedLanguage(paramsMap, true)), LocationEntity.class);
                            if (entity != null) {
                                mFinalRequestLanguage = "en";
                            }
                        } catch (TaskException e2) {
                            e2.printStackTrace();
                        }
                    }
                }
            }
        }
        return entity;
        // waitForWeather();
//      SimplePropertyPreFilter filter = new SimplePropertyPreFilter(Student.class, "id","age");
//      String jsonStu =JSON.toJSONString(students,filter);
    }

//    private String weatherBody;
//
//    private synchronized void waitForWeather() {
//        try {
//            wait();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private synchronized void notifyForWeather() {
//        notifyAll();
//    }


    public LocationEntity getCityByLocationKey(String locationKey, String lang) throws NetworkRequestException {
        // http://api.accuweather.com/locations/v1/58194.json?apikey=af7408e9f4d34fa6a411dd92028d4630
        String baseUrl = LOCATIONKEY_SEARCH_BASE_URL + locationKey + ".json?";
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
        paramsMap.put("language", language);

        LocationEntity entity = null;
        try {
            if (!TextUtils.isEmpty(mFinalRequestLanguage)) {
                paramsMap.put("language", mFinalRequestLanguage);
            }
            entity = requestForJSON(produceUrl(baseUrl, paramsMap), LocationEntity.class);
        } catch (TaskException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * 获取天气的现状。tips：因为返回的现状是以数组形式返回的，所以是List<CurrentConditionEntity>.
     *
     * @param locationKey
     * @param lang
     * @return
     */
    public List<CurrentConditionEntity> getCurrentCondition(String locationKey, String lang) throws NetworkRequestException {
        // http://api.accuweather.com/currentconditions/v1/58194.json?apikey=af7408e9f4d34fa6a411dd92028d4630
        String baseUrl = CURRENTCONDITION_BASE_URL + locationKey + ".json?";
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
        paramsMap.put("language", language);
        paramsMap.put("details", true);

        List<CurrentConditionEntity> list = null;
        try {
            if (!TextUtils.isEmpty(mFinalRequestLanguage)) {
                paramsMap.put("language", mFinalRequestLanguage);
            }
            list = requestForJSONList(produceUrl(baseUrl, paramsMap), CurrentConditionEntity.class);
        } catch (TaskException e) {
            e.printStackTrace();
        }

        return list;
    }

    public ForecastDailyEntity getCurrentForecast(String locationKey, String lang) throws NetworkRequestException {
        // http://api.accuweather.com/forecasts/v1/daily/1day/58341?apikey=af7408e9f4d34fa6a411dd92028d4630
        // http://api.accuweather.com/forecasts/v1/daily/5day/335315?apikey={your key}
        Log.d("wxj", "AccuWeather : getCurrentForecast: ");
        String baseUrl = FORECAST_DAILY_BASE_URL + locationKey + "?";
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
        paramsMap.put("language", language);

        ForecastDailyEntity entity = null;
        try {
            if (!TextUtils.isEmpty(mFinalRequestLanguage)) {
                paramsMap.put("language", mFinalRequestLanguage);
            }
            entity = requestForJSON(produceUrl(baseUrl, paramsMap), ForecastDailyEntity.class);
        } catch (TaskException e) {
            e.printStackTrace();
        }

        return entity;
    }

    public List<ForecastHourlyEntity> getForecastHourlyEntity(String locationKey) throws NetworkRequestException {
        String lang = mContext.getResources().getConfiguration().locale.toString();
        String baseUrl = FORECAST_HOURLY_BASE_URL + locationKey + ".json?";
        String language = handleLanguage(lang);
        Map paramsMap = new HashMap<String, String>();
        paramsMap.put("apikey", ACCUWEATHER_APIKEY);
        paramsMap.put("language", language);

        List<ForecastHourlyEntity> forecastHourlyEntityList = null;
        try {
            if (!TextUtils.isEmpty(mFinalRequestLanguage)) {
                paramsMap.put("language", mFinalRequestLanguage);
            }
            forecastHourlyEntityList = requestForJSONList(produceUrl(baseUrl, paramsMap), ForecastHourlyEntity.class);
        } catch (TaskException e) {
            e.printStackTrace();
        }
        return forecastHourlyEntityList;
    }

    private String produceUrl(String baseUrl, Map paramsMap) {
        if (!paramsMap.isEmpty()) {
            Iterator iterator = paramsMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                baseUrl += entry.getKey() + "=" + entry.getValue();
                if (iterator.hasNext()) {
                    baseUrl += "&";
                }
            }
        }
        Log.i("sichard", "final url = " + baseUrl);
        return baseUrl;
    }

    private String handleLanguage(String language) {
        if (TextUtils.isEmpty(language)) {
            language = "EN"; // 默认为EN
        } else {
            // 系统语言格式为：language_coutry[zh_CN_#Hans]，accweather格式为zh-cn
            if (language.length() > 5) {
                language = language.substring(0, 5);
            }
            language = language.toLowerCase().replace("_", "-");
        }

        return language;
    }

    /**
     * 处理不支持的语言，有些只支持部分，例如 支持da不支持da-dk
     *
     * @param map
     * @param forceEnglish map中的语言强制设置为默认的英语
     * @return
     */
    private Map<String, String> checkUnsupportedLanguage(Map<String, String> map, boolean forceEnglish) {
        if (forceEnglish) {
            map.put("language", "en");
            return map;
        }
        String language = map.get("language");
        if (language != null && language.contains("-")) {
            language = language.substring(0, language.indexOf("-"));
        } else if (language != null && language.contains("_")) {
            language = language.substring(0, language.indexOf("_"));
        } else {
            language = "en";
        }
        map.put("language", language);
        return map;
    }

    /**
     * @param url
     * @param t
     * @param <T>
     * @return 返回向AccuWeather请求获得的数据实体
     */
    private <T> T requestForJSON(String url, Class<T> t) throws TaskException, NetworkRequestException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        T entity = null;
        try {
            Response response = client.newCall(request).execute();
            if (response != null & response.isSuccessful()) {
                String json = response.body().string();
                entity = (T) JSON.parseObject(json, t);
                if (entity != null) {
                    Log.w("sjh5", "entity = " + entity.toString());
                    return entity;
                } else {
                    Log.e("sjh5", "entity null ");
                }

            } else {
                if (response != null) {
                    Log.e("sichard", "AccuWeather|requestForJSON:" + "failed:" + response.body().string());
                    if (response.code() == LANGUAGE_ERROR) {
                        Log.e("sjh5", "response error code 400 and throw Exception.");
                        throw new TaskException(String.valueOf(LANGUAGE_ERROR));
                    }
                } else {
                    Log.e("sjh5", "向AccuWeather请求数据失败  response null ");
                }
            }
        } catch (IOException e) {
            Log.e("sjh5", "IOException (map be no Internet) (requestForJSON) : " + e.getMessage());
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("thread interrupted")) {
                return null;
            }
            throw new NetworkRequestException("100", e.getMessage());
        } catch (NetworkOnMainThreadException e) {
            Log.e("sjh5", "NetworkOnMainThreadException : " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        return entity;
    }

    /**
     * @param url
     * @param t
     * @param <T>
     * @return 返回向AccuWeather请求获得的数据实体List
     */
    private <T> List<T> requestForJSONList(String url, Class<T> t) throws TaskException, NetworkRequestException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        List<T> list = null;
        try {
            Response response = client.newCall(request).execute();
            if (response != null & response.isSuccessful()) {
                String json = response.body().string();
                if ("CurrentConditionEntity".equals(t.getSimpleName())) {
//                    Log.i("sichard", "AccuWeather|requestForJSONList:" + json);
                    list = JSON.parseArray(json, t);
                    if (list != null) {
                        Log.w("sjh5", "entity = " + Arrays.toString(list.toArray()));
                    } else {
                        Log.e("sjh5", "list is null ");
                    }
                } else if ("LocationEntity".equals(t.getSimpleName())) {
                    list = JSON.parseArray(json, t);
                    if (list != null) {
                        Log.w("sjh5", "entity = " + Arrays.toString(list.toArray()));
                    } else {
                        Log.e("sjh5", "list is null ");
                    }
                } else if ("ForecastHourlyEntity".equals(t.getSimpleName())) {
                    list = JSON.parseArray(json, t);
                    if (list != null) {
                        Log.w("sjh5", "entity = " + Arrays.toString(list.toArray()));
                    } else {
                        Log.e("sjh5", "list is null ");
                    }
                }

            } else {
                if (response != null) {
                    if (response.code() == LANGUAGE_ERROR) {
                        throw new TaskException(String.valueOf(LANGUAGE_ERROR));
                    }
                } else {
                    Log.e("sjh5", "向AccuWeather请求list数据失败  response null ");
                }
            }
        } catch (IOException e) {
            Log.e("sjh5", "IOException (map be no Internet) (requestForJSONList) : " + e.getMessage());
            if (!TextUtils.isEmpty(e.getMessage()) && e.getMessage().contains("thread interrupted")) {
                return null;
            }
            throw new NetworkRequestException("100", e.getMessage());
        } catch (NetworkOnMainThreadException e) {
            Log.e("sjh5", "NetworkOnMainThreadException : " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

        return list;
//        client.newCall(request).enqueue(new Callback() {
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                if (response != null & response.isSuccessful()) {
//                    try {
//                        json[0] =  response.body().string();
//                        Log.i("sjh5", "json = " + json[0] + "  " + response.toString());
//
//                        if("CurrentConditionEntity".equals(t.getSimpleName())){
//                           List<T> list = JSON.parseArray(json[0], t);
//                            if(list != null){
//                                Log.w("sjh5", "entity = " + Arrays.toString(list.toArray()) );
//                            }
//                            else{
//                                Log.e("sjh5", "list is null " );
//                            }
//                        }
//                        else{
//                            T entity = (T)JSON.parseObject(json[0], t);
//                            if(entity != null){
//                                Log.w("sjh5", "entity = " + entity.toString()  );
//                            }
//                            else{
//                                Log.e("sjh5", "entity null " );
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e("sjh5", "reponse transfer error : " + e.getMessage());
//                    } finally {
//                        weatherBody = null;
//                        notifyForWeather();
//                    }
//                }
//                else{
//                    Log.e("sjh5", "response not successful. " + response.toString());
//                    weatherBody = null;
//                    notifyForWeather();
//                }
//            }
//
//            @Override
//            public void onFailure(Call call, IOException e) {
//                Log.e("sjh5", "onFailure : " + e.getMessage());
//            }
//
//
//        });    //.execute();


    }


    class TaskException extends Exception {
        private String code;
        private String msg;

        public TaskException(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public TaskException(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    /**
     * 处理网络请求偶尔会失败，出现如下错误，过段时间再请求就好了.
     * com.tcl.launcherpro E/sjh5: IOException (map be no Internet) : Unable to resolve host "api.accuweather.com": No address associated with hostname
     * com.tcl.launcherpro E/sjh5: IOException (map be no Internet) (requestForJSON) : failed to connect to api.accuweather.com/23.3.123.250 (port 80) after 10000ms
     */
    public static class NetworkRequestException extends Exception {
        private String code;
        private String msg;

        public NetworkRequestException(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public NetworkRequestException(String code) {
            this.code = code;
        }

        public String getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

}
