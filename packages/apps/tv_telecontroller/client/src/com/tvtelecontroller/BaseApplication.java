package com.tvtelecontroller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.tvtelecontroller.utils.SendDataThread;

import android.app.Application;
import android.util.Log;

public class BaseApplication extends Application{
	
	private final String TAG = BaseApplication.class.getSimpleName();
	
	public static BaseApplication mySelf;
	private static Properties mProperties;
	
	public String ipAddress;
	public SendDataThread thread = null;
	public static boolean isConnected = false;
	
	/**单例**/
	public static BaseApplication getInstance(){
		
		if(mySelf==null){
			mySelf = new BaseApplication();
		}
		return mySelf;
	}
	
	public SendDataThread getThread(){
		return thread;
		
	}
	
	public String getIp(){
		
		return ipAddress;
	}
	
	public void setIp(String s){
		
		this.ipAddress=s;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		loadConfigFile();
		setIp("");
	}
	
	/**加载配置文件**/
	private void loadConfigFile(){
		mProperties = new Properties();
		try {
			InputStream input = BaseApplication.this.getAssets().open("configuration.properties");
			mProperties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "加载配置文件出错:" + e.toString());
		}
	}
	
	/**Log开关**/
	public static boolean isOpenLog(){
		String isOpenLog = mProperties.getProperty("isOpenLog").trim();
		if ("0".equals(isOpenLog)) {
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public void onTerminate() {
		isConnected = false;
		super.onTerminate();
	}

}
