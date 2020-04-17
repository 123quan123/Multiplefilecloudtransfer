 package com.mec.mfct.section;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mec.mfct.exception.PeerDownException;

/**
 * 
 * <ol>
 * 功能：接受底层实现
 * <li></li>
 * <li></li>
 * <li></li>
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public class ReceiveAndSend implements IReceiveAndSend {
    private int bufferSize = DEFAULT_BUFFER_SIZE;

    public int getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public void send(DataOutputStream dos, byte[] value) throws IOException {
        dos.write(value);
    }

    @Override
    public byte[] receive(DataInputStream dis, int size) throws IOException, PeerDownException {
        byte[] result = new byte[size];
        int restLen = size;
        int offset = 0;
        int readLen = 0;
        int len = 0;
        while (restLen > 0) {
            len = (restLen > bufferSize ? bufferSize : restLen);
            readLen = dis.read(result, offset, len);
            if (readLen == -1) {
                throw new PeerDownException("对端下线");
            }
            offset += readLen;
            restLen -= readLen;
        }
        
        return result;
    }

}
