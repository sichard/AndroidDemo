package com.tcl.launcherpro.search.common;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;

import com.tcl.launcherpro.search.AppsFilter;
import com.tcl.launcherpro.search.SearchSDK;
import com.tcl.launcherpro.search.data.App.AppInfo;
import com.tcl.launcherpro.search.utils.AppUtil;
import com.tcl.launcherpro.search.utils.StringUtil;
import com.tcl.launcherpro.search.view.SearchUtils;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 *<br>类描述：功能表搜索数据
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-7-27</b>
 */
public class SearchAppManager implements Comparator<AppInfo> {
	private Context mContext;
	private String mKey;
	private List<AppInfo> mBuffer;
	private List<AppInfo> mAppList;
	private SearchUtils mSearchUtils;
	/** App列表是否发生该表标志位 */
	private boolean mIsAppListChanged = false;

	// 是否自己加载app
	static private boolean mIsLoadApp = true;
	
	public SearchAppManager(Context context) {
		mContext = context;
		mSearchUtils = SearchUtils.getInstance(context);
		if (mIsLoadApp) {
			mAppList = AppUtil.getInstalledAppList(context);
		}
	}

	public void reloadApplist(Context context){
		if (mIsLoadApp) {
			mAppList = AppUtil.getInstalledAppList(context);
		}
		mBuffer = null;
	}

	/**
	 * 外部配置Apps
	 * @param context
	 * @param resolveInfos
	 * @param icons
     */
	public void loadAppInfo(Context context, List<ResolveInfo> resolveInfos, List<Drawable> icons){
		mAppList = AppUtil.getAppList(context, resolveInfos, icons);
	}

	public List<AppInfo> search(String key) {
		if (key == null || key.equals("")) {
			return null;
		}
		List<AppInfo> resultList = new ArrayList<AppInfo>();
		List<AppInfo> all = null;

		//新关键词包含上一次关键词，就在上一次的结果中找
		if (mKey != null && !mKey.equals("") && key.contains(mKey) && mBuffer != null && !mIsAppListChanged) {
			all = mBuffer;
		} else {
			all = mAppList;
		}

		mBuffer = new ArrayList<AppInfo>();

		for (AppInfo appInfo : all) {
			String title = appInfo.getTitle();
			MatchResult item = mSearchUtils.match(key, title);
			if (item != null && item.mMatchWords > 0) {
				item.key = key;
				appInfo.mMatchResult = item;
				if(appInfo.getIcon()!=null) {
					if (SearchSDK.getInstance().getThemeTools() != null) {
						Drawable systemIcon = appInfo.getSystemIcon();
						if (systemIcon != null) {
							systemIcon = SearchSDK.getInstance().getThemeTools().createIcon(appInfo.getComponentName(), systemIcon);
						}
						appInfo.setIcon(systemIcon);
					}
				}
				// 搜索过滤
				AppsFilter appsFilter = SearchSDK.getInstance().getAppsFilter();
				boolean isFilter = false;
				if (appsFilter != null) {
					isFilter = appsFilter.isFilter(appInfo.getPackageName());
				}
				if (!isFilter) {
					resultList.add(appInfo);
				}

				mBuffer.add(appInfo);
			}
		}
		mKey = key;
		Collections.sort(resultList, this); //排序
		return resultList;
	}

	/**
	 * <br>功能简述：刷新App列表数据，以便搜索可以及时搜索到新安装的应用程序
	 * <br>注意:
	 */
	public void updateAppListDate() {
		if (mIsLoadApp) {
			mAppList = AppUtil.getInstalledAppList(mContext);
		}
		mIsAppListChanged = true;
	}
	
	@Override
	public int compare(AppInfo lhs, AppInfo rhs) {
		String lAppTitle = lhs.getTitle();
		String rAppTitle = rhs.getTitle();
		return compareString(lAppTitle, rAppTitle);
	}

	/** <br>功能简述:比较两个字符串值的大小
	 * <br>功能详细描述:
	 * <br>注意:
	 * @param lStr
	 * @return:-1:lStr<rStr 0:lStr == rStr 1:lStr>rStr
	 */
	public static final int compareString(String lStr, String rStr) {
		if (null == lStr && null == rStr) {
			return 0;
		}
		if (null == lStr) {
			return -1;
		}
		if (null == rStr) {
			return 1;
		}

		//把中文字符转换成拼音
		if (StringUtil.isContainsChinese(lStr)) {
			lStr = StringUtil.convertStringToChineseSell(lStr);
		}

		if (StringUtil.isContainsChinese(rStr)) {
			rStr = StringUtil.convertStringToChineseSell(rStr);
		}

		//把字符串全部转换成大写
		String lUpStr = lStr.toUpperCase();
		String rUpStr = rStr.toUpperCase();

		//获取本地字符比较器对两个字符串进行比较
		Collator collator = Collator.getInstance(Locale.getDefault());
		return collator.compare(lUpStr, rUpStr);
	}

	/**
	 * 获取应用列表
	 * @return
     */
	public List<AppInfo> getAppList() {
		return mAppList;
	}

	public AppInfo getAppInfo(String componentName) {
		if (mAppList != null) {
			for (AppInfo appInfo : mAppList) {
				if (appInfo.getComponentName().toShortString().equals(componentName)) {
					return appInfo;
				}
			}
		}
		return null;
	}

	public static void setIsLoadApp(boolean isLoadApp) {
		mIsLoadApp = isLoadApp;
	}
}
