 package com.mec.mfct.strategy;

import java.util.ArrayList;
import java.util.List;

import com.mec.mfct.section.FileSectionInfo;

/**
 * 
 * <ol>
 * 功能：资源分配实现
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class ResourceAllocation implements IResourceAllocation {
    private int maxSectionLength;
    
    public ResourceAllocation() {
        maxSectionLength = DEFAULT_MAX_SECTION_LENGTH;
    }
    
    @Override
    public void setMaxSectionLength(int maxSectionLength) {
        if (maxSectionLength > MIN_SECTION_LENGTH) {
            this.maxSectionLength = maxSectionLength;
        }
    }

    @Override
    public List<List<FileSectionInfo>> allocationSectionInfo(List<FileSectionInfo> fsiList, int senderCount) {
        if (senderCount < 0) {
            return null;
        }
        List<List<FileSectionInfo>> sectionListList = new ArrayList<>();
        for (int index = 0; index < senderCount; index++) {
            List<FileSectionInfo> sectionList = new ArrayList<FileSectionInfo>();
            sectionListList.add(sectionList);
        }
        
        int index = 0;
        for (FileSectionInfo fsi : fsiList) {
            int fSize = fsi.getSize();
            if (fSize < maxSectionLength) {
                List<FileSectionInfo> indexList = sectionListList.get(index);
                indexList.add(fsi);
                index = (index + 1) % senderCount;
                continue;
            }
            
            long offset = 0L;
            int restLen = fSize;
            int len;
            while (restLen > 0) {
                len = restLen > maxSectionLength ? maxSectionLength : restLen;
                List<FileSectionInfo> indexList = sectionListList.get(index);
                indexList.add(new FileSectionInfo(fsi.getFileHandle(), offset + fsi.getOffset(), len));
                offset += len;
                restLen -= len;
                index = (index + 1) % senderCount;
            }
        }
        return sectionListList;
    }
}
