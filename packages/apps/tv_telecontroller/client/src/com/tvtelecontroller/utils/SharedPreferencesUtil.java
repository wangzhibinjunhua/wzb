package com.tvtelecontroller.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferencesUtil {
	
	private static SharedPreferences mPreferences;
	public static SharedPreferencesUtil mySelf;
	
	private static final String SHAREDPREFERENCES = "TV_TELECONTROLLER";
	private static final String IS_FIRST_ENTER_APP = "is_first_enter_app";
	private static final String CONNECTIONED_IP = "connectioned_ip";
	
	public SharedPreferencesUtil(Context context) {
		mPreferences = context.getSharedPreferences(SHAREDPREFERENCES,Context.MODE_PRIVATE);
	}
	
	/**µ¥Àý**/
	public static SharedPreferencesUtil getInstance(Context context){
		if(mySelf==null){
			mySelf = new SharedPreferencesUtil(context);
		}
		return mySelf;
	}
	
	public static void setIsFirstEnter(boolean isFirst){
		Editor editor = mPreferences.edit();
		editor.putBoolean(IS_FIRST_ENTER_APP, isFirst);
		editor.commit();
	}
	
	public static boolean getIsFirstEnter(){
		return mPreferences.getBoolean(IS_FIRST_ENTER_APP, true);
	}
	
	public static void setConnectedIp(String ip){
		Editor editor = mPreferences.edit();
		editor.putString(CONNECTIONED_IP, ip);
		editor.commit();
	}
	
	public static String getConnectedIp(){
		return mPreferences.getString(CONNECTIONED_IP, "");
	}

}
