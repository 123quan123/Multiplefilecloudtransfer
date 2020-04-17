package com.mec.mfct.strategy;

import java.util.List;

import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * 功能：节点选择策略接口
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public interface INodeSelectStrtegy {
	int DEFAULT_MAX_SEND_COUNT = 5;
	int MIN_SEND_COUNT = 1;
	
	List<Node> selectNodeList(List<Node> nodeList);
	void setMaxSenderCount(int maxSendCount);
}
