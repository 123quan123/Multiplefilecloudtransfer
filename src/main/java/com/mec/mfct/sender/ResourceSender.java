 package com.mec.mfct.sender;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.annotation.RMIInterfaces;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * ���ܣ���Դ�ṩ�˸�������ȥ�����̴߳�resourcePool�з�����ԴƬ��
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
@RMIInterfaces(rmiInterfaces = {ISendSection.class})
public class ResourceSender implements ISendSection {

    @Override
    public void sendSectionInfo(Node receiverNode, ResourceBaseInfo rbi) {
        
        Sender senderClient = new Sender();
        senderClient.setNode(receiverNode);
        senderClient.setRbi(rbi);
        
        new Thread(senderClient).start();
    }
     
}
