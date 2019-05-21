package com.sichard.java.test;

/**
 * 类描述：单例
 *
 * @author caosc
 * @date 2018-12-19
 */
public class SingleTon {

    public static String test = "123";
    static {
        System.out.println("init SingleTon-" + Thread.currentThread().getName());
    }

    public static SingleTon getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        static {
            System.out.println("init Holder-" + Thread.currentThread().getName());
        }
        static SingleTon INSTANCE = new SingleTon();
    }

    public void isInit() {
        System.out.println("All is initialized!-" + Thread.currentThread().getName());
    }
}
