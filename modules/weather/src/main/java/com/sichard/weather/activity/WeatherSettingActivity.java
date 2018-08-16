package com.sichard.weather.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sichard.weather.Interface.IWeatherDataObserver;
import com.sichard.weather.R;
import com.sichard.weather.WeatherDataChangeEvent;
import com.sichard.weather.WeatherDataManager;
import com.sichard.weather.adapter.LocalCityAdapter;
import com.sichard.weather.view.DragSlideListView;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.util.Collections;
import java.util.List;

/**
 * <br>类描述:天气设置activity
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/20</b>
 */

public class WeatherSettingActivity extends WeatherBaseActivity implements View.OnClickListener, IWeatherDataObserver, AdapterView.OnItemClickListener {

    private DragSlideListView mListView;
    private LocalCityAdapter mAdapter;
    private View mFooterView;
    private ImageView mCloseBtn;
    private LinearLayout mFixedPositionLayout;
    private TextView mFixedPositionCity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_weather_setting);
        super.onCreate(savedInstanceState);
        WeatherDataManager.getInstance().register(this);
        initView();
        initData();
    }

    private void initView() {
        findViewById(R.id.activity_weather_setting_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                return false;
            }
        });
        mCloseBtn = (ImageView) findViewById(R.id.weather_setting_closed_btn);
        mCloseBtn.setOnClickListener(this);
        mListView = (DragSlideListView) findViewById(R.id.weather_city_list);
        mFixedPositionLayout = (LinearLayout) findViewById(R.id.fixed_position);
        mFixedPositionLayout.setOnClickListener(this);
        mFixedPositionCity = (TextView) findViewById(R.id.fixed_position_city);
    }

    private void initData() {
        final WeatherDataEntity fixedPositionData = WeatherDataManager.getInstance().getFixedPositionData();
        if (fixedPositionData != null) {
            mFixedPositionLayout.setVisibility(View.VISIBLE);
            mFixedPositionCity.setText(fixedPositionData.mLocationEntity.getLocalizedName());
        } else {
            mFixedPositionLayout.setVisibility(View.GONE);
        }
        final List<WeatherDataEntity> weatherDataEntityList = WeatherDataManager.getInstance().getSavedWeatherDataEntityList();
        mAdapter = new LocalCityAdapter(this, weatherDataEntityList);
        mFooterView = LayoutInflater.from(this).inflate(R.layout.weather_local_list_footer, mListView, false);
        mFooterView.setOnClickListener(this);
        mListView.addFooterView(mFooterView);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setOnChangeListener(new DragSlideListView.OnChangedListener() {
            @Override
            public void onChange(int start, int to) {
                if (to >= weatherDataEntityList.size() || start >= weatherDataEntityList.size()) {
                    return;
                }
                synchronized (weatherDataEntityList) {

                    //数据交换
                    if (start < to) {
                        for (int i = start; i < to; i++) {
                            Collections.swap(weatherDataEntityList, i, i + 1);
                        }
                    } else if (start > to) {
                        for (int i = start; i > to; i--) {
                            Collections.swap(weatherDataEntityList, i, i - 1);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == mFooterView) {
            Intent intent = new Intent(this, CityActivity.class);
            startActivity(intent);
        } else if (v == mCloseBtn) {
            finish();
        } else if (v == mFixedPositionLayout) {
            this.setResult(0);
            Intent intent = new Intent();
            intent.putExtra("select_page", 0);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onDataChanged(WeatherDataChangeEvent event, List<WeatherDataEntity> chgDataList) {
        if (chgDataList == null || chgDataList.size() <= 0) {
            return;
        }
        // 如果是event是swap，则不需要更新数据，因为长按拖动时，列表已更新
        if (event == WeatherDataChangeEvent.ADD) {
            mAdapter.addData(chgDataList);
        } else if (event == WeatherDataChangeEvent.DELETE) {
            mAdapter.deleteData(chgDataList);
        } else if (event == WeatherDataChangeEvent.FIXED_POSITION_ADD) {
            mFixedPositionLayout.setVisibility(View.VISIBLE);
            mFixedPositionCity.setText(chgDataList.get(0).mLocationEntity.getLocalizedName());
        } else if (event == WeatherDataChangeEvent.FIXED_POSITION_CHANGE) {
            mFixedPositionCity.setText(chgDataList.get(0).mLocationEntity.getLocalizedName());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WeatherDataManager.getInstance().unregister(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // 如果加入了自动定位的城市，则WeatherCityActivity选择的位置需要加1
        if (mFixedPositionLayout.getVisibility() == View.VISIBLE) {
            this.setResult(position + 1);
            Intent intent = new Intent();
            intent.putExtra("select_page", position + 1);
            this.setResult(RESULT_OK, intent);
            finish();
        } else {
            this.setResult(position);
            Intent intent = new Intent();
            intent.putExtra("select_page", position);
            this.setResult(RESULT_OK, intent);
            finish();
        }
    }
}
