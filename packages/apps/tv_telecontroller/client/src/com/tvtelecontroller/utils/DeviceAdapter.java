package com.tvtelecontroller.utils;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.tvtelecontroller.BaseApplication;
import com.tvtelecontroller.R;
import com.tvtelecontroller.activity.RemoteControlActivity;

public class DeviceAdapter extends BaseAdapter {

	private final int REFRESH_VIEW = 0xFFFFF0;
	
	private Activity mActivity;
	private List<DeviceBean> mList;
	private PopupWindow mPopup;
	private VDialog progressDialog;
	boolean revieverflag = true;
	private String connectString;
	private int currPosition = -1;
	private DBHelper mDB;

	public DeviceAdapter(Activity activity,List<DeviceBean> list) {
		mActivity = activity;
		mList = list;
		mDB = new DBHelper(mActivity);
		mDB.open();
	}

	public void setmList(List<DeviceBean> mList) {
		this.mList = mList;
	}

	@Override
	public int getCount() {
		if (null != mList && mList.size() > 0) {
			return mList.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		currPosition = position;
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mActivity).inflate(
					R.layout.device_list_item, null);
			holder.deviceName = (TextView) convertView
					.findViewById(R.id.device_name);
			holder.connDevice = (TextView) convertView
					.findViewById(R.id.connection);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.deviceName.setText(mList.get(position).getName());
		holder.deviceIp = mList.get(position).getConnIp();
		holder.isConnection = mList.get(position).isConn();
		if (holder.isConnection.equals("0")) {
			holder.connDevice.setText("已连接");
		} else {
			holder.connDevice.setText("未连接");
		}

		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				initPopup(position);
				showPopup();
				setListener();
			}
		});

		return convertView;
	}

	public class ViewHolder {

		TextView deviceName;
		TextView connDevice;
		String deviceIp;
		String isConnection;

	}
	
	PopupWindow connSuccPop;
	private void connectonSuccessPopup(){
		
		View view = LayoutInflater.from(mActivity).inflate(R.layout.connection_success_layout, null);
		TextView name = (TextView)view.findViewById(R.id.connectioned_device_name);
		TextView userControll = (TextView)view.findViewById(R.id.user_controller);
		int width = DeviceParams.getScreenWidth(mActivity);
		connSuccPop = new PopupWindow(view, width-30,LayoutParams.WRAP_CONTENT);
		connSuccPop.setOutsideTouchable(true);
		connSuccPop.setFocusable(true);
		Drawable drawable = mActivity.getResources().getDrawable(R.drawable.orang_bg);
		connSuccPop.setBackgroundDrawable(drawable);
		
		name.setText("已接入"+mList.get(currPosition).getName());
		userControll.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				//跳转到虚拟遥控器
				Intent intent = new Intent(mActivity,RemoteControlActivity.class);
				mActivity.startActivity(intent);
				dismissSuccessPop();
			}
		});
	}
	
	private void showSuccessPopup(){
		View parent = ((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
		if(null!=connSuccPop&&!connSuccPop.isShowing()){
			connSuccPop.showAtLocation(parent, Gravity.CENTER, 0, 0);
		}
	}
	
	private void dismissSuccessPop(){
		
		if(null!=connSuccPop){
			connSuccPop.dismiss();
		}
		
	}

	public class revieverThread extends Thread {
		public void run() {
			DatagramSocket ds = null;
			try {
				if(ds==null){
					ds = new DatagramSocket(null);
					ds.setReuseAddress(true);
					ds.bind(new InetSocketAddress(9101));
				}
				sleep(3000);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (null != progressDialog) {
				progressDialog.dismisssDialog();
			}
			byte[] buffer = new byte[1024];
			DatagramPacket dp_receive = new DatagramPacket(buffer,
					buffer.length);
			while (revieverflag) {
				try {
					if(null!=ds){
						ds.receive(dp_receive);
						connectString = new String(dp_receive.getData(), 0,dp_receive.getLength());
						Log.d("wzb0415", "constr=" + connectString);
						if (connectString.equals("000")) {
							revieverflag = false;
							SharedPreferencesUtil.getInstance(mActivity).setConnectedIp(mList.get(currPosition).getConnIp());
							mHandler.sendEmptyMessage(REFRESH_VIEW);
							BaseApplication.isConnected = true;
							ds.close();
						}else{
							mHandler.sendEmptyMessage(SHOW_TOAST);
						}
					}else{
						mHandler.sendEmptyMessage(SHOW_TOAST);
					}
					
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
//			ds.disconnect();
//			ds.close();
			
		}
	}
	
	private final int SHOW_TOAST = 0xFFFFF1;
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
			case REFRESH_VIEW:
				connectonSuccessPopup();
				showSuccessPopup();
				mDB.deleteDevice(mList.get(currPosition));
				DeviceBean device = new DeviceBean();
				device._id = mDB.query().size();
				device.name = mList.get(currPosition).getName();
				device.connIp = mList.get(currPosition).getConnIp();
				device.isConn = "0";
				mDB.insertDevice(device);
				notifyDataSetChanged();
				break;
			case SHOW_TOAST:
				Toast.makeText(mActivity, "连接失败~", 500).show();
				break;
			}
		};
	};

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

	private TextView deviceName;
	private TextView ipAddress;
	private TextView currCondition;
	private TextView connTxt;
	//初始化弹窗
	private void initPopup(int position) {
		View view = LayoutInflater.from(mActivity).inflate(R.layout.connection_popup_layout, null);
		deviceName = (TextView) view.findViewById(R.id.device_name);
		ipAddress = (TextView) view.findViewById(R.id.ip_address);
		currCondition = (TextView) view.findViewById(R.id.curr_condition);
		connTxt = (TextView) view.findViewById(R.id.immediately_connnection);
		int width = DeviceParams.getScreenWidth(mActivity);
		mPopup = new PopupWindow(view, width-30,LayoutParams.WRAP_CONTENT);
		mPopup.setOutsideTouchable(true);
		mPopup.setFocusable(true);
		Drawable drawable = mActivity.getResources().getDrawable(R.drawable.orang_bg);
		mPopup.setBackgroundDrawable(drawable);
		deviceName.setText(mList.get(position).getName());
		ipAddress.setText(mList.get(position).getConnIp());
		if (mList.get(position).isConn().equals("0")) {
			currCondition.setText("已连接");
			connTxt.setVisibility(View.GONE);
		} else {
			currCondition.setText("未连接");
			connTxt.setVisibility(View.VISIBLE);
		}
	}
	//显示弹窗
	private void showPopup() {
		View parent = ((ViewGroup) mActivity.findViewById(android.R.id.content)).getChildAt(0);
		if (null != mPopup && !mPopup.isShowing()) {
			mPopup.showAtLocation(parent, Gravity.CENTER, 0, 0);
		} else if (mPopup.isShowing()) {
			mPopup.dismiss();
		}
	}
	
	private void dismissPopup(){
		if(null!=mPopup){
			mPopup.dismiss();
		}
	}
	//关闭弹窗
	private void setListener(){
		
		connTxt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				progressDialog = new VDialog(mActivity);
				progressDialog.showDialog("", "正在连接...");
				String ip = mList.get(currPosition).getConnIp();
				BaseApplication.getInstance().setIp(ip);
				sendKeyData(99);
				dismissPopup();
				revieverThread thread = new revieverThread();
				revieverflag=true;
				thread.start();
			}
		});
	}
	
}
