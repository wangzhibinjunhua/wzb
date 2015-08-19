package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import anti.drop.device.BaseActivity;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.adapter.HomeListAdapter;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.DBHelper;
import anti.drop.device.utils.SharedPreferencesUtils;
import anti.drop.device.utils.SlideDeleteListView;
import anti.drop.device.utils.SlideDeleteListView.DelButtonClickListener;

@SuppressLint("ShowToast")
public class HomeActivity extends BaseActivity implements DelButtonClickListener{

	private static final int REQUEST_ENABLE_BT = 2;

	private ImageView backView;
	private TextView titleView;
	private ImageView searchRadio;// 搜索按钮
	private SlideDeleteListView deviceList;// 搜索到的设备

	private HomeListAdapter mAdapter;
	private List<DeviceBean> mDBData  = new ArrayList<DeviceBean>();
	private DBHelper mDBHelper;
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothManager bluetoothManager;
	private BluetoothLeClass mBLE;
	private BaseApplication mApp;
	private static HomeActivity instance;
	
	private long firstTime = 0;
	private Intent mIntent = null;
	private boolean flag = false;
	private int mRssi = 0;
	private Thread mRssiThread;
	static String state_addr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		initView();
		initData();
	}
	
	private void findViewById() {

		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		searchRadio = (ImageView) findViewById(R.id.home_page_search_btn);
		deviceList = (SlideDeleteListView) findViewById(R.id.home_page_device_list);
	}
	
	private void initView() {
		instance = this;
		findViewById();
		backView.setVisibility(View.GONE);
		titleView.setText("防丢小助手");
		mDBHelper = DBHelper.getInstance(this);
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		// 一进入应用，判断该设备是否支持对蓝牙BLE的支持
		boolean isSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if (!isSupport) {
			Toast.makeText(this, "当前设备不支持最新蓝牙4.0技术", 1000);
		}
		// 蓝牙是否开启
		if (null == mBluetoothAdapter || !mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		mApp = (BaseApplication)getApplication();
		mBLE = mApp.get_ble();
		if (!mBLE.initialize()) {
			finish();
		}
		setListener();
	}
	
	@Override
	protected void onResume() {
		initData_new();
		super.onResume();
		mAdapter.notifyDataSetChanged();
	}
	
	private void initData(){
		mDBHelper.open();
		
		mDBData = mDBHelper.query();
		mAdapter = new HomeListAdapter(this, mDBData);
		deviceList.setAdapter(mAdapter);
		
		if(null!=mDBData&&mDBData.size()>0){
			for(int i=0;i<mDBData.size();i++){
				boolean isconnected = mBLE.connect(mDBData.get(i).getAddress());
				if(isconnected){
					mDBHelper.alter(mDBData.get(i), 0x00000c);
				}
				
				mDBData = mDBHelper.query();
				mAdapter.setDeveiceData(mDBData);
				mAdapter.notifyDataSetChanged();
			}
		}
		
		
		flag = true;
		mRssiThread = new Thread(rssiThread);
		mRssiThread.start();
		
	}
	
	private void initData_new(){
		mDBHelper.open();
		
		mDBData = mDBHelper.query();
		mAdapter = new HomeListAdapter(this, mDBData);
		deviceList.setAdapter(mAdapter);
		
		if(null!=mDBData&&mDBData.size()>0){
			for(int i=0;i<mDBData.size();i++){
//				boolean isconnected = mBLE.connect(mDBData.get(i).getAddress());
//				if(isconnected){
//					mDBHelper.alter(mDBData.get(i), 0x00000c);
//				}
				
				mDBData = mDBHelper.query();
				mAdapter.setDeveiceData(mDBData);
				mAdapter.notifyDataSetChanged();
			}
		}
		
		
		flag = true;
		mRssiThread = new Thread(rssiThread);
		mRssiThread.start();
		
	}
	
	private void setListener() {

		searchRadio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			    mIntent = new Intent(HomeActivity.this,SearchResultActivity.class);
				startActivity(mIntent);
			}
		});
		
		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SharedPreferencesUtils.getInstanse(HomeActivity.this)
				.setAddress(mDBData.get(arg2).getAddress());
				SharedPreferencesUtils.getInstanse(HomeActivity.this)
				.setBellName(mDBData.get(arg2).getName());
				Intent intent = new Intent(HomeActivity.this,DetailActivity.class);
				startActivity(intent);
			}
		});
		
		deviceList.setDelButtonClickListener(HomeActivity.this);
		mBLE.setOnConnectListener(mConnectListener);
		mBLE.setOnDisconnectListener(mDisconnectListener);
		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		flag = false;
		mRssiThread = null;
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		flag = false;
		mRssiThread = null;
	}

	@Override
	protected void onDestroy() {
		mDBHelper.close();
		mBLE.disconnect();
		super.onDestroy();
	}
	
	public static void mFinish(){
		instance.finish();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			long secondTime = System.currentTimeMillis();
			if (secondTime - firstTime > 2000) {// 如果两次按键时间间隔大于2秒，则不退出
				Toast.makeText(this, "再按一次退出应用", 666).show();
				firstTime = secondTime;// 更新firstTime
				return true;
			} else {//两次按键小于2秒时，退出应用
				clearData();
				System.exit(0);
				System.gc();
				return false;
			}
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	// 退出应用，数据库中所有数据的状态置为未连接
	private void clearData() {
		if (mDBData != null && mDBData.size() > 0) {
			for (int i = 0; i < mDBData.size(); i++) {
				mDBHelper.alter(mDBData.get(i), 0x0000000a);
			}
		}
	}

	@Override
	public void clickHappend(int position) {
		mDBHelper.deleteDevice(mDBData.get(position));
		//刷新列表
		mAdapter.setDeveiceData(mDBData);
		mAdapter.notifyDataSetChanged();
	}
	
	private BluetoothLeClass.OnConnectListener mConnectListener = new BluetoothLeClass.OnConnectListener() {
		
		@Override
		public void onConnect(BluetoothGatt gatt,String addr) {
				Log.d("wzb","1111111111 connect");
				state_addr=addr;
				mHandler.sendEmptyMessage(8888);
		}
	};
	
	private BluetoothLeClass.OnDisconnectListener mDisconnectListener = new BluetoothLeClass.OnDisconnectListener() {
		
		@Override
		public void onDisconnect(BluetoothGatt gatt,String addr) {
			Log.d("wzb","222222222 disconnect");
			state_addr=addr;
			mHandler.sendEmptyMessage(7777);
			
		}
	};
	
	
	
	private Thread rssiThread = new Thread(){
		public void run() {
			while(flag){
				try {
					sleep(2000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				if(mBLE.getRssiVal()){
					mRssi=BluetoothLeClass.getBLERSSI();
					if (mDBData != null && mDBData.size() > 0) {
						for (int i = 0; i < mDBData.size(); i++) {
							mDBHelper.alter2(mDBData.get(i),mRssi);
							mHandler.sendEmptyMessage(2222);
						}
					}
				}
			}
		};
	};
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch(msg.what){
			case 2222:
				mDBData = mDBHelper.query();
				mAdapter.setDeveiceData(mDBData);
				mAdapter.notifyDataSetChanged();
				break;
			case 7777:
				if(null!=mDBData&&mDBData.size()>0){
					for(int i=0;i<mDBData.size();i++){
						if(mDBData.get(i).getAddress().equals(state_addr)){
							mDBHelper.alter(mDBData.get(i), 0x00000a);
						mDBData = mDBHelper.query();
						mAdapter.setDeveiceData(mDBData);
						mAdapter.notifyDataSetChanged();
						}
					}
				}
				break;
			case 8888:
				if(null!=mDBData&&mDBData.size()>0){
					for(int i=0;i<mDBData.size();i++){
						if(mDBData.get(i).getAddress().equals(state_addr)){
							mDBHelper.alter(mDBData.get(i), 0x00000c);
						mDBData = mDBHelper.query();
						mAdapter.setDeveiceData(mDBData);
						mAdapter.notifyDataSetChanged();
						}
					}
				}
				break;
				
			}
		};
	};

}
