package com.updateservice;

import com.updateservice.XmlParse;
import com.updateservice.md5;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;
import android.content.Context;
import android.content.DialogInterface;

import android.os.SystemProperties;
import com.updateservice.UpdateReceiver; 
import android.util.Log;



public class ReceiverProc extends Activity {
	private DownloadManager  manager=null;
	private Button ButtonOK;
	private Button ButtonCancel;
 	private File Filexml = new File(UpdateService.xmlFilePath);
 	private File FileTar = new File(UpdateService.FileTarPath);
 	private String md5str="error";
 	private Firmware firmware=null;
 	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.mydialog);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //setContentView(R.layout.float_activity);
	    manager =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);
		/*ButtonOK = ((Button)findViewById(R.id.okButton));
		ButtonOK.setOnClickListener(new View.OnClickListener()  {
        	public void onClick(View paramView)   {          	            	 
        		OKClick();
        	}
        });         
		ButtonCancel = ((Button)findViewById(R.id.cancelButton));
		ButtonCancel.setOnClickListener(new View.OnClickListener()  {
        	public void onClick(View paramView)   {          	            	 
        		CancelClick();
        	}
        });*/

		Builder dialog=new AlertDialog.Builder(this);
    	dialog.setTitle(R.string.bartitle);
    	dialog.setMessage(R.string.update_message);
    	dialog.setPositiveButton( R.string.BtnYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {            	            	
    				//if(!BarProc() ) {
    					OKClick();
						
    				//}
                } 	
            });
    	dialog.setNegativeButton(R.string.BtnNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // No, hence do nothing   
                	CancelClick();
                }
         });            	
    	dialog.show();

		String tmpString=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getName();
		tmpString = "/sdcard/"+tmpString+"/"+UpdateService.SaveFileName;		
		moveFile(tmpString,"/sdcard");		
		if(UpdateReceiver.downloadid >=0 ) {
			manager.remove(UpdateReceiver.downloadid);
		}
		if( !FileTar.exists() ) { 
			Toast.makeText(this, "no update file!",3).show();
			if(Filexml.isFile() && Filexml.exists()) {     
				Filexml.delete();     
	    	}
			checkQuit(true);
		} else {
			firmware = ReadWriteXml(true, -1);
		}
		//OKClick();
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
				/*if(bWrite && firmware!=null) {				   
					firmware.setlastdownlaod(""+id);
					xmlparse.xmlWriteToFile(firmwareupdate,UpdateService.xmlFilePath);
				}*/					
			} catch (FileNotFoundException e) {			
				firmware = null;
			}
		}	
		return firmware;
	}
	
	public void UpdateFile() {			
		File updateFile = new File(UpdateService.FileTarPath);
			if (updateFile.exists()){
			try {
					RecoverySystem.installPackage(getApplicationContext(), updateFile); 
				} catch (IOException e) {
					e.printStackTrace(); 
				}
			}
	}	 

	private void OKClick()   {
		Log.d("ReceiverProc","OKClick");
		if(Filexml.isFile() && Filexml.exists()) {     
			Filexml.delete();     
    	}
		if( firmware !=null && FileTar.exists() ) {	    		    		
    		File Filemd5 = new File(UpdateService.FileTarPath);	
    		md5str = md5.md5sum(UpdateService.FileTarPath);
			Log.d("ReceiverProc","md5str="+md5str+"firmware.getmd5()"+firmware.getmd5());
    		if( ( (int)Filemd5.length()==firmware.getsize() ) &&
    			md5str.equalsIgnoreCase(firmware.getmd5()) ) {
    			Log.d("ReceiverProc","UpdateFile");
    			UpdateFile(); 
    			checkQuit(true);
    		}
		}			
	}
	private void CancelClick() {
		if(UpdateReceiver.downloadid >=0 ) {
			manager.remove(UpdateReceiver.downloadid);
		}	
		if(Filexml.isFile() && Filexml.exists()) {     
			Filexml.delete();     
    	}
	    checkQuit(true);
	}
	private void checkQuit(boolean paramBoolean)   {
	       super.finish();
	}  
	
    public static boolean moveFile(String srcFileName, String destDirName) {          
		File srcFile = new File(srcFileName);   
		if(!srcFile.exists() || !srcFile.isFile())    
			return false;          
		File destDir = new File(destDirName);   
		if (!destDir.exists())   
			destDir.mkdirs();          
		return srcFile.renameTo(new File(destDirName + File.separator + srcFile.getName()));   
	} 
}
