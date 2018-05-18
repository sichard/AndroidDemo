package com.tcl.launcherpro.search.common;


/**
 *<br>类描述：
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016/7/8</b>
 */
public final class TimeConstant {	
	/** 1秒，单位：ms */
	public static final int ONE_SECOND = 1000;	
	/** 1分钟，单位：ms */
	public static final int ONE_MINUTE = 60 * ONE_SECOND;	
	/** 1小时，单位:ms */
	public static final int ONE_HOUR = 60 * ONE_MINUTE;
	/** 1天，单位:ms */
	public static final int ONE_DAY = 24 * ONE_HOUR;
	/** 1年，单位:ms */
	public static final int ONE_YEAR = 365 * ONE_DAY;

	/** 日志时间格式 */
	public static final String LOG_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";
	/** 日志文件时间格式 */
	public static final String LOG_FILE_DATE_FORMAT = "yyyy-MM-dd_HH-mm-ss.SSS";
	/** 日期时间格式 */
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";
}
