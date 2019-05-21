package com.sichard.demo.view.recyclerview.viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;


/**
 * 主页小卡片
 */
public class SmallCardView extends RecyclerView implements SmallCardViewAdapter.OnItemClickListener {
    private BigCardContainerView mBigCardContainerView;

    public SmallCardView(Context context) {
        super(context);
        initView(context);
    }

    public SmallCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public SmallCardView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        SmallCardViewAdapter adapter = new SmallCardViewAdapter(context);
        adapter.setOnItemClickListener(this);
        setAdapter(adapter);
        setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

        addItemDecoration(new LineIndicatorDecoration());
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mBigCardContainerView != null) {
            mBigCardContainerView.setVisibility(VISIBLE);
            mBigCardContainerView.setClickPosition(position);
        }
    }

    public void setBigCardContainerView(BigCardContainerView bigCardContainerView) {
        mBigCardContainerView = bigCardContainerView;
    }
}
