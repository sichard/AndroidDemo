package com.sichard.weather.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.sichard.weather.R;
import com.sichard.weather.activity.CityActivity;
import com.sichard.weather.weatherData.LocationEntity;

import java.util.List;

public class CityAdapter extends BaseAdapter {
	private static final boolean DEBUG = true;
	Context mContext;
	List<LocationEntity> mData;

	public CityAdapter(Context context, List<LocationEntity> data) {
		this.mContext = context;
		mData = data;
	}

	@Override
	public int getCount() {
		return mData.size();
	}

	@Override
	public Object getItem(int position) { //onItemClick中的getItemAtPosition才会调这里。Listview加载时不调这里
		if(DEBUG) Log.i("sjh0", "getItem position = " + position);
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(DEBUG) Log.i("sjh0", "getView position = " + position);
		ViewHolder holder = null;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.search_city_layout, parent, false);
			holder.textView = (TextView) convertView.findViewById(R.id.auto_locate_city);
			holder.searchKey = (TextView) convertView.findViewById(R.id.search_key);
			convertView.setTag(holder);
		}
		else{
			holder = (ViewHolder) convertView.getTag();
		}

		LocationEntity locationEntity = mData.get(position);
		holder.textView.setText(CityActivity.getDisplayCity(locationEntity));
		holder.searchKey.setText(locationEntity.getDisplayName());
		return convertView;
	}
	
	
	private static class ViewHolder{
		TextView searchKey;
		TextView textView;
	}

}
