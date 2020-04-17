 package com.mec.mfct.receiver;

import java.util.ArrayList;
import java.util.List;

import com.mec.mfct.center.ICenterAction;
import com.mec.mfct.exception.WrongAgainTransferException;
import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.mfct.resource.ResourceStructInfo;
import com.mec.mfct.section.FileSectionInfo;
import com.mec.mfct.sender.ISendSection;
import com.mec.mfct.strategy.INodeSelectStrtegy;
import com.mec.mfct.strategy.IResourceAllocation;
import com.mec.mfct.strategy.NodeSelectStrtegy;
import com.mec.mfct.strategy.ResourceAllocation;
import com.mec.mfct.view.IRecieveViewAction;
import com.mec.rmi.core.ClientProxy;
import com.mec.rmi.core.RMIClient;
import com.mec.rmi.node.INetNode;
import com.mec.rmi.node.Node;
import com.mec.util.MThreadPool;

/**
 * 
 * <ol>
 * ���ܣ��������ע����������ڵ�
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class ResourceRequestor {
    public static final String DEFAULT_RMI_IP = "192.168.41.1";
    public static final int DEFAULT_RMI_PORT = 54188;
     
    private String resourceCenterRmiIp;
    private int resourceCenterRmiPort;
    
    private ClientProxy clientProxy;
    
    /**
     * ���кܶ���ṩ��Դ���͵Ľڵ�ʱ����Ҫ��һ����Դ��Ƭ�ֱ����ṩ��
     * �������Դ����Ľӿ�
     */
    private IResourceAllocation resourceAllocation;
    /**
     * ���ⲿ��view��ʵ�ֵĽӿ�
     */
    private IRecieveViewAction recieveViewAction;
    /**
     * �ڵ�ѡ����ԣ������ڿͻ��˵Ľڵ�ѡ��
     */
    private INodeSelectStrtegy nodeSelectStrtegy;
    
    private List<Node> reallySendNodeList;
    private ReceiverServer receiverServer;
    
    private static int port = ReceiveServerPortPool.next();
    
    public ResourceRequestor() {
        resourceCenterRmiIp = DEFAULT_RMI_IP;
        resourceCenterRmiPort = DEFAULT_RMI_PORT;
        nodeSelectStrtegy = new NodeSelectStrtegy();
        reallySendNodeList = new ArrayList<Node>();
        this.resourceAllocation = new ResourceAllocation();
        receiverServer = new ReceiverServer();
        receiverServer.setThreadPool(new MThreadPool().newInstance(false));
    }

    public IResourceAllocation getResourceAllocation() {
        return resourceAllocation;
    }

    public void setResourceAllocation(IResourceAllocation resourceAllocation) {
        this.resourceAllocation = resourceAllocation;
    }

    public String getResourceCenterRmiIp() {
        return resourceCenterRmiIp;
    }

    public void setResourceCenterRmiIp(String resourceCenterRmiIp) {
        this.resourceCenterRmiIp = resourceCenterRmiIp;
    }
    
    public int getResourceCenterRmiPort() {
        return resourceCenterRmiPort;
    }

    public void setResourceCenterRmiPort(int resourceCenterRmiPort) {
        this.resourceCenterRmiPort = resourceCenterRmiPort;
    }
    
    public IRecieveViewAction getRecieveViewAction() {
        return recieveViewAction;
    }

    public void setRecieveViewAction(IRecieveViewAction recieveViewAction) {
        this.recieveViewAction = recieveViewAction;
    }

    private ClientProxy prepareClientProxy() {
        if (clientProxy == null) {
            synchronized (ResourceRequestor.class) {
                if (clientProxy == null) {
                    clientProxy = new ClientProxy();
                    clientProxy.setRmiClient(new RMIClient(resourceCenterRmiPort, resourceCenterRmiIp));
                }
            }
        }
        return clientProxy;
    }
    
    /**
     * ������Դ��
     * 1.�����������ṩ���б�
     * 2.���ĵ����б��Բ���ѡ��ڵ�
     * 3.������Դ
     * 4.��ϵ�ڵ㲢�������ܶ�
     * @param rbi
     * @param selfPath
     */
    public void requestResource(ResourceBaseInfo rbi, String selfPath) {
            if (recieveViewAction == null) {
                System.out.println("��������view");
                return;
            }
            List<Node> nodeList = getPortFromCenter(rbi);
            List<Node> resultNode = nodeSelectStrtegy.selectNodeList(nodeList);
            if (resultNode == null) {
                recieveViewAction.receiveFail("��Դ����δ���� || ��Դ����û�и���Դ -- ���Ժ�����");
                return;
            }
            
            int senderCount = resultNode.size();
            
            receiverServer.setRbi(new ResourceBaseInfo(rbi).setAbsoluteRoot(selfPath));
            receiverServer.setPort(port);
            receiverServer.setDelayTime(5000);
            receiverServer.setAction(recieveViewAction);
            receiverServer.startUp();
            List<List<FileSectionInfo>> fsiListList = resourceAllocation.allocationSectionInfo(rbi.getFsiList(), senderCount);
            Node me = new Node(ReceiverServer.ip, port);
            int reallySenderCount = 0;
            try {
                reallySenderCount = distributeSendSection(me, resultNode, rbi, fsiListList);
            } catch (Exception e) {
            }
            
            if (reallySenderCount > 0 && reallySenderCount <= senderCount) {
                receiverServer.setReallySendNodeList(reallySendNodeList);
            } else {
                recieveViewAction.hasNoSender();
                ReceiverServer.close();
                return;
            }
    }
    
    public void requestResourceAgain(List<Node> nodeList, ResourceBaseInfo rbi, String selfPath) throws Exception {
      
            if (nodeList.isEmpty()) {
                recieveViewAction.hasNoSender();
                throw new WrongAgainTransferException("�ϵ���Port�����ã�����������");
            }
            int senderCount = nodeList.size();
            
            List<List<FileSectionInfo>> fsiListList = resourceAllocation.allocationSectionInfo(rbi.getFsiList(), senderCount);
            Node me = new Node(ReceiverServer.ip, port);
            int reallySenderCount = distributeSendSection(me, nodeList, rbi, fsiListList);
            
            if (reallySenderCount > 0 && reallySenderCount <= senderCount) {
                receiverServer.setReallySendNodeList(reallySendNodeList);
                ReceiverServer.setTimerFlag(false);
            } else {
                recieveViewAction.hasNoSender();
                ReceiverServer.close();
                return;
            }
    }
    
    private int distributeSendSection(Node receiver, List<Node> senderList, ResourceBaseInfo orgRbi, List<List<FileSectionInfo>> fsiListList) throws Exception {
        int senderCount = senderList.size();
        int reallySenderCount = senderCount;
        
        for (Node node : senderList) {
            reallySendNodeList.add(node);
        }
        
        ClientProxy rmiClientProxy = new ClientProxy();
        RMIClient rmiClient = new RMIClient();
        
        for (int i = 0; i < senderCount; i++) {
            INetNode senderNode = senderList.get(i);
            
            rmiClient.setRmiIp(senderNode.getIp());
            rmiClient.setRmiPort(senderNode.getPort());
            
            rmiClientProxy.setRmiClient(rmiClient);
            ResourceBaseInfo targetResource = new ResourceBaseInfo(orgRbi);
            targetResource.setFsiList(fsiListList.get(i));
            ISendSection resourceSender = rmiClientProxy.getProxy(ISendSection.class, false);
            try {
                resourceSender.sendSectionInfo(receiver, targetResource);
                for (List<FileSectionInfo> list : fsiListList) {
                    System.out.println(list);
                }
            } catch (Exception e) {
                
                rmiClient.setRmiIp(resourceCenterRmiIp);
                rmiClient.setRmiPort(resourceCenterRmiPort);
                clientProxy.setRmiClient(rmiClient);
                
                ICenterAction action = clientProxy.getProxy(ICenterAction.class, false);
                boolean ok = action.logOutNode(new Node(senderNode.getIp(), senderNode.getPort()));
                if (ok == false) {
                    throw new Exception("ע������ע���ڵ�ʧ�ܣ�ǿ���˳���������ּ���ѭ��");
                } else {
//                    System.out.println("ע���ڵ�" + senderNode + "�ɹ���");
                }
                reallySendNodeList.remove(senderNode);
                reallySenderCount--;
            }
        }
        
        return reallySenderCount;
    }
    
    public List<Node> getPortFromCenter(ResourceBaseInfo rbi) {
        ClientProxy clientProxy = prepareClientProxy();
        ICenterAction action = clientProxy.getProxy(ICenterAction.class, false);
        try {
            List<FileSectionInfo> fsiList = rbi.getFsiList();
            List<ResourceStructInfo> rsiList = rbi.getRsiList();
            rbi.setFsiList(null);
            rbi.setRsiList(null);
            List<Node> nodeList = action.requestResource(rbi);
            rbi.setFsiList(fsiList);
            rbi.setRsiList(rsiList);
            
            if (nodeList.size() == 0) {
//                System.out.println("ע�������޸���Դ");
                return null;
            }
            
            return nodeList;
        } catch (Exception e) {
//            System.out.println("ע������δ����");
            return null;
        }
    }
    

}
