package com.tcl.launcherpro.search.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.tcl.launcherpro.search.data.searchInWeb.SearchInWebInfo;

/**
 * AppCenter搜索项View
 * Created by lunou on 2016/12/29.
 */
public class SearchInWebItemView extends RelativeLayout implements View.OnClickListener {

    private SearchInWebInfo mSearchInWebInfo;

    public SearchInWebItemView(Context context) {
        super(context);
    }

    public SearchInWebItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchInWebItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Runnable action = mSearchInWebInfo.getAction();
        if (action != null) {
            action.run();
        }
    }

    public void setSearchInWebInfo(SearchInWebInfo searchInWebInfo) {
        this.mSearchInWebInfo = searchInWebInfo;
    }
}
