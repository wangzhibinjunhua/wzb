package anti.drop.device.view;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import anti.drop.device.BaseActivity;
import anti.drop.device.R;
import anti.drop.device.adapter.BellAdapter;
import anti.drop.device.utils.SharedPreferencesUtils;

public class ChooseBellActivity extends BaseActivity{
	
	private ImageView backView;
	private TextView titleView;
	private ListView bellList;//À—À˜µΩµƒ…Ë±∏
	private List<String> musicData;
	private BellAdapter mAdapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.choose_device);
		initView();
	}
	
	private void initView(){
		
		backView = (ImageView)findViewById(R.id.title_back);
		titleView = (TextView)findViewById(R.id.title_text);
		bellList = (ListView)findViewById(R.id.choose_device_listview);
		
		titleView.setText("—°‘Ò¡Â…˘");
		musicData = new ArrayList<String>();
		musicData.add("¡Â…˘1");
		musicData.add("¡Â…˘2");
		musicData.add("¡Â…˘3");
		
		mAdapter = new BellAdapter(this,musicData);
		bellList.setAdapter(mAdapter);
		
		backView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		bellList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				SharedPreferencesUtils.getInstanse(ChooseBellActivity.this)
				.setMusicName(musicData.get(arg2));
				ChooseBellActivity.this.finish();
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
