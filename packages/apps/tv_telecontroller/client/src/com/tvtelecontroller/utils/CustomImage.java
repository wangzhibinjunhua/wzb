package com.tvtelecontroller.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.tvtelecontroller.R;

public class CustomImage extends FrameLayout{
	
	private TextView text;
	private View instance;
	private Context mContext;

	public CustomImage(Context context) {
		super(context);
		mContext = context;
		initView();
	}
	
	public CustomImage(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initView();
	}
	
	private void initView(){
		
		instance = LayoutInflater.from(mContext).inflate(R.layout.custom_image_view, this);
		text = (TextView)instance.findViewById(R.id.custom_text);
		
	}
	
	public void setText(String str){
		text.setText(str);
	}
	
}
