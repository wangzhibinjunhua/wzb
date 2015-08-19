package com.eken.rcservice;

import java.net.DatagramPacket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.IBinder;
import android.util.Log;
import android.view.WindowManager;

/**
 * @description  
 * @version 1.0
 * 2014-5-21 ÉÏÎç9:00:51
 */
public class CoreService extends Service{
	
	private static final String TAG="CoreService";
	private RemoteServer mRemoteServer;

	//udp broadcast
	//static String local_ip="192.168.10.111";
	String local_ip = "110.110.110.110";
	static int BROADCAST_PORT=9898;
	static String BROADCAST_IP="224.0.0.1";
	InetAddress inetAddress=null;
	MulticastSocket multicastSocket=null;
	UdpThread mUdpThread;

	int index;
	AudioManager mAudioManager;;
	private Context mContext;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void onCreate(){
		mContext = this;
		mRemoteServer = new RemoteServer(this);
		udpSendBroadcast();
		mUdpThread=new UdpThread();
		Log.d(TAG,"======CoreService onCreate=========");
		
	}
	
	public int onStartCommand(Intent intent,int flags,int startId){
		
		mRemoteServer.start();
		mUdpThread.start();

		return Service.START_STICKY;
	}

	void udpSendBroadcast(){
		try{
			inetAddress=InetAddress.getByName(BROADCAST_IP);
			multicastSocket=new MulticastSocket(BROADCAST_PORT);
			multicastSocket.setTimeToLive(1);
			multicastSocket.joinGroup(inetAddress);
		}catch(Exception e){
			Log.d("wzb","udp_send_broadcast err");
		}

	}
	
	public class UdpThread extends Thread{
		public void run(){
			DatagramPacket dataPacket=null;
			while(true){
				try{
					local_ip = getLocalIpAddress();
					byte[] data=local_ip.getBytes();
					dataPacket=new DatagramPacket(data,data.length,inetAddress,BROADCAST_PORT);
					Log.d("wzb","multicastsocket send===1111111=========" + local_ip);
					multicastSocket.send(dataPacket);
					android.os.SystemClock.sleep(1000);		
				
				}catch(Exception e){
					Log.d("wzb","mulsocket send err");
				}
			}

		}
		
	}
	
	public String getLocalIpAddress() 
	{
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ){
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ){
					InetAddress inetAddress = enumIpAddr.nextElement();
					
					if(!inetAddress.isLoopbackAddress() &&(inetAddress instanceof Inet4Address)){
						Log.d("wzb","33333333333333333333333=" + inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString()+":"+inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException e) {
			// TODO: handle exception
			Log.e("LQ", e.toString());
		}
		
		return null;
	}


}
