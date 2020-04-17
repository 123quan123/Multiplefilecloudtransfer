 package com.mec.mfct.section;

 /**
  * 
  * <ol>
  * 功能：未接收到的资源片段的表示
  * </ol>
  * @author Quan
  * @date 2020/03/06
  * @version 0.0.1
  */
public class UnReceivedFileSectionInfo {                                                
     private int fileHandle;                                                            
     private long size;                                                                  
     private long offset;                                                               
                                                                                        
     public UnReceivedFileSectionInfo() {                                                         
                                                                                        
     }                                                                                  
                                                                                        
     public UnReceivedFileSectionInfo(int fileHandle, long offset, long size) {                    
        setFileHandle(fileHandle);                                                      
        setOffset(offset);                                                              
        setSize(size);                                                                  
     }                                                                                  
                                                                                        
     public UnReceivedFileSectionInfo(FileSectionInfo fileSectionInfo) {                          
        setFileHandle(fileSectionInfo.getFileHandle());                                 
        setOffset(fileSectionInfo.getOffset());                                         
        setSize(fileSectionInfo.getSize());                                             
     }                                                                                  
                                                                                         
    public int getFileHandle() {                                                        
        return fileHandle;                                                              
    }                                                                                   
                                                                                        
    public void setFileHandle(int fileHandle) {                                         
        this.fileHandle = fileHandle;                                                   
    }                                                                                   
                                                                                        
    public long getSize() {                                                              
        return size;                                                                    
    }                                                                                   
                                                                                        
    public void setSize(long size) {                                                     
        this.size = size;                                                               
    }                                                                                   
                                                                                        
    public long getOffset() {                                                           
        return offset;                                                                  
    }                                                                                   
                                                                                        
    public void setOffset(long offset) {                                                
        this.offset = offset;                                                           
    }                                                                                   
                                                                                        
    public boolean isRightSection(long offset, long size2) {                              
        return (this.offset <= offset) && (this.offset + this.size) >= (offset + size2); 
    }                                                                                   
                                                                                        
    @Override                                                                           
    public String toString() {                                                          
        StringBuffer res = new StringBuffer("fileHandle:");                             
        res.append(this.fileHandle).append(",")                                         
        .append("offset:").append(this.offset).append(",")                              
        .append("size:").append(this.size);                                             
                                                                                        
        return res.toString();                                                          
    }                                                                                   
}                                                                                       
                                                                                        