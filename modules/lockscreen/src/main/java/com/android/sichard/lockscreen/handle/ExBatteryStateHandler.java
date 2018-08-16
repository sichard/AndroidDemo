package com.android.sichard.lockscreen.handle;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.SystemClock;

import com.android.sichard.lockscreen.constants.Const;
import com.android.sichard.lockscreen.constants.ConsumptionConstant;
import com.android.sichard.lockscreen.framework.SingleInstanceBase;
import com.android.sichard.lockscreen.utils.BatteryInfo;
import com.android.sichard.lockscreen.utils.ChargeTimeCalculator;

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * <br>类描述:新版的充电状态切换处理类
 * <br>功能详细描述:
 * 
 * @author  liyang
 * @date  [2012-11-20]
 */
public class ExBatteryStateHandler extends SingleInstanceBase {


	/** 未知充电模式 */
	public static final int CHARGING_MODE_UNKNOWN = -1;
	/** 健康充电模式 */
	public static final int CHARGING_MODE_HEALTH = 0;
	/** 完全充电模式 */
	public static final int CHARGING_MODE_FULL = 1;
	/** 过度充电模式 **/
	public static final int CHARGING_MODE_EXCESSIVE = 2;

	/** 没有充电 */
	public static final int CHARGING_NO = 0;
	/** 充电过程中 */
	public static final int CHARGING_IN = 1;
	/** 充电完成 */
	public static final int CHARGING_FINISH = 2;
	/** 充电过度 */
	public static final int CHARGING_EXCESSIVE = 3;


	private static final int MSG_RECORD_LAST_CHARGE = 1;

	// 按时间切换：8点
	private static final int MSG_CANCEL_POWER_TIP_NOTIFYCATION = 0;


	/** 充完电后，进入过度充电的时长 */
	private static final int CHARGE_EXCESSIVE_TIME_OUT = 10 * 3600 * 1000;
	private static final int REQUEST_CODE_PENDING_OVER = 0;


	private static final int MSG_DELETE_OLD_RECENT_DATA = 2;
	
	private static final int MSG_RECORD_RECENT_CHARGE = 3;

	private int mChargeMode = CHARGING_MODE_UNKNOWN;
	private int mTotalTime;
	private int mLevel;
	private int mOldLevel = -1; // 在Intent.ACTION_BATTERY_CHANGED之前，电池的电量
	private int mOldTemperature = -1;

	private int mStartLevel;

	private AlarmManager mAlarmManager;
	private PendingIntent mExcessivePendingIntent;

	private static final double DOUBLE_10 = 10.0;
	private long mLastHighTempAlarmTime = 0; //上一次提示时间
	private boolean mHighTempAlarmFlag = true; //防止高温提示窗口提示频率过高的标识


	private static ExBatteryStateHandler sInstance;
	private BroadcastReceiver mBatteryStateReceiver;
	private BroadcastReceiver mBatteryTempReceiver;

	private ExBatteryStateHandler() {
		mAlarmManager = (AlarmManager) sContext.getSystemService(Context.ALARM_SERVICE);

		mLevel = BatteryInfo.getLevelPercent(sContext);
		mStartLevel = mLevel;

		updateChargingState();

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_BATTERY_CHANGED);
		filter.addAction(Intent.ACTION_POWER_CONNECTED);
		filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
		filter.addAction(Const.ACTION_PENDING_EXCESSIVE);
		filter.addAction(Const.ACTION_FORCE_STOP_RINGTONE);
		mBatteryStateReceiver = new BatteryStateChangeReceiver();
		sContext.registerReceiver(mBatteryStateReceiver, filter);

		//处理电池高温提醒
		IntentFilter batteryFilter = new IntentFilter();
		batteryFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		mBatteryTempReceiver = new BatteryTemperatureChangeReceiver();
		sContext.registerReceiver(mBatteryTempReceiver, batteryFilter);
		
		mHandler.sendEmptyMessage(MSG_DELETE_OLD_RECENT_DATA);
	}

	public static ExBatteryStateHandler getInstance() {
		if (sContext == null) {
			return null;
		}
		if (sInstance == null) {
			sInstance = new ExBatteryStateHandler();
		}
		return sInstance;
	}

	public void onStartCommand() {
		broadcastCurrentState();
	}

	public void release() {
		if (mBatteryStateReceiver != null) {
			try {
				sContext.unregisterReceiver(mBatteryStateReceiver);
				mBatteryStateReceiver = null;
			} catch (Exception e) {
			}
		}

		if (mBatteryTempReceiver != null) {
			try {
				sContext.unregisterReceiver(mBatteryTempReceiver);
				mBatteryTempReceiver = null;
			} catch (Exception e) {
			}
		}
		sInstance = null;
	}

	private void updateChargingState() {
		if (mLevel < Const.ONE_HUNDRED) {
			if (isUsingExternalElectricity()) {
				mChargeMode = CHARGING_MODE_HEALTH;
			} else {
				mChargeMode = CHARGING_MODE_UNKNOWN;
			}
			// 取数据
			ArrayList<Map<String, Integer>> secondsLevelList = null;
			mTotalTime = ChargeTimeCalculator.newCalcTotalChargeSeconds(mLevel, secondsLevelList);

		} else {
			if (isUsingExternalElectricity()) {
				if (mStartLevel < ConsumptionConstant.INTEGER_20) {
					mChargeMode = CHARGING_MODE_FULL;
				} else {
					mChargeMode = CHARGING_MODE_HEALTH;
				}
			} else {
				mChargeMode = CHARGING_MODE_UNKNOWN;
			}
			mTotalTime = -1;
		}
	}

	private int broadcastCurrentState() {
		int logicalState = CHARGING_NO;
		if (mChargeMode == CHARGING_MODE_UNKNOWN) {
			logicalState = CHARGING_NO;
		} else if (mChargeMode == CHARGING_MODE_EXCESSIVE) {
			logicalState = CHARGING_EXCESSIVE;
		} else if (mLevel == Const.ONE_HUNDRED) {
			logicalState = CHARGING_FINISH;
		} else {
			logicalState = CHARGING_IN;
		}

		Intent intent = new Intent(Const.ACTION_BATTERY_CHANGED);
		intent.putExtra(Const.EXTRA_MODE, mChargeMode);
		intent.putExtra(Const.EXTRA_TOTAL_TIME, mTotalTime);
		intent.putExtra(Const.EXTRA_LEVEL, mLevel);
		intent.putExtra(Const.EXTRA_LOGICAL_STATE, logicalState);
		sContext.sendBroadcast(intent);
		return logicalState;
	}

	private boolean isUsingExternalElectricity() {
		// 判断是否正在使用USB电量
		return BatteryInfo.isElectricity(sContext);
	}

	/**
	 * 取消电池电量的提醒
	 */
	private void cancelPowerTipNotify() {
		NotificationManager notificationManager = (NotificationManager) sContext
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(Const.NOTIFY_TAG, Const.NOTIFY_POWER_TIP_ID);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case MSG_CANCEL_POWER_TIP_NOTIFYCATION :
					cancelPowerTipNotify();
					break;
				case MSG_RECORD_LAST_CHARGE :
					updateLastChargeDetail();
					break;
				case MSG_DELETE_OLD_RECENT_DATA :
					break;
				case MSG_RECORD_RECENT_CHARGE :
					break;
				default :
					break;
			}
		};
	};

	/**
	 * <br>功能简述: 获取电池温度过高提醒设置项的值
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private boolean getHighTempHintConfigValue() {
		SharedPreferences preferences = sContext.getSharedPreferences(Const.SYS_SETTING_CONFIGURATION,
				Context.MODE_WORLD_READABLE);
		int state = preferences.getInt(Const.HIGH_TEMP_KEY, Const.OFF);
		return state == Const.ON;
	}

	/**
	 * <br>功能简述:防止提示窗口弹出频率过于频繁
	 * <br>功能详细描述: 通过时间间隔和温度来控制
	 * <br>注意:
	 * @return
	 */
	private boolean tipControl() {
		//间隔时间大于 1 小时
		long intervalTime = SystemClock.elapsedRealtime() - mLastHighTempAlarmTime;
		boolean timeValue = intervalTime > (Const.SECOND_PER_HOUR * Const.MINI_SECOND_UNIT);

		return timeValue && mHighTempAlarmFlag;
	}

	private void setLastEndTime(long time) {
		SharedPreferences preferences = sContext.getSharedPreferences(Const.SYS_CONFIGURATION, Context.MODE_PRIVATE);
		SharedPreferences.Editor edit = preferences.edit();
		edit.putLong(Const.LAST_CHARGE_END_TIME, time);
		edit.commit();
	}

	private void updateLastChargeDetail() {
		mLevel = BatteryInfo.getLevelPercent(sContext);

		setLastEndTime(System.currentTimeMillis());

//		updateChargeHabit();
//		updateChargingState();

//		broadcastCurrentState();

		// 目的在于断开充电的时候能够取消过度充电PendingIntent
		if (mExcessivePendingIntent != null) {
			mAlarmManager.cancel(mExcessivePendingIntent);
		}
		mOldLevel = -1;
	}

	/**
	 * 
	 * <br>类描述:监听电池状态变化广播的接收器
	 * <br>功能详细描述:
	 * 
	 * @author  liyang
	 * @date  [2012-11-23]
	 */
	private class BatteryStateChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(Intent.ACTION_BATTERY_CHANGED)) {
				mLevel = BatteryInfo.getLevelPercent(context);
				int temperature = BatteryInfo.getTemperature(context);
				if (mOldLevel == mLevel && temperature == mOldTemperature) {
					return;
				}

				if (mLevel == Const.ONE_HUNDRED) {
					mHandler.sendEmptyMessage(MSG_RECORD_RECENT_CHARGE);
				}
				mOldTemperature = temperature;
				mOldLevel = mLevel;
				updateChargingState();
				broadcastCurrentState();

			} else if (action.equals(Intent.ACTION_POWER_CONNECTED)) {
				mLevel = BatteryInfo.getLevelPercent(context);
				updateChargingState();

				mStartLevel = mLevel;

				broadcastCurrentState();
				// 设置过度充电的PendingIntent
				long nextTime = SystemClock.elapsedRealtime() + CHARGE_EXCESSIVE_TIME_OUT;
				Intent excessiveIntent = new Intent(Const.ACTION_PENDING_EXCESSIVE);
				mExcessivePendingIntent = PendingIntent.getBroadcast(sContext, REQUEST_CODE_PENDING_OVER,
						excessiveIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				mAlarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, nextTime, mExcessivePendingIntent);

			} else if (action.equals(Intent.ACTION_POWER_DISCONNECTED)) {
				mHandler.sendEmptyMessage(MSG_RECORD_LAST_CHARGE);

			} else if (action.equals(Const.ACTION_PENDING_EXCESSIVE)) {
				if (isUsingExternalElectricity()) {
					mChargeMode = CHARGING_MODE_EXCESSIVE;
					mTotalTime = -1;

					broadcastCurrentState();

				}
			} else if (action.equals(Const.ACTION_FORCE_STOP_RINGTONE)) {

			}
		}

	}

	/**
	 * 
	 * <br>类描述:处理电池高温提示的监听者
	 * <br>功能详细描述:
	 * 
	 * @author  liyang
	 * @date  [2012-11-20]
	 */
	private class BatteryTemperatureChangeReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();

			if (Intent.ACTION_BATTERY_CHANGED.equals(action)) {

				int temperature = BatteryInfo.getTemperature(context);
				//取出来的温度值要除以10才是真实温度
				double result = temperature / DOUBLE_10;
				if (result >= Const.HIGHT_TEMPERATURE) {

					//获取系统设置
					if (!getHighTempHintConfigValue()) {
						return;
					}

					if (!tipControl()) {
						return;
					}
				} else {
					//温度低于警戒线标识
					mHighTempAlarmFlag = true;
				}
			}
		}
	}
}
