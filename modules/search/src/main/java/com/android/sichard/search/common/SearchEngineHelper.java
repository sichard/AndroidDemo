package com.android.sichard.search.common;

import android.content.Context;

import com.android.sichard.search.R;
import com.android.sichard.search.data.PreferencesManager;
import com.android.sichard.search.utils.DeviceUtil;

/**
 * <br>类表述:搜索引擎辅助类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date   2016-8-17</b>
 */
public class SearchEngineHelper {

    /** 是否显示搜索app结果 */
    public static final String KEY_IS_SHOW_APP = "key_is_show_app";
    /** 是否显示搜索联系人结果 */
    public static final String KEY_IS_SHOW_CONTACT = "key_is_show_contact";
    /** 是否显示搜索短信结果 */
    public static final String KEY_IS_SHOW_MESSAGE = "key_is_show_message";
    /** 搜索引擎url */
    public static final String KEY_SEARCH_ENGINE_URL = "key_search_engine_url";
    /** 搜索引擎改变的Action */
    public static final String ACTION_ENGINE_CHANGED = "com.android.sichard.search.action.SEARCH_ENGINE_CHANGED";
    /** 搜索结果是否显示App的Action */
    public static final String ACTION_APP_CHANGED = "com.android.sichard.search.action.SEARCH_APP_CHANGED";
    /** 搜索结果是否显示Contact的Action */
    public static final String ACTION_CONTACT_CHANGED = "com.android.sichard.search.action.SEARCH_CONTACT_CHANGED";
    /** 搜索结果是否显示Message的Action */
    public static final String ACTION_MESSAGE_CHANGED = "com.android.sichard.search.action.SEARCH_MESSAGE_CHANGED";
    /** 谷歌地址 */
    public static final String SEARCH_ENGINE_GOOGLE_URL = "http://www.google.com/search?q=";
    /** 百度地址 */
    public static final String SEARCH_ENGINE_BAI_DU_URL = "http://www.baidu.com/s?wd=";
    /** 必应地址 */
    public static final String SEARCH_ENGINE_BING_URL = "http://www.bing.com/search?q=";
    /** Yandex地址 */
    public static final String SEARCH_ENGINE_YANDEX_URL = "http://www.yandex.com/search?text=";

    public static int getEngineImg(Context mContext) {
        String defaultURL = SEARCH_ENGINE_GOOGLE_URL;
        String local = DeviceUtil.getLocal(mContext);
        if ("cn".equals(local)) {
            defaultURL = SEARCH_ENGINE_BAI_DU_URL;
        } else if ("ru".equals(local)) {
            defaultURL = SEARCH_ENGINE_YANDEX_URL;
        }
        String engine = PreferencesManager.getInstance(mContext).getString(KEY_SEARCH_ENGINE_URL, defaultURL);
        switch (engine) {
            case SEARCH_ENGINE_GOOGLE_URL:
                return R.drawable.ic_search_google;
            case SEARCH_ENGINE_BAI_DU_URL:
                return R.drawable.ic_broswer_baidu;
            case SEARCH_ENGINE_BING_URL:
                return R.drawable.ic_broswer_bing;
            case SEARCH_ENGINE_YANDEX_URL:
                return R.drawable.ic_search_yandex;
        }

        return R.drawable.ic_search_google;
    }

    public static String getEngine(Context mContext) {
        String defaultURL = SEARCH_ENGINE_GOOGLE_URL;
        String local = DeviceUtil.getLocal(mContext);
        if ("cn".equals(local)) {
            defaultURL = SEARCH_ENGINE_BAI_DU_URL;
        } else if ("ru".equals(local)) {
            defaultURL = SEARCH_ENGINE_YANDEX_URL;
        }
        return PreferencesManager.getInstance(mContext).getString(KEY_SEARCH_ENGINE_URL, defaultURL);

    }
}
