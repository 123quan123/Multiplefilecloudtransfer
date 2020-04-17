 package com.mec.mfct.resource;

 /**
  * 
  * <ol>
  * ���ܣ�������Դ�����·���ṹ��
  * <li>fileHandle ������Դ�ı�ʶID</li>
  * <li>filePath ���·��</li>
  * <li>Fsize ������Դ���ܳ���</li>
  * <li>checksum ������Դ�ļ����������Ƿ�����ȷ����Դ</li>
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
