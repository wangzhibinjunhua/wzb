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
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;
import anti.drop.device.adapter.SearchResultAdapter;
import anti.drop.device.pojo.DeviceBean;

public class SearchResultActivity extends BaseActivity{

	private ImageView backView;
	private TextView titleView;
	private ListView resultList;
	
	private SearchResultAdapter mAdapter;
	private List<DeviceBean> listData;
	
	public BluetoothAdapter mBluetoothAdapter;
	public BluetoothManager bluetoothManager;
	
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
		
	}
	
	private void setListener(){
		
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
	}
	
	private void initView(){
		bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
		mBluetoothAdapter = bluetoothManager.getAdapter();
		findViewById();
		titleView.setText("搜索结果");
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
				}
			},10000);
			mScanning = true;
			mBluetoothAdapter.startLeScan(mLeScanCallback);
		}else{
			mScanning = false;
			mBluetoothAdapter.stopLeScan(mLeScanCallback);
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
					bean.rssi = rssi;
					
					if(!contrast(listData,device)){
						listData.add(bean);
					}
					
					mAdapter.setListData(listData);
					mAdapter.notifyDataSetChanged();
					
				}
			});
		}
	};
	
	//查询某个列表中是否包含该设备信息,有则返回true,没有则返回false
	private boolean contrast(List<DeviceBean> list,BluetoothDevice device){
		String address = device.getAddress();
		boolean result = false;
		for(int i=0;i<list.size();i++){
			if(address.equals(list.get(i).getAddress())){
				result =  true;
			}
		}
		return result;
	}
	
}
