 package com.mec.mfct.sender;

import java.lang.reflect.UndeclaredThrowableException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.ThreadPoolExecutor;

import com.HTT.classTableMapping.PropertiesUtil;
import com.mec.mfct.center.ICenterAction;
import com.mec.mfct.resource.ResourceBaseInfo;
import com.mec.rmi.core.ClientProxy;
import com.mec.rmi.core.MethodFactory;
import com.mec.rmi.core.RMIClient;
import com.mec.rmi.core.RMIServer;
import com.mec.rmi.node.Node;

/**
 * 
 * <ol>
 * 功能：sender方所需要开启的RMIServer服务器，接受来自需求方的请求
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class SenderRmiServer {
    static String ip;
    static {
        InetAddress address;
        try {
            address = InetAddress.getLocalHost();
            ip = address.getHostAddress();
            System.out.println("ip: " + ip);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }
     private ResourcePool resourcePool;
     private Node me;
     private RMIServer SenderServer;
     private ThreadPoolExecutor threadPoolExecutor;
     private MethodFactory methodFactory;
     private boolean open;
     private ICenterAction action;
     private Node centerNode;
     private ClientProxy clientProxy;
     
     public SenderRmiServer() {
         methodFactory = new MethodFactory();
         methodFactory.collectionMethod("com.mec.mfct.sender");
         SenderServer = new RMIServer();
         centerNode = new Node("192.168.41.1", 54188);
         clientProxy = new ClientProxy();
         clientProxy.setRmiClient(new RMIClient(centerNode.getPort(), centerNode.getIp()));
         loadConfig();
     }
     
     private void loadConfig() {
         loadConfig("/config.port.properties");
     }
     
     private void loadConfig(String path) {
         PropertiesUtil.loadProperties(path);
         String configPort = PropertiesUtil.getValue("rmiPort");
         if (configPort != null) {
             int port = Integer.valueOf(configPort);
             System.out.println("配置Port：" + port);
             SenderServer.setPort(port);
             me = new Node(ip, port);
         }
     }
     
     public boolean registryResource(Node netNode, ResourceBaseInfo rbi) {
         if (resourcePool == null) {
             System.out.println("请先设置resourcePool");
             return false;
         }
//         if (!open) {
//             open = startServer();
//         }
         if (netNode == null) {
             netNode = me;
         }
         resourcePool.addResource(rbi);
         boolean ok = false;
         try {
             action = clientProxy.getProxy(ICenterAction.class, false);
             ok = action.registryNode(netNode, rbi);
         } catch (UndeclaredThrowableException e) {
            System.out.println("注册中心未开启");
            System.out.println(e);
            return false;
        }
         return ok;
     }
     
     public boolean logOutNode(Node netNode, ResourceBaseInfo rbi) {
         if (resourcePool == null) {
             System.out.println("请先设置resourcePool");
             return false;
         }
         if (netNode == null) {
             netNode = me;
         }
         action = clientProxy.getProxy(ICenterAction.class, false);
         boolean ok = action.logOutNode(netNode);
         if (ok) {
             if (!SenderServer.isShutDown()) {
                 SenderServer.shutDown();
                 open = false;
             }
             return true;
         }
         return false;
     }
     
     public boolean startServer() {
         SenderServer.setThreadPool(threadPoolExecutor);
         open = SenderServer.startRMIServer();
         return open;
     }
     
     public void setResourcePool(ResourcePool resourcePool) {
        this.resourcePool = resourcePool;
     }

     public void closeServer() {
         if (!SenderServer.isShutDown() || open) {
             SenderServer.shutDown();
             open = false;
             System.out.println("关闭服务器");
         }
     }
     
     public void setThreadPool(ThreadPoolExecutor threadPoolExecutor) {
         this.threadPoolExecutor = threadPoolExecutor;
     }
}
