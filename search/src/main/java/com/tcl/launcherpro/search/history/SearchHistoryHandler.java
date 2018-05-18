package com.tcl.launcherpro.search.history;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;

import com.tcl.launcherpro.search.AppsFilter;
import com.tcl.launcherpro.search.SearchSDK;
import com.tcl.launcherpro.search.common.SearchAppManager;
import com.tcl.launcherpro.search.data.App.AppInfo;
import com.tcl.launcherpro.search.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 *<br>类描述：搜索历史处理类
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date   2016-7-27</b>
 */
public class SearchHistoryHandler {

	/** preference名称 */
	private static final String SEARCH_HISTORY = "hi_search_history";
	/** 分隔符 */
	private static final String SEPARATOR = ":";
	/** 默认历史记录最大条数 */
	private static final int DEFAULT_HISTORY_MAX_COUNT = 10;
	/** 搜索历史记录 */
	public static final String HISTORY = "hi_history";

	private Context mContext;
	private SharedPreferences mPreferences;
	private Editor mEditor;

	public SearchHistoryHandler(Context context) {
		this.mContext = context;
		mPreferences = context.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	/**
	 * <br>功能简述：保存搜索历史
	 * @param componentName 要保存AppInfo的mId
	 */
	public void saveHistory(ComponentName componentName) {
		if (componentName == null) {
			return;
		}
		String history = String.valueOf(componentName.toShortString());
		if (mPreferences == null) {
			mPreferences = mContext.getSharedPreferences(SEARCH_HISTORY, Context.MODE_PRIVATE);
		}
		if (!TextUtils.isEmpty(history)) {
			String historyStr = mPreferences.getString(HISTORY, "");
			if (TextUtils.isEmpty(historyStr)) {
				historyStr = history;
			} else if (!historyStr.contains(SEPARATOR)) {
				if (!history.equals(historyStr.trim())) {
					historyStr = new StringBuilder(history).append(SEPARATOR).append(historyStr).toString();
				}
			} else {
				historyStr = getNewHistoryString(historyStr, history);
			}
			mEditor.putString(HISTORY, historyStr).commit();
		}
	}

	private String getNewHistoryString(final String historyStr, final String history) {
		String[] histories = historyStr.split(SEPARATOR);
		// 查找并移除相同history记录
		// 如果删除了相同history的记录后, 前面的历史记录往后挪动一位, 最后留空0位置
		boolean containSameHistory = false;
		for (int i = histories.length - 1; i >= 0; --i) {
			if (containSameHistory) {
				if (i > 0) {
					histories[i] = histories[i - 1];
				}
			} else {
				if (history.equals(histories[i])) {
					if (i > 0) {
						histories[i] = histories[i - 1];
					}
					containSameHistory = true;
				}
			}
		}

		int currentCount = histories.length;
		// 找到相同记录, 将记录移动到首位
		if (containSameHistory) {
			histories[0] = history;
		} else if (currentCount < DEFAULT_HISTORY_MAX_COUNT) { // 当前历史记录数小于最大记录
			String[] newHistories = new String[currentCount + 1];
			System.arraycopy(histories, 0, newHistories, 1, currentCount);
			newHistories[0] = history;
			histories = newHistories;
			++currentCount;
		} else {
			// 历史记录条数达到DEFAULT_HISTORY_MAX_COUNT, 历史记录往后挪动1位
			for (int i = DEFAULT_HISTORY_MAX_COUNT - 1; i > 0; --i) {
				histories[i] = histories[i - 1];
			}
			currentCount = DEFAULT_HISTORY_MAX_COUNT;
			// 插入新的历史记录
			histories[0] = history;
		}

		String newHistoryStr = null;
		if (currentCount > 1) {
			newHistoryStr = StringUtil.join(histories, SEPARATOR, 0, currentCount);
		} else {
			newHistoryStr = history;
		}

		return newHistoryStr;
	}

	/**
	 * <br>功能简述：获取历史记录
	 * @return 历史记录中的AppInfo列表
	 */
	public List<AppInfo> getHistory() {
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		String history = mPreferences.getString(HISTORY, "");
		String[] histories = history.split(SEPARATOR);

		String[] componentNames = new String[histories.length];
		for (int i = 0; i < histories.length; i++) {
			if (!TextUtils.isEmpty(histories[i])) {
				componentNames[i] = histories[i];
			}
		}

		for (int i = 0; i < componentNames.length; i++) {
			SearchAppManager searchManager = SearchSDK.getInstance().getSearchManager();
			if (searchManager != null) {
				AppInfo appInfo = searchManager.getAppInfo(componentNames[i]);
				if (appInfo != null) {
					// 过滤
					AppsFilter appsFilter = SearchSDK.getInstance().getAppsFilter();
					boolean isFilter = false;
					if (appsFilter != null) {
						isFilter = appsFilter.isFilter(appInfo.getPackageName());
					}
					if (!isFilter) {
						appInfos.add(appInfo);
					}
				} else {
					if (!TextUtils.isEmpty(componentNames[i])) {
						deleteHistory(ComponentName.unflattenFromString(componentNames[i]));
					}
				}
			}
		}
		return appInfos;
	}

	/**
	 * 清除搜索历史记录
	 */
	public void clearHistory() {
		mEditor.remove(HISTORY).commit();
	}

	/**
	 * <br>功能简述：根据传入appId删除历史记录
	 * <br>注意:
	 * @param mId 要删除应用的Id
	 */
	public void deleteHistory(ComponentName componentName) {
		String appId = String.valueOf(componentName.toShortString());
		String history = mPreferences.getString(HISTORY, "");
		if (history.contains(appId)) {
			String[] histories = history.split(SEPARATOR);
			for (int i = 0; i < histories.length; i++) {
				if (appId.equals(histories[i])) {
					for (int j = i; j < histories.length - 1; j++) {
						histories[j] = histories[j + 1];
					}
				}
			}
			history = StringUtil.join(histories, SEPARATOR, 0, histories.length - 1);
			mPreferences.edit().putString(HISTORY, history).commit();
		}
	}
}
