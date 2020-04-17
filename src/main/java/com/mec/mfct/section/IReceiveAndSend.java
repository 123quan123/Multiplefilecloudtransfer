 package com.mec.mfct.section;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.mec.mfct.exception.PeerDownException;

/**
 * 
 * <ol>
 * 功能：接受和发送的接口类，可以自行选择不同的实现
 * </ol>
 * @author Quan
 * @date 2020/03/06
 * @version 0.0.1
 */
public interface IReceiveAndSend {
    public static final int DEFAULT_BUFFER_SIZE = 1 << 16;//32K
     
    void send(DataOutputStream dos, byte[] value) throws IOException;
    byte[] receive(DataInputStream dis, int size) throws IOException, PeerDownException;
}
