package com.android.sichard.search;

/**
 * <br>类描述:常量类
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2016-12-8</b>
 */
public class Constants {
    public static String PACKAGE_NAME;
    // 跳转到searchSetting的Action
    public static String ACTION_SEARCHSETTING;
    // 跳转到searchFeedBack的Action
    public static String ACTION_FEEDBACK;
    public static String DEFAULT_PREFERENCE_NAME;
    // 该地方会被反射用到，不能删
    public static final String ACTIVITY_SEARCHSETTING = "searchSetting";
    // 该地方会被反射用到，不能删
    public static final String ACTIVITY_FEEDBACK = "searchFeedback";

    public static void init(String packName) {
        // 动态配置报名
        PACKAGE_NAME = packName;
        ACTION_SEARCHSETTING = PACKAGE_NAME + ".hisearch.ACTION_SEARCHSETTING";
        ACTION_FEEDBACK = PACKAGE_NAME + ".hisearch.ACTION_SEARCHFEEDBACK";
        DEFAULT_PREFERENCE_NAME = Constants.PACKAGE_NAME + ".search";
    }
}
