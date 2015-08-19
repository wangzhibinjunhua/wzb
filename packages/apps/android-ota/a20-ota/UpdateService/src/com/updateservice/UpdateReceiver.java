package com.updateservice;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import com.updateservice.UpdateService;
import com.updateservice.ReceiverProc;
import com.updateservice.XmlParse;
import android.util.Log;

public class UpdateReceiver	extends BroadcastReceiver {
	public static long downloadid=-1;
 	private File Filexml = new File(UpdateService.xmlFilePath);
 	private Firmware firmware=null;
 	private String tmpString="";
	private void launchcal(Context context) {
		Intent newintent = new Intent(context,ReceiverProc.class);
		newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newintent);
	}	
	
	public Firmware ReadWriteXml(final boolean bWrite,long id){	
		Firmware firmware=null;
		List<Firmware> firmwareupdate = null; 
		XmlParse xmlparse=new XmlParse();
		InputStream xmlis;
		if(Filexml.exists() ) { 
			try {
				xmlis = new FileInputStream(Filexml);
				firmwareupdate = xmlparse.readXML(xmlis);
				firmware = firmwareupdate.get(0);
				if(bWrite && firmware!=null) {				   
					//firmware.setlastdownlaod(""+id);
					xmlparse.xmlWriteToFile(firmwareupdate,UpdateService.xmlFilePath);
				}					
			} catch (FileNotFoundException e) {			
				firmware = null;
			}
		}	
		return firmware;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.d("onReceive","TODO Auto-generated method stub");
      if(intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)){
	  	Log.d("onReceive","intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE");

        /*long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        if( Filexml.exists() ) {
        	firmware=ReadWriteXml(false, downloadid);
        	tmpString = firmware.getlastdownlaod();        	
        }	
        if(tmpString!=null && !tmpString.equalsIgnoreCase("")) {
        	downloadid = Long.parseLong(tmpString);
        }    	
		if( downloadid == downId ) {
			Log.d("onReceive","downloadid="+downId);
    		launchcal(context);
    	}*/	
      } 
	}
}
