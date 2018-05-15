package com.sichard.demo.javatest;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;

import com.android.sichard.common.BaseActivity;

import java.util.ArrayList;
import java.util.List;


/**
 * <br>类描述:java代码测试
 * <br>详细描述:
 * <br><b>Author sichard</b>
 * <br><b>Date 18-4-18</b>
 */

public class JavaTestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        List<View> list = new ArrayList<>();
        View view = new View(this);
        list.add(view);
        Log.i("sichard", "JavaTestActivity|onCreate:======" + view.toString());
        view = new SearchView(this);
        Log.i("sichard", "JavaTestActivity|onCreate:======" + view.toString());
        Log.e("sichard", "JavaTestActivity|onCreate:======" + list.get(0).toString());
    }
}
