package com.android.sichard.common.rx;

/**
 * <br>类描述：
 * <br>详细描述：
 * <br><b>Author sichard</b>
 * <br><b>Date 2019-7-24</b>
 */
public interface Source<T> {
    T call() throws Exception;
}
