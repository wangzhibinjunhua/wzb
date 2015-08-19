package com.tvtelecontroller.utils;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.util.Log;

public class RequestIPAddress extends Thread {

	private final String TAG = RequestIPAddress.class.getSimpleName();

	private MulticastSocket multicastSocket = null;
	private static int BROADCAST_PORT = 9898;
	private static String BROADCAST_IP = "224.0.0.1";
	private InetAddress inetAddress = null;
	private DatagramPacket dPacket = null;
	public static boolean flag = true; // 循环搜索ip标志
	private Activity mActivity;
	private DBHelper mDB;

	public static ArrayList<String> ip_key = new ArrayList<String>();
	public static ArrayList<String> ip_value = new ArrayList<String>();

	public RequestIPAddress(Activity activity) {
		try {
			multicastSocket = new MulticastSocket(BROADCAST_PORT);
			inetAddress = InetAddress.getByName(BROADCAST_IP);
			multicastSocket.joinGroup(inetAddress);
		} catch (Exception e) {
			e.printStackTrace();
			Log4L.e(TAG, e.toString());
		}
		mActivity = activity;
		mDB = new DBHelper(mActivity);
		mDB.open();
	}

	private int index = 0;
	@Override
	public void run() {
		byte buf[] = new byte[1024];
		dPacket = new DatagramPacket(buf, buf.length);
		while (flag) {
			try {
				multicastSocket.receive(dPacket);
				String aStr = new String(buf, 0, dPacket.getLength());
				Log.d("wzb0414","astr="+aStr);
				if (aStr != null) {
					String[] strip = aStr.split(":");
					if (strip.length == 2) {
						String ipa = strip[0];
						if (!ip_key.contains(ipa)) {
							ip_key.add(strip[0]);
							ip_value.add(strip[1]);
							//搜索成功，先删除之前存储的数据，再将当前搜索到的数据保存
							mDB.deleteAll();
							DeviceBean device = new DeviceBean();
							device._id = index;
							device.name = strip[0];
							device.connIp = strip[1];
							device.isConn = "1";
							mDB.insertDevice(device);
							index++;
							strip = null;
						}
						Log.d("wzb0414","ip_key.size="+ip_key.size());
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				Log4L.e(TAG, e.toString());
			}
		}
	}
	
	public void refresh(DeviceAdapter adapter,List<DeviceBean> list){
		
		adapter.setmList(list);
		adapter.notifyDataSetChanged();
		
	}
	
	public void setFlag(boolean mFlag){
		flag = mFlag;
	}

}
