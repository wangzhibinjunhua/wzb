package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;
import anti.drop.device.adapter.DeviceAdapter;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.DBHelper;
import anti.drop.device.utils.SharedPreferencesUtils;

public class ModifyActivity extends BaseActivity{
	
	private ImageView backView;
	private TextView titleView;
	private ListView deviceList;//ËÑË÷µ½µÄÉè±¸
	
	private DeviceAdapter mAdapter;
	private List<String> listData;
	private DBHelper mDBHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_device);
		initView();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void findViewById(){
		
		backView = (ImageView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		deviceList = (ListView)findViewById(R.id.choose_device_listview);
		
	}
	
	private void setListener(){
		
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		deviceList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				
				String address = SharedPreferencesUtils.getInstanse(ModifyActivity.this).getAddress();
				DeviceBean device = new DeviceBean();
				device.address = address;
				mDBHelper.alter(device, "name",listData.get(arg2));
				SharedPreferencesUtils.getInstanse(ModifyActivity.this).setDeviceName(listData.get(arg2));
				finish();
			}
		});
		
	}
	
	private void initView(){
		findViewById();
		titleView.setText("Ãû³Æ");
		mDBHelper = new DBHelper(this);
		mDBHelper.open();
		listData = new ArrayList<String>();
		listData.add("Ô¿³×");
		listData.add("Ç®°ü");
		listData.add("¹«ÎÄ°ü");
		listData.add("IPAD");
		listData.add("±Ê¼Ç±¾µçÄÔ");
		listData.add("±³°ü");
		listData.add("·À¶ªÆ÷1");
		listData.add("·À¶ªÆ÷2");
		listData.add("·À¶ªÆ÷3");
		listData.add("·À¶ªÆ÷4");
		
		mAdapter = new DeviceAdapter(this,listData);
		deviceList.setAdapter(mAdapter);
		mDBHelper = DBHelper.getInstance(this);
		mDBHelper.open();
		setListener();
	}
	
}
