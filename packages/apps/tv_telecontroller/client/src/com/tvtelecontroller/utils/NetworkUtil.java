package com.tvtelecontroller.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	
	private static NetworkUtil instance;
	
	public static NetworkUtil getInstance(){
		if(null==instance){
			instance = new NetworkUtil();
		}
		return instance;
	}
	
	/*
	 * 判断网络是否连接上
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}
	
	public static boolean isWifiConnected(Context context){
		boolean result = false;
		if (context != null) {
			ConnectivityManager connManager = (ConnectivityManager)
					context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWifi.isConnected()){
				result = true;
			}else{
				result = false;
			}
		}
		return result;
	}

}
