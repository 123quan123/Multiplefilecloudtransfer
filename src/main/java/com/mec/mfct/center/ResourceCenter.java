 package com.mec.mfct.center;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.HTT.classTableMapping.PropertiesUtil;
import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.annotation.RMIInterfaces;
import com.mec.rmi.node.INetNode;
import com.mec.rmi.node.Node;

/**
 * 这里的方法可以通过RPC来调用
 * <ol>
 * 功能：资源管理中心
 * <li>收集注册节点</li>
 * <li>注销节点并按照因子定期处理</li>
 * <li>请求资源时发出所有已有资源节点</li>
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
@RMIInterfaces(rmiInterfaces = {ICenterAction.class})
public class ResourceCenter implements ICenterAction {
    private static final double DEFAULT_THRESHOLD = 1.0;
     
    private static final Map<Integer, List<Node>> resourceNodeMap = new HashMap<Integer, List<Node>>();
    private static final Map<Integer, Node> nodeMap = new HashMap<Integer, Node>();
    private static final Map<Integer, ResourceBaseInfo> resourceMap = new HashMap<Integer, ResourceBaseInfo>();
     
    private static double validityThreshold = DEFAULT_THRESHOLD;
    private static int removeNodeCount;
     
    public ResourceCenter() {
    }
    
    private void loadNodeSelectStrategy() {
        
        String strValidityThreshold = PropertiesUtil.getValue("validityThreshold");
        if (strValidityThreshold != null) {
            try {
                double threshold = Double.valueOf(strValidityThreshold);
                validityThreshold = threshold;
            } catch (Exception e) {
            }
        }
    }
    
    public void loadNetNodeStrategyConfig(String resConfigPath) {
        PropertiesUtil.loadProperties(resConfigPath);
        loadNodeSelectStrategy();
    }

    @Override
    public boolean registryNode(Node Node, List<ResourceBaseInfo> resourceList) {
       int nodeHashCode = Node.hashCode();
       nodeMap.put(nodeHashCode, Node);
       
       for (ResourceBaseInfo resourceBaseInfo : resourceList) {
           int resourceHashCode = resourceBaseInfo.hashCode();
           resourceMap.put(resourceHashCode, resourceBaseInfo);
           
           synchronized (resourceNodeMap) {
               List<Node> nodeList = resourceNodeMap.get(resourceHashCode);
               if (nodeList == null) {
                   nodeList = new LinkedList<Node>();
                   resourceNodeMap.put(resourceHashCode, nodeList);
               }
               nodeList.add(Node);
               for (INetNode node : nodeList) {
                System.out.println(node);
            }
           }
           return true;
       }
       return false;
    }

    @Override
    public boolean registryNode(Node Node, ResourceBaseInfo rbi) {
        boolean ok;
        ArrayList<ResourceBaseInfo> rbiList = new ArrayList<ResourceBaseInfo>();
        rbiList.add(rbi);
        ok = registryNode(Node, rbiList);
        if (ok) {
            showRelationMap();
            return true;
        }
        showRelationMap();
        return false;
    }

    /**
     * 注销时按照因子紧缩map
     */
    @Override
    public boolean logOutNode(Node Node) {
        int nodeHashCode = Node.hashCode();
        Node node2 = nodeMap.remove(nodeHashCode);
        System.out.println(node2 + "没空没空了");
        
        cleanRelationMap();
        
        removeNodeCount++;
        if (nodeMap.size() <= 0) {
            return true;
        }
        if ((double) removeNodeCount/nodeMap.size() >= validityThreshold) {
            //紧缩关系map；
            cleanRelationMap();
            return true;
        }
        showRelationMap();
         return true;
    }
    
    private void showRelationMap() {
        for (Integer resourceId : resourceNodeMap.keySet()) {
            List<Node> orgNodeList = resourceNodeMap.get(resourceId);
            System.out.println(resourceMap.get(resourceId) + " : ");
            for (Node iNetNode : orgNodeList) {
                System.out.print(iNetNode + ": \n");
           }
        }
    }
    
    private void cleanRelationMap() {
        synchronized (resourceNodeMap) {
         for (Integer resourceId : resourceNodeMap.keySet()) {
             List<Node> nodeList = new ArrayList<Node>();
             List<Node> orgNodeList = resourceNodeMap.get(resourceId);
             for (Node iNetNode : orgNodeList) {
                 Node node = nodeMap.get(iNetNode.hashCode());
                if (node == null) {
                    System.out.println("153153");
                    continue;
                }
                nodeList.add(iNetNode);
            }
            resourceNodeMap.put(resourceId, nodeList);
         }
         removeNodeCount = 0;
        }
    }

    @Override
    public List<Node> requestResource(ResourceBaseInfo rbi) {
        rbi.setFsiList(null);
        rbi.setFsiList(null);
        int rbiHashCode = rbi.hashCode();
        List<Node> nodeList = new ArrayList<Node>();
         
        synchronized (resourceNodeMap) {
            nodeList = resourceNodeMap.get(rbiHashCode);
        }
        
        if (nodeList == null) {
            //TODO
            //让拥有此节点的资源来注册
            return null;
        }
       
        return nodeList;
    }

}
