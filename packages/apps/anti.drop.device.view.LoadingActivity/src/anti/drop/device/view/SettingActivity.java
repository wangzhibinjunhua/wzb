package anti.drop.device.view;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import anti.drop.device.BaseActivity;
import anti.drop.device.BaseApplication;
import anti.drop.device.R;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.DBHelper;
import anti.drop.device.utils.SharedPreferencesUtils;

public class SettingActivity extends BaseActivity implements OnCheckedChangeListener,OnClickListener{
	
	private ImageView backView;
	private TextView titleView;
	private TextView goodsName;
	private TextView bellName;
	private RelativeLayout goodsLayout;
	private RelativeLayout bellLayout;
	private ToggleButton callBellToggle;
	private Button ignoreplmBtn;
	
	private Intent mIntent;
	
	private BluetoothLeClass mBLE;
	private String address = "";
	private DBHelper mDB;
	BaseApplication mApp;
	private List<DeviceBean> deviceData;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setting);
		initView();
	}
	
	private void findViewById(){
		
		backView = (ImageView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		goodsName = (TextView)findViewById(R.id.goods_name);
		bellName = (TextView)findViewById(R.id.bell_name);
		goodsLayout = (RelativeLayout)findViewById(R.id.goods_layout);
		bellLayout = (RelativeLayout)findViewById(R.id.bell_layout);
		callBellToggle = (ToggleButton)findViewById(R.id.call_bell_toggle);
		ignoreplmBtn = (Button)findViewById(R.id.ignore_prelost_machine);
	}
	
	private void setListener(){
		
		backView.setOnClickListener(this);
		goodsLayout.setOnClickListener(this);
		bellLayout.setOnClickListener(this);
		ignoreplmBtn.setOnClickListener(this);
		callBellToggle.setOnCheckedChangeListener(this);
	}
	
	private void initView(){
		mApp = (BaseApplication)getApplication();
		mBLE=mApp.get_ble();
		if(!mBLE.initialize()){
			finish();
		}
		findViewById();
		titleView.setText("设置");
		mIntent = getIntent();
		address = SharedPreferencesUtils.getInstanse(this).getAddress();
		mDB = new DBHelper(this);
		mDB.open();
		deviceData = mDB.query();
		setListener();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		bellName.setText(SharedPreferencesUtils.getInstanse(this).getMusicName());
		goodsName.setText(SharedPreferencesUtils.getInstanse(this).getDeviceName());
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if(buttonView==callBellToggle){
			if(isChecked){
				SharedPreferencesUtils.getInstanse(this).setIsCloseCallBell(true);
			}else{
				SharedPreferencesUtils.getInstanse(this).setIsCloseCallBell(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		switch(resId){
		case R.id.title_back:
			finish();
			break;
		case R.id.goods_layout:
			mIntent = new Intent(this,ModifyActivity.class);
			startActivity(mIntent);
			break;
		case R.id.bell_layout:
			mIntent = new Intent(this,ChooseBellActivity.class);
			startActivity(mIntent);
			break;
		case R.id.ignore_prelost_machine:
			mBLE.disconnect();//断开连接该设备
			ignoreplmBtn.setText("已忽略该防丢器");
			if(null!=deviceData&&deviceData.size()>0){
				for(int i=0;i<deviceData.size();i++){
					if(address.equals(deviceData.get(i).getAddress())){
						mDB.alter(deviceData.get(i), 0x0000000a);
					}
				}
			}
			HomeActivity.mFinish();
			DetailActivity.mFinish();
			mIntent = new Intent(SettingActivity.this,HomeActivity.class);
			startActivity(mIntent);
			finish();
			break;
		}
	}
}
