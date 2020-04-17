 package com.mec.mfct.receiver;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mec.mfct.exception.PeerDownException;
import com.mec.mfct.exception.WrongHeadLenException;
import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.mfct.resource.ResourceStructInfo;
import com.mec.mfct.section.FileSection;
import com.mec.mfct.section.FileSectionInfo;
import com.mec.mfct.section.UnReceivedFileInfo;
import com.mec.mfct.section.UnReceivedFileSectionInfo;
import com.mec.mfct.view.IRecieveViewAction;

/**
 * 
 * <ol>
 * ���ܣ������ṩ�˷�������Դ�̵߳Ľ����߳�
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class Receiver implements Runnable {
     private Socket socket;
     private DataInputStream dis;
     private ResourceBaseInfo rbi;
     private Map<Integer, ResourceStructInfo> rsiMap;
     private Map<Integer, UnReceivedFileInfo> urfMap;
     private IRecieveViewAction action;
     
     public Receiver(Socket socket, Map<Integer, UnReceivedFileInfo> urfMap, ResourceBaseInfo rbi, IRecieveViewAction action) {
         this.rbi = rbi;
         this.socket = socket;
         this.urfMap = urfMap;
         this.action = action;
         this.rsiMap = new HashMap<Integer, ResourceStructInfo>();
         
         List<ResourceStructInfo> rsiList = rbi.getRsiList();
         for (ResourceStructInfo resourceStructInfo : rsiList) {
             rsiMap.put(resourceStructInfo.getFileHandle(), resourceStructInfo);
         }
         //��Ľ�
//         new Thread(this).start();
//         new MThreadPool().newInstance().execute(this);
     }

    @Override
    public void run() {
        String absoluteRoot = rbi.getAbsoluteRoot();
        RandAccessFilePool randAccessFilePool = new RandAccessFilePool();
        try {
            this.dis = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            System.out.println("ĳ�����̳߳���");
            close();
            return;
        }
        while (true) {
            FileSection fileSection = new FileSection();
            try {
                fileSection.receiveFileSection(dis);
//                System.out.println(fileSection);
                FileSectionInfo fileSectionInfo = fileSection.getFileSectionInfo();
                if (fileSectionInfo == new FileSectionInfo(-1, -1, -1)) {
                    close();
                    throw new Exception("�Է�����Դ");
                }
                long receiveSize = fileSectionInfo.getSize();
                
                byte[] value = fileSection.getValue();
                
                int fileHandle = fileSectionInfo.getFileHandle();
//                System.out.println("fileHandle" + fileHandle);
                ResourceStructInfo rsi = rsiMap.get(fileHandle);
                String filePath = null;
                filePath = absoluteRoot + rsi.getFilePath();
                RandomAccessFile raf = randAccessFilePool.getRaf(filePath);
                raf.seek(fileSectionInfo.getOffset());
                raf.write(value);
                
                UnReceivedFileInfo unReceivedFileInfo = urfMap.get(fileHandle);
                unReceivedFileInfo.afterReceiveSection(new UnReceivedFileSectionInfo(fileSectionInfo));    
                action.change(receiveSize, rbi.getTotalSize());
            } catch (PeerDownException e) {
                System.out.println("�Է��رգ���������");
                close();
                if (ReceiverServer.isServerCanClose()) {
                    ReceiverServer.close();
                }
                return;
            } catch (WrongHeadLenException e) {
                System.out.println("����ͷ��Ϣ���������");
            } catch (IOException e) {
//                 e.printStackTrace();
                close();
            } catch (Exception e) {
//                 e.printStackTrace();
                close();
            }
        }
    }
    
    private void close() {
        if (socket != null || !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                socket = null;
            }
        }
        if (dis != null) {
            try {
                dis.close();
            } catch (IOException e) {
                dis = null;
            }
        }
    }
}
