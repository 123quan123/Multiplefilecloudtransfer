 package com.mec.mfct.sender;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.annotation.RMIInterfaces;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * 功能：资源提供端根据请求去开启线程从resourcePool中发送资源片段
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
