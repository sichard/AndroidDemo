package com.sichard.demo.view.listview.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;
import com.sichard.demo.view.listview.DragSlideListView;
import com.sichard.demo.view.listview.SlideItemLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * <br>类描述:可拖动可左滑的ListView界面
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-30</b>
 */
public class DragSlideListViewActivity extends BaseActivity implements DragSlideListView.OnChangedListener {
    private DragSlideListView mDragSwapListView;
    private List<String> mTitleList = new ArrayList<>();
    private LayoutInflater mInflater;
    private DragAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_slide_listview);
        mDragSwapListView = findViewById(R.id.drag_slide_list_view);
        initData();
    }

    private void initData() {
        mInflater = LayoutInflater.from(this);
        //注意此处需要新建一个ArrayList存放数据，因为Arrays.asList()的列表不能进行add和remove操作
        mTitleList.addAll(Arrays.asList(getResources().getStringArray(R.array.titles)));
        mAdapter = new DragAdapter();
        mDragSwapListView.setAdapter(mAdapter);
        mDragSwapListView.setOnChangeListener(this);
    }

    @Override
    public void onChange(int start, int to) {
        if (to >= mTitleList.size() || start >= mTitleList.size()) {
            return;
        }
        synchronized (this) {
            //数据交换
            if (start < to) {
                for (int i = start; i < to; i++) {
                    Collections.swap(mTitleList, i, i + 1);
                }
            } else if (start > to) {
                for (int i = start; i > to; i--) {
                    Collections.swap(mTitleList, i, i - 1);
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    class DragAdapter extends BaseAdapter {
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
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.slide_list_item, parent, false);
                holder.mName = convertView.findViewById(R.id.slide_item_name);
                holder.mDelete = convertView.findViewById(R.id.slide_item_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mName.setText(mTitleList.get(position));
            final SlideItemLayout finalContentView = (SlideItemLayout) convertView;
            holder.mDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finalContentView.closeMenu();
                    mTitleList.remove(position);
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
