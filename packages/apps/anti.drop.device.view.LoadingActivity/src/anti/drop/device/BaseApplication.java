package anti.drop.device;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;
import anti.drop.device.utils.BluetoothLeClass;
import anti.drop.device.utils.LocationUtil;
import anti.drop.device.utils.SharedPreferencesUtils;

import com.baidu.mapapi.SDKInitializer;

/**
 * 应用启动项，一些静态值在该类中进行初始化
 * @author LuoYong
 */
public class BaseApplication extends Application{
	
	private final String TAG = BaseApplication.class.getSimpleName();
	
	public static Context mContext;
	private static Properties mProperties;
	public static BaseApplication instance;
	
	public BluetoothLeClass ble;
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContext = this.getApplicationContext();
		init();
		instance = this;
		SDKInitializer.initialize(getApplicationContext());
		
	}
	
	private void init() {
		loadConfigFile();//加载配置文件
		// 一进入应用，判断该设备是否支持对蓝牙BLE的支持
		boolean isSupport = getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
		if (isSupport) {
			ble=new BluetoothLeClass(mContext);
			LocationUtil location = new LocationUtil(this);
			double lati = location.getmLatitude()+0.00256;
			double longi = location.getmLongitude()+0.00256;
			SharedPreferencesUtils.getInstanse(this).setDeviceLatitude(String.valueOf(lati));
			SharedPreferencesUtils.getInstanse(this).setDeviceLongitude(String.valueOf(longi));
		}else{
			Toast.makeText(this, "当前设备不支持最新蓝牙4.0技术", 1000);
		}
	}
	
	public void set_ble(BluetoothLeClass b){
		ble=b;
	}
	
	public BluetoothLeClass get_ble(){
		return ble;
	}
	/**
	 * 加载配置文件
	 */
	private void loadConfigFile() {
		mProperties = new Properties();
		try {
			InputStream input = BaseApplication.this.getAssets().open("configuration.properties");
			mProperties.load(input);
		} catch (IOException e) {
			e.printStackTrace();
			Log.i(TAG, "加载配置文件出错:" + e.toString());
		}
	}
	
	/**
	 * Log开关
	 * @return
	 */
	public static boolean isOpenLog() {
		String isOpenLog = mProperties.getProperty("isOpenLog").trim();
		if ("0".equals(isOpenLog)) {
			return true;
		} else {
			return false;
		}
	}

}
