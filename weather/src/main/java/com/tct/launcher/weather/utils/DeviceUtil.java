package com.tct.launcher.weather.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 *<br>类描述：获取设备信息的工具类
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016/7/8</b>
 */
public class DeviceUtil {

	/** api level 是否大于等于19 */
	public static final boolean IS_KITKAT = Build.VERSION.SDK_INT >= 19;

	/**
	 * 判断当前网络是否可以使用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkOK(Context context) {
		boolean result = false;
		if (context != null) {
			try {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm != null) {
					NetworkInfo networkInfo = cm.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						result = true;
					}
				}
			} catch (NoSuchFieldError e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	public static void closeKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
