package com.sichard.java.test.algorithm;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2020-3-7
 */
public class Swap {
    public static void swap(int a[], int i, int j) {
        int temp;
        temp = a[i];
        a[i] = a[j];
        a[j] = temp;
    }
}
