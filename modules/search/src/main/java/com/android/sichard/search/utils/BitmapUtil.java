package com.android.sichard.search.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.util.Log;

/**
 *<br>类描述：位图操作工具方法类
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016/7/8</b>
 */
public class BitmapUtil {
	private static final String TAG = "BitmapUtil";
	/** 用于构建位图的临时画布 */
	private static final Canvas CANVAS = new Canvas();

	static {
		CANVAS.setDrawFilter(new PaintFlagsDrawFilter(Paint.DITHER_FLAG, Paint.FILTER_BITMAP_FLAG));
	}

	public static final Bitmap createScaledBitmap(Bitmap bmp, int scaleWidth, int scaleHeight) {
		Bitmap pRet = null;
		if (null == bmp) {
			Log.i(TAG, "create scale bitmap function param bmp is null");
			return pRet;
		}

		if (scaleWidth == bmp.getWidth() && scaleHeight == bmp.getHeight()) {
			return bmp;
		}

		try {
			pRet = Bitmap.createScaledBitmap(bmp, scaleWidth, scaleHeight, true);
		} catch (OutOfMemoryError e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap out of memory");
		} catch (Exception e) {
			pRet = null;
			Log.i(TAG, "create scale bitmap exception");
		}

		return pRet;
	}
}
