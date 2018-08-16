package com.android.sichard.lockscreen.utils;

import com.android.sichard.lockscreen.constants.Const;

import java.util.Calendar;

/**
 * <br>类描述:时间工具类
 * @author  caoshichao
 * @date  [2015-4-20]
 */
public class TimeUtil {

	/**
	 * 四小时的毫秒数
	 */
	private static final long MILLIS_OF_FOUR_HOURS = Const.MILLIS_SECOND_PER_HOUR * 4;
	
	/**
	 * 功能简述:获取传入时间距零点的毫秒数
	 * @param currentMillisTime 传入日期的毫秒数
	 * @return
	 */
	public static long getMillisFromZero(long currentMillisTime) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(currentMillisTime);
		long fromZero = calendar.get(Calendar.HOUR_OF_DAY) * Const.MILLIS_SECOND_PER_HOUR
				+ calendar.get(Calendar.MINUTE) * Const.MILLIS_SECOND_PER_MINITE + calendar.get(Calendar.SECOND)
				* Const.MILLIS_SECOND_PER_SECOND + calendar.get(Calendar.MILLISECOND);
		return fromZero;
	}

	/**
	 * 功能简述:获取传入时间零点时的毫秒数
	 * @param currentMillisTime 传入日期的毫秒数
	 * @return
	 */
	public static long getMillisOfZero(long currentMillisTime) {
		return currentMillisTime - getMillisFromZero(currentMillisTime);
	}

	/** 
	 * 功能简述:获取传入日期的4点、8点、12点、16点、20点的毫秒数。间隔4小时
	 * 注意：此处是24时计时法
	 * @param currentMillisTime 传入日期的毫秒数
	 * @return
	 */
	public static long[] getMillisOfFourHour(long currentMillisTime) {
		long[] points = new long[5];
		long zero = getMillisOfZero(currentMillisTime);
		long millisOfFour = zero + MILLIS_OF_FOUR_HOURS; //4点钟的毫秒数
		for (int i = 0; i < points.length; i++) {
			points[i] = millisOfFour + i * MILLIS_OF_FOUR_HOURS;
		}
		return points;
	}
}
