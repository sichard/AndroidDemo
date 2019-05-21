package com.sichard.demo.view.recyclerview.viewpager;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sichard.demo.R;

/**
 * AI主页小卡片的Adapter
 */
public class SmallCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements View.OnClickListener {
    private Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int[] mResId = new int[]{R.drawable.ic_small_card_item1, R.drawable.ic_small_card_item2, R.drawable.ic_small_card_item3, R.drawable.ic_small_card_item4};

    SmallCardViewAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.layout_small_card_item, parent, false);
        return new RecyclerView.ViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Glide.with(mContext).load(mResId[position]).into((ImageView) holder.itemView);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
    }

    @Override
    public int getItemCount() {
        return mResId.length;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(v, (Integer) v.getTag());
        }
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }


    interface OnItemClickListener {
        /**
         * Item被点击时调用
         *
         * @param view     被点击的view
         * @param position view在RecyclerView中的位置
         */
        void onItemClick(View view, int position);
    }

}
