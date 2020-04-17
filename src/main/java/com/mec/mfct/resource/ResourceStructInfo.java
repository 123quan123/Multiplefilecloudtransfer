 package com.mec.mfct.resource;

 /**
  * 
  * <ol>
  * 功能：单个资源的相对路径结构类
  * <li>fileHandle 单个资源的标识ID</li>
  * <li>filePath 相对路径</li>
  * <li>Fsize 单个资源的总长度</li>
  * <li>checksum 单个资源的检验和来检测是否是正确的资源</li>
  * </ol>
  * @author Quan
  * @date 2020/03/06
  * @version 0.0.1
  */
public class ResourceStructInfo {
    private int fileHandle;
    private String filePath;
    private long Fsize;
    private int checksum;
    
    public ResourceStructInfo() {
    }
    
    public ResourceStructInfo(ResourceStructInfo rsi) {
        this.fileHandle = rsi.fileHandle;
        this.filePath = rsi.filePath;
        this.Fsize = rsi.Fsize;
        this.checksum = rsi.checksum;
    }
    
    public int getFileHandle() {
        return fileHandle;
    }
    
    public void setFileHandle(int fileHandle) {
        this.fileHandle = fileHandle;
    }
    
    public String getFilePath() {
        return filePath;
    }
    
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public long getFsize() {
        return Fsize;
    }

    public void setFsize(long fsize) {
        Fsize = fsize;
    }
  
    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return "ResourceStructInfo [fileHandle=" + fileHandle + ", filePath=" + filePath + ", Fsize=" + Fsize
            + ", checksum=" + checksum + "]";
    }
   
}
