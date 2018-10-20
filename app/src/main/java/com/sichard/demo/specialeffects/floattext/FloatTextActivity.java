package com.sichard.demo.specialeffects.floattext;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.sichard.demo.R;

import java.util.ArrayList;

/**
 * <br>类描述：浮动的TextView
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-9-13</b>
 */
public class FloatTextActivity extends Activity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_text);
        FloatTextView floatTextView = findViewById(R.id.float_text_view);
        final ArrayList<String> textList = new ArrayList<>();
        textList.add("1111");
        textList.add("2222222");
        textList.add("3333333");
        textList.add("44444444");
        textList.add("555555555");
        floatTextView.setTextList(textList);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
