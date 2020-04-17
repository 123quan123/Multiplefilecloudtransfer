 package com.mec.mfct.receiver;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadPoolExecutor;

import com.HTT.classTableMapping.PropertiesUtil;
import com.mec.mfct.exception.WrongAgainTransferException;
import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.mfct.resource.ResourceStructInfo;
import com.mec.mfct.section.FileSectionInfo;
import com.mec.mfct.section.UnReceivedFileInfo;
import com.mec.mfct.section.UnReceivedFileSectionInfo;
import com.mec.mfct.view.IRecieveViewAction;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * ���ܣ���Դ������ṩ������������Դ����ʱ������
 * <li>DEFAULT_GET_PORT���ɸ��ݲ���ѡ��ÿ�δ���Դ��������ȡ���Ǵӻ����л�ȡ</li>
 * <li>timerFlag����ʱ����������̣߳����������</li>
 * <li>��������Դδ��ɣ���ϵ�����</li>
 * <li>iRegistrySelf: ���Լ�����Դ��������ע��Ľӿ�</li>
 * <li>IRecieveViewAction�������֪ͨ��������Ľӿ�</li>
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class ReceiverServer implements Runnable {
     static String ip;
     static {
         InetAddress address;
         try {
             address = InetAddress.getLocalHost();
             ip = address.getHostAddress();
         } catch (UnknownHostException e) {
             e.printStackTrace();
         }
     }
    private static int port;
    private static final String DEFAULT_GET_PORT = "local";
    private static ServerSocket receiveServer;
    private static volatile boolean timerFlag = false;
    private static Map<Integer, UnReceivedFileInfo> urfMap;
    
    private ResourceBaseInfo rbi;
    private List<Node> localNodeList;
    
    private static ThreadPoolExecutor executor;
    private static ThreadGroup threadGroup;
    
    private String getPortConfig = DEFAULT_GET_PORT;
    
    private static Timer timer;
    private long delayTime;
    
    private static IRegistrySelf iRegistrySelf;
    private IRecieveViewAction action;
    
    public ReceiverServer() {
        localNodeList = new ArrayList<Node>();
        loadConfig();
    }
    
    private void loadConfig() {
        loadConfig("/config.sender.properties");
    }
    
    private void loadConfig(String path) {
        PropertiesUtil.loadProperties(path);
        String getPortConfig = PropertiesUtil.getValue("requestAgainFrom");
        if (getPortConfig != null && getPortConfig.equalsIgnoreCase("center")) {
            this.getPortConfig = getPortConfig;
        }
    }
    
    public IRecieveViewAction getAction() {
        return action;
    }

    public void setAction(IRecieveViewAction action) {
        this.action = action;
    }

    public ReceiverServer setRbi(ResourceBaseInfo rbi) {
        this.rbi = rbi;
        urfMap = new HashMap<Integer, UnReceivedFileInfo>();
        List<ResourceStructInfo> rsiList = rbi.getRsiList();
        long totalSize = 0;
        for (ResourceStructInfo rsi : rsiList) {
            int handle = rsi.getFileHandle();
            long size = rsi.getFsize();
            totalSize += size;
            UnReceivedFileInfo urf = new UnReceivedFileInfo(handle, size);
            
            urfMap.put(handle, urf);
        }
        rbi.setTotalSize(totalSize);
        return this;
    }

    public void setReallySendNodeList(List<Node> reallySendNodeList) {
        this.localNodeList = reallySendNodeList;
    }

    private static boolean isUrfMapOver() {
        for (UnReceivedFileInfo info : urfMap.values()) {
            if (!info.isOk()) {
                return false;
            }
        }
        return true;
    }
    
    public long getDelayTime() {
        return delayTime;
    }

    public void setDelayTime(long delayTime) {
        this.delayTime = delayTime;
    }

    public static boolean isServerCanClose() {
        if (isUrfMapOver()) {
            System.out.println("*******************************");
            System.out.println("ȫ������");
            System.out.println("*******************************");
          //  iRegistrySelf.registrySelf(new Node(ip, 0));
            return true;
        }
        return false;
    }
    
    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        ReceiverServer.port = port;
        System.out.println("port :" + port);
    }

    public ResourceBaseInfo getRbi() {
        return rbi;
    }
    
    
    public static boolean isTimerFlag() {
        return timerFlag;
    }

    public static void setTimerFlag(boolean timerFlag) {
        ReceiverServer.timerFlag = timerFlag;
    }

    public void setThreadPool(ThreadPoolExecutor threadPoolExecutor) {
         executor = threadPoolExecutor;
    }

    public void startUp() {
        if (action == null) {
            System.out.println("��������action");
            return;
        }
        
        try {
            receiveServer = new ServerSocket(port);
            threadGroup = new ThreadGroup("�����߳���");
            executor.execute(this);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void showMap() {
        for (UnReceivedFileInfo info : urfMap.values()) {
            System.out.println(info);
        }
    }
    
    @Override
    public void run() {
        if (rbi == null) {
//            System.out.println("����յ���Դ��Ϣ�����ڣ�");
            action.receiveFail("����յ���Դ��Ϣ�����ڣ�");
            return;
        }
        while (!isServerCanClose()) {
            try {
                Socket receive = receiveServer.accept();
                Receiver receiver = new Receiver(receive, urfMap, rbi, action);
                @SuppressWarnings("unused")
                Thread receiverThread = new Thread(threadGroup, receiver);
                executor.execute(receiver);
                if (timerFlag == false) {
                    timerFlag = true;
                    timer = new Timer();
                    timer.schedule(new TimerTask() {
                        
                        @Override
                        public void run() {
                            if (threadGroup.activeCount() > 0 || !isServerCanClose()) {
                                cleanThreadGroup();
                                if (isServerCanClose()) {
                                    action.receiveSuccess(rbi.getName());
                                    ReceiverServer.close();
                                    return;
                                }
                                showMap();
                                System.out.println("�ϵ�����");

                                List<FileSectionInfo> fsiList = collectionAgainSection();
                                rbi.setFsiList(fsiList);
                                ResourceRequestor requestor = new ResourceRequestor();
                                List<Node> nodeList = null;
                                if (getPortConfig == DEFAULT_GET_PORT) {
                                    nodeList = localNodeList;
                                } else {
                                    nodeList = requestor.getPortFromCenter(rbi);
                                }
                                try {
                                    requestor.requestResourceAgain(nodeList, rbi, rbi.getAbsoluteRoot());
                                } catch (WrongAgainTransferException e) {
                                    ReceiverServer.close();
                                    action.receiveFail("�ϵ�����ʧ��");
                                    e.printStackTrace();
                                    return;
                                } catch (Exception e) {
                                    ReceiverServer.close();
                                    action.receiveFail("�ϵ�����ʧ��");
                                    e.printStackTrace();
                                    return;
                                }
                            }
                        }
                    }, delayTime);
                }
            } catch (IOException e) {
                System.out.println("�رս��ն˷�����");
                close();
                return;
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private static void cleanThreadGroup() {
        Thread[] threads = new Thread[threadGroup.activeCount()];
        threadGroup.enumerate(threads);
        
        for (Thread thread : threads) {
            System.out.println("�ر�" + thread.getName());
            thread.stop();
        }
    }
    
    private List<FileSectionInfo> collectionAgainSection() {
        List<FileSectionInfo> fsiList = new ArrayList<FileSectionInfo>();
        for (UnReceivedFileInfo info : urfMap.values()) {
            List<UnReceivedFileSectionInfo> urfsiList = info.getSections();
            for (UnReceivedFileSectionInfo uInfo : urfsiList) {
                FileSectionInfo fileSectionInfo = 
                  new FileSectionInfo(uInfo.getFileHandle(), uInfo.getOffset(), (int)uInfo.getSize());
                fsiList.add(fileSectionInfo);
            }
        }
        return fsiList;
    }
    
    public static void close() {
        if (  receiveServer != null || !receiveServer.isClosed()) {
            try {
                receiveServer.close();
            } catch (IOException e) {
                receiveServer = null;
            }
        }
       {
            RandAccessFilePool.closePool();
            ReceiveServerPortPool.returnPort(port);
            try {
                timer.cancel();
            } catch (Exception e) {
            }
            executor.shutdownNow();
            cleanThreadGroup();
        }
    }

}
