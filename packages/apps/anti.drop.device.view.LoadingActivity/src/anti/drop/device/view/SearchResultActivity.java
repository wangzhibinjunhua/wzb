package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;
import anti.drop.device.adapter.SearchResultAdapter;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.DBHelper;

public class SearchResultActivity extends BaseActivity implements OnClickListener{

	private ImageView backView;
	private TextView titleView;
	private ListView resultList;
	private ProgressBar mPorgress;
	
	private SearchResultAdapter mAdapter;
	private List<DeviceBean> listData;
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothManager bluetoothManager;
	private DBHelper mDBHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_result);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		startScan(false);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		startScan(false);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		startScan(false);
	}
	
	private void findViewById(){
		
		backView = (ImageView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		resultList = (ListView)findViewById(R.id.search_result_listview);
		mPorgress = (ProgressBar)findViewById(R.id.title_progress);
	}
	
	private void setListener(){
		backView.setOnClickListener(this);
	}
	
	private void initView(){
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		mDBHelper = DBHelper.getInstance(this);
		mDBHelper.open();
		findViewById();
		mPorgress.setVisibility(View.VISIBLE);
		titleView.setText("ËÑË÷½á¹û");
		listData = new ArrayList<DeviceBean>();
		mAdapter = new SearchResultAdapter(this,listData);
		resultList.setAdapter(mAdapter);
		setListener();
		startScan(true);
	}
	
	private Handler mHandler = new Handler();
	boolean mScanning = false;
	private void startScan(boolean enable){
		
		if (enable){
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					mScanning = false;
					mBluetoothAdapter.stopLeScan(mLeScanCallback);
					mPorgress.setVisibility(View.GONE);
				}
			},10000);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
			mPorgress.setVisibility(View.VISIBLE);
		}else{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
			mPorgress.setVisibility(View.GONE);
		}
	}
	
	private BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
		@Override
		public void onLeScan(final BluetoothDevice device, final int rssi,
				byte[] scanRecord) {

			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					DeviceBean bean = new DeviceBean();
					
					bean.name = device.getName();
					bean.address = device.getAddress();
					bean.status = device.getBondState();
					
					if(!listData.contains(device)){
						listData.add(bean);
					}
					
					mAdapter.setListData(listData);
					mAdapter.notifyDataSetChanged();
				}
			});
		}
	};

	@Override
	public void onClick(View v) {
		if(v==backView){
			finish();
		}
	}
	
}
