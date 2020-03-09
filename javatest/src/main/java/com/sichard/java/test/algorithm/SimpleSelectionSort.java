package com.sichard.java.test.algorithm;

/**
 * 类描述：简单选择排序
 * 核心思想：选择数组中最小的数，依次放在数组的前列。
 *
 * @author caosc
 * @date 2020-3-9
 */
public class SimpleSelectionSort {
    public static void main(String[] args) {
        int[] arr = {9, 1, 5, 8, 3, 7, 4, 6, 2};
        System.out.println("排序之前：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        // 简单选择排序
        simpleSelectionSort(arr);

        System.out.println();
        System.out.println("排序之后：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    /**
     * 简单选择排序
     */
    private static void simpleSelectionSort(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            // 将当前下标定义为最小值下标
            int min = i;

            for (int j = i + 1; j < arr.length; j++) {
                // 如果有小于当前最小值的关键字, 则将此关键字的下标赋值给min
                if (arr[min] > arr[j]) {
                    min = j;
                }
            }

            // 若min不等于i，说明找到最小值，交换
            if (i != min) {
                swap(arr, i, min);
            }
        }
    }

    /**
     * 元素交换位置
     */
    private static void swap(int[] arr, int i, int min) {
        int tmp = arr[min];
        arr[min] = arr[i];
        arr[i] = tmp;
    }

}
