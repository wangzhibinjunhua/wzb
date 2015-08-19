package com.tvtelecontroller.utils;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;



/**
 *	AUTHOR(wzb<wangzhibin_x@foxmail.com>)
 *	2015年4月13日下午2:32:58
 *  
 */
public class SendDataThread extends Thread{
	
	private final String TAG = SendDataThread.class.getSimpleName();

	
	private String ipaddr = "";
	private String sendData="";
	static final int SERVER_PORT = 9101;
	private Thread thread;
	public SendDataThread(String str1,String str2,Thread t){
		
		this.sendData=str1;
		this.ipaddr=str2;
		this.thread=t;
	}
	
	/**
	 * 按键触发事件通过UDP方式发送到服务器端
	 * @author LuoYong
	 */
		@Override
		public void run() {
			DatagramSocket socket = null;
			try {
				if(socket==null){
					socket = new DatagramSocket(null);
					socket.setReuseAddress(true);
					socket.bind(new InetSocketAddress(SERVER_PORT));
				}
				InetAddress serverAddress = InetAddress.getByName(ipaddr);
				byte data[] = sendData.getBytes();
				DatagramPacket packet = new DatagramPacket(data, data.length,serverAddress, SERVER_PORT);
				socket.send(packet);
				socket.close();
			} catch (Exception e) {
				e.printStackTrace();
				Log4L.e(TAG, e.toString());
			} finally{
				if(thread!=null){
					thread = null;
//					socket.disconnect();
//					socket.close();
				}
			}
		}

}
