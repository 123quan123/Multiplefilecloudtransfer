 package com.mec.mfct.sender;

import java.util.HashMap;
import java.util.Map;

import com.mec.mfct.resource.ResourceBaseInfo;
/**
 * 每个资源端应该有的
 * <ol>
 * 功能：存放资源框架
 * </ol>
 * @author Quan
 * @date 2020/02/08
 * @version 0.0.1
 */
public class ResourcePool {
    private static final Map<String, ResourceBaseInfo> resourcePool = new HashMap<String, ResourceBaseInfo>();
     
    public ResourcePool() {
    }
    /**
              * 接受APP服务器发过来的资源框架信息<br>
     * <li>
                 * 情况一：
                 *  若发送的资源框架信息没有存在，则加入
     * </li>
     * <li>
             * 情况二：
             *  若发送的资源框架信息存在，则比较版本
     * <ol>
             *  一：若版本一致，不处理
     * </ol><ol>
             *  二：若不一致则按照第一种情况处理。
     * </ol>
     * </li>
     * @param rbi
     * @return
     */
    public boolean addResource(ResourceBaseInfo rbi) {
        String name = rbi.getName();
        ResourceBaseInfo orgRbi = resourcePool.get(name);
        
        if (orgRbi == null || rbi.getVersion() != orgRbi.getVersion()) {
            resourcePool.put(name, rbi);
            return true;
        }
        return false;
    }
    
    public boolean removeResource(ResourceBaseInfo rbi) {
        String name = rbi.getName();
        ResourceBaseInfo orgRbi = resourcePool.get(name);
        
        if (orgRbi != null && rbi.getVersion() == orgRbi.getVersion()) {
            resourcePool.remove(name, orgRbi);
            return true;
        }
        return false;
    }
    
    public static ResourceBaseInfo getResourceBaseInfo(String name) {
        return resourcePool.get(name);
    }
    
    public boolean isEmpty() {
        return resourcePool.isEmpty();
    }
}
