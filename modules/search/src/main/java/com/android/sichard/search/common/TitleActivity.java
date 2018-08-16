package com.android.sichard.search.common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.sichard.search.R;


public abstract class TitleActivity extends AppCompatActivity {

    protected Activity mActivity;
    protected Context mContext;

    protected static final int INVALID_INT = 0;

    private LinearLayout mTitleLayout;
    private TextView mTitle;
    private LinearLayout mTitleBack;
    private ImageView mBackIcon;

    public void onPluginCreate(Activity activity, Context context, Bundle savedInstanceState) {
        mActivity = activity;
        mContext = context;
        init();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mContext = this;
        init();
    }

    private void init() {
        View RootView = LayoutInflater.from(mContext).inflate(R.layout.activity_title, null);
        mActivity.setContentView(RootView);
        initViewStub();
        findView();
        initView();
    }

    private void initViewStub() {
        ViewStub viewStub = (ViewStub) mActivity.findViewById(R.id.viewstub_content);
        viewStub.setLayoutResource(getContentLayoutId());
        viewStub.inflate();
    }

    private void findView() {
        mTitleLayout = (LinearLayout) mActivity.findViewById(R.id.title_layout);
        mTitle = (TextView) mActivity.findViewById(R.id.tv_title);
        mTitleBack = (LinearLayout) mActivity.findViewById(R.id.title_back);
        mBackIcon = (ImageView) mActivity.findViewById(R.id.back_icon);
        findContentView();
    }

    private void initView() {
        initContentView();
        mTitleLayout.setBackgroundColor(mContext.getResources().getColor(getTitleBackgroundColor()));
        if(getTitleText() != INVALID_INT) {
            mTitle.setText(mContext.getResources().getText(getTitleText()));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        // {@ modify by rui.yan 2016/11/9
        // description: 点击范围过大,导致用户点击标题也会退出界面,这里仅限点击返回按钮退出界面
        //mTitleBack.setOnClickListener(new View.OnClickListener() {
        mBackIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        // @}
    }

    public View findViewById(int id) {
        if (mActivity == this) {//非插件情况
            return super.findViewById(id);
        } else {//插件情况
            return mActivity.findViewById(id);
        }
    }

    public void sendBroadcast(Intent intent) {
        if (mActivity == this) {//非插件情况
            super.sendBroadcast(intent);
        } else {//插件情况
            mActivity.sendBroadcast(intent);
        }
    }

    protected void setTitle(String title) {
        mTitle.setText(title);
    }

    protected @StringRes
    int getTitleText() {
        return INVALID_INT;
    }

    abstract protected void findContentView();
    abstract protected void initContentView();
    abstract protected @LayoutRes
    int getContentLayoutId();
    protected @ColorRes int getTitleBackgroundColor() {
        return R.color.primary;
    }

}
