 package com.mec.mfct.receiver;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * 关于port池，首先有如下基本设定：<br>
 * prot池的取值范围必须有限，且设置最大和最小值范围；<br>
 * 其中的port，可以反复使用。<br>
 * 实现手段有简单、普通、可回收三种：<br>
 * 简单模式：定义一个整型量，初值为minPort；每次申请后自增；当增加为maxPort后，回绕重新分配<br>
 * 普通模式：定义一个线程安全的队列，并用从minPort到maxPort的整型量初始化；<br>
 *      设定如下几个方法：
 * <ul>
 *      <li>boolean hasNext();只要队列非空，则，返回真；</li>
 *      <li>int next();总是返回队首port，并"出队列";</li>
 *      <li>void returnPort(int port);归还port到队尾。</li>
 * </ul>
 * 可回收模式：定义两个线程安全的队列，分别为:已分配port队列和未分配port队列；<br>
 * 且，其中的已分配port队列的泛型类，包括：<br>
 * int port;<br>
 * long time;<br>
 * ReceiveServer server;<br>
 * 其中的time是分配时间，以System.currentMilliTime()为值；<br>
 * server是用port建立的接收服务器；<br>
 * 并启用DidaDida时钟，将超过30分钟未归还的port，通过server强制关闭，并回收port。<br>
 * 超时时间可配置；port范围可配置。
 * @author quan
 *
 */
public class ReceiveServerPortPool {
     private static final int DEFAULT_PORT_SIZE = 100;
     private static Queue<Integer> portQueue = new LinkedBlockingDeque<Integer>();
     
     static {
         for(int i = 0; i < DEFAULT_PORT_SIZE; i++) {
             portQueue.add(Integer.valueOf(54000 + i));
         }
     }
     
     public ReceiveServerPortPool() {
     }
     
     public static boolean hasNext() {
         return portQueue.size() != 0;
     }
     
     public static int next() {
         return portQueue.poll();
     }
     
     public static boolean returnPort(int port) {
         if (portQueue.size() >= DEFAULT_PORT_SIZE) {
             return false;
         }
         portQueue.add(port);
         return true;
     }
}
