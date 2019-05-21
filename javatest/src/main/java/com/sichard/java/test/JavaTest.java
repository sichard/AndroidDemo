package com.sichard.java.test;

/**
 *非主动使用类字段演示
 **/
public class JavaTest{
    public static void main(String[]args){
//        System.out.println(ConstClass.HELLO_WORLD);
//        System.out.println(SubClass.HELLO_WORLD);
//        System.out.println(SingleTon.test);
//        SingleTon.getInstance().isInit();

        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    SingleTon.getInstance().isInit();
                }
            }, "thread-" + i).start();
        }
    }


    public enum Day2 {
        MONDAY("星期一"),
        TUESDAY("星期二"),
        WEDNESDAY("星期三"),
        THURSDAY("星期四"),
        FRIDAY("星期五"),
        SATURDAY("星期六"),
        SUNDAY("星期日");//记住要用分号结束

        private String desc;//中文描述

        /**
         * 私有构造,防止被外部调用
         *
         * @param desc
         */
        private Day2(String desc) {
            this.desc = desc;
        }

        /**
         * 覆盖
         *
         * @return
         */
        @Override
        public String toString() {
            return desc;
        }


        public static void main(String[] args) {
            for (Day2 day : Day2.values()) {
                System.out.println("name:" + day.name() +
                        ",desc:" + day.toString());
            }
        }
    }

}
