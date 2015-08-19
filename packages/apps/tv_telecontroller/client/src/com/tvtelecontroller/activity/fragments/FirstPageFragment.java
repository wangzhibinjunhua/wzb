package com.tvtelecontroller.activity.fragments;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.tvtelecontroller.BaseApplication;
import com.tvtelecontroller.R;
import com.tvtelecontroller.activity.RemoteControlActivity;
import com.tvtelecontroller.utils.DBHelper;
import com.tvtelecontroller.utils.DeviceAdapter;
import com.tvtelecontroller.utils.DeviceBean;
import com.tvtelecontroller.utils.DeviceParams;
import com.tvtelecontroller.utils.NetworkUtil;
import com.tvtelecontroller.utils.RequestIPAddress;
import com.tvtelecontroller.utils.SendDataThread;
import com.tvtelecontroller.utils.SharedPreferencesUtil;
import com.tvtelecontroller.utils.VDialog;

/**
 * 首页
 * 
 * @author LuoYong
 */
public class FirstPageFragment extends Fragment implements OnClickListener {

	private final int SCAN_FINISH = 0xFFFFF0;
	private final int REFRESH_CONNECTED = 0xFFFFF2;
	private RequestIPAddress mScanReuqest;
	private RelativeLayout searchLayout;
	private TextView noDataTip;
	private ListView deviceList;
	private DeviceAdapter mAdapter;
	private List<DeviceBean> mData;
	private int lenght = 0;
	private Handler mHandler;
	private VDialog progressDialog;
	private PopupWindow mPopup;
	private DBHelper mDB;
	private VDialog progressDialog_1;
	private String connectedIp = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.connection_device, container, false);
		initView(view);
		return view;
	}

	private void findViewById(View view) {
		searchLayout = (RelativeLayout) view.findViewById(R.id.search_layout);
		noDataTip = (TextView) view.findViewById(R.id.device_no_data_tip);
		deviceList = (ListView) view.findViewById(R.id.device_list);
	}

	private void setListener(final View view) {
		searchLayout.setOnClickListener(this);
	}

	public static boolean isFirstEnter = true;
	private void initView(View view) {
		mDB = new DBHelper(getActivity());
		mDB.open();
		findViewById(view);
		mAdapter = new DeviceAdapter(this.getActivity(), mData);
		deviceList.setAdapter(mAdapter);

		// 尝试连接已经连接过的IP地址。
		connectedIp = SharedPreferencesUtil.getInstance(this.getActivity())
				.getConnectedIp();
		if(isFirstEnter){
			if (!connectedIp.equals("")) {
				// 连接
				progressDialog_1 = new VDialog(this.getActivity());
				progressDialog_1.showDialog("", "正在连接...");
				BaseApplication.getInstance().setIp(connectedIp);
				sendKeyData(99);
				revieverThread thread = new revieverThread();
				revieverflag = true;
				thread.start();
			} else {
				mDB.deleteAll();
			}
			
			isFirstEnter = false;
		}

		setListener(view);
		progressDialog = new VDialog(this.getActivity());
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SCAN_FINISH:
					scanDevice();
					break;
				case REFRESH_CONNECTED:
					mDB.deleteDevice(new DeviceBean(connectedIp,connectedIp,"1"));
					DeviceBean device = new DeviceBean();
					device._id = mDB.query().size();
					device.name = connectedIp;
					device.connIp = connectedIp;
					device.isConn = "0";
					mDB.insertDevice(device);
					mAdapter.setmList(mData);
					mAdapter.notifyDataSetChanged();
					connectonSuccessPopup();
					showSuccessPopup();
					revieverflag = true;
					break;
				}
				super.handleMessage(msg);
			}
		};
	}

	private float abs[] = new float[7];
	public static String sendString;
	private SendDataThread thread = null;

	public void sendKeyData(int value) {
		abs[0] = value;
		thread = BaseApplication.getInstance().getThread();
		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < abs.length; i++) {

			if (i != abs.length - 1) {
				stringBuffer.append(abs[i] + " ");
			} else {
				stringBuffer.append(abs[i]);
			}
		}
		sendString = stringBuffer.toString();
		startSendDataThread(sendString, BaseApplication.getInstance().getIp(),
				thread);

	}

	public void startSendDataThread(String str1, String str2, Thread t) {

		thread = new SendDataThread(str1, str2, t);
		thread.start();
	}

	@Override
	public void onResume() {
		if(NetworkUtil.isWifiConnected(this.getActivity())){
			scanDevice();
		}else{
			Toast.makeText(this.getActivity(), "当前网络未连接~", 500).show();
		}
		super.onResume();
	}

	private void scanDevice() {
		mData = mDB.query();
		if (null != mData && mData.size() > 0) {
			noDataTip.setVisibility(View.GONE);
			deviceList.setVisibility(View.VISIBLE);
		} else {
			noDataTip.setVisibility(View.VISIBLE);
			deviceList.setVisibility(View.GONE);
		}

		mAdapter.setmList(mData);
		mAdapter.notifyDataSetChanged();
	}

	private void search() {
		RequestIPAddress.ip_key.clear();
		RequestIPAddress.ip_value.clear();
		mDB.deleteAll();
		startScanThread();
		showSearchDialog();
	}

	private void showSearchDialog() {

		progressDialog.showDialog();
		Thread thread = new Thread() {
			public void run() {
				try {
					sleep(5000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				progressDialog.dismisssDialog();
				mScanReuqest.setFlag(false);
				if (mScanReuqest != null) {
					mScanReuqest = null;

				}
				mHandler.sendEmptyMessage(SCAN_FINISH);
			}
		};
		thread.start();
	}

	/**
	 * 启动扫描线程
	 */
	private void startScanThread() {

		mScanReuqest = new RequestIPAddress(this.getActivity());
		mScanReuqest.start();
		mScanReuqest.setFlag(true);
	}

	@Override
	public void onClick(View v) {
		int resId = v.getId();
		switch (resId) {
		case R.id.title_back:
			break;
		case R.id.search_layout:// 重新扫描
			search();
			break;
		}
	}

	/**
	 * 判断是否连接成功的线程
	 * 
	 * @author LuoYong
	 */
	boolean revieverflag = true;
	private String connectString;

	public class revieverThread extends Thread {
		public void run() {
			DatagramSocket ds = null;
			try {
				if (ds == null) {
					ds = new DatagramSocket(null);
					ds.setReuseAddress(true);
					ds.bind(new InetSocketAddress(9101));
				}
				sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (null != progressDialog_1) {
				progressDialog_1.dismisssDialog();
			}
			byte[] buffer = new byte[1024];
			DatagramPacket dp_receive = new DatagramPacket(buffer,
					buffer.length);
			while (revieverflag) {
				try {
					if (null != ds) {
						ds.receive(dp_receive);
						connectString = new String(dp_receive.getData(), 0,
								dp_receive.getLength());
						Log.d("wzb0415", "constr=" + connectString);
						if (connectString.equals("000")) {
							revieverflag = false;
							mHandler.sendEmptyMessage(REFRESH_CONNECTED);
							ds.close();
							BaseApplication.isConnected = true;
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	PopupWindow connSuccPop;

	private void connectonSuccessPopup() {

		View view = LayoutInflater.from(this.getActivity()).inflate(
				R.layout.connection_success_layout, null);
		TextView name = (TextView) view
				.findViewById(R.id.connectioned_device_name);
		TextView userControll = (TextView) view
				.findViewById(R.id.user_controller);
		int width = DeviceParams.getScreenWidth(this.getActivity());
		connSuccPop = new PopupWindow(view, width - 30,
				LayoutParams.WRAP_CONTENT);
		connSuccPop.setOutsideTouchable(true);
		connSuccPop.setFocusable(true);
		Drawable drawable = this.getActivity().getResources()
				.getDrawable(R.drawable.orang_bg);
		connSuccPop.setBackgroundDrawable(drawable);

		name.setText("已接入" + connectedIp);
		userControll.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到虚拟遥控器
				Intent intent = new Intent(
						FirstPageFragment.this.getActivity(),
						RemoteControlActivity.class);
				FirstPageFragment.this.getActivity().startActivity(intent);
				dismissSuccessPop();
			}
		});
	}

	private void showSuccessPopup() {
		View parent = ((ViewGroup) this.getActivity().findViewById(android.R.id.content))
				.getChildAt(0);
		if (null != connSuccPop && !connSuccPop.isShowing()) {
			connSuccPop.showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}

	private void dismissSuccessPop() {

		if (null != connSuccPop) {
			connSuccPop.dismiss();
		}

	}

}
