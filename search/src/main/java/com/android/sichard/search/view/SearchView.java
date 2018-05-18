package com.android.sichard.search.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sichard.search.Constants;
import com.android.sichard.search.R;
import com.android.sichard.search.RecentAppAdapter;
import com.android.sichard.search.SearchAdapter;
import com.android.sichard.search.SearchSDK;
import com.android.sichard.search.common.SearchAppManager;
import com.android.sichard.search.common.SearchEngineHelper;
import com.android.sichard.search.contact.ContactProvider;
import com.android.sichard.search.data.App.AppInfo;
import com.android.sichard.search.data.PreferencesManager;
import com.android.sichard.search.data.appcenter.AppCenterInfo;
import com.android.sichard.search.data.appcenter.AppCenterUtil;
import com.android.sichard.search.data.contact.ContactInfo;
import com.android.sichard.search.data.contact.ContactPhotoCache;
import com.android.sichard.search.data.hotSite.HotSiteList;
import com.android.sichard.search.data.hotgame.HotGameList;
import com.android.sichard.search.data.message.MessageInfo;
import com.android.sichard.search.data.message.MessageProvider;
import com.android.sichard.search.data.searchInWeb.SearchInWebInfo;
import com.android.sichard.search.utils.DeviceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 类描述：搜索界面外层容器 <br>
 * 详细描述： <br>
 * <b>Author sichard</b> <br>
 * <b>Date 2016-12-7</b>
 */
public class SearchView extends LinearLayout
        implements
            TextWatcher,
            TextView.OnEditorActionListener,
            View.OnClickListener {
    private Context mContext;
    /** 搜索引擎图标 */
    private ImageView mSearchEngineImg;
    /** 搜索输入框 */
    private EditText mInputBox;
    /** 搜索结果 */
    private SearchResultListView mSearchResultView;
    /** 清除按钮 */
    private View mClearText;
    /** 无搜索内容提示 */
    private TextView mSearchNoneContent;
    /** 搜索框 */
    private View mSearchBar;
    /** 网页搜索 */
    private SearchWebView mSearchWebView;
    /** 搜索管理器 */
    private SearchAppManager mAppSearchManager;
    /** 搜索列表的Adapter */
    private SearchAdapter mSearchAdapter;
    /** 的Adapter */
    private RecentAppAdapter mRecentAdapter;
    private List<AppInfo> recentAppList = new ArrayList<>();
    private View mSearchEngineSwitch;
    private ImageView mSearchIcon;

    private AppMonitor mAppMonitor;

    public SearchView(Context context) {
        super(context);
        mContext = context;
    }

    public SearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public SearchView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mInputBox = (EditText) findViewById(R.id.search_editText);
        mInputBox.addTextChangedListener(this);
        mInputBox.setOnEditorActionListener(this);

        mSearchResultView = (SearchResultListView) findViewById(R.id.search_result_view);
        mSearchResultView.setSearchView(this);
        mSearchResultView.setDivider(null);
        mSearchAdapter = new SearchAdapter(mContext);
        mRecentAdapter = new RecentAppAdapter(mContext);
        mSearchResultView.setAdapter(mRecentAdapter);
        mSearchResultView.invalidate();


        mClearText = findViewById(R.id.search_clear_text);
        mClearText.setOnClickListener(this);
        mClearText.setVisibility(GONE);

        mSearchNoneContent = (TextView) findViewById(R.id.search_none_content);

        mSearchEngineSwitch = findViewById(R.id.search_engine_switch);
        mSearchEngineSwitch.setOnClickListener(this);

        mSearchEngineImg = (ImageView) findViewById(R.id.search_engine_image);
        mSearchEngineImg.setImageResource(SearchEngineHelper.getEngineImg(mContext));

        mSearchBar = findViewById(R.id.search_bar);

        mSearchIcon = (ImageView) findViewById(R.id.search_img);
        mSearchIcon.setOnClickListener(this);

        mSearchWebView = (SearchWebView) findViewById(R.id.search_web_view);
        registerReceiver();
        registerAppMonitor();
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(SearchEngineHelper.ACTION_ENGINE_CHANGED);
        filter.addAction(SearchEngineHelper.ACTION_APP_CHANGED);
        filter.addAction(SearchEngineHelper.ACTION_CONTACT_CHANGED);
        filter.addAction(SearchEngineHelper.ACTION_MESSAGE_CHANGED);
        mContext.registerReceiver(new SearchReceiver(), filter);
    }

    private void registerAppMonitor() {
        try {
            mAppMonitor = new AppMonitor();
            // 注册应用包安装，卸载，更新事件广播监听器
            IntentFilter filter = new IntentFilter(Intent.ACTION_PACKAGE_ADDED);
            filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
            filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
            filter.addDataScheme("package");
            mContext.registerReceiver(mAppMonitor, filter);
            // 注册sd卡应用包资源是否可获取广播事件监听
            filter = new IntentFilter();
            filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_AVAILABLE);
            filter.addAction(Intent.ACTION_EXTERNAL_APPLICATIONS_UNAVAILABLE);
            mContext.registerReceiver(mAppMonitor, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unRegisterAppMonitor() {
        try {
            mContext.unregisterReceiver(mAppMonitor);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class SearchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case SearchEngineHelper.ACTION_ENGINE_CHANGED:
                    mSearchEngineImg.setImageResource(SearchEngineHelper.getEngineImg(context));
                    if (mSearchWebView != null && mSearchWebView.getVisibility() == VISIBLE) {
                        showWebSearch();
                    }
                    break;
                case SearchEngineHelper.ACTION_APP_CHANGED:
                    if (mInputBox != null && mInputBox.getText() != null) {
                        onTextChanged(mInputBox.getText().toString(), 0, 0, 0);
                    }
                    break;
                case SearchEngineHelper.ACTION_CONTACT_CHANGED:
                    if (mInputBox != null && mInputBox.getText() != null) {
                        onTextChanged(mInputBox.getText().toString(), 0, 0, 0);
                    }
                    break;
                case SearchEngineHelper.ACTION_MESSAGE_CHANGED:
                    if (mInputBox != null && mInputBox.getText() != null) {
                        onTextChanged(mInputBox.getText().toString(), 0, 0, 0);
                    }
                    break;
            }
        }
    }

    /**
     * 应用安装卸载更新监听
     */
    private class AppMonitor extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent) {
                return;
            }
            SearchSDK.getInstance().getSearchManager().reloadApplist(mContext);
            updateSearchResult();
            SearchSDK.getInstance().updateRecent();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (mSearchResultView != null) {
            if (s.length() == 0) {
                mSearchResultView.setAdapter(mRecentAdapter);
                mSearchResultView.invalidate();
                mSearchResultView.setVisibility(VISIBLE);
                mClearText.setVisibility(GONE);
                mSearchNoneContent.setVisibility(GONE);
                mSearchIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_search));
                // 关闭搜索WebView
                closeSearchWebView();
            } else {
                mSearchIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_enable));
                mClearText.setVisibility(VISIBLE);
                List<AppInfo> appInfos = null;
                if (PreferencesManager.getInstance(mContext).getBoolean(SearchEngineHelper.KEY_IS_SHOW_APP, true)) {
                    // app搜索结果
                    // start ouyonglun: mAppSearchManager可能存在未加载的情况，所有最好用到的时候才取
                    if (mAppSearchManager == null) {
                        mAppSearchManager = SearchSDK.getInstance().getSearchManager();
                    }
                    if (mAppSearchManager != null) {
                        appInfos = mAppSearchManager.search(s.toString());
                    }
                    // end
                }
                mSearchAdapter.setAppList(appInfos);

                ArrayList<ContactInfo> contactInfos = null;
                if (PreferencesManager.getInstance(mContext).getBoolean(SearchEngineHelper.KEY_IS_SHOW_CONTACT, true)) {
                    // 联系人搜索结果
                    contactInfos = ContactProvider.getInstance().getContacts(s.toString());
                    mSearchAdapter.setContactList(contactInfos);
                    mSearchAdapter.setContactPhotoLoadListener(new ContactPhotoCache.ContactPhotoLoadListener() {

                        @Override
                        public void onLoadFinished(ContactInfo contact) {
                            final ContactInfo info = contact;
                            // 需要加null判断因为onLoadFinished回调需要一定的时间，
                            // 该时间段内退出搜索mSearchResultView.getHandler()会为null,
                            // 而且该时候处理更新是没意义的。所以直接null保护一下就可以了
                            if (mSearchResultView.getHandler() != null) {
                                mSearchResultView.getHandler().post(new Runnable() {
                                    @Override
                                    public void run() {
                                        View convertview = mSearchResultView.findViewWithTag(info);
                                        if (null != convertview) {
                                            mSearchAdapter.updateConvertView(convertview, info);
                                        }
                                    }
                                });
                            }
                        }
                    });
                } else {
                    mSearchAdapter.setContactList(null);
                }

                List<MessageInfo> messageInfoList = null;
                if (PreferencesManager.getInstance(mContext).getBoolean(SearchEngineHelper.KEY_IS_SHOW_MESSAGE, true)) {
                    messageInfoList = MessageProvider.getInstance().searchMessage(s.toString());
                }
                mSearchAdapter.setMessageListAndMore(messageInfoList);

                // searchInWeb
                SearchInWebInfo searchInWebInfo = new SearchInWebInfo(null, s.toString());
                searchInWebInfo.setAction(new Runnable() {
                    @Override
                    public void run() {
                        showWebSearch();
                    }
                });
                mSearchAdapter.setSearchInWeb(searchInWebInfo);

                AppCenterInfo appCenterInfo = AppCenterUtil.getAppCenterInfo(getContext(), s.toString());
                if (appCenterInfo != null) {
                    mSearchAdapter.setAppCenterList(appCenterInfo);
                }

                // 重新设置一次adapter，mSearchResultView才会重新调用onLayout，进而调用app列表的onLayout
                mSearchResultView.setAdapter(mSearchAdapter);

//                if (appInfos != null && appInfos.size() <= 0 && contactInfos != null && contactInfos.size() <= 0
//                        && messageInfoList != null && messageInfoList.size() <= 0 && appCenterInfo == null) {
//                    mSearchNoneContent.setVisibility(VISIBLE);
//                    mSearchNoneContent.setText(getResources().getString(R.string.search_none_content));
//                } else {
//                    mSearchNoneContent.setVisibility(GONE);
//                }
            }

        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            showWebSearch();
            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mSearchWebView.getVisibility() == VISIBLE) {
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                if(mSearchWebView.webViewGoBack()) {

                }else {
                    closeSearchWebView();
                }
                return true;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onClick(View v) {
        if (v == mClearText) {
            // 清除editText上的文字
            if (v == mClearText) {
                mInputBox.clearFocus();
                mInputBox.setText("");
                mClearText.setVisibility(GONE);
                // 关闭搜索WebView
                closeSearchWebView();
                return;
            }
        } else if (v == mSearchEngineSwitch) {
            try {
                final Intent intent = new Intent(Constants.ACTION_SEARCHSETTING);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (v == mSearchIcon) {
            showWebSearch();
        }
    }

    /**
     * 关闭搜索WebView
     */
    private void closeSearchWebView() {
        if (mSearchWebView != null && mSearchWebView.getVisibility() == VISIBLE) {
            if (mSearchWebView instanceof SearchWebView) {
                ((SearchWebView) mSearchWebView).removeWebView();
            }
            mSearchWebView.setVisibility(GONE);
        }
    }

    public void showWebSearch() {
        Editable text = mInputBox.getText();
        if (text == null) {
            return;
        }
        String key = text.toString();
        if (TextUtils.isEmpty(key)) {
            return;
        }
        showInputMethod(false);
        String engine = SearchEngineHelper.getEngine(getContext());
        String url =
                PreferencesManager.getInstance(mContext).getString(SearchEngineHelper.KEY_SEARCH_ENGINE_URL,
                        engine)
                        + key;

        mSearchWebView.loadUrl(url);
    }

    /**
     * 从网页搜索返回时，更新搜索结果
     *
     * @param isRequestFailed true，网络访问成功；false，有网络但网络请求失败(比如国内不翻墙访问谷歌)
     */
    public void updateSearchResult(boolean isRequestFailed) {
        if (DeviceUtil.isNetworkOK(mContext) && !isRequestFailed) {
            updateSearchResult();
        } else {
            mSearchNoneContent.setVisibility(VISIBLE);
            mSearchNoneContent.setText(getResources().getString(R.string.search_net_not_available));
            mSearchResultView.setVisibility(GONE);
        }
    }

    private void updateSearchResult() {
        if (mInputBox != null && mInputBox.getText() != null) {
            onTextChanged(mInputBox.getText().toString(), 0, 0, 0);
        }
    }

    public void showInputMethod(final boolean isShow) {
        // 此处延时是为了等待界面加载完成
        // 隐藏的时候，不需要延迟
        if (isShow) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager imm =
                            (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mInputBox.requestFocus();
                    imm.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
                }
            }, 100);
        } else {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            mInputBox.clearFocus();
            imm.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    public String getInputText() {
        return String.valueOf(mInputBox.getText());
    }

    public void setRecentAppList(List<AppInfo> recentAppList) {
        this.recentAppList.clear();
        for (AppInfo info : recentAppList) {
            if(info != null){
                Drawable d = info.getIcon();
                if (d != null) {
                    if (SearchSDK.getInstance().getThemeTools() != null) {
                        Drawable systemIcon = info.getSystemIcon();
                        if (systemIcon != null) {
                            systemIcon = SearchSDK.getInstance().getThemeTools()
                                    .createIcon(info.getComponentName(), systemIcon);
                        }
                        info.setIcon(systemIcon);
                    }
                }
                this.recentAppList.add(info);
            }
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mRecentAdapter.setAppList(recentAppList);
        //一进来不让edittext获得焦点
        showInputMethod(false);

        mRecentAdapter.setGameSiteList(new HotGameList(mContext));

        mRecentAdapter.setHotSiteList(new HotSiteList(mContext));
        
        if (mSearchWebView != null) {
            mRecentAdapter.setSearchWebView(mSearchWebView);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        releaseSearchView();
    }

    public void releaseSearchView() {
        mInputBox.clearFocus();
        mInputBox.setText("");
        mClearText.setVisibility(GONE);
        mSearchWebView.setVisibility(GONE);
    }
}
