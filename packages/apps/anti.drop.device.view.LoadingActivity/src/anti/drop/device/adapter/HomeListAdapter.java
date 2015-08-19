package anti.drop.device.adapter;

import java.util.List;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import anti.drop.device.R;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.DBHelper;
import anti.drop.device.utils.SharedPreferencesUtils;

public class HomeListAdapter extends BaseAdapter {

	private Context mContext;
	private List<DeviceBean> deveiceData;
	
	private HomeListAdapter instance;
	private DBHelper mDBHelper;

	public HomeListAdapter(Context context, List<DeviceBean> list) {
		mContext = context;
		deveiceData = list;
		
		instance = this;
		mDBHelper = new DBHelper(mContext);
		mDBHelper.open();
	}

	public void setDeveiceData(List<DeviceBean> deveiceData) {
		this.deveiceData = deveiceData;
	}

	@Override
	public int getCount() {
		if (null != deveiceData && deveiceData.size() > 0) {
			return deveiceData.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return deveiceData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.home_page_list_item, null);

		TextView name = (TextView) convertView
				.findViewById(R.id.home_page_item_myobject);
		TextView isbind = (TextView) convertView
				.findViewById(R.id.home_page_item_isbind);
		final ImageView face = (ImageView) convertView
				.findViewById(R.id.home_page_item_face);
		final ImageView delect = (ImageView) convertView
				.findViewById(R.id.home_page_item_delete);

		//name.setText(deveiceData.get(position).getName());
		name.setText(SharedPreferencesUtils.getInstanse(mContext).getDeviceNamefromAddr(deveiceData.get(position).getAddress()));
		int isconn = deveiceData.get(position).getStatus();
		int rssi = deveiceData.get(position).getRssi();
		
		face.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				face.setImageResource(R.drawable.face_icon_2);
				delect.setVisibility(View.VISIBLE);
			}
		});
		
		delect.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mDBHelper.deleteDevice(deveiceData.get(position));
				instance.notifyDataSetChanged();
			}
		});
		
		if (isconn == 0x0000000c) {
			isbind.setText("已经匹配");
			if(rssi>-80){
				face.setImageResource(R.drawable.face_icon_1);
			}else if(rssi<=-80&&rssi>-90){
				face.setImageResource(R.drawable.face_icon_4);
			}else if(rssi<=-90&&rssi>-100){
				face.setImageResource(R.drawable.face_icon_3);
			}else if(rssi<=-100){
				face.setImageResource(R.drawable.face_icon_3);
			}
		} else if (isconn == 0x0000000b) {
			isbind.setText("匹配正在进行中");
			face.setImageResource(R.drawable.face_icon_2);
		} else if (isconn == 0x0000000a) {
			isbind.setText("丢失设备");
			face.setImageResource(R.drawable.face_icon_2);
		}
		return convertView;
	}

}
