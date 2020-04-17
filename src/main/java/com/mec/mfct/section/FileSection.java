 package com.mec.mfct.section;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mec.mfct.exception.PeerDownException;
import com.mec.mfct.exception.WrongHeadLenException;

/**
 * 
 * <ol>
 * ���ܣ��ļ�Ƭ����
 * <li>fileSectionInfo������Դ��Ƭ�α�ʾ</li>
 * <li>value�����ڵ�ֵ�ı�ʾ</li>
 * <li>receiveAndSend����Ƭ�ν��ܺͷ��͵Ĺ���</li>
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
     * a������Դʱʹ��16�ֽ��������װ���͵�����ԴƬ�ε�ͷ��ֵ
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
     * a������Դʱʹ��16�ֽ��������װ���͵�����ԴƬ�ε�ͷ��ֵ
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
