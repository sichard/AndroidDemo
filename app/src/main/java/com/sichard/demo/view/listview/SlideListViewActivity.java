package com.sichard.demo.view.listview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

import java.util.Arrays;
import java.util.List;

/**
 * <br>类描述:可滑动的ListView
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-29</b>
 */
public class SlideListViewActivity extends BaseActivity {
    private SlideListView mSlideListView;
    private List<String> mTitleList;
    private LayoutInflater mLayoutInflater;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slide_listview);
        mSlideListView = findViewById(R.id.slide_list_view);
        initData();
    }

    private void initData() {
        mLayoutInflater = LayoutInflater.from(this);
        mTitleList = Arrays.asList(getResources().getStringArray(R.array.titles));
        mSlideListView.setAdapter(new SlideAdapter());
    }

    class SlideAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mTitleList.size();
        }

        @Override
        public Object getItem(int position) {
            return mTitleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                holder = new SlideAdapter.ViewHolder();
                convertView = mLayoutInflater.inflate(R.layout.slide_list_item, parent, false);
                holder.mName = convertView.findViewById(R.id.slide_item_name);
                holder.mDelete = convertView.findViewById(R.id.slide_item_delete);
                convertView.setTag(holder);
            } else {
                holder = (SlideAdapter.ViewHolder) convertView.getTag();
            }
            holder.mName.setText(mTitleList.get(position));
            final SlideItemLayout finalContentView = (SlideItemLayout) convertView;
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mTitleList.remove(position);
                    finalContentView.smoothCloseMenu();
                    notifyDataSetChanged();
                }
            });
            return convertView;
        }

        private class ViewHolder {
            TextView mName;
            TextView mDelete;
        }
    }
}
