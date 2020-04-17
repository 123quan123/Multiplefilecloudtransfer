package com.mec.mfct.receiver;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mec.util.CreateFileUtil;

/**
 * 
 * <ol>
 * 功能：用来将文件的读写句柄放在map中，提高操作利用率
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class RandAccessFilePool {
    private static Map<String, RandomAccessFile> rafPool;
    	
    static {
        rafPool = new ConcurrentHashMap<>();
    }
    
	RandAccessFilePool() {
	}
	
	RandomAccessFile getRaf(String filePath) {
		RandomAccessFile raf = rafPool.get(filePath);
		
		if (raf == null) {
			try {
                // TODO 根据filePath，创建相关目录
			    CreateFileUtil.createFile(filePath);
				raf = new RandomAccessFile(filePath, "rw");
				rafPool.put(filePath, raf);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		
		return raf;
	}
	
	public static void closePool() {
	    for (RandomAccessFile raf : rafPool.values()) {
	        try {
                raf.close();
            } catch (IOException e) {
                raf = null;
            }
	    }
	}
}
