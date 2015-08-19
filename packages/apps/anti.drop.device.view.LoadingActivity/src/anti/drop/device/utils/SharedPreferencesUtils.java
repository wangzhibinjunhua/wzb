package anti.drop.device.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SharedPreferencesUtils {

	private static SharedPreferencesUtils instanse;
	private SharedPreferences mSharedPreferences;
	private final String APP_INFO = "app_info";
	
	private final String IS_CLOSE_CALL_BELL = "is_close_call_bell";//是否关闭报警声
	private final String IS_NOTICE = "is_notice";//是否弹窗通知
	private final String PRE_LOST_MACHINE = "pre_lost_machine";//防丢器
	private final String LONGITUDE = "longitude";
	private final String LATITUDE = "latitude";
	private final String ADDRESS = "address";
	private final String BELL_DISTANCE = "bell_distance";
	private final String BELL_NAME = "bell_name";
	private final String MUSIC_NAME = "music_name";
	private final String BELL_PATH = "bell_path";
	private final String DEVICE_NAME = "device_name";
	private final String DEVICE_LONGITUDE = "device_longitude";
	private final String DEVICE_LATITUDE = "device_latitude";
	private final String IS_ENTER_CARME_TAPE = "is_enter_carme_tape";//是否进入拍照或者录音界面
	
	public SharedPreferencesUtils(Context context) {
		mSharedPreferences = context.getSharedPreferences(APP_INFO,
				Context.MODE_PRIVATE);
	}

	public static SharedPreferencesUtils createInstanse(Context context) {
		if (instanse == null) {
			instanse = new SharedPreferencesUtils(context);
		}
		return instanse;
	}

	public static SharedPreferencesUtils getInstanse(Context context) {
		if (instanse == null) {
			instanse = new SharedPreferencesUtils(context);
		}
		return instanse;
	}
	
	public void setIsCloseCallBell(boolean isAutoLogin){
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(IS_CLOSE_CALL_BELL, isAutoLogin);
		editor.commit();
	}
	
	public boolean getIsCloseCallBell(){
		boolean isCloseCallBell = mSharedPreferences.getBoolean(IS_CLOSE_CALL_BELL, true);
		return isCloseCallBell;
	}
	
	public void setIsNotice(boolean isAutoLogin){
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(IS_NOTICE, isAutoLogin);
		editor.commit();
	}
	
	public boolean getIsNotice(){
		boolean isNotice = mSharedPreferences.getBoolean(IS_NOTICE, false);
		return isNotice;
	}
	
	public void setPreLostMachine(boolean isAutoLogin){
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(PRE_LOST_MACHINE, isAutoLogin);
		editor.commit();
	}
	
	public boolean getPreLostMachine(){
		boolean preLostMachine = mSharedPreferences.getBoolean(PRE_LOST_MACHINE, false);
		return preLostMachine;
	}
	
	public void setLongitude(String longitude){
		Editor editor = mSharedPreferences.edit();
		editor.putString(LONGITUDE, longitude);
		editor.commit();
	}
	
	public String getLongitude(){
		String longitude = mSharedPreferences.getString(LONGITUDE, "");
		return longitude;
	}
	
	public void setLatitude(String latitude){
		Editor editor = mSharedPreferences.edit();
		editor.putString(LATITUDE, latitude);
		editor.commit();
	}
	
	public String getLatitude(){
		String longitude = mSharedPreferences.getString(LATITUDE, "");
		return longitude;
	}
	
	public void setAddress(String address){
		Editor editor = mSharedPreferences.edit();
		editor.putString(ADDRESS, address);
		editor.commit();
	}
	
	public String getAddress(){
		String address = mSharedPreferences.getString(ADDRESS, "");
		return address;
	}
	
	public void setBellDistance(int distance){
		Editor editor = mSharedPreferences.edit();
		editor.putInt(BELL_DISTANCE, distance);
		editor.commit();
	}
	
	public int getBellDistance(){
		int distance = mSharedPreferences.getInt(BELL_DISTANCE, 2);//默认报警距离是2米
		return distance;
	}
	
	public void setBellName(String name){
		Editor editor = mSharedPreferences.edit();
		editor.putString(BELL_NAME, name);
		editor.commit();
	}
	
	public void setMusicName(String name){
		Editor editor = mSharedPreferences.edit();
		editor.putString(MUSIC_NAME, name);
		editor.commit();
	}
	
	public String getMusicName(){
		String bellName = mSharedPreferences.getString(MUSIC_NAME, "铃声1");
		return bellName;
	}
	
	public String getBellName(){
		String bellName = mSharedPreferences.getString(BELL_NAME, "ble");
		return bellName;
	}
	
	public void setBellPath(String path){
		Editor editor = mSharedPreferences.edit();
		editor.putString(BELL_PATH, path);
		editor.commit();
	}
	
	public String getBellPath(){
		String bellPath = mSharedPreferences.getString(BELL_PATH, "");
		return bellPath;
	}
	
	public void setDeviceName(String name){
		Editor editor = mSharedPreferences.edit();
		editor.putString(DEVICE_NAME, name);
		editor.commit();
	}
	
	public String getDeviceName(){
		String name = mSharedPreferences.getString(DEVICE_NAME, "钱包");
		return name;
	}
	
	public void setDeviceLongitude(String longitude){
		Editor editor = mSharedPreferences.edit();
		editor.putString(DEVICE_LONGITUDE, longitude);
		editor.commit();
	}
	
	public String getDeviceLongitude(){
		String longitude = mSharedPreferences.getString(DEVICE_LONGITUDE, "");
		return longitude;
	}
	
	public void setDeviceLatitude(String latitude){
		Editor editor = mSharedPreferences.edit();
		editor.putString(DEVICE_LATITUDE, latitude);
		editor.commit();
	}
	
	public String getDeviceLatitude(){
		String latitude = mSharedPreferences.getString(DEVICE_LATITUDE, "");
		return latitude;
	}
	
	public void setIsEnter(boolean isEnter){
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean(IS_ENTER_CARME_TAPE, isEnter);
		editor.commit();
	}
	
	public boolean getIsEnter(){
		boolean isEnter = mSharedPreferences.getBoolean(IS_ENTER_CARME_TAPE, false);
		return isEnter;
	}
	
	public void setDeviceNamefromAddr(String addr,String name){
		Editor editor = mSharedPreferences.edit();
		editor.putString(addr, name);
		editor.commit();
	}
	
	public String getDeviceNamefromAddr(String addr){
		String name = mSharedPreferences.getString(addr, "AX V01");
		return name;
	}
	
	public void set_modify_name(String name){
		Editor editor = mSharedPreferences.edit();
		editor.putString("00000", name);
		editor.commit();
	}
	
	public String get_modify_name(){
		String name = mSharedPreferences.getString("00000","防丢器");
		return name;
	}
	
}
