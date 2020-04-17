 package com.mec.mfct.sender;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.mfct.resource.ResourceStructInfo;
import com.mec.mfct.section.FileSection;
import com.mec.mfct.section.FileSectionInfo;
import com.mec.rmi.node.INetNode;

/**
 * 
 * <ol>
 * 功能：处理发送的单线程
 * <li>每个文件的句柄应该有一个池子管理，这样可减少资源的开销</li>
 * <li>依照请求端的请求去发送相关资源片段</li>
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class Sender implements Runnable {
    private Map<String, RandomAccessFile> rafPool = new HashMap<String, RandomAccessFile>();
     
    private INetNode node;
    private ResourceBaseInfo rbi;
     
    private Socket sender;
    private DataOutputStream dos;
     
    public Sender() {
    }

    public INetNode getNode() {
        return node;
    }

    public void setNode(INetNode node) {
        this.node = node;
    }

    public ResourceBaseInfo getRbi() {
        return rbi;
    }

    public void setRbi(ResourceBaseInfo rbi) {
        this.rbi = rbi;
    }
    
    private void connectToServer() throws UnknownHostException, IOException {
        System.out.println("333");
        try {

            System.out.println(node.getIp() + ":" + node.getPort());
            sender = new Socket(node.getIp(), node.getPort());
        } catch (Exception e) {
            System.out.println("5461161655181651165");
        }
        System.out.println(node.getIp() + ":" + node.getPort());
        this.dos = new DataOutputStream(sender.getOutputStream());
        System.out.println("dis" + dos);
    }
    
    private ResourceStructInfo getRsiByFileHandle(int fileHandle, List<ResourceStructInfo> rsiList) {
        for (ResourceStructInfo rsi : rsiList) {
            if (rsi.getFileHandle() == fileHandle) {
                return rsi;
            }
        }
        return null;
    }
    
    private RandomAccessFile getRaf(String filePath) throws FileNotFoundException{
        RandomAccessFile raf = rafPool.get(filePath);
        if (raf == null) {
            raf = new RandomAccessFile(filePath, "r");
            rafPool.put(filePath, raf);
        }
        return raf;
    }
    
    private void closeFile() {
        for (RandomAccessFile accessFile : rafPool.values()) {
            try {
                accessFile.close();
            } catch (IOException e) {
            }
        }
    }
    
    private void sendSection() {
        ResourceBaseInfo orgRbi = ResourcePool.getResourceBaseInfo(rbi.getName());
        String orgAbsolutePath = orgRbi.getAbsoluteRoot();
        List<FileSectionInfo> fsiList = rbi.getFsiList();
        List<ResourceStructInfo> rsiList = orgRbi.getRsiList();
        for (FileSectionInfo fsi : fsiList) {
            int handle = fsi.getFileHandle();
            String filePath = getRsiByFileHandle(handle, rsiList).getFilePath();
            String path = orgAbsolutePath + filePath;
            
            long offset = fsi.getOffset();
            int size = fsi.getSize();
            byte[] buffer = new byte[size];
            try {
                RandomAccessFile raf = getRaf(path);
                raf.seek(offset);
                raf.read(buffer, 0, size);
                FileSection fileSection = new FileSection();
                fileSection.setFileSectionInfo(new FileSectionInfo(handle, offset, size));
                fileSection.setValue(buffer);
                fileSection.sendFileSection(dos);
            } catch (IOException e) {
                System.out.println(e);
            }
            
        }
        System.out.println("sendsend99999999999999999999999999999999999999999999999999999999999999999999999");
    }
    
    private void sendNoResource() {
            try {
                FileSection fileSection = new FileSection();
                fileSection.setFileSectionInfo(new FileSectionInfo(-1, -1, -1));
                fileSection.sendFileSection(dos);
            } catch (IOException e) {
                System.out.println(e);
            }
        System.out.println("sendsend888888888888888888888888888888888888888888888");
    }

    @Override
    public void run() {
        try {
            
            System.out.println("111");
            connectToServer();

            ResourceBaseInfo rbiFromPool = ResourcePool.getResourceBaseInfo(rbi.getName());
            if (rbiFromPool == null) {
                sendNoResource();
                close();
                System.out.println("send完成，通道关闭");
                return;
            }
            
            System.out.println("222");
            sendSection();
            close();
            System.out.println("send完成，通道关闭");
        } catch (UnknownHostException e) {
        } catch (IOException e) {
        }
        closeFile();
    }
    
    private void close() {
        if (sender != null || !sender.isClosed()) {
            try {
                sender.close();
            } catch (IOException e) {
            } finally {
                sender = null;
            }
        }
        if (dos != null) {
            try {
                dos.close();
            } catch (IOException e) {
            } finally {
                dos = null;
            }
        }
    }
}
