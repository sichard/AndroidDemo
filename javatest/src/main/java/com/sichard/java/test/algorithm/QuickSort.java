package com.sichard.java.test.algorithm;

/**
 * 类描述：
 *
 * @author caosc
 * @date 2020-3-7
 */
public class QuickSort {
    public static int[] arr = {50, 10, 90, 30, 70, 40, 80, 60, 20};
    public static void main(String[] args) {
        System.out.println("排序之前：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        // 快速排序
        quickSort(arr, 0, arr.length - 1);

        System.out.println();
        System.out.println("排序之后：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    public static void quickSort(int[] arr, int low, int high) {
        if (low < high) {
            // 将数组一分为2，返回枢轴值pivot所在的位置
            int pivot = partition(arr, low, high);
            // 对低位子序列递归排序
            quickSort(arr, low, pivot - 1);
            // 对高位子序列递归排序
            quickSort(arr, pivot + 1, high);
        }
//        ArrayPrint.arrayPrint(arr);
    }

    /**
     * 使枢轴记录到达正确的位置，并返回其所在的位置
     */
    private static int partition(int[] arr, int low, int high) {
        // 选定枢轴为low所对应的值,序列的第一条记录作为枢轴元素
        int pivot = arr[low];
        // 从低位往高位遍历
        while (low < high) {
            // 在高位找到比枢轴大的元素，符合要求，继续寻找
            while (low < high && pivot <= arr[high]) {
                high--;
            }
            // 将比枢轴元素小的记录交换到低位
            arr[low] = arr[high];
            // 在低位找到比枢轴小的元素，符合要求，继续寻找
            while (low < high && pivot >= arr[low]) {
                low++;
            }
            // 将比枢轴记录大的元素交换到高位
            arr[high] = arr[low];
        }
        // 将枢轴放在正确的排序位置
        arr[low] = pivot;
        // 返回枢轴元素所在的位置
        return low;
    }


    //    public static void quickSort(int[] arr, int low, int high) {
//        if (low < high) {
//            int pivot = partition(arr, low, high);
//            quickSort(arr,low, pivot - 1);
//            quickSort(arr,pivot + 1, high);
//        }
//        ArrayPrint.arrayPrint(arr);
//    }
//
//    private static int partition(int[] arr, int low, int high) {
//        int pivot = arr[low];
//        while (low < high) {
//            while (low < high && pivot <= arr[high]) {
//                high--;
//            }
//            arr[low] = arr[high];
//            while (low < high && pivot >= arr[low]) {
//                low++;
//            }
//            arr[high] = arr[low];
//            arr[low] = pivot;
//        }
//        return low;
//    }
    class Record {

    }
}
