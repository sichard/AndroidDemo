package com.sichard.demo.view.recyclerview.viewpager;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述：RecyclerView的Activity
 * <br>详细描述：用于展示的RecyclerView的ItemDecoration的用法以及RecyclerView改造成ViewPager的用法
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-10-25</b>
 */
public class RecyclerViewActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recyclerview);
        initView();
    }

    private void initView() {
        BigCardContainerView bigCardContainerView = findViewById(R.id.big_card_container_view);
        SmallCardView smallCardView = findViewById(R.id.smallCardView);
        smallCardView.setBigCardContainerView(bigCardContainerView);
    }

    public void onClick(View view) {

    }
}
