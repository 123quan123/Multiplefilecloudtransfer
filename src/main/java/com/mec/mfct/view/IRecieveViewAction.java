package com.mec.mfct.view;

import java.net.Socket;

/**
 * 
 * <ol>
 * ���ܣ�view����ʾ�Ľӿ�
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public interface IRecieveViewAction {
	void linkedOnetoView(Socket sender);
	void hasNoSender();
	void receiveFail(String message);
    void change(long value, long maxValue);
    void receiveSuccess(String name);
}
