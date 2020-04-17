 package com.mec.mfct.strategy;

import java.util.List;

import com.mec.mfct.section.FileSectionInfo;

/**
 * 
 * <ol>
 * 功能：节点选择策略接口 
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public interface IResourceAllocation {
    int DEFAULT_MAX_SECTION_LENGTH = 1 << 22;
    int MIN_SECTION_LENGTH = 1 << 16;
    
    void setMaxSectionLength(int maxSectionLength);
    List<List<FileSectionInfo>> allocationSectionInfo(List<FileSectionInfo> fsiList, int senderCount);
}
