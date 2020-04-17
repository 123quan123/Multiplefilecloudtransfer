 package com.mec.mfct.section;

import com.mec.mfct.exception.WrongHeadLenException;

/**
 * 
 * <ol>
 * 功能：用来表示单个资源的类或其片段
 * <li>fileHandle 单个资源的标识ID 与 rsiList 中的资源结构对应</li>
 * <li>size标识这段单个资源片段的大小</li>
 * <li>offset标识从单个资源的其实位置开始</li>
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class FileSectionInfo {
     public static final int DEFAULT_HEAD_BYTE_LEN = 16;
     
     private int fileHandle;
     private int size;
     private long offset;
     
     public FileSectionInfo() {
       
     }
     
     public FileSectionInfo(int fileHandle, long offset, int size) {
        setFileHandle(fileHandle);
        setOffset(offset);
        setSize(size);
     }
     
     public FileSectionInfo(FileSectionInfo fileSectionInfo) {
        setFileHandle(fileSectionInfo.getFileHandle());
        setOffset(fileSectionInfo.getOffset());
        setSize(fileSectionInfo.getSize());
     }
     
     /**
      *  a在这里我们规定了其资源传输中的规定，以16字节开始发送分别如下代表
      *  bFileHandle：代表传输中的哪个文件ID
      *  bOffset：单个资源的哪个位置开始
      *  bSize：单个资源的传输长度
      * @param byteSectionInfo
      * @throws WrongHeadLenException
      */
     public FileSectionInfo(byte[] byteSectionInfo) throws WrongHeadLenException {
         if (byteSectionInfo.length != DEFAULT_HEAD_BYTE_LEN) {
             throw new WrongHeadLenException("错误的头字节数");
         }
         
         byte[] bFileHandle = ByteString.getBytesAt(byteSectionInfo, 0, 4);
         byte[] bOffset = ByteString.getBytesAt(byteSectionInfo, 4, 8);
         byte[] bSize = ByteString.getBytesAt(byteSectionInfo, 12, 4);
         
         setFileHandle(ByteString.bytesToInt(bFileHandle));
         setOffset(ByteString.bytesToLong(bOffset));
         setSize(ByteString.bytesToInt(bSize));
     }
     
     public byte[] toBytes() {
         byte[] result = new byte[DEFAULT_HEAD_BYTE_LEN];
         
         byte[] bFileHandle = ByteString.intToBytes(this.fileHandle);
         byte[] bOffset = ByteString.longToBytes(this.offset);
         byte[] bSize = ByteString.intToBytes(this.size);
         
         ByteString.setBytesAt(result, 0, bFileHandle);
         ByteString.setBytesAt(result, 4, bOffset);
         ByteString.setBytesAt(result, 12, bSize);
         
         return result;
     }
    
    public int getFileHandle() {
        return fileHandle;
    }

    public void setFileHandle(int fileHandle) {
        this.fileHandle = fileHandle;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }
    
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + fileHandle;
        result = prime * result + (int)(offset ^ (offset >>> 32));
        result = prime * result + size;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        FileSectionInfo other = (FileSectionInfo)obj;
        if (fileHandle != other.fileHandle)
            return false;
        if (offset != other.offset)
            return false;
        if (size != other.size)
            return false;
        return true;
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
