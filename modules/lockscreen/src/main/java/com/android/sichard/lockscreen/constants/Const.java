package com.android.sichard.lockscreen.constants;

/**
 * 省电中用到的各种常量
 * 
 * @author oulingmei
 */
public class Const {

	public static final int ON = 1;
	public static final int OFF = 0;
	public static final int UNKNOWN = -1;

	/** 应答当前电池续航时间的Action */
	public static final String ACTION_ANSWER_AVAILABLE_TIME = "com.android.sichard.constants.answer_available_time";
	public static final int NOTIFY_POWER_TIP_ID = 1; // 充电开始、充电完成状态指示ID
	public static final String NOTIFY_TAG = "com.android.sichard.lockscreen.notify";

	public static final String IS_CONFIG_SIGN = "is_config_sign";

	/**
	 * ACTION_BATTERY_CHANGED：服务通知界面进行充电状态更新的广播
	 * EXTRA_MODE：充电模式(普通充电、健康充电、完全充电、涓流充电) EXTRA_STATE：充电状态（快速充电、连续充电、涓流充电）
	 * EXTRA_STATE_TIME：充电状态中所需的时间 EXTRA_TOTAL_TIME：充电总共所需的时间 EXTRA_LEVEL：当前电量(0
	 * - 100) EXTRA_CHARGING_STATE：是否正在充电
	 */
	public static final String ACTION_BATTERY_CHANGED = "com.android.sichard.constants.ACTION_BATTERY_CHANGED";
	public static final String EXTRA_MODE = "extra_mode";
	public static final String EXTRA_TOTAL_TIME = "extra_total_time";
	public static final String EXTRA_LEVEL = "extra_level";
	public static final String EXTRA_LOGICAL_STATE = "extra_logical_state";

	/**
	 * 涓流充电完成，将要进入过度充电的PendingIntent的Action
	 */
	public static final String ACTION_PENDING_EXCESSIVE = "com.android.sichard.constants.ACTION_PENDING_EXCESSIVE";

	public static final String ACTION_FORCE_STOP_RINGTONE = "com.android.sichard.constants.ACTION_FORCE_STOP_RINGTONE";
	public static final String SYS_CONFIGURATION = "sys_configuration";
	public static final String SYS_SETTING_CONFIGURATION = "sys_setting_configuration";	// 保存系统设置且需要备份的 SharedPreferences文件名。
	public static final int INTELLIGENT_CHARGE_LEVEL_POINT_NUM = 100; // 充电电量值
	public static final int INTELLIGENT_CHARGE_RECORD_MAX_NUM = 10; // 智能充电数据表数据个数

	public static final int SECOND_PER_MINITE = 60; // 每分钟有多少秒
	public static final int MINITE_PER_HOUR = 60; // 每小时有多少分钟
	public static final int SECOND_PER_HOUR = 3600; // 每小时有多少秒

	public static final int MINI_SECOND_UNIT = 1000; // 秒、毫秒之间的换算单位
	public static final int ONE_HUNDRED = 100; // 100的数值
	public static final int HIGHT_TEMPERATURE = 46; //当电池温度达到46°C 时会提示
	public static final String HIGH_TEMP_KEY = "high_temp_key";
	public static final String LAST_CHARGE_END_TIME = "last_charge_end_time";

	public static final int MILLIS_SECOND_PER_HOUR = SECOND_PER_HOUR * MINI_SECOND_UNIT;
	public static final int MILLIS_SECOND_PER_MINITE = SECOND_PER_MINITE * MINI_SECOND_UNIT;
	public static final int MILLIS_SECOND_PER_SECOND = MINI_SECOND_UNIT;
	public static final long MILLIS_SECOND_PER_DAY = 24 * MILLIS_SECOND_PER_HOUR;
}
