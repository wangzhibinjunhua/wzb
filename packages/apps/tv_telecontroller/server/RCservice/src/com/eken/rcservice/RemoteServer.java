package com.eken.rcservice;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


import android.app.AlertDialog;

import android.app.Instrumentation;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.os.Looper;
import android.view.MotionEvent;
import android.os.SystemClock;




/**
 * @author wzb <wangzhibin_x@foxmail.com>
 * @description
 * @version 1.0 2014-5-21 上午9:06:49
 */
public class RemoteServer extends Thread {

	private static final String TAG = "RemoteServer";
	private static final int BROADCAST_SERVER_PORT = 9101;
	DatagramSocket mSocket;
	DatagramPacket reviever;
	private MyLib mylib;
	String recDate;
	private Context mContext;

	int key_data;
	int mouse_data[] = new int[3];
	float gsensor_data[] = new float[3];
	int index;
	AudioManager mAudioManager;


	public RemoteServer(Context context) {
		mContext = context;
		try {
			mSocket = new DatagramSocket(BROADCAST_SERVER_PORT);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "create socket failed !!!");
			e.printStackTrace();
		}

	}

	public void run() {
		Log.d(TAG, "====remote server thread start !!!");
		byte[] buffer = new byte[1024];
		reviever = new DatagramPacket(buffer, buffer.length);
		while (true) {
			
			try {
				mSocket.receive(reviever);
				handle_event(reviever);
				reviever.setLength(1024);	
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static long time_last=0;

	private void sendKeyCode(final int keyCode) {
		//防止多次点击
		if(System.currentTimeMillis()<(time_last+200)){
				
				return;
		}else{
			time_last=System.currentTimeMillis();		
		}
		
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(keyCode);
				} catch (Exception e) {
					Log.e("Exception when sendPointerSync", e.toString());
				}
			}
		}.start();
	}

	

	
	void send_to_user(){
		String send_str="000";
		try{
			DatagramPacket dp=new DatagramPacket(send_str.getBytes(),send_str.length(),reviever.getAddress(),
				BROADCAST_SERVER_PORT);
			mSocket.send(dp);
		}catch (Exception e) {
				e.printStackTrace();
		}	
	}

	// packet data 统一用float a[7] 格式来发送
	// a[0]=keycode;a[1]=mouseX;a[2]=mouseY;a[3]=mouseFlag;a[4]-a[6]为gsensor数据

	private void handle_event(DatagramPacket packet) {

		String str = new String(packet.getData(), 0, packet.getLength());
		Log.d(TAG, "get packet data " + str);
	
		String tokens[] = str.trim().split("\\s+");
		
		key_data = (int) Float.parseFloat(tokens[0]);
		if (key_data == 0) {
			mouse_data[0] = (int) Float.parseFloat(tokens[1]);
			mouse_data[1] = (int) Float.parseFloat(tokens[2]);
			mouse_data[2] = (int) Float.parseFloat(tokens[3]);

			mylib = new MyLib();
			mylib.writeevent(mouse_data);

	
			

		} else {
			switch (key_data) {
			case 1:
			
				sendKeyCode(KeyEvent.KEYCODE_ENTER);
				
				break;
			case 2:
				
				sendKeyCode(KeyEvent.KEYCODE_DPAD_LEFT);
				break;
			case 3:
				
				sendKeyCode(KeyEvent.KEYCODE_DPAD_RIGHT);
				break;
			case 4:
				
				sendKeyCode(KeyEvent.KEYCODE_DPAD_UP);
				break;
			case 5:
				
				sendKeyCode(KeyEvent.KEYCODE_DPAD_DOWN);
				break;
			case 6:

				
				sendKeyCode(KeyEvent.KEYCODE_BACK);
				break;
			case 7:
				
				sendKeyCode(KeyEvent.KEYCODE_HOME);
				break;
			case 8:
				
				sendKeyCode(KeyEvent.KEYCODE_VOLUME_DOWN);
				break;
			case 9:
				
				sendKeyCode(KeyEvent.KEYCODE_VOLUME_UP);
				break;
			case 10:
				
				sendKeyCode(KeyEvent.KEYCODE_MENU);
				break;
			case 11:
				
				sendKeyCode(KeyEvent.KEYCODE_POWER);
				break;
			case 12:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_SELECT);
				break;
			case 13:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_START);
				break;
			case 14:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_X);
				break;
			case 15:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_Y);
				break;
			case 16:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_A);
				break;
			case 17:
				sendKeyCode(KeyEvent.KEYCODE_BUTTON_B);
				break;
			case 18:
				sendKeyCode(KeyEvent.KEYCODE_VOLUME_MUTE);
				break;
			case 19:
				sendKeyCode(KeyEvent.KEYCODE_0);
				break;
			case 20:
				sendKeyCode(KeyEvent.KEYCODE_1);
				break;
			case 21:
				sendKeyCode(KeyEvent.KEYCODE_2);
				break;
			case 22:
				sendKeyCode(KeyEvent.KEYCODE_3);
				break;
			case 23:
				sendKeyCode(KeyEvent.KEYCODE_4);
				break;
			case 24:
				sendKeyCode(KeyEvent.KEYCODE_5);
				break;
			case 25:
				sendKeyCode(KeyEvent.KEYCODE_6);
				break;
			case 26:
				sendKeyCode(KeyEvent.KEYCODE_7);
				break;
			case 27:
				sendKeyCode(KeyEvent.KEYCODE_8);
				break;
			case 28:
				sendKeyCode(KeyEvent.KEYCODE_9);
				break;
			case 99:
			
				send_to_user();
				break;
		
			default:
				break;

			}

		}
	}

}
