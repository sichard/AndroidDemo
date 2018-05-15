package com.tct.launcher.weather.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tct.launcher.weather.R;
import com.tct.launcher.weather.WeatherDataManager;
import com.tct.launcher.weather.view.SlideItemLayout;
import com.tct.launcher.weather.weatherData.WeatherDataEntity;

import java.util.List;

/**
 * <br>类描述:本地城市列表的Adapter
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 2017/3/25</b>
 */

public class LocalCityAdapter extends BaseAdapter {
    private Context mContext;
    private final List<WeatherDataEntity> mList;

    public LocalCityAdapter(Context context, List<WeatherDataEntity> weatherDataEntities) {
        this.mContext = context;
        this.mList = weatherDataEntities;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public WeatherDataEntity getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup parent) {
        ViewHolder holder;
        if (contentView == null) {
            holder = new ViewHolder();
            contentView = LayoutInflater.from(mContext).inflate(R.layout.weather_local_list_item, parent, false);
            holder.locationName = (TextView) contentView.findViewById(R.id.weather_local_item_name);
            holder.imageView = (ImageView) contentView.findViewById(R.id.weather_reorder_image);
            holder.delete = (TextView) contentView.findViewById(R.id.weather_local_item_delete);
            contentView.setTag(holder);
        } else {
            holder = (ViewHolder) contentView.getTag();
        }
        holder.locationName.setText(mList.get(position).mLocationEntity.getDisplayName());
        final SlideItemLayout finalContentView = (SlideItemLayout) contentView;
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final WeatherDataEntity weatherDataEntity = mList.remove(position);
                finalContentView.smoothCloseMenu();
                notifyDataSetChanged();
                WeatherDataManager.getInstance().removeWeatherData(weatherDataEntity.mLocationEntity);
            }
        });
        return contentView;
    }

    /**
     * 获取城市列表
     * @return
     */
    public List<WeatherDataEntity> getCityList(){
        return mList;
    }


    /**
     * 添加数据
     * @param chgDataList
     */
    public void addData(List<WeatherDataEntity> chgDataList) {
        mList.addAll(chgDataList);
        notifyDataSetChanged();
    }

    /**
     * 删除数据
     * @param chgDataList
     */
    public void deleteData(List<WeatherDataEntity> chgDataList) {
        mList.removeAll(chgDataList);
        notifyDataSetChanged();
    }

    private static class ViewHolder {
        ImageView imageView;
        TextView locationName;
        TextView delete;
    }
}
