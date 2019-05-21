package com.sichard.demo.androidtest;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2019-2-14
 */
public class AndroidTestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_android_test);
    }
}
