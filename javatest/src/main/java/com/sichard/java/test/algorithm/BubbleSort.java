package com.sichard.java.test.algorithm;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2020-3-7
 */
public class BubbleSort {
    public static int[] arr = {1, 1, 2, 0, 9, 3, 12, 7, 8, 3, 4, 65, 22};

    public static void main(String[] args) {
        bubbleSort2(arr);
    }

    public static int[] bubbleSort1(int[] a) {
        int times = 0;
        int i, j;
        for (i = 0; i < a.length; i++) {
            for (j = a.length - 1; j > i; j--) {
                if (a[j - 1] > a[j]) {
                    Swap.swap(a, j, j - 1);
                }
            }
            times++;
        }
        ArrayPrint.arrayPrint(a);
        return a;
    }

    /**
     * 加入是否交换的flag
     * @param a
     * @return
     */
    public static int[] bubbleSort2(int[] a) {
        int times = 0;
        int i, j;
        boolean flag = true;
        for (i = 0; i < a.length && flag; i++) {
            flag = false;
            for (j = a.length - 1; j > i; j--) {
                if (a[j - 1] > a[j]) {
                    Swap.swap(a, j, j - 1);
                    flag = true;
                }
            }
            times++;
        }
        StringBuilder builder = new StringBuilder();
        for (int i1 : a) {
            builder.append(i1);
            builder.append(",");
        }
        builder.append(times);
        builder.append("times");
        System.out.println(builder.toString());
        return a;
    }

    public static int[] bubbleSort3(int[] a) {
        int times = 0;
        int i, j;
        boolean swap = true;
        for (i = 0; i < a.length && swap; i++) {
            swap = false;
            for (j = a.length - 1; j > i; j--) {
                if (a[j - 1] > a[j]) {
                    Swap.swap(a, j, j - 1);
                    swap = true;
                }
            }
            times++;
        }
        StringBuilder builder = new StringBuilder();
        for (int i1 : a) {
            builder.append(i1);
            builder.append(",");
        }
        builder.append(times);
        builder.append("times");
        System.out.println(builder.toString());
        return a;
    }
}
