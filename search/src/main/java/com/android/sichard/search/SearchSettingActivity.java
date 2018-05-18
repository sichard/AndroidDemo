package com.android.sichard.search;

import android.content.Intent;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.android.sichard.search.common.SearchEngineHelper;
import com.android.sichard.search.common.TitleActivity;
import com.android.sichard.search.data.PreferencesManager;


import java.util.HashMap;

/**
 * <br>类表述:搜索设置
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date   2016-8-17</b>
 */
public class SearchSettingActivity extends TitleActivity implements View.OnClickListener {

    /**
     * 引擎选择布局
     */
    private LinearLayout mEngineGoogle, mEngineBaiDu, mEngineBing, mEngineYandex;
    /**
     * 是否开启选项开关
     */
    private Switch mAppsSwitch, mContactSwitch, mMessageSwitch;
    /**
     * 引擎选择指示器
     */
    private ImageView mGoogleSelect, mBaiDuSelect, mBingSelect, mYandexSelect;
    /**
     * 引擎选择指示器
     */
    private ImageView mGoogleNoSelect, mBaiDuNoSelect, mBingNoSelect, mYandexNoSelect;
    /**
     * 是否开启选项总布局
     */
    private LinearLayout mAppsSwitchLayout, mContactSwitchLayout, mMessageSwitchLayout;
    /**
     * feedback入口
     */
    private LinearLayout mItemFeedback;

    private String oringeEngine = "";
    private String currentEngine = "";

    private boolean mSearchAppSwitch = true;
    private boolean mSearchContactsSwitch = true;
    private boolean mSearchMessageSwitch = true;

    @Override
    protected void findContentView() {
        mEngineGoogle = (LinearLayout) findViewById(R.id.search_engine_google);
        mEngineBaiDu = (LinearLayout) findViewById(R.id.search_engine_baidu);
        mEngineBing = (LinearLayout) findViewById(R.id.search_engine_bing);
        mEngineYandex = (LinearLayout) findViewById(R.id.search_engine_yandex);

        mGoogleSelect = (ImageView) findViewById(R.id.search_select_google);
        mBaiDuSelect = (ImageView) findViewById(R.id.search_select_baidu);
        mBingSelect = (ImageView) findViewById(R.id.search_select_bing);
        mYandexSelect = (ImageView) findViewById(R.id.search_select_yandex);

        mGoogleNoSelect = (ImageView) findViewById(R.id.search_no_select_google);
        mBaiDuNoSelect = (ImageView) findViewById(R.id.search_no_select_baidu);
        mBingNoSelect = (ImageView) findViewById(R.id.search_no_select_bing);
        mYandexNoSelect = (ImageView) findViewById(R.id.search_no_select_yandex);

        mAppsSwitch = (Switch) findViewById(R.id.search_switch_local_app);
        mContactSwitch = (Switch) findViewById(R.id.search_switch_contact);
        mMessageSwitch = (Switch) findViewById(R.id.search_switch_message);

        mAppsSwitchLayout = (LinearLayout) findViewById(R.id.search_switch_local_app_layout);
        mContactSwitchLayout = (LinearLayout) findViewById(R.id.search_switch_contact_layout);
        mMessageSwitchLayout = (LinearLayout) findViewById(R.id.search_switch_message_layout);

        mItemFeedback = (LinearLayout)findViewById(R.id.layout_feedback);
    }

    @Override
    protected void initContentView() {
        mEngineGoogle.setOnClickListener(this);
        mEngineBaiDu.setOnClickListener(this);
        mEngineBing.setOnClickListener(this);
        mEngineYandex.setOnClickListener(this);
        mItemFeedback.setOnClickListener(this);

        mAppsSwitch.setChecked(PreferencesManager.getInstance(this).getBoolean(SearchEngineHelper.KEY_IS_SHOW_APP, true));
        mAppsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    mSearchAppSwitch = false;
                }
                PreferencesManager.getInstance(mActivity).putBoolean(SearchEngineHelper.KEY_IS_SHOW_APP, isChecked);
                mActivity.sendBroadcast(new Intent(SearchEngineHelper.ACTION_APP_CHANGED));
            }
        });
        mContactSwitch.setChecked(PreferencesManager.getInstance(mActivity).getBoolean(SearchEngineHelper.KEY_IS_SHOW_CONTACT, true));
        mContactSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    mSearchContactsSwitch = false;
                }
                PreferencesManager.getInstance(mActivity).putBoolean(SearchEngineHelper.KEY_IS_SHOW_CONTACT, isChecked);
                mActivity.sendBroadcast(new Intent(SearchEngineHelper.ACTION_CONTACT_CHANGED));
            }
        });
        mMessageSwitch.setChecked(PreferencesManager.getInstance(mActivity).getBoolean(SearchEngineHelper.KEY_IS_SHOW_MESSAGE, true));
        mMessageSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(!isChecked) {
                    mSearchMessageSwitch = false;
                }
                PreferencesManager.getInstance(mActivity).putBoolean(SearchEngineHelper.KEY_IS_SHOW_MESSAGE, isChecked);
                mActivity.sendBroadcast(new Intent(SearchEngineHelper.ACTION_MESSAGE_CHANGED));
            }
        });

        mAppsSwitchLayout.setOnClickListener(this);
        mContactSwitchLayout.setOnClickListener(this);
        mMessageSwitchLayout.setOnClickListener(this);

        setEngineState();
    }

    private void setEngineState() {
        String engine = SearchEngineHelper.getEngine(mActivity);
        oringeEngine = engine;
        currentEngine = engine;
        switch (engine) {
            case SearchEngineHelper.SEARCH_ENGINE_GOOGLE_URL:
                mGoogleSelect.setVisibility(View.VISIBLE);
                mBaiDuSelect.setVisibility(View.GONE);
                mBingSelect.setVisibility(View.GONE);
                mYandexSelect.setVisibility(View.GONE);

                mGoogleNoSelect.setVisibility(View.GONE);
                mBaiDuNoSelect.setVisibility(View.VISIBLE);
                mBingNoSelect.setVisibility(View.VISIBLE);
                mYandexNoSelect.setVisibility(View.VISIBLE);
                break;
            case SearchEngineHelper.SEARCH_ENGINE_BAI_DU_URL:
                mGoogleSelect.setVisibility(View.GONE);
                mBaiDuSelect.setVisibility(View.VISIBLE);
                mBingSelect.setVisibility(View.GONE);
                mYandexSelect.setVisibility(View.GONE);

                mGoogleNoSelect.setVisibility(View.VISIBLE);
                mBaiDuNoSelect.setVisibility(View.GONE);
                mBingNoSelect.setVisibility(View.VISIBLE);
                mYandexNoSelect.setVisibility(View.VISIBLE);
                break;
            case SearchEngineHelper.SEARCH_ENGINE_BING_URL:
                mGoogleSelect.setVisibility(View.GONE);
                mBaiDuSelect.setVisibility(View.GONE);
                mBingSelect.setVisibility(View.VISIBLE);
                mYandexSelect.setVisibility(View.GONE);

                mGoogleNoSelect.setVisibility(View.VISIBLE);
                mBaiDuNoSelect.setVisibility(View.VISIBLE);
                mBingNoSelect.setVisibility(View.GONE);
                mYandexNoSelect.setVisibility(View.VISIBLE);
                break;
            case SearchEngineHelper.SEARCH_ENGINE_YANDEX_URL:
                mGoogleSelect.setVisibility(View.GONE);
                mBaiDuSelect.setVisibility(View.GONE);
                mBingSelect.setVisibility(View.GONE);
                mYandexSelect.setVisibility(View.VISIBLE);

                mGoogleNoSelect.setVisibility(View.VISIBLE);
                mBaiDuNoSelect.setVisibility(View.VISIBLE);
                mBingNoSelect.setVisibility(View.VISIBLE);
                mYandexNoSelect.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.activity_search_setting;
    }

    @Override
    protected int getTitleText() {
        return R.string.search_setting;
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();
        HashMap<String,String> searchEngineReport = new HashMap();
        if (v == mEngineGoogle) {
            mGoogleSelect.setVisibility(View.VISIBLE);
            mBaiDuSelect.setVisibility(View.GONE);
            mBingSelect.setVisibility(View.GONE);
            mYandexSelect.setVisibility(View.GONE);

            mGoogleNoSelect.setVisibility(View.GONE);
            mBaiDuNoSelect.setVisibility(View.VISIBLE);
            mBingNoSelect.setVisibility(View.VISIBLE);
            mYandexNoSelect.setVisibility(View.VISIBLE);
            PreferencesManager.getInstance(mActivity).putString(SearchEngineHelper.KEY_SEARCH_ENGINE_URL, SearchEngineHelper.SEARCH_ENGINE_GOOGLE_URL);
            currentEngine = SearchEngineHelper.SEARCH_ENGINE_GOOGLE_URL;
            intent.setAction(SearchEngineHelper.ACTION_ENGINE_CHANGED);
            mActivity.sendBroadcast(intent);
        } else if (v == mEngineBaiDu) {
             mGoogleSelect.setVisibility(View.GONE);
            mBaiDuSelect.setVisibility(View.VISIBLE);
            mBingSelect.setVisibility(View.GONE);
            mYandexSelect.setVisibility(View.GONE);

            mGoogleNoSelect.setVisibility(View.VISIBLE);
            mBaiDuNoSelect.setVisibility(View.GONE);
            mBingNoSelect.setVisibility(View.VISIBLE);
            mYandexNoSelect.setVisibility(View.VISIBLE);
            PreferencesManager.getInstance(mActivity).putString(SearchEngineHelper.KEY_SEARCH_ENGINE_URL, SearchEngineHelper.SEARCH_ENGINE_BAI_DU_URL);
            currentEngine = SearchEngineHelper.SEARCH_ENGINE_BAI_DU_URL;
            intent.setAction(SearchEngineHelper.ACTION_ENGINE_CHANGED);
            mActivity.sendBroadcast(intent);
        }   else if (v == mEngineBing) {
            mGoogleSelect.setVisibility(View.GONE);
            mBaiDuSelect.setVisibility(View.GONE);
            mBingSelect.setVisibility(View.VISIBLE);
            mYandexSelect.setVisibility(View.GONE);

            mGoogleNoSelect.setVisibility(View.VISIBLE);
            mBaiDuNoSelect.setVisibility(View.VISIBLE);
            mBingNoSelect.setVisibility(View.GONE);
            mYandexNoSelect.setVisibility(View.VISIBLE);
            currentEngine = SearchEngineHelper.SEARCH_ENGINE_BING_URL;
            PreferencesManager.getInstance(mActivity).putString(SearchEngineHelper.KEY_SEARCH_ENGINE_URL, SearchEngineHelper.SEARCH_ENGINE_BING_URL);
            intent.setAction(SearchEngineHelper.ACTION_ENGINE_CHANGED);
            mActivity.sendBroadcast(intent);
        }  else if (v == mEngineYandex) {
            mGoogleSelect.setVisibility(View.GONE);
            mBaiDuSelect.setVisibility(View.GONE);
            mBingSelect.setVisibility(View.GONE);
            mYandexSelect.setVisibility(View.VISIBLE);

            mGoogleNoSelect.setVisibility(View.VISIBLE);
            mBaiDuNoSelect.setVisibility(View.VISIBLE);
            mBingNoSelect.setVisibility(View.VISIBLE);
            mYandexNoSelect.setVisibility(View.GONE);
            currentEngine = SearchEngineHelper.SEARCH_ENGINE_YANDEX_URL;
            PreferencesManager.getInstance(mActivity).putString(SearchEngineHelper.KEY_SEARCH_ENGINE_URL, SearchEngineHelper.SEARCH_ENGINE_YANDEX_URL);
            intent.setAction(SearchEngineHelper.ACTION_ENGINE_CHANGED);
            mActivity.sendBroadcast(intent);
        } else if (v == mAppsSwitchLayout) {
            mAppsSwitch.setChecked(!mAppsSwitch.isChecked());
        } else if (v == mContactSwitchLayout) {
            mContactSwitch.setChecked(!mContactSwitch.isChecked());
        } else if (v == mMessageSwitchLayout) {
            mMessageSwitch.setChecked(!mMessageSwitch.isChecked());
        } else if (v == mItemFeedback) {
            try {
                intent = new Intent(Constants.ACTION_FEEDBACK);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("source","from_search_setting");
                mActivity.startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
