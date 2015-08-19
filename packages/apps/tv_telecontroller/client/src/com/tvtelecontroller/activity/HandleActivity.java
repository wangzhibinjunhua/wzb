package com.tvtelecontroller.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tvtelecontroller.BaseActivity;
import com.tvtelecontroller.BaseApplication;
import com.tvtelecontroller.R;
import com.tvtelecontroller.utils.Log4L;
import com.tvtelecontroller.utils.SendDataThread;

public class HandleActivity extends BaseActivity implements OnClickListener{
	
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
	
	public final static int TV_KEYCODE_SELECT = 12;
	public final static int TV_KEYCODE_START = 13;
	public final static int TV_KEYCODE_A = 16;
	public final static int TV_KEYCODE_B = 17;
	public final static int TV_KEYCODE_X = 14;
	public final static int TV_KEYCODE_Y = 15;

	private ImageView backImage;
	private ImageView upImage;
	private ImageView belowImage;
	private ImageView leftImage;
	private ImageView rightImage;
	private ImageView okImage;
	private TextView selectTv;
	private TextView startTv;
	private ImageView xKeyImage;
	private ImageView yKeyImage;
	private ImageView aKeyImage;
	private ImageView bKeyImage;

	private float abs[] = new float[7];
	public static String sendString;
	public static String ipaddr = "";
	private SendDataThread thread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		thread = BaseApplication.getInstance().getThread();
		setContentView(R.layout.handle_mode_layout);
		initView();
	}

	private void findViewById() {
		backImage = (ImageView)findViewById(R.id.handle_back_image);
		upImage = (ImageView)findViewById(R.id.up_handle);
		belowImage = (ImageView)findViewById(R.id.below_handle);
		leftImage = (ImageView)findViewById(R.id.left_handle);
		rightImage = (ImageView)findViewById(R.id.right_handle);
		okImage = (ImageView)findViewById(R.id.handle_ok_key);
		selectTv = (TextView)findViewById(R.id.select_key);
		startTv = (TextView)findViewById(R.id.start_key);
		xKeyImage = (ImageView)findViewById(R.id.x_handle);
		yKeyImage = (ImageView)findViewById(R.id.y_handle);
		aKeyImage = (ImageView)findViewById(R.id.a_handle);
		bKeyImage = (ImageView)findViewById(R.id.b_handle);
	}

	private void setListener() {
		backImage.setOnClickListener(this);
		upImage.setOnClickListener(this);
		belowImage.setOnClickListener(this);
		leftImage.setOnClickListener(this);
		rightImage.setOnClickListener(this);
		okImage.setOnClickListener(this);
		selectTv.setOnClickListener(this);
		startTv.setOnClickListener(this);
		xKeyImage.setOnClickListener(this);
		yKeyImage.setOnClickListener(this);
		aKeyImage.setOnClickListener(this);
		bKeyImage.setOnClickListener(this);
	}

	private void initView() {
		findViewById();
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
		int value = -1;
		int resId = v.getId();
		switch (resId) {
		case R.id.handle_back_image:
			finish();
			break;
		case R.id.up_handle:
			value = TV_KEYCODE_DPAD_UP;
			break;
		case R.id.below_handle:
			value = TV_KEYCODE_DPAD_DOWN;
			break;
		case R.id.left_handle:
			value = TV_KEYCODE_DPAD_LEFT;
			break;
		case R.id.right_handle:
			value = TV_KEYCODE_DPAD_RIGHT;
			break;
		case R.id.handle_ok_key:
			value = TV_KEYCODE_ENTER;
			break;
		case R.id.select_key:
			value = TV_KEYCODE_SELECT;
			break;
		case R.id.start_key:
			value = TV_KEYCODE_START;
			break;
		case R.id.x_handle:
			value = TV_KEYCODE_X;
			break;
		case R.id.y_handle:
			value = TV_KEYCODE_Y;
			break;
		case R.id.a_handle:
			value = TV_KEYCODE_A;
			break;
		case R.id.b_handle:
			value = TV_KEYCODE_B;
			break;
		}
		sendKeyData(value);
		Log4L.print("客户端时间:", System.currentTimeMillis());
	}
	
}
