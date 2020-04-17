 package com.mec.mfct.section;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mec.mfct.exception.PeerDownException;
import com.mec.mfct.exception.WrongHeadLenException;

/**
 * 
 * <ol>
 * 功能：文件片段类
 * <li>fileSectionInfo单个资源的片段表示</li>
 * <li>value：对于的值的表示</li>
 * <li>receiveAndSend：对片段接受和发送的工具</li>
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class FileSection {
     private FileSectionInfo fileSectionInfo;
     private byte[] value;
     private IReceiveAndSend receiveAndSend;
     
     public FileSection() {
         receiveAndSend = new ReceiveAndSend();
    }

    public void setReceiveAndSend(IReceiveAndSend receiveAndSend) {
        this.receiveAndSend = receiveAndSend;
    }

    public FileSectionInfo getFileSectionInfo() {
        return fileSectionInfo;
    }

    public void setFileSectionInfo(FileSectionInfo fileSectionInfo) {
        this.fileSectionInfo = fileSectionInfo;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }
    
    /**
     * a接收资源时使用16字节来将其封装发送单个资源片段的头和值
     * @param dis
     * @throws IOException
     * @throws WrongHeadLenException
     * @throws PeerDownException
     */
    public void receiveFileSection(DataInputStream dis) throws IOException, WrongHeadLenException, PeerDownException {
        byte[] headSection = receiveAndSend.receive(dis, FileSectionInfo.DEFAULT_HEAD_BYTE_LEN);
        this.fileSectionInfo = new FileSectionInfo(headSection);
        this.value = receiveAndSend.receive(dis, fileSectionInfo.getSize());
    }
    
    /**
     * a发送资源时使用16字节来将其封装发送单个资源片段的头和值
     * @param dos
     * @throws IOException
     */
    public void sendFileSection(DataOutputStream dos) throws IOException{
        receiveAndSend.send(dos, fileSectionInfo.toBytes());
        receiveAndSend.send(dos, value);
    }

    @Override
    public String toString() {
        return "FileSection [fileSectionInfo=" + fileSectionInfo + "]";
    }
    
    
}
