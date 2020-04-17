 package com.mec.mfct.test;

public class TestThreadLocal {
     public static void main(String[] args) throws InterruptedException {

         int threads = 3;
         InnerClass innerClass = new InnerClass();
         for(int i = 1; i <= threads; i++) {
             new Thread(new Runnable() {
                
                @Override
                public void run() {
                    for(int j = 0; j < 4; j++) {
                        innerClass.add(String.valueOf(j));
                        innerClass.print();
                      }
                      innerClass.set("hello world");
                    }
                }).start();
       }
     }
       private static class InnerClass {

         public void add(String newStr) {
           StringBuilder str = StringBuilderFactory.local.get();
           StringBuilderFactory.local.set(str.append(newStr));
         }

         public void print() {
           System.out.printf("Thread name:%s , ThreadLocal hashcode:%s, Instance hashcode:%s, Value:%s\n",
           Thread.currentThread().getName(),
           StringBuilderFactory.local.hashCode(),
           StringBuilderFactory.local.get().hashCode(),
           StringBuilderFactory.local.get().toString());
         }

         public void set(String words) {
           StringBuilderFactory.local.set(new StringBuilder(words));
           System.out.printf("Set, Thread name:%s , ThreadLocal hashcode:%s,  Instance hashcode:%s, Value:%s\n",
           Thread.currentThread().getName(),
           StringBuilderFactory.local.hashCode(),
           StringBuilderFactory.local.get().hashCode(),
           StringBuilderFactory.local.get().toString());
         }
       }

       private static class StringBuilderFactory {

         private static ThreadLocal<StringBuilder> local = new ThreadLocal<StringBuilder>() {
           @Override
           protected StringBuilder initialValue() {
             return new StringBuilder();
           }
         };

       }
}
