package com.sichard.weather.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sichard.weather.AccuWeather;
import com.sichard.weather.Interface.OnLocationChangedListener;
import com.sichard.weather.R;
import com.sichard.weather.WeatherDataManager;
import com.sichard.weather.WeatherLocationManager;
import com.sichard.weather.WeatherWidgetManager;
import com.sichard.weather.adapter.CityAdapter;
import com.sichard.weather.utils.DeviceUtil;
import com.sichard.weather.weatherData.LocationEntity;
import com.sichard.weather.weatherData.WeatherDataEntity;

import java.util.List;

/**
 *<br>类描述：手动添加城市的界面
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 17-6-5</b>
 */
public class CityActivity extends WeatherBaseActivity implements View.OnClickListener, OnLocationChangedListener {
    private static final int NORMAL = 0;
    private static final int EMPTY = 1;
    private static final int NO_NETWORK = 2;
    private static final int NO_RESULT = 3;
    private EditText mCityEditText;
    private TextView mLocatedCity;
    private ImageView mRefreshCity;
    private ImageView mLocateIcon;
    private ListView mListView;
    private LinearLayout mNoResult;
    private RelativeLayout mTipsView;
    private LocationEntity mCurrentLocationEntity;
    private ImageView mBackBtn, mCleanBtn;
    private AsyncTask<Double, Void, LocationEntity> mGetCityTask;
    /** 是否需要自动定位或重新定位的标志位 */
    private boolean mIsAutoLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_city);
        super.onCreate(savedInstanceState);
        findContentView();
    }

    private void findContentView() {
        mCityEditText = (EditText) findViewById(R.id.search_city_edit);
        mLocatedCity = (TextView) findViewById(R.id.auto_locate_city);
        mRefreshCity = (ImageView) findViewById(R.id.refresh_locate);
        mLocateIcon = (ImageView) findViewById(R.id.locate_icon);
        mNoResult = (LinearLayout) findViewById(R.id.no_result_layout);
        mTipsView = (RelativeLayout) findViewById(R.id.tips_view);
        mListView = (ListView) findViewById(R.id.listview);
        mBackBtn = (ImageView) findViewById(R.id.weather_location_back_btn);
        mBackBtn.setOnClickListener(this);
        mCleanBtn = (ImageView) findViewById(R.id.weather_text_clean_btn);
        mCleanBtn.setOnClickListener(this);


        mCityEditText.addTextChangedListener(mTextChangedListener);
        mCityEditText.setOnEditorActionListener(mEditorActionListener);
        mCityEditText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });

        mRefreshCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WeatherLocationManager.getInstance().requestLocation();
            }
        });

        mLocatedCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mCurrentLocationEntity != null){
                    Intent intent = new Intent(CityActivity.this, WeatherCityActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        mIsAutoLocation = getIntent().getBooleanExtra("IS_AUTO_LOCATION", false);
        if (mIsAutoLocation) {
            loadLocation();
        } else {
            findViewById(R.id.auto_locate).setVisibility(View.GONE);
        }
    }

    private void loadLocation() {
        WeatherLocationManager.getInstance().addOnLocationChangedListener(this);
        WeatherLocationManager.getInstance().requestLocation();
    }

    @Override
    public void onLocationStart() {
        mLocateIcon.setImageResource(R.drawable.ic_weather_location);
        mLocatedCity.setText(getResources().getString(R.string.locating_tip));
        mRefreshCity.setVisibility(View.GONE);
    }

    @Override
    public void onLocationTimeout() {
        onLocationFail();
    }

    @Override
    public void onLocationFail() {
        mCurrentLocationEntity = null;
        mLocateIcon.setImageResource(R.drawable.location_failed);
        mLocatedCity.setText(getResources().getString(R.string.locate_fail));
        mRefreshCity.setVisibility(View.VISIBLE);
        WeatherLocationManager.getInstance().removeOnLocationListener(this);
    }

    @Override
    public void onLocationSuccess(double latitude, double longitude) {
        mGetCityTask = new GetCityLocationEntity().execute(latitude, longitude);
        WeatherLocationManager.getInstance().removeOnLocationListener(this);
    }

    /**
     * 通过经纬度获取LocationEntity的task
     */
    private class GetCityLocationEntity extends AsyncTask<Double, Void, LocationEntity> {

        @Override
        protected LocationEntity doInBackground(Double... params) {
            String language = getResources().getConfiguration().locale.toString();
            LocationEntity locationEntity = null;
            try {
                //此处返回的区域太精确，需要二次定位到所需要的城市
                locationEntity = AccuWeather.getInstance(CityActivity.this.getApplicationContext()).getCityByCoordinate(params[0], params[1], language);
                // 二次定位所需要的城市
                if (locationEntity != null && locationEntity.Key != null && locationEntity.Details != null && locationEntity.Details.CanonicalLocationKey != null
                        && !locationEntity.Key.equals(locationEntity.Details.CanonicalLocationKey)) {
                    locationEntity = AccuWeather.getInstance(CityActivity.this.getApplicationContext()).getCityByLocationKey(locationEntity.Details.CanonicalLocationKey, language);
                } else if (locationEntity != null && TextUtils.isEmpty(locationEntity.LocalizedName)) {//zh_tw定位到香港的时候有且仅有LocalizedName为空，改成zh即可.
                    String lang = language;
                    if (language != null && language.contains("_")) {
                        lang = language.substring(0, language.indexOf("_"));
                    }
                    locationEntity = AccuWeather.getInstance(CityActivity.this.getApplicationContext()).getCityByLocationKey(locationEntity.Details.CanonicalLocationKey, lang);
                } else {
                }
            } catch (AccuWeather.NetworkRequestException e) {
                e.printStackTrace();
            }
            return locationEntity;
        }

        @Override
        protected void onPostExecute(LocationEntity locationEntity) {
            super.onPostExecute(locationEntity);
            if (locationEntity == null) {
                return;
            }
            mCurrentLocationEntity = locationEntity;
            StringBuilder sb = new StringBuilder();
            sb.append(TextUtils.isEmpty(locationEntity.getDisplayName()) ? "" : locationEntity.getDisplayName());
            sb.append(getDisplayCity(locationEntity));
            mLocatedCity.setText(sb.toString());

            WeatherDataManager.getInstance().saveFixedPositionData(new WeatherDataEntity(locationEntity));
        }
    }

    /**
     * 监听输入框变化
     */
    TextWatcher mTextChangedListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s){
            searchCity();
        }
    };

    TextView.OnEditorActionListener mEditorActionListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.i("sjh3", "actionId = " + actionId);
            if(event != null)
                Log.i("sjh3",  "event" + event.toString());
            if (actionId == EditorInfo.IME_ACTION_SEND || actionId == EditorInfo.IME_ACTION_SEARCH
                    || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                DeviceUtil.closeKeyboard(CityActivity.this, mCityEditText);
                searchCity();
                return true;
            }
            return false;
        }
    };

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.weather_location_back_btn) {
            finish();
        } else if (i == R.id.weather_text_clean_btn) {
            if (mCityEditText != null) {
                mCityEditText.setText("");
            }
        }
    }

    /**
     * 联网搜索城市
     */
    public void searchCity() {
        String searchStr = mCityEditText.getText().toString().trim();
        searchStr = searchStr.replaceAll(" ", "");
        displayCitiesToSearchList(null, EMPTY); // 先清空 new ArrayList<LocationEntity>()

        if (TextUtils.isEmpty(searchStr)) {
            cancelSearchTask();
            return;
        }
        String language = this.getResources().getConfiguration().locale.toString();
        new SearchCityTask(searchStr, language).execute();
    }


    private SearchCityTask searchCityTask;

    private class SearchCityTask extends AsyncTask<Void, Void, List<LocationEntity>> {
        String query;
        String language;

        public SearchCityTask(String query, String language) {
            this.query = query;
            this.language = language;
            cancelSearchTask();
            searchCityTask = this;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if(!DeviceUtil.isNetworkOK(CityActivity.this)){
                displayCitiesToSearchList(null, NO_NETWORK);
                cancelSearchTask();
            }
        }

        @Override
        protected  List<LocationEntity> doInBackground(Void... voids) {
            if (searchCityTask == null) {
                return null;
            }
            List<LocationEntity> searchCityList = null;
            try {
                searchCityList = AccuWeather.getInstance(CityActivity.this.getApplicationContext()).getCityList(query, language);
            } catch (AccuWeather.NetworkRequestException e) {
                e.printStackTrace();
                // redoGetCityTask(query, language);
                return null;
            }
            return searchCityList;
        }

        @Override
        protected void onPostExecute(List<LocationEntity> searchCityList) {
            super.onPostExecute(searchCityList);
            displayCitiesToSearchList(searchCityList, NORMAL);
            searchCityTask = null;
        }
    }


    private void cancelSearchTask(){
        if (searchCityTask != null) {
            Log.i("sjh3",  "searchCityTask cancel" );
            searchCityTask.cancel(true);
        }
        searchCityTask = null;

    }


    private void displayCitiesToSearchList(List<LocationEntity> searchCityList, int status) {
        if(status == EMPTY){
            mNoResult.setVisibility(View.GONE);
            mTipsView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
        else if(status == NO_NETWORK){
            mNoResult.setVisibility(View.VISIBLE);
            mTipsView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
        else if(searchCityList != null && searchCityList.size() == 0) { // 无匹配结果
            Log.i("sjh3", "SearchCityEntity ");
            mNoResult.setVisibility(View.VISIBLE);
            mTipsView.setVisibility(View.GONE);
            mListView.setVisibility(View.GONE);
        }
        else if(searchCityList != null && searchCityList.size() > 0) {
            Log.i("sjh3", "SearchCityEntity " + searchCityList.toString());
            mNoResult.setVisibility(View.GONE);
            mTipsView.setVisibility(View.GONE);
            mListView.setVisibility(View.VISIBLE);

            CityAdapter adapter = new CityAdapter(this, searchCityList);
            mListView.setAdapter(adapter);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.i("sjh0", "onItemClick position:" + position);

                    Object itemEntityData = parent.getItemAtPosition(position);
                    Log.i("sjh0", " itemEntityData = " + itemEntityData.toString());
                    selectCity((LocationEntity)itemEntityData);
                }
            });
        }

    }

    /**
     * @param entity 点击的城市信息
     *
     */
    private void selectCity(LocationEntity entity) {
        if (mIsAutoLocation) {
            WeatherDataManager.getInstance().saveFixedPositionData(new WeatherDataEntity(entity));
            WeatherWidgetManager.getsInstance().requestSelectedCityWeather(entity.Key);
            finish();
            return;
        }
        if (WeatherDataManager.getInstance().isHaveData()) {
            WeatherDataManager.getInstance().saveWeatherDataEntity(new WeatherDataEntity(entity, null, null, null));
        } else {
            WeatherDataManager.getInstance().saveWeatherDataEntity(new WeatherDataEntity(entity, null, null, null));
            Intent intent = new Intent(this, WeatherCityActivity.class);
            startActivity(intent);
        }
        this.finish();
    }

    public static String getDisplayCity(LocationEntity entity) {
        String endSuffix = "";
        try {
            endSuffix = "," + entity.country.getDisplayName() + "(" + entity.administrativeArea.getDisplayName() + ")";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return endSuffix;
    }

    @SuppressLint("NewApi")
    private void destoryEditText() {
        try {
            mCityEditText.clearFocus();
            mCityEditText.removeTextChangedListener(mTextChangedListener);
            mCityEditText.setOnEditorActionListener(null);
            if(DeviceUtil.IS_KITKAT){
                mCityEditText.cancelPendingInputEvents();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        Log.i("sjh3",  " CityActivity onDestroy" );
        super.onDestroy();
        destoryEditText();
        cancelSearchTask();
        if (mGetCityTask != null) {
            mGetCityTask.cancel(true);
            mGetCityTask = null;
        }
        WeatherLocationManager.getInstance().removeOnLocationListener(this);
        WeatherLocationManager.getInstance().stopLocation();
    }
}
