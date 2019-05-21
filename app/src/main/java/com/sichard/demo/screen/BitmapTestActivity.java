package com.sichard.demo.screen;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android.sichard.common.BaseActivity;
import com.sichard.demo.R;

/**
 * <br>类描述：Bitmap测试类
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2018-11-6</b>
 */
public class BitmapTestActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.base_animation_test, null);
        Log.i("sichardcao", "BitmapTestActivity|onCreate:" + bitmap.getByteCount());
    }
}
