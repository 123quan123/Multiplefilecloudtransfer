package com.mec.mfct.section;

import java.util.LinkedList;
import java.util.List;

/**
 * 
 * <ol>
 * 功能：表示一个资源的接受情况
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class UnReceivedFileInfo {
	private int fileHandle;
	private List<UnReceivedFileSectionInfo> sections;
	
	public UnReceivedFileInfo(int fileHandle, long size) {
		this.fileHandle = fileHandle;
		sections = new LinkedList<UnReceivedFileSectionInfo>();
		sections.add(new UnReceivedFileSectionInfo(fileHandle, 0, size));
	}
	
	
	private int getRightSectionIndex(UnReceivedFileSectionInfo sectionInfo) throws Exception {
		int index = 0;
		long offset = sectionInfo.getOffset();
		long size = sectionInfo.getSize();
		
		for (index = 0; index < sections.size(); index++) {
		    UnReceivedFileSectionInfo info = sections.get(index);
			if (info.isRightSection(offset, size)) {
				return index;
			}
		}
        throw new Exception("片段" + sectionInfo + "异常");
	}
	
	public int getFileHandle() {
        return fileHandle;
    }

    public void setFileHandle(int fileHandle) {
        this.fileHandle = fileHandle;
    }

    public List<UnReceivedFileSectionInfo> getSections() {
        return sections;
    }

    public void setSections(List<UnReceivedFileSectionInfo> sections) {
        this.sections = sections;
    }

    public void afterReceiveSection(UnReceivedFileSectionInfo sectionInfo) {
		int index;
		try {
			index = getRightSectionIndex(sectionInfo);
		
			UnReceivedFileSectionInfo orgSection = sections.get(index);
			
			long orgOffset = orgSection.getOffset();
			long orgSize = orgSection.getSize();
			
			long curOffset = sectionInfo.getOffset();
			long curSize = sectionInfo.getSize();
			
			long leftOffset = orgOffset;
			long leftSize = (curOffset - orgOffset);
			
			long rightOffset = curOffset + curSize;
			long rightSize = (orgOffset + orgSize - rightOffset);
			
			sections.remove(index);
			if (leftSize > 0) {
				sections.add(new UnReceivedFileSectionInfo(fileHandle, leftOffset, leftSize));
			}
			if (rightSize > 0) {
				sections.add(new UnReceivedFileSectionInfo(fileHandle, rightOffset, rightSize));
			}
//			System.out.println("合并");
		} catch (Exception e) {
//			e.printStackTrace();
		}
	}
	
	public boolean isOk() {
	    return sections.size() == 0;
	}


    @Override
    public String toString() {
        return "UnReceivedFileInfo [fileHandle=" + fileHandle + ", sections=" + sections.size() + "]";
    }
	
	
}
