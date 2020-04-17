package com.mec.mfct.strategy;

import java.util.ArrayList;
import java.util.List;

import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * 功能：节点选择策略接口实现
 * <li>使用桶排序的方法以发送节点的次数来进行自平衡</li>
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class NodeSelectStrtegy implements INodeSelectStrtegy {
	private int maxSenderCount;

	public NodeSelectStrtegy() {
		maxSenderCount = DEFAULT_MAX_SEND_COUNT;
	}
	
	@Override
	public void setMaxSenderCount(int maxSenderCount) {
		this.maxSenderCount = maxSenderCount > MIN_SEND_COUNT 
				? maxSenderCount : MIN_SEND_COUNT;
	}

	@Override
	public List<Node> selectNodeList(List<Node> orgSendList) {
		List<Node> nodeList = orgSendList;
		try {
			int senderCount = nodeList.size();
			if (senderCount <= 1) {
				return nodeList;
			}
			
			senderCount = nodeList.size();
			if (senderCount > maxSenderCount) {
				nodeList = selectMinSendNode(nodeList);
			}
			return nodeList;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

    private List<Node> selectMinSendNode(List<Node> nodeList) {
		List<Node> resultNodeList = new ArrayList<Node>();
		int index = 0;
		
		Node maxNode = nodeList.get(index);
		for (index = 1; index < nodeList.size(); index++) {
		    Node node = nodeList.get(index);
			if (maxNode.getSendTime() < node.getSendTime()) {
				maxNode = node;
			}
		}
		int[] sendCount = new int[maxNode.getSendTime()];
		
		for (index = 0; index < nodeList.size(); index++) {
			int nodeSendTime = nodeList.get(0).getSendTime();
			sendCount[nodeSendTime]++;
		}

		int maxSenderCount = this.maxSenderCount;
		for (index = 0; index < maxSenderCount; index++) {
			if (maxSenderCount >= 0) {
				maxSenderCount -= sendCount[index];
				if (maxSenderCount < 0) {
					sendCount[index] += maxSenderCount;
				}
			} else {
				sendCount[index] = 0;
			}
		}
		
		for (index = 0; index < nodeList.size(); index++) {
		    Node node = nodeList.get(index);
			int count = node.getSendTime();
			if (sendCount[count] >= 0) {
				resultNodeList.add(node);
				sendCount[count]--;
			}
		}
		return resultNodeList;
	}
}
