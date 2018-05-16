package com.sichard.weather.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sichard.weather.R;
import com.sichard.weather.WeatherDataChangeEvent;
import com.sichard.weather.WeatherDataManager;
import com.sichard.weather.WeatherFragment;
import com.sichard.weather.adapter.WeatherCityAdapter;
import com.sichard.weather.weatherData.WeatherDataEntity;
import com.tct.launcher.weather.Interface.IWeatherDataObserver;

import java.util.ArrayList;
import java.util.List;

/**
 * <br>类描述:天气城市详情的Activity
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/15</b>
 */

public class WeatherCityActivity extends WeatherBaseActivity implements View.OnClickListener, IWeatherDataObserver {
    private static final int REQUEST_CODE = 0x1234;
    private ViewPager mViewPager;
    private WeatherCityAdapter mAdapter;
    private LinearLayout mNavigateBar;
    private ImageView mCloseAdBt;
    private Button mAdAction;
    private View mWeatherAdLayout;
    private boolean mIsInstallAdApp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_weather_city);
        super.onCreate(savedInstanceState);
        WeatherDataManager.getInstance().register(this);
        initView();
        initData();
    }

    private void initView() {
        mViewPager = (ViewPager) findViewById(R.id.weather_view_pager);
        mNavigateBar = (LinearLayout) findViewById(R.id.weather_city_activity_navigate);
        findViewById(R.id.weather_back).setOnClickListener(this);
        findViewById(R.id.weather_setting_btn).setOnClickListener(this);

        mAdAction = (Button) findViewById(R.id.weather_ad_button);
        mCloseAdBt = (ImageView) findViewById(R.id.close_ad_button);
        mWeatherAdLayout = findViewById(R.id.weather_ad_layout);

        mAdAction.setOnClickListener(this);
        mCloseAdBt.setOnClickListener(this);
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo("com.tct.weather", 0);
        } catch (Exception e) {
        }
        mIsInstallAdApp = info != null;
        if (mIsInstallAdApp) {
            mAdAction.setText(R.string.market_slogan_open);
        } else {
            mAdAction.setText(R.string.market_slogan_install);
        }
    }

    private void initData() {
        List<WeatherFragment> fragmentList = new ArrayList<>();

        // 添加自动定位获得的城市
        final WeatherDataEntity fixedPositionData = WeatherDataManager.getInstance().getFixedPositionData();
        if (fixedPositionData != null) {
            final WeatherFragment weatherFragment = new WeatherFragment();
            weatherFragment.setLocationEntity(fixedPositionData);
            weatherFragment.setNavigateBar(mNavigateBar);
            weatherFragment.setFixedPosition(true);
            fragmentList.add(weatherFragment);
        }

        // 添加用户手动添加的城市
        List<WeatherDataEntity> weatherDataEntityList = WeatherDataManager.getInstance().getSavedWeatherDataEntityList();
        if (weatherDataEntityList != null && weatherDataEntityList.size() > 0) {
            for (int i = 0; i < weatherDataEntityList.size(); i++) {
                final WeatherDataEntity weatherDataEntity = weatherDataEntityList.get(i);
                final WeatherFragment weatherFragment = new WeatherFragment();
                weatherFragment.setLocationEntity(weatherDataEntity);
                weatherFragment.setNavigateBar(mNavigateBar);
                fragmentList.add(weatherFragment);
            }
        }

        if (fragmentList == null || fragmentList.size() <= 0) {
            return;
        }
        mAdapter = new WeatherCityAdapter(getSupportFragmentManager());
        mAdapter.setFragmentList(fragmentList);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                // 当切换城市时，更新NavigateBar的位置
                final WeatherFragment fragment = (WeatherFragment) ((WeatherCityAdapter) (mViewPager.getAdapter())).getItem(position);
                final View view = fragment.getView();
                if (view != null) {
                    final int scrollY = view.getScrollY();
                    mNavigateBar.scrollTo(0, scrollY);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.weather_back) {
            finish();

        } else if (i == R.id.weather_setting_btn) {
            Intent intent = new Intent(this, WeatherSettingActivity.class);
            startActivityForResult(intent, REQUEST_CODE);

        }
        if (v == mAdAction) {
            if (mIsInstallAdApp) {
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                ComponentName cn = new ComponentName("com.tct.weather", "com.tct.weather.MainActivity");
                intent.putExtra("from_lscreen", true);
                intent.setComponent(cn);

                WeatherDataEntity mWeatherDataEntity = null;
                if (mAdapter != null && mViewPager != null) {
                    WeatherFragment weatherFragment = (WeatherFragment) mAdapter.getItem(mViewPager.getCurrentItem());
                    mWeatherDataEntity = weatherFragment.getWeatherDataEntity();
                }
                if (mWeatherDataEntity != null && mWeatherDataEntity.mLocationEntity != null && !TextUtils.isEmpty(mWeatherDataEntity.mLocationEntity.Key)) {
                    intent.putExtra("newCityKey", mWeatherDataEntity.mLocationEntity.Key);
                }
                startActivity(intent);
            } else {
                String weatherApp = "com.tct.weather";
                try {
                    Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.vending");
                    // package name and activity
                    ComponentName comp = new ComponentName("com.android.vending", "com.google.android.finsky.activities.LaunchUrlHandlerActivity");
                    launchIntent.setComponent(comp);
                    launchIntent.setData(Uri.parse("market://details?id=" + weatherApp));
                    launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(launchIntent);
                } catch (Exception anfe) {
                    Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + weatherApp));
                    intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent1);
                }
            }
        } else if (v == mCloseAdBt) {
            mWeatherAdLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == RESULT_OK) {
            mViewPager.setCurrentItem(data.getIntExtra("select_page", 0));
        }
    }

    @Override
    public void onDataChanged(WeatherDataChangeEvent event, List<WeatherDataEntity> chgDataList) {
        final WeatherDataEntity fixedPositionDataEntity = WeatherDataManager.getInstance().getFixedPositionData();
        if (fixedPositionDataEntity == null && chgDataList == null) {
            return;
        }

        if (event == WeatherDataChangeEvent.ADD) {
            for(WeatherDataEntity weatherDataEntity : chgDataList) {
                final WeatherFragment weatherFragment = new WeatherFragment();
                weatherFragment.setLocationEntity(weatherDataEntity);
                weatherFragment.setNavigateBar(mNavigateBar);
                mAdapter.addFragment(weatherFragment);
            }
        } else if (event == WeatherDataChangeEvent.DELETE){
            //删除城市
            for(WeatherDataEntity weatherDataEntity : chgDataList) {
                mAdapter.removeFragment(weatherDataEntity);
            }
        } else if (event == WeatherDataChangeEvent.SWAP) {
            // TODO: 17-6-5 此处将原有数据删除，重新创建Fragment，是否可以对原有数据排序
            //天气设置界面拖动发生位置改变时，此处需对应对数据进行更新
            List<WeatherFragment> fragmentList = new ArrayList<>();
            // 添加自动定位的城市
            if (fixedPositionDataEntity != null) {
                final WeatherFragment fixedPositionWeatherFragment = new WeatherFragment();
                fixedPositionWeatherFragment.setLocationEntity(fixedPositionDataEntity);
                fixedPositionWeatherFragment.setNavigateBar(mNavigateBar);
                fixedPositionWeatherFragment.setFixedPosition(true);
                fragmentList.add(fixedPositionWeatherFragment);
            }
            // 添加用户手动添加的城市
            for (int i = 0; i < chgDataList.size(); i++) {
                final WeatherDataEntity weatherDataEntity = chgDataList.get(i);
                final WeatherFragment weatherFragment = new WeatherFragment();
                weatherFragment.setLocationEntity(weatherDataEntity);
                weatherFragment.setNavigateBar(mNavigateBar);
                fragmentList.add(weatherFragment);
            }
            mAdapter.updateFragmentList(fragmentList);
        } else if (event == WeatherDataChangeEvent.FIXED_POSITION_ADD) {
            final WeatherDataEntity weatherDataEntity = chgDataList.get(0);
            final WeatherFragment weatherFragment = new WeatherFragment();
            weatherFragment.setLocationEntity(weatherDataEntity);
            weatherFragment.setNavigateBar(mNavigateBar);
            weatherFragment.setFixedPosition(true);
            mAdapter.addFixedPositionFragment(weatherFragment);
        } else if (event == WeatherDataChangeEvent.FIXED_POSITION_CHANGE) {
            final WeatherDataEntity weatherDataEntity = chgDataList.get(0);
            final WeatherFragment weatherFragment = new WeatherFragment();
            weatherFragment.setLocationEntity(weatherDataEntity);
            weatherFragment.setNavigateBar(mNavigateBar);
            weatherFragment.setFixedPosition(true);
            mAdapter.changeFixedPositionFragment(weatherFragment);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeatherDataManager.getInstance().unregister(this);
    }
}
