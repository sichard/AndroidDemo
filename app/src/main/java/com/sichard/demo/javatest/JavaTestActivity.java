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
    static final int MAX = 10000;
    static final String[] arr = new String[MAX];
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        for (int i = 0; i < 100; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
////                Log.i("sichard", "JavaTestActivity|run:" + SampleDCLInstance.getInstance().toString());
////                Log.i("sichard", "JavaTestActivity|run:" + SingletonBase.instance(TestInstance.class).toString());
//                    TestInstance.getInstance().test(Thread.currentThread().getName());
////                    SingletonBase.destroy(TestInstance.getInstance());
//
//                }
//            }, "thread " + i).start();
//        }

//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        Log.i("sichardcao", "JavaTestActivity|onCreate:" + format.format(new Date()));
//        String a = "123";
//        String b = new String("123");
//
//        String c = new String("456");
//        String d = c.intern();
//        String e = "456";
//
//        String f = "456";
//        String g = new String("456");
//
//        Log.i("sichardcao", "JavaTestActivity|onCreate:" + (a == b) + "||" + (c == d) + "||"
//                + (d==e) + "|" + (f == e) + "||" + (f==g));

//        String s0 = "kvill";
//        String s1 = new String("kvill");
//        String s2 = new String("kvill");
//        System.out.println(s0 == s1);
//        System.out.println("**********");
//        s1.intern();
//        s2 = s2.intern(); //把常量池中“kvill”的引用赋给s2
//        System.out.println(s0 == s1);
//        System.out.println(s0 == s1.intern());
//        System.out.println(s0 == s2);

//        String s = new String("1");
//        s.intern();
//        String s2 = "1";
//        System.out.println(s == s2);
//
//        String s3 = new String("1") + new String("1");
//        s3.intern();
//        String s4 = "11";
//        System.out.println(s3 == s4);

//        String s = new String("1");
//        String s2 = "1";
//        s.intern();
//        System.out.println(s == s2);
//
//        String s3 = new String("1") + new String("1");
//        String s4 = "11";
//        s3.intern();
//        System.out.println(s3 == s4);


//        Integer[] DB_DATA = new Integer[10];
//        for (int i = 0; i < DB_DATA.length; i++) {
//            DB_DATA[i] = i;
//        }
//        long t = System.currentTimeMillis();
//        for (int i = 0; i < MAX; i++) {
////            arr[i] = new String(String.valueOf(DB_DATA[i % DB_DATA.length]));
//            arr[i] = new String(String.valueOf(DB_DATA[i % DB_DATA.length])).intern();
//        }
//
//        System.out.println((System.currentTimeMillis() - t) + "ms");


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SingletonBase.destroy(TestInstance.getInstance());
    }
}
