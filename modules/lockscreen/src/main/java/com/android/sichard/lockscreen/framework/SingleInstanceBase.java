package com.android.sichard.lockscreen.framework;

import android.content.Context;

/**
 * 
 * <br>类描述:需要使用context的单例类的基类
 * <br>功能详细描述:
 * 
 * @author  guoweijie
 * @date  [2012-9-14]
 */
public abstract class SingleInstanceBase {
	protected static Context sContext;

	public static void registerContext(Context applicationContext) {
		if (sContext == null) {
			sContext = applicationContext;
		}
	}

	public Context getContext() {
		return sContext;
	}

	public void release() {
	}
}
