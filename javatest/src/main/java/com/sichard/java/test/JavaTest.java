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

//        for (int i = 0; i < 10; i++) {
//            new Thread(new Runnable() {
//                @Override
//                public void run() {
//                    SingleTon.getInstance().isInit();
//                }
//            }, "thread-" + i).start();
//        }

        float test = 12.355f;
        System.out.println(Math.round(test * 10) / 10f);
    }


    /**
     * 枚举
     */
    public enum Day2 {
        /**
         * 周一
         */
        MONDAY("星期一"),
        TUESDAY("星期二"),
        WEDNESDAY("星期三"),
        THURSDAY("星期四"),
        FRIDAY("星期五"),
        SATURDAY("星期六"),
        SUNDAY("星期日");

        //中文描述
        private String desc;

        /**
         * 私有构造,防止被外部调用
         *
         * @param desc 描述
         */
        private Day2(String desc) {
            this.desc = desc;
        }

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
