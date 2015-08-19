package anti.drop.device.utils;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Handler;
import anti.drop.device.pojo.DeviceBean;

public class BluetoothScann {

	public static List<DeviceBean> bluetoothInfo = new ArrayList<DeviceBean>();//改列表用来保存本次进入应用的结果
	private DBHelper mDBHelper;
	private static BluetoothScann instance;
	private Context mContext;
	String bluetoothAddress = "";//蓝牙地址
	private Handler mHandler = null;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothManager bluetoothManager;
	
	public BluetoothScann(Context context){
		mContext = context;
		mDBHelper = DBHelper.getInstance(mContext);
		mHandler = new Handler();
		bluetoothManager = (BluetoothManager)mContext.getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
	}
	
	public static BluetoothScann getInstance(Context context){
		if(instance==null){
			instance = new BluetoothScann(context);
		}
		return instance;
	}
	
	//启动扫描
	public void startScan(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mBluetoothAdapter.startLeScan(mLeScanCallback);
			}
		});
	}
	
	//停止扫描
	public void stopScan(){
		mHandler.post(new Runnable(){
			@Override
			public void run() {
				mBluetoothAdapter.stopLeScan(mLeScanCallback);
			}
		});
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		public void onLeScan(android.bluetooth.BluetoothDevice device, int rssi, byte[] scanRecord) {
			
			bluetoothAddress = device.getAddress();
			if(null!=bluetoothInfo&&bluetoothInfo.size()>0){
				for(int i=0;i<bluetoothInfo.size();i++){
					//去重处理
					if(!bluetoothAddress.equals(bluetoothInfo.get(i).getAddress())){
						//添加到列表
						bluetoothInfo.add(new DeviceBean(
								device.getAddress(),
								device.getName(),
								device.getBondState(),
								0));
						//插入到数据库
						mDBHelper.insertDevice(new DeviceBean(
								device.getAddress(),
								device.getName(),
								device.getBondState()
								,0));
					}
				}
			}
		};
	};

}
