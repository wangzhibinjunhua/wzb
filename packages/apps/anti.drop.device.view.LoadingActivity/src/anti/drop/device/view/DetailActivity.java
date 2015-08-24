package anti.drop.device.view;

import java.util.List;
import java.util.UUID;

import android.R.integer;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.BluetoothLeClass.OnDataAvailableListener;
import anti.drop.device.utils.BluetoothLeClass.OnServiceDiscoverListener;
import anti.drop.device.utils.CommunicationUtil;
import anti.drop.device.utils.LocationUtil;
import anti.drop.device.utils.SharedPreferencesUtils;

public class DetailActivity extends BaseActivity implements OnClickListener {

	
	private final int REFRESH_BATTERY = 0x00000F;
	
	private ImageView backView;
	private TextView titleView;
	private ImageView bellImage;
	private TextView deviceName;
	private SeekBar distanceSeek;
	private ImageView betteryElectImg;
	private RelativeLayout takeLayout;
	private RelativeLayout tapeLayout;
	private RelativeLayout mapLayout;
	private RelativeLayout setLayout;
	private Intent mIntent = null;
	private String name = "";
	private String address = "";
	private BluetoothLeClass mBLE;
	BaseApplication app;
	private int batteryElectricity = 0;//电池电量
	private boolean flag = false;
	private static DetailActivity instance;
	private BluetoothGattCharacteristic mBluetoothGattCharacteristic;
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothManager bluetoothManager;
	public static int mRssi = 0;
	private float mDistance = 0;
	private int bellDistance = 2;
	private boolean callBell;
    Thread mRssiThread;
	private boolean isFirst = true;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case REFRESH_BATTERY:
				boolean isDigit = isDigits(CommunicationUtil.bytesToHexString(mBluetoothGattCharacteristic.getValue()));
				if(isDigit){
					batteryElectricity = Integer.parseInt(CommunicationUtil.bytesToHexString(mBluetoothGattCharacteristic.getValue()));
					if(batteryElectricity>=0&&batteryElectricity<20){
						betteryElectImg.setImageResource(R.drawable.battery_icon_0);
					}else if(batteryElectricity>=20&&batteryElectricity<40){
						betteryElectImg.setImageResource(R.drawable.battery_icon_1);
					}else if(batteryElectricity>=40&&batteryElectricity<60){
						betteryElectImg.setImageResource(R.drawable.battery_icon_2);
					}else if(batteryElectricity>=60&&batteryElectricity<80){
						betteryElectImg.setImageResource(R.drawable.battery_icon_3);
					}else if(batteryElectricity>=80&&batteryElectricity<100){
						betteryElectImg.setImageResource(R.drawable.battery_icon_4);
					}
				}
				break;
			case 0000:
				mDistance = calculateDistance(mRssi);
				bellDistance = SharedPreferencesUtils.getInstanse(DetailActivity.this).getBellDistance();//获取默认的报警距离
				if(callBell){
					if(mDistance>bellDistance ||mDistance==bellDistance){
						LocationUtil locationU = new LocationUtil(DetailActivity.this);
						SharedPreferencesUtils.getInstanse(DetailActivity.this)
						.setDeviceLongitude(String.valueOf(locationU.getmLongitude()));
						SharedPreferencesUtils.getInstanse(DetailActivity.this)
						.setDeviceLatitude(String.valueOf(locationU.getmLatitude()));
						ring();
						sendData("a3");
					}
				}
				break;
			}
		};
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
				}
				Log.d("wzb","mRssi="+mRssi);
				mHandler.sendEmptyMessage(0000);
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.device_detail);
		instance = this;
		app=(BaseApplication)getApplication();
		mBLE=app.get_ble();
		if (!mBLE.initialize()) {
			finish();
		}
		//add by wzb
		Intent data=getIntent();
		int device_id=data.getExtras().getInt("deviceid");
		Log.d("www","deviceid="+device_id+"gatt="+app.get_gatt(device_id));
		mBLE.setBluetoothGatt(app.get_gatt(device_id));
		readData();
		initView();
		// 发现BLE终端的Service时回调
		mBLE.setOnServiceDiscoverListener(mOnServiceDiscover);
		// 收到BLE终端数据交互的事件
		mBLE.setOnDataAvailableListener(mOnDataAvailable);
	}

	@Override
	protected void onResume() {
		super.onResume();
		name = SharedPreferencesUtils.getInstanse(this).getDeviceName();
		deviceName.setText(name);
		mRssiThread=new Thread(rssiThread);
		flag=true;
		mRssiThread.start();
	}
	
	@Override
	protected void onStop() {
		flag = false;
		super.onStop();
		mRssiThread=null;
	}
	
	@Override
	protected void onPause() {
		flag = false;
		super.onPause();
		mRssiThread=null;
	}
	
	public static void mFinish(){
		instance.finish();
	}
	
	private float calculateDistance(int rssi){
		float distance = 0;
		if(rssi>-72){
			distance = 0.5f;
		}else if(rssi<=-72&&rssi>-80){
			distance = 1.0f;
		}else if(rssi<=-80&&rssi>-85){
			distance = 3.0f;
		}else if(rssi<=-85&&rssi>-87){
			distance = 5.0f;
		}else if(rssi<=-87&&rssi>-90){
			distance = 7.0f;
		}else if(rssi<=-90&&rssi>-93){
			distance = 10.0f;
		}else if(rssi<=-93&&rssi>-95){
			distance = 12.0f;
		}else if(rssi<=-95&&rssi>-100){
			distance = 20.0f;
		}
		return distance;
	}
	
	private boolean isDigits(String str){
		for (int i = str.length();--i>=0;){
			if (!Character.isDigit(str.charAt(i))){
				return false;
			}
		}
		return true;
	}
	
	private void findViewById() {

		backView = (ImageView) findViewById(R.id.title_back);
		titleView = (TextView) findViewById(R.id.title_text);
		deviceName = (TextView) findViewById(R.id.detail_name);
		distanceSeek = (SeekBar) findViewById(R.id.detail_seekbar);
		betteryElectImg = (ImageView) findViewById(R.id.battery_electricity);
		takeLayout = (RelativeLayout) findViewById(R.id.take_layout);
		tapeLayout = (RelativeLayout) findViewById(R.id.tape_layout);
		mapLayout = (RelativeLayout) findViewById(R.id.map_layout);
		setLayout = (RelativeLayout) findViewById(R.id.set_layout);
		bellImage = (ImageView) findViewById(R.id.detail_bell);
		
		distanceSeek.setProgress(SharedPreferencesUtils.getInstanse(DetailActivity.this).getBellDistance());
	}

	private void setListener() {
		backView.setOnClickListener(this);
		takeLayout.setOnClickListener(this);
		tapeLayout.setOnClickListener(this);
		mapLayout.setOnClickListener(this);
		setLayout.setOnClickListener(this);
		bellImage.setOnClickListener(this);
		
		
		
		distanceSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				//停止滑动时，记录下报警距离
				SharedPreferencesUtils.getInstanse(DetailActivity.this)
				.setBellDistance(seekBar.getProgress()+2);
			}
			
		});
		
	}

	private void initView() {
		Log.d("wzb","initview");
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		findViewById();
		titleView.setText("详情");
		mIntent = getIntent();
		address = SharedPreferencesUtils.getInstanse(this).getAddress();
		//mBLE.connect(address);
		flag = true;
		callBell = SharedPreferencesUtils.getInstanse(this).getIsCloseCallBell();
		setListener();
		createDialog();
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		switch (resId) {
		case R.id.title_back:
			finish();
			break;
		case R.id.detail_bell:
			sendData("A3");
			break;
		case R.id.take_layout:
			//接入拍照页面
			start_camera();
			break;
		case R.id.tape_layout:
			//进入录音界面
			mIntent = new Intent(this, TapeActivity.class);
			startActivity(mIntent);
			break;
		case R.id.map_layout:
			mIntent = new Intent(this, MapLocationActivity.class);
			startActivity(mIntent);
			break;
		case R.id.set_layout:
			mIntent = new Intent(this,SettingActivity.class);
			startActivity(mIntent);
			break;
		}
	}

	void start_camera() {
		Intent intent = new Intent(this, CameraActivity.class);
		startActivity(intent);
	}

	private void readData() {
		//boolean ret = mBLE.setCharacteristicNotification(
			//	mBLE.getBluetoothGattCharacteristic(0), true);
		//add by wzb 20150823
		BluetoothGattService readService=mBLE.getBluetoothGatt().getService(UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"));
		if(readService!=null){
			BluetoothGattCharacteristic read_bgc=readService.getCharacteristic(UUID.fromString("0000fff1-0000-1000-8000-00805f9b34fb"));
			boolean ret= mBLE.setCharacteristicNotification(read_bgc,true);
			Log.d("www","readdata="+ret);
		}
		
	
	}

	private void sendData(String data) {
//		 if (mBLE.getBluetoothGattCharacteristic(1) != null) {
//		 int data_16 = Integer.parseInt(data, 16);
//		 mBLE.getBluetoothGattCharacteristic(1).setValue(
//		 new byte[] { (byte) data_16 });
//		 mBLE.writeCharacteristic(mBLE.getBluetoothGattCharacteristic(1));
//		 }
		
		
		if (mBLE.getSupportedGattServices() != null) {
			BluetoothGattService sendService = mBLE
					.getBluetoothGatt()
					.getService(
							UUID.fromString("0000fff0-0000-1000-8000-00805f9b34fb"));
			int data_16 = Integer.parseInt(data, 16);
			if (sendService != null) {
				BluetoothGattCharacteristic bgc = sendService
						.getCharacteristic(UUID
								.fromString("0000fff2-0000-1000-8000-00805f9b34fb"));
				bgc.setValue(new byte[] { (byte) data_16 });
				mBLE.getBluetoothGatt().writeCharacteristic(bgc);
				Log.d("wzb", "sendService=" + sendService + "bgc=" + bgc);
			}
		}
	}

	private void displayGattServices(List<BluetoothGattService> gattServices) {
		if (gattServices == null)
			return;

		for (BluetoothGattService gattService : gattServices) {
			// -----Service的字段信息-----//
			int type = gattService.getType();
			Log.e("wzb",
					"-->service type:" + CommunicationUtil.getServiceType(type));
			Log.e("wzb", "-->includedServices size:"
					+ gattService.getIncludedServices().size());
			Log.e("wzb", "-->service uuid:" + gattService.getUuid());

			// -----Characteristics的字段信息-----//
			List<BluetoothGattCharacteristic> gattCharacteristics = gattService
					.getCharacteristics();
			for (final BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
				Log.e("wzb", "---->char uuid:"
						+ gattCharacteristic.getUuid().toString());

				int permission = gattCharacteristic.getPermissions();
				Log.e("wzb",
						"---->char permission:"
								+ CommunicationUtil
										.getCharPermission(permission));

				int property = gattCharacteristic.getProperties();
				Log.e("wzb",
						"---->char property:"
								+ CommunicationUtil.getCharPropertie(property));

				byte[] data = gattCharacteristic.getValue();
				if (data != null && data.length > 0) {
					Log.e("wzb", "---->char value:" + new String(data));
				}

				// ff1 --ff7 对应 ff[0]-ff[6]
				if (gattCharacteristic.getUuid().toString()
						.equals("0000fff2-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 1);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff1-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 0);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff3-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 2);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff4-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 3);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff5-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 4);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff6-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 5);
				} else if (gattCharacteristic.getUuid().toString()
						.equals("0000fff7-0000-1000-8000-00805f9b34fb")) {
					mBLE.setBluetoothGattCharacteristic(gattCharacteristic, 6);
				}

				// -----Descriptors的字段信息-----//
				List<BluetoothGattDescriptor> gattDescriptors = gattCharacteristic
						.getDescriptors();
				for (BluetoothGattDescriptor gattDescriptor : gattDescriptors) {
					Log.e("wzb",
							"-------->desc uuid:" + gattDescriptor.getUuid());
					int descPermission = gattDescriptor.getPermissions();
					Log.e("wzb",
							"-------->desc permission:"
									+ CommunicationUtil
											.getDescPermission(descPermission));

					byte[] desData = gattDescriptor.getValue();
					if (desData != null && desData.length > 0) {
						Log.e("wzb", "-------->desc value:"
								+ new String(desData));
					}
				}
			}
		}
	}

	/**
	 * 搜索到BLE终端服务的事件
	 */
	private BluetoothLeClass.OnServiceDiscoverListener mOnServiceDiscover = new OnServiceDiscoverListener() {

		@Override
		public void onServiceDiscover(BluetoothGatt gatt) {
			//displayGattServices(mBLE.getSupportedGattServices());
			//readData();
		}
	};

	/**
	 * 收到BLE终端数据交互的事件
	 */
	private BluetoothLeClass.OnDataAvailableListener mOnDataAvailable = new OnDataAvailableListener() {

		/**
		 * BLE终端数据被读的事件
		 */
		@Override
		public void onCharacteristicRead(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic, final int status) {
			if (status == BluetoothGatt.GATT_SUCCESS)
				Log.d("wzb",
						"onCharRead "
								+ gatt.getDevice().getName()
								+ " read "
								+ characteristic.getUuid().toString()
								+ " -> "
								+ CommunicationUtil
										.bytesToHexString(characteristic
												.getValue()));
		}

		/**
		 * 收到BLE终端写入数据回调
		 */
		@Override
		public void onCharacteristicWrite(BluetoothGatt gatt,
				BluetoothGattCharacteristic characteristic) {
			Log.d("wzb",
					"onCharWrite "
							+ gatt.getDevice().getName()
							+ " write "
							+ characteristic.getUuid().toString()
							+ " -> "
							+ CommunicationUtil.bytesToHexString(characteristic
									.getValue()));
			mBluetoothGattCharacteristic = characteristic;
			mHandler.sendEmptyMessage(REFRESH_BATTERY);
		}
	};
	
	MediaPlayer player;
	private void ring() {
		if(player!=null&&player.isPlaying()){
			player.stop();
		}
		
		String name = SharedPreferencesUtils.getInstanse(this).getMusicName();
		if(name.equals("铃声1")){
			player = MediaPlayer.create(this, R.raw.bell_1);
		}else if(name.equals("铃声2")){
			player = MediaPlayer.create(this, R.raw.bell_2);
		}else if(name.equals("铃声3")){
			player = MediaPlayer.create(this, R.raw.bell_3);
		}else{
			player = MediaPlayer.create(this, R.raw.bell_1);
		}
		player.start();
		showDialog();
	}
	
	AlertDialog dialog;
	private AlertDialog createDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("防丢小助手");
		builder.setMessage("你的设备已经离开了安全距离");
		builder.setPositiveButton("我知道了", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		});
		dialog = builder.create();
		return dialog;
	}
	
	private void showDialog(){
		if(isFirst){
			if(dialog!=null&&!dialog.isShowing()){
				dialog.show();
			}
			isFirst = false;
		}
	}
	
}
