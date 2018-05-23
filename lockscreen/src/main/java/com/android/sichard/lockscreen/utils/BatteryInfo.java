package com.android.sichard.lockscreen.utils;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * @类描述 电池信息类(别吐槽啊反编译的代码，你懂的～～)
 * @author caoshichao
 * @date [2015-6-8]
 */
public class BatteryInfo {

	public static int getLevelPercent(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		if (intent == null) {
			return 0;
		}

		int level = intent.getIntExtra("level", 0);
		if (level == 0) {
			return 0;
		}

		int scale = intent.getIntExtra("scale", 0);
		if (scale == 0) {
			return 0;
		}

		return level * 100 / scale;
	}


	public static boolean isUsingUsbElectricity(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		if (intent == null) {
			return false;
		}

		return 2 == intent.getIntExtra("plugged", 0);
	}

	public static boolean isUsingAcElectricity(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		if (intent == null) {
			return false;
		}

		return 1 == intent.getIntExtra("plugged", 0);
	}

	public static boolean isElectricity(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		if (intent == null) {
			return false;
		}

		if(intent.getIntExtra("plugged", 0) == 1 || intent.getIntExtra("plugged", 0) == 2) {
			return true;
		} else {
			return false;
		}

	}

	public static int getTemperature(Context context) {
		Intent intent = context.registerReceiver(null, new IntentFilter("android.intent.action.BATTERY_CHANGED"));
		if (intent == null) {
			return 0;
		}

		return intent.getIntExtra("temperature", 0);
	}

}