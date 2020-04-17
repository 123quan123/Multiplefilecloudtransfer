 package com.mec.mfct.test;

import com.mec.mfct.center.CenterServer;
import com.mec.util.MThreadPool;

public class TestCenter {

    public static void main(String[] args) {
        CenterServer centerServer = new CenterServer();
        centerServer.setThreadPool(new MThreadPool().newInstance());
        centerServer.startServer();
    }

}
