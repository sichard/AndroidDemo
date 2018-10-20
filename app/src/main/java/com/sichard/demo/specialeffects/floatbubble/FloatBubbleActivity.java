package com.sichard.demo.specialeffects.floatbubble;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.WindowManager;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述：
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-13</b>
 */
public class FloatBubbleActivity extends BaseActivity {

    private FloatBubbleView mDWView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_bubble);
        //初始化操作
        initView();
        initData();
    }


    /**
     * 初始化View
     */
    private void initView() {
        //透明状态栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //透明导航栏
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        mDWView = findViewById(R.id.float_bubble);
    }

    /**
     * 初始化Data
     */
    private void initData() {
        //设置气泡绘制者
        BubbleDrawer bubbleDrawer = new BubbleDrawer(this);
        //设置渐变背景 如果不需要渐变 设置相同颜色即可
        bubbleDrawer.setBackgroundGradient(new int[]{0xffffffff, 0xffffffff});
        //给SurfaceView设置一个绘制者
        mDWView.setDrawer(bubbleDrawer);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDWView.onDrawResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDWView.onDrawPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDWView.onDrawDestroy();
    }
}
