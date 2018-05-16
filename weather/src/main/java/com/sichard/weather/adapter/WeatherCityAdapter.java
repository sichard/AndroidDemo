package com.sichard.weather.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.sichard.weather.WeatherFragment;
import com.sichard.weather.WeatherTaskManager;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.util.List;

/**
 * <br>类描述:城市列表详情页的Adapter
 * <br>详细描述:继承FragmentStatePagerAdapter，提前加载ViewPager所需数据，为了可以动态删除，需要实现getItemPosition()方法，
 * 并且返回PagerAdapter.POSITION_NONE.
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/15</b>
 */

public class WeatherCityAdapter extends FragmentStatePagerAdapter {
    private List<WeatherFragment> mFragmentList;

    public WeatherCityAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    public void setFragmentList(List<WeatherFragment> fragmentList) {
        this.mFragmentList = fragmentList;
        notifyDataSetChanged();
    }

    /**
     * 向详情页面添加指定城市
     * @param weatherFragment
     */
    public void addFragment(WeatherFragment weatherFragment) {
        if (weatherFragment != null) {
            mFragmentList.add(weatherFragment);
            notifyDataSetChanged();
        }
    }

    /**
     * 向详情页面添加自动定位的城市
     * @param weatherFragment
     */
    public void addFixedPositionFragment(WeatherFragment weatherFragment) {
        if (weatherFragment != null) {
            mFragmentList.add(0, weatherFragment);
            notifyDataSetChanged();
        }
    }

    /**
     * 更换自动定位的城市
     * @param weatherFragment
     */
    public void changeFixedPositionFragment(WeatherFragment weatherFragment) {
        if (weatherFragment != null) {
            mFragmentList.remove(0);
            mFragmentList.add(0, weatherFragment);
            notifyDataSetChanged();
        }
    }

    /**
     * 移除详情页面指定的城市
     * @param entity
     */
    public void removeFragment(WeatherDataEntity entity) {
        if (entity == null || mFragmentList == null) {
            return;
        }
        for (int i = 0; i < mFragmentList.size(); i++) {
            final WeatherFragment weatherFragment = mFragmentList.get(i);
            final WeatherDataEntity weatherDataEntity = weatherFragment.getWeatherDataEntity();
            if (entity.mLocationEntity.Key.equals(weatherDataEntity.mLocationEntity.Key)) {
                mFragmentList.remove(i);
                notifyDataSetChanged();
                return;
            }
        }
    }

    /**
     * 当{@link com.tct.launcher.weather.activity.WeatherSettingActivity}界面拖动改变位置时调用，更新详情界面的列表
     * @param fragmentList
     */
    public void updateFragmentList(List<WeatherFragment> fragmentList) {
        this.mFragmentList.clear();
        this.mFragmentList.addAll(fragmentList);
        WeatherTaskManager.execTaskOnUIThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
