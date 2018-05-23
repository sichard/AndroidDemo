package com.android.sichard.lockscreen.constants;

/**
 * 各种硬件状态的耗电单位
 * 
 * @author wenjiaming
 * 
 */
// 数值的单位时间值为秒(即：每秒消耗多少电量)
public class ConsumptionConstant {
	public final static double WIFI_ON_POWER = 0.008; // WIFI打开并空闲过程中电量消耗常数

	public final static double CPU_AVERAGE = 0.0027; // CPU平均电量消耗常数

	public final static double GPRS_ON_POWER = 0.008; // 移动网络打开并空闲过程中电量消耗常数

	public final static double SCREEN_AREA_POWER = 0.00096; // 屏幕每平方英寸电量消耗常数

	public final static double PHONE_ON_POWER = 0.0027; // 手机打开并控件过程中电量消耗常数(即待机状态)

	public final static double PROGRESS_ON_POWER = 0.000135; // 每个App或Service运行的电量消耗常数

	public final static double BLUETOOTH_ON_POWER = 0.008; // 蓝牙电量消耗常数

	public final static double AUTO_SYNC_ON_POWER = 0.008; // 同步更新电量消耗常数

	public final static double GPS_ON_POWER = 0.024; // GPS电量消耗常数


	public static final int INTEGER_20 = 20;
}
