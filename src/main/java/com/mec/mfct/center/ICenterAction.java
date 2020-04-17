 package com.mec.mfct.center;

import java.util.List;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * ���ܣ���Դ�������ĵĽӿ�
 * <li>ע��ڵ�</li>
 * <li>ע���ڵ�</li>
 * <li>��������ڵ�����</li>
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
