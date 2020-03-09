package com.sichard.java.test.algorithm;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2020-3-8
 */
public class ArrayPrint {
    public static void arrayPrint(int[] arr) {
        StringBuilder builder = new StringBuilder();
        builder.append("数组序列为：");
        for (int i : arr) {
            builder.append(i);
            builder.append(" ");
        }
        System.out.println(builder.toString());
    }
}
