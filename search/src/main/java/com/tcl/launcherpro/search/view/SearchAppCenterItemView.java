package com.tcl.launcherpro.search.view;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.tcl.launcherpro.search.data.appcenter.AppCenterInfo;

/**
 * AppCenter搜索项View
 * Created by lunou on 2016/12/29.
 */
public class SearchAppCenterItemView extends RelativeLayout implements View.OnClickListener {

    private AppCenterInfo mAppCenterInfo;

    public SearchAppCenterItemView(Context context) {
        super(context);
    }

    public SearchAppCenterItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchAppCenterItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        // 跳转到AppCenter
        String action = "com.tcl.action.apps.search";
        String searchContent = "";
        String pkgName = getContext().getPackageName();
        if (mAppCenterInfo != null) {
            searchContent = mAppCenterInfo.getSearchContent();
        }
        try {
            Intent intent = new Intent();
            intent.setAction(action);
            intent.putExtra("kw", searchContent);
            intent.putExtra("from", pkgName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAppCenterInfo(AppCenterInfo appCenterInfo) {
        this.mAppCenterInfo = appCenterInfo;
    }
}
