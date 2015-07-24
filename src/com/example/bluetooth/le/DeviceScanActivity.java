/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.bluetooth.le;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.example.bluetooth.le.iBeaconClass.iBeacon;
//import com.iflytek.voiceads.AdError;
//import com.iflytek.voiceads.AdKeys;
//import com.iflytek.voiceads.IFLYAdListener;
//import com.iflytek.voiceads.IFLYAdSize;
//import com.iflytek.voiceads.IFLYInterstitialAd;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
	private final static String TAG = DeviceScanActivity.class.getSimpleName();
	private final static String UUID_KEY_DATA = "0000ffe1-0000-1000-8000-00805f9b34fb";
	private IFLYInterstitialAd interstitialAd;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    /**搜索BLE终端*/
    private BluetoothAdapter mBluetoothAdapter;

    private boolean mScanning;
    private Handler mHandler;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 60000;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        
        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //开启蓝牙
        mBluetoothAdapter.enable();
        createInterstitialAd();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Initializes list view adapter.
        mLeDeviceListAdapter = new LeDeviceListAdapter(this);
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

        	final iBeacon ibeacon = iBeaconClass.fromScanData(device,rssi,scanRecord);
        	runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mLeDeviceListAdapter.addDevice(ibeacon);
                    mLeDeviceListAdapter.notifyDataSetChanged();
                }
            });
        }
    };
    public void createInterstitialAd() {
		//此广告位为Demo专用，广告的展示不产生费用
	    final String adUnitId = "96B2AFE02B5FA854B8F08DCCCC916BD2";
	    //创建插屏广告，传入广告位ID
		interstitialAd = IFLYInterstitialAd.createInterstitialAd(this, adUnitId);
	    //设置广告尺寸
	    interstitialAd.setAdSize(IFLYAdSize.INTERSTITIAL);
		//设置下载广告前，弹窗提示
	    interstitialAd.setParameter(AdKeys.DOWNLOAD_ALERT, "true");
	    
	    //请求广告，添加监听器
    	interstitialAd.loadAd(mAdListener);
	}
	
	IFLYAdListener mAdListener = new IFLYAdListener(){

		/**
		 * 广告请求成功
		 */
		@Override
		public void onAdReceive() {
			//展示广告
	    	interstitialAd.showAd();
	    	
		    Log.d("Ad_Android_Demo", "onAdReceive");
		}

		/**
		 * 广告请求失败
		 */
		@Override
		public void onAdFailed(AdError error) {
		    Log.d("Ad_Android_Demo", "onAdFailed:"+error.getErrorCode()+","+
					error.getErrorDescription());
		}

		/**
		 * 广告被点击
		 */
		@Override
		public void onAdClick() {
		    Log.d("Ad_Android_Demo", "onAdClick");
		}

		/**
		 * 广告被关闭
		 */
		@Override
		public void onAdClose() {
		    Log.d("Ad_Android_Demo", "onAdClose");
		}
    };

}