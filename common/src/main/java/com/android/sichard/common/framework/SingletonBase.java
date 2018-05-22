package com.android.sichard.common.framework;

import android.content.Context;
import android.util.Log;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * <br>类描述：单例基类
 * <br>详细描述：基于DCL(Double Check Lock双重检查锁定)的通过泛型类简化单例类的创建
 * <br><b>Author sichard</b>
 * <br><b>Date 18-5-18</b>
 */

public class SingletonBase {
    public static Context sContext;

    public static void registerContext(Context applicationContext) {
        if (sContext == null) {
            sContext = applicationContext;
        }
    }

    /***
     * 采用ConcurrentHashMap集合进行存储
     * <br/>Class 作为key ---对象的类型
     * <br/>Object 作为value---对象的实例化
     * <br/>实现对象的类型和对象的实例化 一一对应
     */
    private static final ConcurrentMap<Class, Object> map = new ConcurrentHashMap<>();

    public static <T> T instance(Class<T> type) {
        // 这里用map.get(type)来判空，切记不可用如下代码来判空,否则会导致创建多个实例
        // Object obj = map.get(type)；
        //if(obj == null)
        if (map.get(type) == null) {//检查是否存在实例，避免不必要的同步
            synchronized (map) {
                if (map.get(type) == null) {//确保创建单例时，单例为空
                    try {
                        //这里利用反射，将私有的构造方法改为共有的，用于创建实例，否则无法创建实例
                        Constructor constructor = type.getDeclaredConstructor();
                        constructor.setAccessible(true);
                        map.put(type, constructor.newInstance());
                    }catch (NoSuchMethodException e) {
                        e.printStackTrace();
                        throw new RuntimeException(type.getSimpleName() + " instance failed!!!，请加入" + type.getSimpleName() +
                                "的默认构造方法。");
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                        throw new RuntimeException(type.getSimpleName() + " instance failed!!!，请检测" + type.getSimpleName() +
                                "的默认构造方法，调用失败！");
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        throw new RuntimeException(type.getSimpleName() + " instance failed!!!，无法调用" + type.getSimpleName() +
                                "的默认构造方法，调用失败！");
                    } catch (InstantiationException e) {
                        e.printStackTrace();
                        throw new RuntimeException(type.getSimpleName() + " instance failed!!!，创建实例失败！");
                    }
                }
            }
        }
        return (T) map.get(type);
    }

//    /**
//     * 销毁单例
//     * @param type 要销毁的单例Class
//     * @param <T>
//     */
//    public static <T> void destroy(Class<T> type) {
//        type.
//        Object remove = map.remove(type);
//        remove = null;
//    }
    /**
     * 销毁单例
     * @param type 要销毁的单例Class
     */
    public static void destroy(SingletonBase type) {
        type.release();
        Class<? extends SingletonBase> aClass = type.getClass();
        Log.e("sichard", "SingletonBase|destroy:" + aClass.getSimpleName());
        Object remove = map.remove(aClass);
        if (remove != null) {
            remove = null;
            Log.i("sichard", "SingletonBase|destroy:" + "instance destroyed");
        } else {
            Log.i("sichard", "SingletonBase|destroy:" + "instance destroyed failed");
        }
    }

    /**
     * 销毁单例时调用
     */
    protected void release() {

    }
}
