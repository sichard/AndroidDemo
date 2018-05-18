package com.android.sichard.search.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;

/**
 *<br>类描述：获取设备信息的工具类
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 2016/7/8</b>
 */
public class DeviceUtil {
	/** api level 是否大于等于11 */
	public static final boolean IS_HONEYCOMB = Build.VERSION.SDK_INT >= 11;
	/** api level 是否大于等于12 */
	public static final boolean IS_HONEYCOMB_MR1 = Build.VERSION.SDK_INT >= 12;
	/** api level 是否大于等于15 */
	public static final boolean IS_OS_4_0_3 = Build.VERSION.SDK_INT >= 15;
	/** api level 是否大于等于16 */
	public static final boolean IS_OS_4_1 = Build.VERSION.SDK_INT >= 16;
	/** api level 是否大于等于18 */
	public static final boolean IS_JELLY_BEAN_3 = Build.VERSION.SDK_INT >= 18;
	/** api level 是否大于等于19 */
	public static final boolean IS_KITKAT = Build.VERSION.SDK_INT >= 19;
	/** api level 是否大于等于20 */
	public static final boolean IS_LARGER_L_VERSION = Build.VERSION.SDK_INT >= 20;
	/** api level 是否大于等于21 */
	public static final boolean IS_LARGER_L_VERSION2 = Build.VERSION.SDK_INT >= 21;
	/** api level 是否大于23 */
	public static final boolean IS_MARSHMALLOW = Build.VERSION.SDK_INT >= 23;


	/** MIUI Rom的host名称 */
	private static final String HOST_NAME_XIAOMI_UI = "miui";

	private static Integer sStatusBarFlag = null; // 4.4 KitKat系统透明状态栏的 flag

	private static boolean sIsTablet = false;

	private final static String MEIZU = "meizu";
	private final static String VIVO = "vivo";
	private final static int HIDE_SMARTBAR = 2;
	
	private final static String PROVLEM_MODELS[] = { "SM-N9", "SM-G9",
	"SM-G9"};

	/** <br>功能简述:获取ANDROID_ID
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static String getAndroidId(Context context) {
		if (null == context) {
			return null;
		}
		return Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
	}

	/** <br>功能简述:获取手机操作系统版本
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static String getOSVersion() {
		return Build.VERSION.RELEASE;
	}

	/** <br>功能简述:获取系统rom版本号
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static String getRomVersion() {
		return Build.ID;
	}

	/**
	 * <br>功能简述:获取手机型号
	 * <br>功能详细描述:
	 * <br>注意:
	 */
	public static String getMachineMode() {
		final String model = Build.MODEL;
		return model;
	}

	/** <br>功能简述:获取sim卡所在国家
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static String getLocal(Context context) {
		// 根据桌面语言设置请求的语言信息
		Locale locale = Locale.getDefault();


		if (locale != null) {
			return locale.getCountry().toLowerCase();
		}

		return null;
	}

	/** <br>功能简述:获取桌面渠道id  从res/raw/uid.txt文件中获取渠道id
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static String getUid(Context context) {

		// TODO: 2016/7/8 加入获取渠道号代码
		return null;
	}

	/** <br>功能简述:获取手机IMSI号，若没有，返回空字符串
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @return
	 */
	public static String getIMSI(Context context) {
		String imsi = "";
		try {
			if (context != null) {
				// 从系统服务上获取了当前网络的MCC(移动国家号)，进而确定所处的国家和地区
				TelephonyManager manager = (TelephonyManager) context
						.getSystemService(Context.TELEPHONY_SERVICE);
				imsi = manager.getSimOperator();
			}
		} catch (Throwable e) {

		}

		return imsi;
	}

	/** <br>功能简述:获取系统统语言
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static String getLanguage() {
		return Locale.getDefault().getLanguage();
	}

	/**
	 * <br>功能简述:伸展通知栏
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param context
	 * @throws Exception
	 */
	public static void expendNotification(Context context) {
		String methodName = "expand";
		if (Build.VERSION.SDK_INT >= 17) {
			// Android4.2接口发生变化
			methodName = "expandNotificationsPanel";
		}

		try {
			Object service = context.getSystemService("statusbar");
			if (service != null) {
				Method expand = service.getClass().getMethod(methodName);
				expand.invoke(service);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * <br>功能简述: 判断手机Rom是不是MIUI V5及以上版本
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isMIUIV5() {
		boolean result = false;
		String host = Build.HOST;
		if (IS_OS_4_1 && host != null && host.toLowerCase() != null
				&& host.toLowerCase().contains(HOST_NAME_XIAOMI_UI)) {
			result = true;
		}
		return result;
	}

	/**
	 * <br>功能简述: 判断手机Rom是不是MIUI V5及以上版本
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	public static boolean isEUI2() {
		boolean result = false;
		String host = Build.HOST;
		if (Build.VERSION.SDK_INT == 19 && host != null && host.toLowerCase() != null
				&& host.toLowerCase().contains("emotion")) {
			result = true;
		}
		return result;
	}

	public static boolean isSamsung() {
		String brand = Build.BRAND;
		String manufacturer = Build.MANUFACTURER;
		if (null != brand && brand.toLowerCase().contains("samsung")) {
			return true;
		}
		if (null != manufacturer && manufacturer.toLowerCase().contains("samsung")) {
			return true;
		}
		return false;
	}

	public static boolean isGoogle() {
		String brand = Build.BRAND;
		if (null != brand && brand.toLowerCase().contains("google")) {
			return true;
		}
		return false;
	}

	/**
	 * 判断当前网络是否可以使用
	 *
	 * @param context
	 * @return
	 */
	public static boolean isNetworkOK(Context context) {
		boolean result = false;
		if (context != null) {
			try {
				ConnectivityManager cm = (ConnectivityManager) context
						.getSystemService(Context.CONNECTIVITY_SERVICE);
				if (cm != null) {
					NetworkInfo networkInfo = cm.getActiveNetworkInfo();
					if (networkInfo != null && networkInfo.isConnected()) {
						result = true;
					}
				}
			} catch (NoSuchFieldError e) {
				e.printStackTrace();
			}
		}

		return result;
	}

	/**
	 * <br>获取当前版本号
	 * @param context
	 * @return
	 */
	public static String getVersionCode(Context context) {
		String version = "unknown";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				version = "" + info.versionCode;
			}
		} catch (Exception e) {
		}
		return version;
	}

	/*
	 * 获取版本号
	 */
	public static String getVersionName(Context context) {
		String version = "unknown";
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (info != null) {
				version = "" + info.versionName;
			}
		} catch (Exception e) {
		}
		return version;
	}

	/**
	 * 是否是平板
	 * @param context
	 * @return
	 */
	public static boolean isTablet(Context context) {
		int layout = context.getResources().getConfiguration().screenLayout;
		sIsTablet = (layout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
		return sIsTablet;
	}

	/**
	 * <br>功能简述:
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param window
	 * @param transparent
	 */
	public static final void setStatusBarTransparent(Window window, boolean transparent) {
		if (window == null || window.getDecorView() == null) {
			return;
		}

		// 4.4 系统的设置方法
		Integer state = getStatusBarTransparentFlag();
		if (state != null) {
			if (transparent) {
				window.addFlags(state);
			} else {
				window.clearFlags(state);
			}
			return;
		}

		// 三星等机器的提供调用方法
		if (transparent) {
			state = getStatusBarMethodString(window.getDecorView());
			if (state != null) {
				window.getDecorView().setSystemUiVisibility(state);
			}
		} else {
			window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
		}
	}

	/***
	 * <br>功能简述: 4.4 系统获取透明状态栏的flag
	 * <br>功能详细描述:
	 * <br>注意:
	 * @return
	 */
	private static final Integer getStatusBarTransparentFlag() {
		if (!IS_KITKAT) {
			return null;
		}
		if (sStatusBarFlag != null) {
			return sStatusBarFlag;
		}
		try {
			Field field = WindowManager.LayoutParams.class.getField("FLAG_TRANSLUCENT_STATUS");
			Class<?> type = field.getType();
			if (type == int.class) {
				sStatusBarFlag = field.getInt(null);
				return sStatusBarFlag;
			}
		} catch (Exception e) {
		}
		return null;
	}

	/**
	 * <br>功能简述: 获取设置透明状态栏的syste ui visibility， 这是部分有提供接口的rom使用的
	 *
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param view
	 * @return
	 */
	private static final Integer getStatusBarMethodString(View view) {
		if (view == null || view.getContext() == null) {
			return null;
		}
		Context context = view.getContext();
		String[] systemSharedLibraryNames = context.getPackageManager()
				.getSystemSharedLibraryNames();
		String fieldName = null;
		for (String lib : systemSharedLibraryNames) {
			if ("touchwiz".equals(lib)) {
				fieldName = "SYSTEM_UI_FLAG_TRANSPARENT_BACKGROUND";
			} else if (lib.startsWith("com.sonyericsson.navigationbar")) {
				fieldName = "SYSTEM_UI_FLAG_TRANSPARENT";
			} else if (lib.startsWith("com.htc.")) {
				//TODO HTC的透明设置方式暂时没有找到，先不做
			}
		}

		if (fieldName != null) {
			try {
				Field field = View.class.getField(fieldName);
				if (field != null) {
					Class<?> type = field.getType();
					if (type == int.class) {
						int value = field.getInt(null);
						return value;
					}
				}
			} catch (Exception e) {
			}
		}
		return null;
	}

	/**
	 * <br>功能简述: 如果是魅族且系统是4.0以上，则隐藏自带的底部smartbar
	 * <br>功能详细描述:
	 * <br>注意: 魅族的Flyme系统，只要对window设置过隐藏，则无论是否跳转到其他会显示smartbar的activity，该window都不会重新show
	 * @param window
	 */
	@SuppressLint("NewApi")
	public static void hideSmartbar(Window window) {
		boolean isSmart4 = isSmart4();
		if (isSmart4) {
			window.getDecorView().setSystemUiVisibility(HIDE_SMARTBAR); // 2隐藏，0显示
		}
	}

	public static boolean isSmart4() {
		boolean isSmartbar4 = Build.BRAND.toLowerCase().contains(MEIZU) && Build.VERSION.SDK_INT >= 14;
		return isSmartbar4;
	}

	public static boolean isVivo() {
		boolean isSmartbar4 = Build.BRAND.toLowerCase().contains(VIVO) && Build.VERSION.SDK_INT >= 14;
		return isSmartbar4;
	}

	public static boolean isLenovo() {
		boolean isLenovo = Build.BRAND.toLowerCase().contains("lenovo") && Build.VERSION.SDK_INT >= 14;
		return isLenovo;
	}

	public static boolean isTCL580() {
		return "TCL 580".equalsIgnoreCase(Build.MODEL);
	}

	public static boolean is8008D() {
		return "ALCATEL ONE TOUCH 8008D".equalsIgnoreCase(Build.MODEL);
	}

	public static boolean isProblemPhone() {
		final String model = Build.MODEL;
		if (model == null) {
			return false;
		}
//		final int size = PROVLEM_MODELS.length;
//		for (int i = 0; i < size; i++) {
			if (model.contains("SM")) {
				return true;
			}
//		}
		return false;
	}

	/**
	 * 返回是否有sim卡(由于部分机型无sim卡时，状态是TelephonyManager.SIM_STATE_UNKNOWN，
	 * 故将状态为TelephonyManager.SIM_STATE_UNKNOWN的也判定为无sim卡)
	 * @param context
	 * @return
     */
	public static boolean isHaveSim(Context context) {
		try {
			int simState = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE)).getSimState();
//
//			String machineMode = getMachineMode();
//			// 对酷派大神note3、魅族PRO 5做的适配
//			String[] airplaneProblemModes = {"8676-A01", "PRO 5"};
//			for (int i = 0; i < airplaneProblemModes.length; i++) {
//				if (machineMode.contains(airplaneProblemModes[i])) {
//					return TelephonyManager.SIM_STATE_ABSENT != simState && TelephonyManager.SIM_STATE_UNKNOWN != simState;
//				}
//			}

			return TelephonyManager.SIM_STATE_ABSENT != simState && TelephonyManager.SIM_STATE_UNKNOWN != simState;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void closeKeyboard(Context context, View view) {
		InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
}
