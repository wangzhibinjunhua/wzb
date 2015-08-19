package anti.drop.device.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import anti.drop.device.R;
import anti.drop.device.utils.SharedPreferencesUtils;

public class BellAdapter extends BaseAdapter {

	private Context mContext;
	private List<String> musicData;
	String bellName = "";

	public BellAdapter(Context context, List<String> list) {
		this.mContext = context;
		this.musicData = list;
		bellName = SharedPreferencesUtils.getInstanse(mContext).getBellName();
	}

	public void setMusicData(List<String> musicData) {
		this.musicData = musicData;
	}

	@Override
	public int getCount() {
		if(null!=musicData&&musicData.size()>0){
			return musicData.size();
		}else{
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		return musicData.get(position);
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
		name.setText(musicData.get(position));
		String bellName = SharedPreferencesUtils.getInstanse(mContext).getBellName();
		if(bellName.equals(musicData.get(position))){
			tick.setVisibility(View.VISIBLE);
		}else{
			tick.setVisibility(View.GONE);
		}
		return convertView;
	}

}
