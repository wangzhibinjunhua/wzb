package anti.drop.device.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import anti.drop.device.R;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.SharedPreferencesUtils;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class HomeListAdapter extends BaseAdapter {

	private Context mContext;
	private List<DeviceBean> deveiceData;

	public HomeListAdapter(Context context, List<DeviceBean> list) {
		mContext = context;
		deveiceData = list;
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
		convertView = LayoutInflater.from(mContext).inflate(R.layout.home_page_list_item, null);
		TextView name = (TextView) convertView.findViewById(R.id.home_page_item_myobject);
		TextView isbind = (TextView) convertView.findViewById(R.id.home_page_item_isbind);
		final ImageView face = (ImageView) convertView.findViewById(R.id.home_page_item_face);

		name.setText(deveiceData.get(position).getName());
		int isconn = deveiceData.get(position).getStatus();
		
		if (isconn == BluetoothDevice.BOND_BONDED) {
			isbind.setText("已经匹配");
			face.setImageResource(R.drawable.face_icon_1);
		} else if (isconn == BluetoothDevice.BOND_BONDING) {
			isbind.setText("正在匹配");
			face.setImageResource(R.drawable.face_icon_2);
		} else if (isconn == 0x0000000a) {
			isbind.setText("丢失设备");
			face.setImageResource(R.drawable.face_icon_2);
		}
		return convertView;
	}

}
