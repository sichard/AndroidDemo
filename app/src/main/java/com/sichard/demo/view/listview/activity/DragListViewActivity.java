package com.sichard.demo.view.listview.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;
import com.sichard.demo.view.listview.DragListView;

import java.util.ArrayList;
import java.util.List;

/**
 *<br>类描述：可拖动的ListView
 *<br>详细描述：
 *<br><b>Author sichard</b>
 *<br><b>Date 18-4-18</b>
 */
public class DragListViewActivity extends BaseActivity implements DragListView.DropViewListener {

    private Context mContext;
    private ListAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_listview);

        List<String> arrays = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            arrays.add("Item " + i);
        }

        mContext = this;

        mAdapter = new ListAdapter(arrays);

        DragListView mListView = findViewById(R.id.drag_listview);
        mListView.setAdapter(mAdapter);
        mListView.setDropViewListener(this);
        //mListView.setDragListener(this);
        //mListView.setDropListener(this);
        //mListView.setRemoveListener(this);
    }

    class ListAdapter extends BaseAdapter {

        private List<String> list;

        public ListAdapter(List<String> lt) {
            list = lt;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.drag_listview_item, parent,false);
                holder = new ViewHolder();
                holder.mTextView = convertView.findViewById(R.id.item_label);
                holder.mImageView = convertView.findViewById(R.id.item_image);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.mTextView.setText(list.get(position));
            holder.mImageView.setImageResource(R.mipmap.part);
            return convertView;
        }

        private void insert(int position, String items) {
            list.remove(items);
            list.add(position, items);
        }

        class ViewHolder {
            TextView mTextView;
            ImageView mImageView;
        }
    }

	/*@Override
    public void drop(int from, int to) {
		mAdapter.insert(to, mAdapter.getItem(from).toString());
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void drag(int from, int to) {
		
	}

	@Override
	public void remove(int which) {
		arrays.remove(which);
	}*/

    @Override
    public void drop(int from, int to) {
        mAdapter.insert(to, mAdapter.getItem(from).toString());
        mAdapter.notifyDataSetChanged();
    }
}
