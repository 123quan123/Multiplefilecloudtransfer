 package com.mec.mfct.center;

import java.util.List;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * 功能：资源管理中心的接口
 * <li>注册节点</li>
 * <li>注销节点</li>
 * <li>处理请求节点需求</li>
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public interface ICenterAction {
     boolean registryNode(Node Node, List<ResourceBaseInfo> resourceList);
     boolean registryNode(Node Node, ResourceBaseInfo rbi);
     boolean logOutNode(Node Node);
     List<Node> requestResource(ResourceBaseInfo rbi);
}
