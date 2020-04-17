package com.mec.mfct.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import com.HTT.Util.ViewTool;
import com.mec.mfct.receiver.ResourceRequestor;
import com.mec.mfct.resource.ResourceBaseInfo;

/**
 * 
 * <ol>
 * 功能：自己写的进度条实现类test，用来show界面
 * </ol>
 * @author Quan
 * @date 2020/03/07
 * @version 0.0.1
 */
public class ViewShowProgressBar implements IRecieveViewAction {
	private JFrame jfrmView;
	private JProgressBar jpgb;
	private volatile int barLen;
	
	public ViewShowProgressBar() {
		jfrmView = new JFrame("接收文件进度条");
		jfrmView.setSize(500, 300);
		jfrmView.setLayout(new BorderLayout());
		jfrmView.setLocationRelativeTo(null);
		jfrmView.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JLabel jlblTopic = new JLabel("接收文件进度条", 0);
		jlblTopic.setFont(new Font("微软雅黑", Font.BOLD, 28));
		jfrmView.add(jlblTopic, "North");
		
		JPanel jpnlProgressBar = new JPanel(new FlowLayout());
		jfrmView.add(jpnlProgressBar);
		
		jpgb = new JProgressBar();
		jpnlProgressBar.add(jpgb);
		jpgb.setStringPainted(true);
		jpgb.setMaximum(100);
	}
	
	public void showView() {
		jfrmView.setVisible(true);

        ResourceBaseInfo rbi = new ResourceBaseInfo();
        rbi.setId(1);
        rbi.setName("m");
        rbi.setAbsoluteRoot("F:\\MobileFile-\\");
        rbi.setVersion(1);
        
        rbi.exploreResource("F:\\MobileFile-\\");
        rbi.saveResource();
        
        ResourceRequestor requestor = new ResourceRequestor();
        requestor.setRecieveViewAction(this);
        requestor.requestResource(rbi, "F:\\test\\");
	}
	
	public void closeView() {
		jfrmView.dispose();
	}

	@Override
	public void change(long value, long maxValue) {
        synchronized (jfrmView) {
            barLen += value;
        }
        Long a = (long)barLen;
        Long b = (long)maxValue;
		int len = (int)((a.doubleValue()/b.doubleValue())*100);
		String strPer = "已完成[" + len + "]%";
		jpgb.setValue(len);
		jpgb.setString(strPer);
		

	}

    @Override
    public void linkedOnetoView(Socket sender) {
        System.out.println(sender.getInetAddress().getHostAddress() + "已连接");
    }

    @Override
    public void hasNoSender() {
        ViewTool.showMessage(jfrmView, "没有发送者，请稍后再试");
    }

    @Override
    public void receiveFail(String message) {
        ViewTool.showMessage(jfrmView, message);
    }

    @Override
    public void receiveSuccess(String name) {
        ViewTool.showMessage(jfrmView, name + "接收全部完成");
    }
}
