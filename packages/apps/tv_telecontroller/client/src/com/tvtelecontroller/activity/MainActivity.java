package com.tvtelecontroller.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.tvtelecontroller.BaseApplication;
import com.tvtelecontroller.R;
import com.tvtelecontroller.activity.fragments.AVFragment;
import com.tvtelecontroller.activity.fragments.BeanFragment;
import com.tvtelecontroller.activity.fragments.FirstPageFragment;
import com.tvtelecontroller.activity.fragments.GameFragment;
import com.tvtelecontroller.utils.DBHelper;
import com.tvtelecontroller.utils.DeviceBean;
import com.tvtelecontroller.utils.NetworkUtil;
import com.tvtelecontroller.utils.SharedPreferencesUtil;

public class MainActivity extends FragmentActivity implements OnClickListener{
	
	private TextView backView;
	private TextView titleView;
	private RadioGroup tabGroip;
	private RadioButton firstRadBtn;
	private ImageView changeImage;
	private View popView;
	private PopupWindow mPopup;
	private TextView handleTv;
	private TextView controllerTv;
	private TextView flickingTv;
	private TextView woboxTv;
	private TextView searchTv;
	private boolean isFirstPage = true;
	private long firstTime = 0; 
//	private int lenght = 0;
	private DBHelper mDB;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		initView();
	}
	
	private void findViewById(){
		backView = (TextView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		tabGroip = (RadioGroup)findViewById(R.id.main_radio_group);
		firstRadBtn = (RadioButton)findViewById(R.id.first_page_radio);
		changeImage = (ImageView)findViewById(R.id.change_image);
	}
	
	public void changeFragment(Fragment targetFragment){
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.main_container, targetFragment,"fragment")
		.setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
		.commit();
	}
	
	private void setListener(){
		tabGroip.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch(checkedId){
				case R.id.first_page_radio:
					changeFragment(new FirstPageFragment());
					titleView.setText("链接");
					break;
				case R.id.av_page_radio:
					changeFragment(new AVFragment());
					titleView.setText("影音");
					break;
				case R.id.game_page_radio:
					changeFragment(new GameFragment());
					titleView.setText("游戏");
					break;
				case R.id.bean_page_radio:
					changeFragment(new BeanFragment());
					titleView.setText("个人");
					break;
				}
			}
		});
		
		handleTv.setOnClickListener(this);
		controllerTv.setOnClickListener(this);
		changeImage.setOnClickListener(this);
	}
	
	private void initView(){
		mDB = new DBHelper(this);
		mDB.open();
		findViewById();
		backView.setVisibility(View.GONE);
		initPopup();
		setListener();
		boolean isConnectNet = NetworkUtil.getInstance().isNetworkConnected(this);
		if(!isConnectNet){
			showDialog();
		}
	}
	
	private void showDialog(){
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("提示");
		builder.setMessage("网络未连接,是否去设置?");
		builder.setPositiveButton("确定", new DialogInterface.OnClickListener(){

			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent wifiSettingsIntent = new Intent("android.settings.WIFI_SETTINGS");   
				   startActivity(wifiSettingsIntent);
			}
		});
		builder.setNegativeButton("取消", null);
		builder.create().show();
	}
	
	private void initPopup(){
		
		popView = LayoutInflater.from(this).inflate(R.layout.extension_function_layout, null);
		handleTv = (TextView)popView.findViewById(R.id.meun_handle);
		controllerTv = (TextView)popView.findViewById(R.id.meun_controlle);
		flickingTv = (TextView)popView.findViewById(R.id.meun_flicking);
		woboxTv = (TextView)popView.findViewById(R.id.meun_doubox);
		searchTv = (TextView)popView.findViewById(R.id.meun_search);
		
		mPopup = new PopupWindow(popView, LinearLayout.LayoutParams.MATCH_PARENT, 
				LinearLayout.LayoutParams.WRAP_CONTENT);
		
		mPopup.setOutsideTouchable(true);
		mPopup.setFocusable(true);
		Drawable drawable = getResources().getDrawable(R.color.popup_bg_color);
		mPopup.setBackgroundDrawable(drawable);
	}
	
	private void showPopup(){
		
		View parent = ((ViewGroup) findViewById(android.R.id.content)).getChildAt(0);
		if(mPopup!=null&&!mPopup.isShowing()){
			mPopup.showAsDropDown(tabGroip, 0, 10);
		}else{
			mPopup.dismiss();
		}
	}
	
	private void dismissPopup(){
		if(mPopup!=null&&mPopup.isShowing()){
			mPopup.dismiss();
		}
	}
	
	@Override
	protected void onResume() {
		changeFragment(new FirstPageFragment());
		titleView.setText("链接");
		firstRadBtn.setChecked(true);
		super.onResume();
	}
	
	@Override
	public void onClick(View v) {
		Intent mIntent = null;
//		lenght = RequestIPAddress.ip_key.size();
		switch(v.getId()){
		case R.id.meun_handle:
			
			if(BaseApplication.isConnected){
				mIntent = new Intent(this,HandleActivity.class);
				startActivity(mIntent);
				isFirstPage = false;
			}else{
				//扫描周围设备
				Toast.makeText(this, "当前没有已连接的设备", 500).show();
				changeFragment(new FirstPageFragment());
				titleView.setText("链接");
			}
			dismissPopup();
			break;
		case R.id.meun_controlle:
			//遥控器
			if(BaseApplication.isConnected){
				mIntent = new Intent(this,RemoteControlActivity.class);
				startActivity(mIntent);
				isFirstPage = false;
			}else{
				Toast.makeText(this, "当前没有已连接的设备", 500).show();
				//扫描周围设备
				changeFragment(new FirstPageFragment());
				titleView.setText("链接");
			}
			dismissPopup();
			break;
		case R.id.change_image:
			showPopup();
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch(keyCode){
		case KeyEvent.KEYCODE_BACK:
			if(!isFirstPage){
				changeFragment(new FirstPageFragment());
				isFirstPage = true;
				return false;
			}else{//如果两次按键时间间隔大于2秒，则不退出
				long secondTime = System.currentTimeMillis();
				if (secondTime - firstTime > 2000){
					Toast.makeText(this, "再按一次退出应用", 1000).show();
					firstTime = secondTime;//更新firstTime  
	                return true;
				}else{//两次按键小于2秒时，退出应用
					String ip = SharedPreferencesUtil.getInstance(this).getConnectedIp();
					mDB.deleteDevice(new DeviceBean(ip,ip,"0"));
					DeviceBean device = new DeviceBean();
					device._id = mDB.query().size();
					device.name = ip;
					device.connIp = ip;
					device.isConn = "1";
					mDB.insertDevice(device);
					System.exit(0); 
					System.gc();
//					DeviceAdapter.ds.disconnect();
				}
			}
			break;
		}
		return super.onKeyDown(keyCode, event);
	}
	
}
