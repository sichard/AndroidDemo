package com.android.sichard.common.framework;

/**
 * <br>类描述:DCL的单例模型
 * <br>详细描述:注意volatile关键字不可缺少，且只在Java JDK1.5才能保证多线程下创建唯一实例
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-20</b>
 */
public class SampleDCLInstance {
    private volatile static SampleDCLInstance mInstance;

    public static SampleDCLInstance getInstance() {
        if (mInstance == null) {
            synchronized (SampleDCLInstance.class) {
                if (mInstance == null) {
                    mInstance = new SampleDCLInstance();
                }
            }
        }
        return mInstance;
    }
}
