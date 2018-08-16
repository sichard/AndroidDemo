package com.android.sichard.common.framework;

import android.util.Log;

/**
 * <br>类描述:继承SingletonBase类的单例测试类
 * <br>详细描述:经过单例测试可以保证多线程访问唯一实例的创建
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-18</b>
 */
public class TestInstance extends SingletonBase {
    private static TestInstance sInstance;
    private TestInstance() {
//        long start = System.currentTimeMillis();
//        for (long i = 0; i < 100000000L; i++) {
//        }
//        long end = System.currentTimeMillis();
//        Log.e("sichardcao", "TestInstance|TestInstance:" + (end - start));
    }

    public static TestInstance getInstance() {
        if (sInstance == null) {
            sInstance = instance(TestInstance.class);
        }
        return sInstance;
    }

    @Override
    public void release() {
        super.release();
    }

    public void test(String name) {
        StringBuffer buffer = new StringBuffer(name);
        buffer.append(" = ");
        buffer.append(this.toString());
        Log.i("sichardcao", "TestInstance|test: Thread " + buffer);
    }

}
