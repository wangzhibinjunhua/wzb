package anti.drop.device.utils;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

public class LocationUtil {
	
	LocationClient mLocationClient;
	MyLocationListenner mListener = new MyLocationListenner();
	double mLongitude;
	double mLatitude;
	
	public LocationUtil(Context context){
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(mListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(1000);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}
	
	
	
	public double getmLongitude() {
		return mLongitude;
	}



	public void setmLongitude(double mLongitude) {
		this.mLongitude = mLongitude;
	}



	public double getmLatitude() {
		return mLatitude;
	}



	public void setmLatitude(double mLatitude) {
		this.mLatitude = mLatitude;
	}



	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			mLongitude = location.getLongitude();
			mLatitude = location.getLatitude();
		}

		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

}
