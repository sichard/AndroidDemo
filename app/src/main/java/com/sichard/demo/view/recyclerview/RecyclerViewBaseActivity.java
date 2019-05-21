package com.sichard.demo.view.recyclerview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.android.sichard.common.BaseActivity;

/**
 * <br>类描述：RecyclerView演示的基类Activity
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-10-26</b>
 */
public abstract class RecyclerViewBaseActivity extends BaseActivity {
    protected RecyclerView recyclerView;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_with_recyclerview);
//        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        onCreateRecyclerView(savedInstanceState);
    }

    public abstract void onCreateRecyclerView(Bundle savedInstanceState);
}
