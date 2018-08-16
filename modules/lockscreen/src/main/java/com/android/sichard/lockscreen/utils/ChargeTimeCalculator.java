package com.android.sichard.lockscreen.utils;


import com.android.sichard.lockscreen.constants.Const;

import java.util.ArrayList;
import java.util.Map;

/**
 * 
 * <br>类描述:充电剩余时间计算工具类
 * <br>功能详细描述:提供在不同的充电阶段时，计算当前充电剩余时间的方法
 * 
 * @author  liyang
 * @date  [2012-9-3]
 */
public class ChargeTimeCalculator {

	/** 快速充电时，每1%电量消耗时间(秒) */
	public static final int RAPID_CHARGE_CONSTANT = 115;
	/** 连续充电时，每1%电量消耗时间(秒) */
	public static final int CONTINUOUS_CHARGE_CONSTANT = 205;
	/** 快速充电、连续充电的界限 */
	public static final int RAPID_CONTINUOUS_LINE = 90;

	/***
	 * <br>功能简述: 计算普通充电过程总共所需要的时间
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param level
	 * @param secondsLevelList 用于智能充电时间计算, 之前充电被记录在数据库的时间数据, 
	 * 			是一个Map的List, Map中保存了0-100 level充1%电所需要的时间.
	 * @return 普通充电过程总共所需要的时间(秒)
	 */
	public static int newCalcTotalChargeSeconds(int level, ArrayList<Map<String, Integer>> secondsLevelList) {
		
		if (level < 0 || level > Const.INTELLIGENT_CHARGE_LEVEL_POINT_NUM) {
			return 0;
		}

		int intelligentChargingTime = 0;
		double intelligentChargingDataPercent = 0.0;
		
		// 数据库记录 有最多10行记录
		// 将"需要充值的百分比的1%充电时间平均值"加起来
		if (secondsLevelList != null && secondsLevelList.size() != 0) {
			int size = secondsLevelList.size();
			// level + 1 开始去计算时间总和
			for (int i = level + 1; i <= Const.INTELLIGENT_CHARGE_LEVEL_POINT_NUM; i++) {
				int totalSecondsPerLevel = 0;
				Map<String, Integer> secondsLevel = null;
				// 对同level的1%充电时间求和, 然后求平均值
				for (int j = 0; j <= size - 1; j++) {
					secondsLevel = secondsLevelList.get(j);
					if (secondsLevel != null) {
						totalSecondsPerLevel += secondsLevel.get("time_level_" + i);
					}
				}
				intelligentChargingTime += totalSecondsPerLevel / size;
			}
			
			// 统计数据比值
			intelligentChargingDataPercent = (double) size / Const.INTELLIGENT_CHARGE_RECORD_MAX_NUM;
		}

		// 计算预测充电时间
		// 刚开始時, 统计数据项会是0, intelligentChargingDataPercent将为0, 
		// 当充电次数多时, 统计数据项达到10次, intelligentChargingDataPercent将为1
		//
		// 下边的公式是什么意思?
		// 时间 = 统计数据比值 * 统计预测时间 + 非统计数据比值 * 假设预测时间;
		// 其中 统计数据比值 = 被记录充电数据行数 / 最大的可记录充电数据行数;
		final int changingTimeOfOnePercent;
		// ???: RAPID_CONTINUOUS_LINE 连续充电阀值? 哪里来的?
		if (level < RAPID_CONTINUOUS_LINE) {
			changingTimeOfOnePercent = RAPID_CHARGE_CONSTANT;
		} else {
			changingTimeOfOnePercent = CONTINUOUS_CHARGE_CONSTANT;
		}
		final int nonIntelligentChargingTime = changingTimeOfOnePercent * (Const.INTELLIGENT_CHARGE_LEVEL_POINT_NUM - level);
		final int predictedChargingTime = (int) (
				(1 - intelligentChargingDataPercent) * nonIntelligentChargingTime
				+ intelligentChargingDataPercent * intelligentChargingTime);

		return predictedChargingTime;
	}
}
