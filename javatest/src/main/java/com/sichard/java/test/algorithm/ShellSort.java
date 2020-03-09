package com.sichard.java.test.algorithm;

/**
 * 类描述：希尔排序法
 * 核心思想：在插入排序的基础上，"增量"交换(跳跃分割)，然后增量逐渐减小为1完成排序。
 *
 * @author caosc
 * @date 2020-3-9
 */
public class ShellSort {
    public static void main(String[] args) {
        int[] arr = {6, 5, 3, 1, 8, 7, 2, 4};
        System.out.println("排序之前：");
        for (int value : arr) {
            System.out.print(value + " ");
        }
        System.out.println("");


        // 希尔排序
        shellSort(arr);

        System.out.println();
        System.out.println("排序之后：");
        for (int value : arr) {
            System.out.print(value + " ");
        }
    }

    /**
     * 希尔排序
     */
    private static void shellSort(int[] arr) {
        int j;
        //gap此处取值
        for (int gap = arr.length / 2; gap > 0; gap = gap / 2) {
            System.out.println("gap:" + gap);
            for (int i = gap; i < arr.length; i++) {
                System.out.println("i:" + i);
                int tmp = arr[i];
                //for循环需要先判定条件十分满足，
                for (j = i; j >= gap && tmp < arr[j - gap]; j = j - gap) {
                    System.out.println("j:" + j);
                    System.out.println("temp:" + tmp);
                    System.out.println("data[" + j + "]:" + arr[j]);
                    arr[j] = arr[j - gap];
                }
                arr[j] = tmp;
            }
            for (int datum : arr) {
                System.out.print(datum + " ");
            }
            System.out.println("");
        }


//        int j = 0;
//        int temp = 0;
//        for (int increment = arr.length / 2; increment > 0; increment /= 2) {
//            System.out.println("increment:" + increment);
//            for (int i = increment; i < arr.length; i++) {
//                 //System.out.println("i:" + i);
//                temp = arr[i];
//                for (j = i - increment; j >= 0; j -= increment) {
//                     //System.out.println("j:" + j);
//                     //System.out.println("temp:" + temp);
//                     //System.out.println("data[" + j + "]:" + arr[j]);
//                    if (temp < arr[j]) {
//                        arr[j + increment] = arr[j];
//                    }
//                }
//                arr[j + increment] = temp;
//            }
//            for (int datum : arr) {
//                System.out.print(datum + " ");
//            }
//        }
    }
}
