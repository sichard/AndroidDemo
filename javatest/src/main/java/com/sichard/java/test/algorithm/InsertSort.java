package com.sichard.java.test.algorithm;

/**
 * 类描述：插入排序法
 *
 * @author caosc
 * @date 2020-3-9
 */
public class InsertSort {

    public static void main(String[] args) {
        int[] arr = {6, 5, 3, 1, 8, 7, 2, 4};
        System.out.println("排序之前：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        // 直接插入排序
        insertSort(arr);

        System.out.println();
        System.out.println("排序之后：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    /**
     * 直接插入排序
     */
    private static void insertSort(int[] arr) {
        // 已排序列表下标
        int j;
        // 待排序元素
        int temp;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < arr[i - 1]) {
                // 赋值给待排序元素
                temp = arr[i];
                // 注意这里的条件是arr[j] > temp；变化的下标最终是和temp(即未排序的第一个元素进行比较)
                for (j = i - 1; j >= 0 && arr[j] > temp; j--) {
                    // 从后往前遍历已排序列表，逐个和待排序元素比较，如果已排序元素较大，则将它后移
                    arr[j + 1] = arr[j];
                }
                // 将待排序元素插入到正确的位置
                arr[j + 1] = temp;
            }
        }
    }

}
