package com.tvtelecontroller.activity.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;

import com.tvtelecontroller.R;
import com.tvtelecontroller.activity.DeviceBindActivity;

/**
 * Œ÷∂π
 * @author LuoYong
 */
public class BeanFragment extends Fragment implements OnClickListener{
	
	private Button devicBind;
	private Button clearCache;
	private Button serverRegu;
	private Button aboutWodou;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
		View view = null;
		view = inflater.inflate(R.layout.bean_layout, container, false);
		initView(view);
		return view;
	}
	
	private void initView(View view){
		
		devicBind = (Button)view.findViewById(R.id.device_bind);
		clearCache = (Button)view.findViewById(R.id.clear_cache);
		serverRegu = (Button)view.findViewById(R.id.server_regulations);
		aboutWodou = (Button)view.findViewById(R.id.about_wodou);
		
		devicBind.setOnClickListener(this);
		clearCache.setOnClickListener(this);
		serverRegu.setOnClickListener(this);
		aboutWodou.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		Intent intent = null;
		int resId = v.getId();
		switch(resId){
		case R.id.device_bind:
			intent = new Intent(this.getActivity(),DeviceBindActivity.class);
			startActivity(intent);
			break;
		case R.id.clear_cache:
			break;
		case R.id.server_regulations:
			break;
		case R.id.about_wodou:
			break;
		}
	}

}
