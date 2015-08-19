package anti.drop.device.adapter;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import anti.drop.device.R;
import anti.drop.device.pojo.DeviceBean;
import anti.drop.device.utils.SharedPreferencesUtils;

public class DeviceAdapter extends BaseAdapter{
	
	private Activity mContext;
	private List<String> mListData;
	private String address;
	
	public DeviceAdapter(Activity context,List<String> list){
		this.mContext = context;
		this.mListData = list;
		this.address = SharedPreferencesUtils.getInstanse(mContext).getAddress();
	}

	@Override
	public int getCount() {
		if(null!=mListData&&mListData.size()>0){
			return mListData.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return mListData.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(R.layout.choose_device_item, null);
		TextView name = (TextView)convertView.findViewById(R.id.device_name);
		ImageView tick = (ImageView)convertView.findViewById(R.id.device_tick);
		name.setText(mListData.get(position));
		return convertView;
	}

}
