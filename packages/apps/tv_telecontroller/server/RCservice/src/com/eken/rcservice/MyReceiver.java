package com.eken.rcservice;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import android.util.Log;


public class MyReceiver extends BroadcastReceiver{

	

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action=intent.getAction();
		if(action.equals("android.intent.action.BOOT_COMPLETED")){
			Log.d("RemoteServer","==boot completed==");
			Intent service=new Intent(context,CoreService.class);
			context.startService(service);
		}
				
		
	}

}
