package com.sichard.demo.javatest;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.android.sichard.common.BaseActivity;
import com.android.sichard.common.framework.SingletonBase;
import com.android.sichard.common.framework.TestInstance;


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
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
//                Log.i("sichard", "JavaTestActivity|run:" + SampleDCLInstance.getInstance().toString());
//                Log.i("sichard", "JavaTestActivity|run:" + SingletonBase.instance(TestInstance.class).toString());
                    TestInstance.getInstance().test(Thread.currentThread().getName());

                }
            }, "thread " + i).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        TestInstance.getInstance().release();
        SingletonBase.destroy(TestInstance.getInstance());
    }
}
