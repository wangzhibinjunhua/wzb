package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

	private final int CONNECTED_LISTENER = 0x000010;
	private final int DISCONNECTED_LISTENER = 0x000011;
	private final int CONNECTED_ALL = 0x000012;
	private final int REFRESH = 0x000013;
	private final int REQUEST_ENABLE_BT = 2;

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
	static String state_addr;
	private int connecNum = 0;//最大连接数不能超过4个
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg){
			switch(msg.what){
			case CONNECTED_LISTENER:
				if(null!=mDBData&&mDBData.size()>0){
					for(int i=0;i<mDBData.size();i++){
						if(mDBData.get(i).getAddress().equals(state_addr)){
							mDBHelper.alter(mDBData.get(i), BluetoothDevice.BOND_BONDED);
						}
					}
					mDBData = mDBHelper.query();
					mAdapter.setDeveiceData(mDBData);
					mAdapter.notifyDataSetChanged();
				}
				break;
			case DISCONNECTED_LISTENER:
				if(null!=mDBData&&mDBData.size()>0){
					for(int i=0;i<mDBData.size();i++){
						if(mDBData.get(i).getAddress().equals(state_addr)){
							mDBHelper.alter(mDBData.get(i), 0x00000a);
						}
					}
					mDBData = mDBHelper.query();
					mAdapter.setDeveiceData(mDBData);
					mAdapter.notifyDataSetChanged();
				}
				break;
			case CONNECTED_ALL:
				//进入应用，连接数据库中所有设备,当连接数量大于4时，停止连接.
				if(mDBData!=null&&mDBData.size()>0){
					for(int i=0;i<mDBData.size();i++){
						if(connecNum<4){
							boolean issuccess = mBLE.connect(mDBData.get(i).getAddress());
							if(issuccess){
								connecNum++;
								mDBHelper.alter(mDBData.get(i), BluetoothDevice.BOND_BONDED);
							}
						}
					}
					mDBData = mDBHelper.query();
					mAdapter.setDeveiceData(mDBData);	
					mAdapter.notifyDataSetChanged();
				}
				break;
			case REFRESH:
				mDBData = mDBHelper.query();
				mAdapter.setDeveiceData(mDBData);
				mAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home_page);
		initView();
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
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
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
		mDBHelper = DBHelper.getInstance(this);
		mDBHelper.open();
		mDBData = mDBHelper.query();
		mAdapter = new HomeListAdapter(this, mDBData);
		deviceList.setAdapter(mAdapter);
		mHandler.sendEmptyMessage(CONNECTED_ALL);
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		mHandler.sendEmptyMessage(REFRESH);
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
				SharedPreferencesUtils.getInstanse(HomeActivity.this).setAddress(mDBData.get(arg2).getAddress());
				SharedPreferencesUtils.getInstanse(HomeActivity.this).setDeviceName(mDBData.get(arg2).getName());
				SharedPreferencesUtils.getInstanse(HomeActivity.this).setMusicName(mDBData.get(arg2).getBell());
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
	}
	
	@Override
	protected void onStop() {
		super.onStop();
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
				mDBHelper.alter(mDBData.get(i), BluetoothDevice.BOND_NONE);
			}
		}
	}

	@Override
	public void clickHappend(int position) {
		if(position<mDBData.size()){
			mDBHelper.deleteDevice(mDBData.get(position));
			mDBData = mDBHelper.query();
			mAdapter.setDeveiceData(mDBData);
			mAdapter.notifyDataSetChanged();
		}
	}
	
	private BluetoothLeClass.OnConnectListener mConnectListener = new BluetoothLeClass.OnConnectListener() {
		
		@Override
		public void onConnect(BluetoothGatt gatt,String addr) {
				state_addr=addr;
				mHandler.sendEmptyMessage(CONNECTED_LISTENER);
				connecNum++;
		}
	};
	
	private BluetoothLeClass.OnDisconnectListener mDisconnectListener = new BluetoothLeClass.OnDisconnectListener() {
		
		@Override
		public void onDisconnect(BluetoothGatt gatt,String addr) {
			state_addr=addr;
			mHandler.sendEmptyMessage(DISCONNECTED_LISTENER);
			connecNum--;
		}
	};

}
