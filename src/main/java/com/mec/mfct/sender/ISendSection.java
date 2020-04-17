 package com.mec.mfct.sender;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.node.Node;

public interface ISendSection {
     void sendSectionInfo(Node receiverNode, ResourceBaseInfo rbi);
}
