package com.tcl.launcherpro.search.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.lang.reflect.Field;

/**
 *<br>类描述：绘制工具类
 *<br>详细描述：
 *<br><b>Autor sichardcao</b>
 *<br><b>Date 2016/7/11</b>
 */
public class DrawUtils {

	public static float sDensity = 1.0f;
	public static int sWidthPixels;
	public static int sHeightPixels;
	public static float sFontDensity;
	
	// 在某些机子上存在不同的density值，所以增加虚拟值
	public static float sVirtualDensity = -1;
	
	// 点击的最大识别距离，超过即认为是移动
	public static int sTouchSlop = 15;  //CHECKSTYLE IGNORE
	
	/**
	 * 屏幕现实占用比
	 * 屏幕区域 ／ (屏幕区域 ＋ 导航栏区域)
	 */
	public static float sDisplayRate;
	
	/**
	 * dip/dp转像素
	 * @param dipValue dip或 dp大小
	 * @return 像素值
	 */
	public static int dip2px(float dipValue) {
		return (int) (dipValue * sDensity + 0.5f); //CHECKSTYLE IGNORE
	}

	/**
	 * 像素转dip/dp
	 * @param pxValue 像素大小
	 * @return dip值
	 */
	public static int px2dip(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale + 0.5f); //CHECKSTYLE IGNORE
	}

	/**
	 * sp 转 px
	 * @param spValue sp大小
	 * @return 像素值
	 */
	public static int sp2px(float spValue) {
		final float scale = sDensity;
		return (int) (scale * spValue);
	}

	/**
	 * px转sp
	 * @param pxValue 像素大小
	 * @return sp值
	 */
	public static int px2sp(float pxValue) {
		final float scale = sDensity;
		return (int) (pxValue / scale);
	}

	public static void resetDensity(Context context) {
		if (context != null && null != context.getResources()) {
			DisplayMetrics metrics = context.getResources().getDisplayMetrics();
			sDensity = metrics.density;
			sFontDensity = metrics.scaledDensity;
			sWidthPixels = metrics.widthPixels;
			sHeightPixels = metrics.heightPixels;
			initVirtureDensity(context.getResources());
		}
	}
	
	private static void initVirtureDensity(Resources resources) {
		Resources res = new Resources(resources.getAssets(), resources.getDisplayMetrics(), resources.getConfiguration());
		try {
			Field field = Resources.class.getDeclaredField("mCompatibilityInfo");
			if (field == null) {
				return;
			}
			field.setAccessible(true);
			Object object = field.get(resources);
			if (null != object) {
				field.set(res, object);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		final DisplayMetrics metrics = resources.getDisplayMetrics();
		final DisplayMetrics localMetrics = res.getDisplayMetrics();
		if (metrics.density != localMetrics.density) {
			sVirtualDensity = localMetrics.density;
		}
	}
}