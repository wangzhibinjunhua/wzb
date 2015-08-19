package com.tvtelecontroller.activity;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketException;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvtelecontroller.BaseActivity;
import com.tvtelecontroller.BaseApplication;
import com.tvtelecontroller.R;
import com.tvtelecontroller.utils.DeviceParams;
import com.tvtelecontroller.utils.Log4L;
import com.tvtelecontroller.utils.SendDataThread;

public class RemoteControlActivity extends BaseActivity implements
		OnClickListener {

	private final String TAG = RemoteControlActivity.class.getSimpleName();
	DatagramSocket mDatagramSocket;
	long system_time=0;
	timeThread tt=null;

	public final static int TV_KEYCODE_ENTER = 1;
	public final static int TV_KEYCODE_DPAD_LEFT = 2;
	public final static int TV_KEYCODE_DPAD_RIGHT = 3;
	public final static int TV_KEYCODE_DPAD_UP = 4;
	public final static int TV_KEYCODE_DPAD_DOWN = 5;
	public final static int TV_KEYCODE_HOME = 7;
	public final static int TV_KEYCODE_BACK = 6;
	public final static int TV_KEYCODE_VOLUME_DOWN = 8;
	public final static int TV_KEYCODE_VOLUME_UP = 9;
	public final static int TV_KEYCODE_MENU = 10;
	public final static int TV_KEYCODE_POWER = 11;
	public final static int TV_KEYCODE_MUTE = 18;// 静音
	public final static int TV_KEYCODE_NUM_0 = 19;
	public final static int TV_KEYCODE_NUM_1 = 20;
	public final static int TV_KEYCODE_NUM_2 = 21;
	public final static int TV_KEYCODE_NUM_3 = 22;
	public final static int TV_KEYCODE_NUM_4 = 23;
	public final static int TV_KEYCODE_NUM_5 = 24;
	public final static int TV_KEYCODE_NUM_6 = 25;
	public final static int TV_KEYCODE_NUM_7 = 26;
	public final static int TV_KEYCODE_NUM_8 = 27;
	public final static int TV_KEYCODE_NUM_9 = 28;

	private int CURR_VIEW = 0;//0、1、2
	
	private FrameLayout mContainer;
	private TextView backView;
	private TextView titleView;
	private ImageView changeImage;
	
	// 遥控器选择页面
	private View modeChangeView;
//	private RadioGroup modeGroup;
	private TextView mouseMode;
	private TextView classicMode;
	// 滑行模式页面[既鼠标]
	private View mouseModeView;
	private Button touchPanel;
	private ImageView homeImage;
	private ImageView meunImage;
	private ImageView addVolImage;
	private ImageView redVolImage;
	private ImageView muteVolImage;
	// 经典模式页面[数字加按键]
	private View keybModeView;
	private ImageView num_1;
	private ImageView num_2;
	private ImageView num_3;
	private ImageView num_4;
	private ImageView num_5;
	private ImageView num_6;
	private ImageView num_7;
	private ImageView num_8;
	private ImageView num_9;
	private ImageView num_0;
	private ImageView homeTv;
	private ImageView backImage;
	private ImageView keybOk;
	private ImageView volAdd;
	private ImageView volRed;
	private ImageView volMute;
	private ImageView upkeyb;
	private ImageView belowkeyb;
	private ImageView leftkeyb;
	private ImageView rightkeyb;

	// 鼠标操作
	private int rawx = 0;
	private int rawy = 0;
	private float abs[] = new float[7];
	private int screenX;
	private int screenY;
	// 手动ip连接
	public static String sendString;
	public static String ipaddr = "";
	private SendDataThread thread = null;
	private GestureDetector mGestureDetector = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.controller_layout);
		initView();
		//add by wzb 
		try {
			if(mDatagramSocket==null){
				mDatagramSocket = new DatagramSocket(null);
				mDatagramSocket.setReuseAddress(true);
				mDatagramSocket.bind(new InetSocketAddress(9101));
			}
			mDatagramSocket = new DatagramSocket(9101);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
		tt=new timeThread();
		timeflag=true;
		tt.start();
		//end
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		timeflag=false;
		if(tt!=null){
			tt=null;
		}
	}

	private void findViewById() {
		mContainer = (FrameLayout) findViewById(R.id.controller_layout);
		backView = (TextView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		changeImage = (ImageView)findViewById(R.id.title_right_image);
		// 选择页面
		modeChangeView = LayoutInflater.from(this).inflate(R.layout.mode_change_layout, null);
//		modeGroup = (RadioGroup)modeChangeView.findViewById(R.id.controll_mode_radiogroup);
		mouseMode = (TextView)modeChangeView.findViewById(R.id.handle_mode_radio);
		classicMode = (TextView)modeChangeView.findViewById(R.id.classic_mode_radio);
		// 滑行模式页面
		mouseModeView = LayoutInflater.from(this).inflate(R.layout.mouse_mode_layout, null);
		touchPanel = (Button)mouseModeView.findViewById(R.id.touch_panel);
		homeImage = (ImageView)mouseModeView.findViewById(R.id.mouse_home);
		meunImage = (ImageView)mouseModeView.findViewById(R.id.mouse_meun);
		addVolImage = (ImageView)mouseModeView.findViewById(R.id.mouse_vol_add);
		redVolImage = (ImageView)mouseModeView.findViewById(R.id.mouse_vol_reduction);
		muteVolImage = (ImageView)mouseModeView.findViewById(R.id.mouse_vol_mute);
		// 经典模式页面
		keybModeView = LayoutInflater.from(this).inflate(R.layout.keyboard_mode_layout, null);
		num_1 = (ImageView) keybModeView.findViewById(R.id.num_1);
		num_2 = (ImageView) keybModeView.findViewById(R.id.num_2);
		num_3 = (ImageView) keybModeView.findViewById(R.id.num_3);
		num_4 = (ImageView) keybModeView.findViewById(R.id.num_4);
		num_5 = (ImageView) keybModeView.findViewById(R.id.num_5);
		num_6 = (ImageView) keybModeView.findViewById(R.id.num_6);
		num_7 = (ImageView) keybModeView.findViewById(R.id.num_7);
		num_8 = (ImageView) keybModeView.findViewById(R.id.num_8);
		num_9 = (ImageView) keybModeView.findViewById(R.id.num_9);
		num_0 = (ImageView) keybModeView.findViewById(R.id.num_0);
		homeTv = (ImageView) keybModeView.findViewById(R.id.num_home);
		backImage = (ImageView) keybModeView.findViewById(R.id.num_meun);
		keybOk = (ImageView) keybModeView.findViewById(R.id.ok_keyb);
		volAdd = (ImageView) keybModeView.findViewById(R.id.vol_keyb_add);
		volRed = (ImageView) keybModeView.findViewById(R.id.vol_keyb_red);
		volMute = (ImageView) keybModeView.findViewById(R.id.vol_keyb_mute);
		upkeyb = (ImageView) keybModeView.findViewById(R.id.up_keyb);
		belowkeyb = (ImageView) keybModeView.findViewById(R.id.below_keyb);
		leftkeyb = (ImageView) keybModeView.findViewById(R.id.left_keyb);
		rightkeyb = (ImageView) keybModeView.findViewById(R.id.right_keyb);
	}

	private void setListener() {
		
		backView.setOnClickListener(this);
		changeImage.setOnClickListener(this);
		mouseMode.setOnClickListener(this);
		classicMode.setOnClickListener(this);
		// 滑行模式页面
		homeImage.setOnClickListener(this);
		meunImage.setOnClickListener(this);
		addVolImage.setOnClickListener(this);
		redVolImage.setOnClickListener(this);
		muteVolImage.setOnClickListener(this);
		touchPanel.setOnTouchListener(new ToutchPanelListener());
		// 经典模式页面
		backImage.setOnClickListener(this);
		num_1.setOnClickListener(this);
		num_2.setOnClickListener(this);
		num_3.setOnClickListener(this);
		num_4.setOnClickListener(this);
		num_5.setOnClickListener(this);
		num_6.setOnClickListener(this);
		num_7.setOnClickListener(this);
		num_8.setOnClickListener(this);
		num_9.setOnClickListener(this);
		num_0.setOnClickListener(this);
		homeTv.setOnClickListener(this);
		backImage.setOnClickListener(this);
		keybOk.setOnClickListener(this);
		volAdd.setOnClickListener(this);
		volRed.setOnClickListener(this);
		volMute.setOnClickListener(this);
		upkeyb.setOnClickListener(this);
		belowkeyb.setOnClickListener(this);
		leftkeyb.setOnClickListener(this);
		rightkeyb.setOnClickListener(this);
	}
	
	private void initView(){
		screenX = DeviceParams.getScreenWidth(this);
		screenY = DeviceParams.getScreenHeight(this);
		mGestureDetector = new GestureDetector(this, new GestureListener());
		findViewById();
		mContainer.removeAllViews();
		mContainer.addView(modeChangeView);
		CURR_VIEW = 0;
		titleView.setText("遥控器");
		changeImage.setVisibility(View.GONE);
		setListener();
	}

	public void sendKeyData(int value) {

		abs[0] = value;

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < abs.length; i++) {

			if (i != abs.length - 1) {
				stringBuffer.append(abs[i] + " ");
			} else {
				stringBuffer.append(abs[i]);
			}
		}
		sendString = stringBuffer.toString();
		startSendDataThread(sendString, BaseApplication.getInstance().getIp(),
				thread);

	}

	public void startSendDataThread(String str1, String str2, Thread t) {

		thread = new SendDataThread(str1, str2, t);
		thread.start();
	}

	@Override
	public void onClick(View v) {
		Intent mIntent = null;
		switch (v.getId()) {
		case R.id.handle_mode_radio:
			mContainer.removeAllViews();
			mContainer.addView(mouseModeView);
			CURR_VIEW = 1;
			titleView.setText("滑行模式");
			changeImage.setVisibility(View.VISIBLE);
			break;
		case R.id.classic_mode_radio:
			mContainer.removeAllViews();
			mContainer.addView(keybModeView);
			CURR_VIEW = 2;
			titleView.setText("经典模式");
			changeImage.setVisibility(View.VISIBLE);
			break;
		case R.id.title_back:
			if(CURR_VIEW==0){
				finish();
			}else if(CURR_VIEW==1||CURR_VIEW==2){
				mContainer.removeAllViews();
				mContainer.addView(modeChangeView);
				CURR_VIEW = 0;
				titleView.setText("遥控器");
				changeImage.setVisibility(View.GONE);
			}
			break;
		case R.id.title_right_image:
			finish();
			break;
		case R.id.mouse_home:
			sendKeyData(TV_KEYCODE_HOME);
			break;
		case R.id.mouse_meun:
			sendKeyData(TV_KEYCODE_MENU);
			break;
		case R.id.mouse_vol_add:
			sendKeyData(TV_KEYCODE_VOLUME_UP);
			break;
		case R.id.mouse_vol_reduction:
			sendKeyData(TV_KEYCODE_VOLUME_DOWN);
			break;
		case R.id.mouse_vol_mute:
			sendKeyData(TV_KEYCODE_MUTE);
			break;
		case R.id.num_1:
			sendKeyData(TV_KEYCODE_NUM_1);
			break;
		case R.id.num_2:
			sendKeyData(TV_KEYCODE_NUM_2);
			break;
		case R.id.num_3:
			sendKeyData(TV_KEYCODE_NUM_3);
			break;
		case R.id.num_4:
			sendKeyData(TV_KEYCODE_NUM_4);
			break;
		case R.id.num_5:
			sendKeyData(TV_KEYCODE_NUM_5);
			break;
		case R.id.num_6:
			sendKeyData(TV_KEYCODE_NUM_6);
			break;
		case R.id.num_7:
			sendKeyData(TV_KEYCODE_NUM_7);
			break;
		case R.id.num_8:
			sendKeyData(TV_KEYCODE_NUM_8);
			break;
		case R.id.num_9:
			sendKeyData(TV_KEYCODE_NUM_9);
			break;
		case R.id.num_0:
			sendKeyData(TV_KEYCODE_NUM_0);
			break;
		case R.id.num_home:
			sendKeyData(TV_KEYCODE_HOME);
			break;
		case R.id.num_meun:
			sendKeyData(TV_KEYCODE_BACK);
			break;
		case R.id.ok_keyb:
			sendKeyData(TV_KEYCODE_ENTER);
			break;
		case R.id.vol_keyb_add:
			sendKeyData(TV_KEYCODE_VOLUME_UP);
			break;
		case R.id.vol_keyb_red:
			sendKeyData(TV_KEYCODE_VOLUME_DOWN);
			break;
		case R.id.vol_keyb_mute:
			sendKeyData(TV_KEYCODE_MUTE);
			break;
		case R.id.up_keyb:
			sendKeyData(TV_KEYCODE_DPAD_UP);
			system_time=System.currentTimeMillis();
			break;
		case R.id.below_keyb:
			sendKeyData(TV_KEYCODE_DPAD_DOWN);
			system_time=System.currentTimeMillis();
			break;
		case R.id.left_keyb:
			sendKeyData(TV_KEYCODE_DPAD_LEFT);
			system_time=System.currentTimeMillis();
			break;
		case R.id.right_keyb:
			sendKeyData(TV_KEYCODE_DPAD_RIGHT);
			system_time=System.currentTimeMillis();
			break;
		}
	}

	public void sendMouseData(MotionEvent event, int state) {
		rawx = (int) event.getRawX();
		rawy = (int) event.getRawY();
		Log4L.d(TAG, "x=" + rawx + ";y=" + rawy);
		abs[0] = 0;
		abs[1] = rawx * 1280 / screenX;
		abs[2] = rawy * 720 / screenY;
		abs[3] = state;
		Log4L.d(TAG, "abs[0]=" + abs[0] + ";abs[1]=" + abs[1]);
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < abs.length; i++) {

			if (i != abs.length - 1) {
				stringBuffer.append(abs[i] + " ");
			} else {
				stringBuffer.append(abs[i]);
			}
		}
		sendString = stringBuffer.toString();
		Log4L.d(TAG, "*************TOUCH*******=" + sendString);
		startSendDataThread(sendString, BaseApplication.getInstance().getIp(),thread);
	}

//add by wzb 20150505
	boolean timeflag=true;
	String keyString="";
	
	//
	public class timeThread extends Thread {
		
		public void run() {
			
			byte[] buffer = new byte[1024];
			DatagramPacket dp_receive = new DatagramPacket(buffer,buffer.length);
			while (timeflag) {
				
				try {
					if (null != mDatagramSocket) {
						mDatagramSocket.receive(dp_receive);
						keyString = new String(dp_receive.getData(), 0,dp_receive.getLength());
						if (keyString.equals("111")) {
							Log4L.d(TAG, "耗费时间为："+String.valueOf((System.currentTimeMillis()-system_time)/2));
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	
//end
	class GestureListener extends SimpleOnGestureListener {

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			return super.onDoubleTap(e);
		}

		@Override
		public boolean onDown(MotionEvent e) {
			return super.onDown(e);
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			Log4L.i(TAG, "onFling:velocityX = " + velocityX + " velocityY"+ velocityY);
			return super.onFling(e1, e2, velocityX, velocityY);
		}

		@Override
		public void onLongPress(MotionEvent e) {
			super.onLongPress(e);
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log4L.i(TAG, "onScroll:distanceX = " + distanceX + " distanceY = "
					+ distanceY);
			return super.onScroll(e1, e2, distanceX, distanceY);
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			sendMouseData(e, 3);
			return super.onSingleTapUp(e);
		}

	}

	class ToutchPanelListener implements View.OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				sendMouseData(event, 0);
				break;
			case MotionEvent.ACTION_MOVE:
				sendMouseData(event, 1);
				break;
			case MotionEvent.ACTION_UP:
				sendMouseData(event, 2);
				break;
			}
			return mGestureDetector.onTouchEvent(event);
		}
	}

}
