 package com.mec.mfct.center;

import java.util.concurrent.ThreadPoolExecutor;

import com.HTT.classTableMapping.PropertiesUtil;
import com.mec.rmi.core.MethodFactory;
import com.mec.rmi.core.RMIServer;

/**
 * 
 * <ol>
 * 功能：需开启一个server用来接收远程请求
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class CenterServer {
    private RMIServer rmiServer;
    private ThreadPoolExecutor threadPoolExecutor;
    private  MethodFactory methodFactory;
    
    public CenterServer() {
        methodFactory = new MethodFactory();
        methodFactory.collectionMethod("com.mec.mfct.center");
        rmiServer = new RMIServer();
        loadConfig();
    }
    
    private void loadConfig() {
        loadConfig("/config.center.properties");
    }
    
    private void loadConfig(String path) {
        PropertiesUtil.loadProperties(path);
        String configPort = PropertiesUtil.getValue("rmiPort");
        if (configPort != null) {
            rmiServer.setPort(Integer.valueOf(configPort));
        }
    }
    
    public boolean startServer() {
        rmiServer.setThreadPool(threadPoolExecutor);
        return rmiServer.startRMIServer();
    }
    
    public boolean closeServer() {
        return rmiServer.shutDown();
    }
    
    public void setThreadPool(ThreadPoolExecutor threadPoolExecutor) {
        this.threadPoolExecutor = threadPoolExecutor;
    }
}
