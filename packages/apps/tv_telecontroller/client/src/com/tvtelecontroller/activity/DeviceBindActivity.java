package com.tvtelecontroller.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tvtelecontroller.BaseActivity;
import com.tvtelecontroller.R;
import com.tvtelecontroller.utils.DeviceBean;
import com.tvtelecontroller.utils.RequestIPAddress;
import com.tvtelecontroller.utils.VDialog;

public class DeviceBindActivity extends BaseActivity implements OnClickListener{
	
	private final String TAG = DeviceBindActivity.class.getSimpleName();
	
	private final int SCAN_FINISH = 0;
	
	private TextView backView;
	private TextView titleView;
	private LinearLayout detectionLayout;
	private LinearLayout bindLayout;
	private LinearLayout dFailureLayout;
	private TextView deviceName;
	private TextView deviceIp;
	private TextView confirRadio;
	private TextView cancelRadio;
	private TextView againBind;
	private TextView connectWifi;
	
	private RequestIPAddress mScanReuqest;
	private VDialog progressDialog;
	private Handler mHandler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_bind);
		initView();
	}
	
	private void findViewById(){
		backView = (TextView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		
		detectionLayout = (LinearLayout)findViewById(R.id.detection_layout);
		bindLayout = (LinearLayout)findViewById(R.id.bind_layout);
		dFailureLayout = (LinearLayout)findViewById(R.id.detection_failure_layout);
		
		deviceName = (TextView)findViewById(R.id.bind_device_name);
		deviceIp = (TextView)findViewById(R.id.bind_device_ip);
		confirRadio = (TextView)findViewById(R.id.confir_bind_device);
		cancelRadio = (TextView)findViewById(R.id.cancel_bind_device);
		againBind = (TextView)findViewById(R.id.again_detection_device);
		connectWifi = (TextView)findViewById(R.id.connection_wifi);
	}
	
	private void setListener(){
		backView.setOnClickListener(this);
		confirRadio.setOnClickListener(this);
		cancelRadio.setOnClickListener(this);
		againBind.setOnClickListener(this);
		connectWifi.setOnClickListener(this);
	}
	
	private void initView(){
		findViewById();
		
		titleView.setText("绑定设备");
		progressDialog = new VDialog(this);
		detectionLayout.setVisibility(View.VISIBLE);
		bindLayout.setVisibility(View.GONE);
		dFailureLayout.setVisibility(View.GONE);
		search();
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SCAN_FINISH:
					scanDevice();
					break;
				}
				super.handleMessage(msg);
			}
		};
		
		setListener();
	}
	
	private void search() {
		RequestIPAddress.ip_key.clear();
		RequestIPAddress.ip_value.clear();
		startScanThread();
		showSearchDialog();
	}
	
	private void showSearchDialog() {

		progressDialog.showDialog("","设备检测中...");
		Thread thread = new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				progressDialog.dismisssDialog();
				mScanReuqest.setFlag(false);
				if (mScanReuqest != null) {
					mScanReuqest = null;

				}
				mHandler.sendEmptyMessage(SCAN_FINISH);
			}
		};
		thread.start();
	}
	
	private int lenght = 0;
	public  List<DeviceBean> mListData = new ArrayList<DeviceBean>();
	private void scanDevice() {
		Log.d(TAG, "RequestIPAddress.ip_key=" + RequestIPAddress.ip_key);
		if (RequestIPAddress.ip_key.size() > 0) {
			lenght = RequestIPAddress.ip_key.size();
			for (int i = 0; i < lenght; i++) {
				mListData.add(new DeviceBean(RequestIPAddress.ip_key.get(i),RequestIPAddress.ip_value.get(i), "1"));
			}
			if(null!=mListData&&mListData.size()>0){
				detectionLayout.setVisibility(View.GONE);
				bindLayout.setVisibility(View.VISIBLE);
				dFailureLayout.setVisibility(View.GONE);
				
				deviceName.setText(mListData.get(0).getName());
				deviceIp.setText(mListData.get(0).getConnIp());
			}
		} else {
			detectionLayout.setVisibility(View.GONE);
			bindLayout.setVisibility(View.GONE);
			dFailureLayout.setVisibility(View.VISIBLE);
		}
	}
	
	/**
	 * 启动扫描线程
	 */
	private void startScanThread() {
		mScanReuqest = new RequestIPAddress(this);
		mScanReuqest.start();
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		switch(resId){
		case R.id.title_back:
			finish();
			break;
		case R.id.confir_bind_device:
			break;
		case R.id.cancel_bind_device:
			
			break;
		case R.id.again_detection_device:
			search();
			break;
		case R.id.connection_wifi:
			Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");   
			   startActivity(wifiSettingsIntent);
			break;
		}
	}

}
