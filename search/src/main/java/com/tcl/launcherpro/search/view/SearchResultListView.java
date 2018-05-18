package com.tcl.launcherpro.search.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;

/**
 * Created by xinjue.wang on 2016/12/28.
 */
public class SearchResultListView extends ListView {

    public SearchResultListView(Context context) {
        super(context, null);
    }

    public SearchResultListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchResultListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    SearchView mSearchView;

    public void setSearchView(SearchView v){
        mSearchView = v;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if(ev.getAction() == MotionEvent.ACTION_DOWN) {
            mSearchView.showInputMethod(false);
        }
        return super.dispatchTouchEvent(ev);
    }
}
