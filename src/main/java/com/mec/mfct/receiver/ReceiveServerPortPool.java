 package com.mec.mfct.receiver;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * ����port�أ����������»����趨��<br>
 * prot�ص�ȡֵ��Χ�������ޣ�������������Сֵ��Χ��<br>
 * ���е�port�����Է���ʹ�á�<br>
 * ʵ���ֶ��м򵥡���ͨ���ɻ������֣�<br>
 * ��ģʽ������һ������������ֵΪminPort��ÿ�������������������ΪmaxPort�󣬻������·���<br>
 * ��ͨģʽ������һ���̰߳�ȫ�Ķ��У����ô�minPort��maxPort����������ʼ����<br>
 *      �趨���¼���������
 * <ul>
 *      <li>boolean hasNext();ֻҪ���зǿգ��򣬷����棻</li>
 *      <li>int next();���Ƿ��ض���port����"������";</li>
 *      <li>void returnPort(int port);�黹port����β��</li>
 * </ul>
 * �ɻ���ģʽ�����������̰߳�ȫ�Ķ��У��ֱ�Ϊ:�ѷ���port���к�δ����port���У�<br>
 * �ң����е��ѷ���port���еķ����࣬������<br>
 * int port;<br>
 * long time;<br>
 * ReceiveServer server;<br>
 * ���е�time�Ƿ���ʱ�䣬��System.currentMilliTime()Ϊֵ��<br>
 * server����port�����Ľ��շ�������<br>
 * ������DidaDidaʱ�ӣ�������30����δ�黹��port��ͨ��serverǿ�ƹرգ�������port��<br>
 * ��ʱʱ������ã�port��Χ�����á�
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
