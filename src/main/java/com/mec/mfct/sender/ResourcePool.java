 package com.mec.mfct.sender;

import java.util.HashMap;
import java.util.Map;

import com.mec.mfct.resource.ResourceBaseInfo;
/**
 * ÿ����Դ��Ӧ���е�
 * <ol>
 * ���ܣ������Դ���
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
              * ����APP����������������Դ�����Ϣ<br>
     * <li>
                 * ���һ��
                 *  �����͵���Դ�����Ϣû�д��ڣ������
     * </li>
     * <li>
             * �������
             *  �����͵���Դ�����Ϣ���ڣ���Ƚϰ汾
     * <ol>
             *  һ�����汾һ�£�������
     * </ol><ol>
             *  ��������һ�����յ�һ���������
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
