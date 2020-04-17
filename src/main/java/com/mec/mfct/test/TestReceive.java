 package com.mec.mfct.test;

import com.mec.mfct.receiver.ResourceRequestor;
import com.mec.mfct.resource.ResourceBaseInfo;

public class TestReceive {
    public static void main(String[] args) {
        ResourceBaseInfo rbi = new ResourceBaseInfo();
        rbi.setId(1);
        rbi.setName("m");
        rbi.setAbsoluteRoot("F:\\MobileFile\\");
        rbi.setVersion(1);
        
        rbi.exploreResource("F:\\MobileFile\\");
        rbi.saveResource();
        
        ResourceRequestor requestor = new ResourceRequestor();
        requestor.requestResource(rbi, "F:\\test\\");
        
      
    }
}
