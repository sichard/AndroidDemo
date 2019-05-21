package com.sichard.demo.view.recyclerview.viewpager;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * AI主页大卡View
 */
public class BigCardView extends RecyclerView {
    private boolean mIsMove;
    private LinearLayoutManager mLayoutManager;
    private int mIndex;
    private BigCardViewAdapter mBigCardViewAdapter;
    private VideoView mVideoView;

    public BigCardView(Context context) {
        super(context);
        initView(context);
    }

    public BigCardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public BigCardView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        mBigCardViewAdapter = new BigCardViewAdapter();
        setAdapter(mBigCardViewAdapter);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        setLayoutManager(mLayoutManager);

        // 添加RecyclerView的滑动控制器
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);

        addItemDecoration(new BigCardIndicatorDecoration());

        addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (mVideoView.isPlaying()) {
                    mVideoView.stopPlayback();
                    mVideoView.setVisibility(GONE);
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //在这里进行第二次滚动
                if (mIsMove) {
                    mIsMove = false;
                    //获取要置顶的项在当前屏幕的位置，mIndex是记录的要置顶项在RecyclerView中的位置
                    int n = mIndex - mLayoutManager.findFirstVisibleItemPosition();
                    if (0 <= n && n < getChildCount()) {
                        //获取要置顶的项顶部离RecyclerView顶部的距离
                        int left = getChildAt(n).getLeft();
                        //最后的移动
                        scrollBy(left, 0);
                    }
                }
            }
        });
    }

    public void moveToPosition(int position) {
        mIndex = position;
        //先从RecyclerView的LayoutManager中获取第一项和最后一项的Position
        int firstItem = mLayoutManager.findFirstVisibleItemPosition();
        int lastItem = mLayoutManager.findLastVisibleItemPosition();
        //然后区分情况
        if (position <= firstItem) {
            //当要置顶的项在当前显示的第一个项的前面时
            scrollToPosition(position);
        } else if (position <= lastItem) {
            //当要置顶的项已经在屏幕上显示时
            int left = getChildAt(position - firstItem).getLeft();
            scrollBy(left, 0);
        } else {
            //当要置顶的项在当前显示的最后一项的后面
            scrollToPosition(position);
            //这里这个变量是用在RecyclerView滚动监听里面的
            mIsMove = true;
        }
    }

    public void setOnItemClickListener(BigCardViewAdapter.OnItemClickListener onItemClickListener) {
        mBigCardViewAdapter.setOnItemClickListener(onItemClickListener);
    }

    public void setVideoView(VideoView videoView) {
        mVideoView = videoView;
    }
}
