 package com.mec.mfct.test;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.mfct.sender.ResourcePool;
import com.mec.mfct.sender.SenderRmiServer;
import com.mec.rmi.node.Node;
import com.mec.util.MThreadPool;

public class TestSend {

    public static void main(String[] args) {
        ResourceBaseInfo rbi = new ResourceBaseInfo();
        rbi.setId(1);
        rbi.setName("m");
        rbi.setAbsoluteRoot("F:\\MobileFile-\\");
        rbi.setVersion(1);
        
        rbi.exploreResource("F:\\MobileFile-\\");
        rbi.saveResource();
        
        ResourcePool resourcePool = new ResourcePool();
        resourcePool.addResource(rbi);
        
        SenderRmiServer senderRmiServer1 = new SenderRmiServer();
        
        senderRmiServer1.setThreadPool(new MThreadPool().newInstance(false));
        senderRmiServer1.startServer();
        senderRmiServer1.setResourcePool(resourcePool);
        ResourceBaseInfo rbim = new ResourceBaseInfo(rbi);
        
        rbim.setFsiList(null);
        rbim.setRsiList(null);
//        System.out.println(rbi);
        boolean ok = senderRmiServer1.registryResource(null, rbim);
//        boolean ok = senderRmiServer1.logOutNode(new Node(55586), rbim);
        
        System.out.println(ok);
        if (!ok) {
          senderRmiServer1.closeServer();
        }
        
    }

}
