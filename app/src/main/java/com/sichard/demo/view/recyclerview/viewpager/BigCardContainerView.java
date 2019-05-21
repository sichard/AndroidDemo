package com.sichard.demo.view.recyclerview.viewpager;

import android.content.Context;
import android.media.MediaPlayer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;

import com.sichard.demo.R;

/**
 * 主页大卡的父容器
 */
public class BigCardContainerView extends FrameLayout implements View.OnClickListener, BigCardViewAdapter.OnItemClickListener {
    private BigCardView mBigCardView;
    private VideoView mVideoView;
    private ImageView mBigCardCloseView;

    public BigCardContainerView(@NonNull Context context) {
        super(context);
    }

    public BigCardContainerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BigCardContainerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mVideoView = findViewById(R.id.big_card_video_view);

        mBigCardView = findViewById(R.id.big_card_view);
        mBigCardView.setOnItemClickListener(this);
        mBigCardView.setVideoView(mVideoView);

        mBigCardCloseView = findViewById(R.id.big_card_close_view);
        mBigCardCloseView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == mBigCardCloseView) {
            if (mVideoView != null && mVideoView.isPlaying()) {
                mVideoView.stopPlayback();
                mVideoView.setVisibility(GONE);
            }
            setVisibility(GONE);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (mVideoView.isPlaying()) {
            return;
        }
//        String uri = "android.resource://" + mContext.getPackageName() + "/" + R.raw.video;
//        String path = Environment.getExternalStorageDirectory().getPath() + "/video_shenzhen.mp4";
        String path = null;
        if (TextUtils.isEmpty(path)) {
            return;
        }
        mVideoView.setVisibility(VISIBLE);
        mVideoView.setVideoPath(path);
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mVideoView.setVisibility(GONE);
            }
        });
        mVideoView.start();
    }

    public void setClickPosition(int position) {
        if (mBigCardView != null) {
            mBigCardView.moveToPosition(position);
        }
    }
}
