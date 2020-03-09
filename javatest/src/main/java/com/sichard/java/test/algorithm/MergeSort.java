package com.sichard.java.test.algorithm;

/**
 * 类描述：归并(分治)排序
 *
 * @author caosc
 * @date 2020-3-9
 */
public class MergeSort {
    public static void main(String[] args) {
        int[] arr = {50, 10, 90, 30, 70, 40, 80, 60, 20};
        System.out.println("排序之前：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }

        // 归并排序
        mergeSort(arr);

        System.out.println();
        System.out.println("排序之后：");
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
    }

    /**
     * 归并排序
     */
    public static void mergeSort(int[] arr) {
        int[] tmpArr = new int[arr.length];
        mergeSort(arr, tmpArr, 0, arr.length - 1);
    }

    private static void mergeSort(int[] arr, int[] tmpArr, int low, int high) {
        if (low < high) {
            // 将数组arr分为arr[0..mid]和arr[mid+1..high]
            int middle = (low + high) / 2;

            // 递归将arr[low..mid]归并为有序的tmpArr[low..mid]
            mergeSort(arr, tmpArr, low, middle);

            // 递归将arr[mid+1..high]归并为有序的tmpArr[mid+1..high]
            mergeSort(arr, tmpArr, middle + 1, high);

            // 将arr[low..mid]和arr[mid+1..high]归并到tmpArr[low..high]
            merge(arr, tmpArr, low, middle + 1, high);
        }
    }

    // 将有序的arr[low..mid]和arr[mid+1..high]归并为有序的tmpArr[low..high]
    private static void merge(int[] arr, int[] tmpArr, int lowPos, int highPos, int highEnd) {
        int lowEnd = highPos - 1;
        int tmpPos = lowPos;
        int numElements = highEnd - lowPos + 1;

        // 将arr中的记录由小到大归并入tmpArr
        while (lowPos <= lowEnd && highPos <= highEnd){
            if (arr[lowPos] <= arr[highPos]){
                tmpArr[tmpPos++] = arr[lowPos++];
            }else{
                tmpArr[tmpPos++] = arr[highPos++];
            }
        }

        // 将剩余的arr[low..mid]复制到tmpArr
        while (lowPos <= lowEnd){
            tmpArr[tmpPos++] = arr[lowPos++];
        }

        // 将剩余的arr[mid+1..high]复制到tmpArr
        while (highPos <= highEnd){
            tmpArr[tmpPos++] = arr[highPos++];
        }

        // Copy tmpArr back
        for (int i = 0; i < numElements; i++, highEnd--){
            arr[highEnd] = tmpArr[highEnd];
        }
    }
}
