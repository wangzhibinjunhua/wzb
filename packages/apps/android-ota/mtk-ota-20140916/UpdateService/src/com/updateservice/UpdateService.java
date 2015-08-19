package com.updateservice;
import com.updateservice.XmlParse;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.util.List;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.Environment;
import android.os.Handler;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

//add by lrx for ftp
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;





public class UpdateService extends Activity {
	private Context mContext=null;   
	private DownloadManager manager=null ;   
    private long lastDownload=-1L;   
//	private Button ButtonOta;
	private Button ButtonOtaNetwork;
	private Button ButtonOtaSdcard;
	private int  xh_count=0;
	private ProgressDialog progressDialog;
	private BufferedInputStream bis;
	private BufferedOutputStream bos;
	public static final String SaveFileName="update.zip";
	private	String FileSrcPath="/mnt/extsd/update.zip";
	private String FileotaPath="/data/data/update.zip";
	public static final String FileTarPath="/data/data/update.zip";	
 	private File FileSrc = new File(FileSrcPath);
 	private File FileTar = new File(FileTarPath);
 	//private String  fileurl="http://www.day-wish.com/Upload/DownFiles/update.zip";
	//private String  urlxml="http://www.day-wish.com/Upload/DownFiles/update.xml";
 	private String  fileurl="ftp://iballmobiles.co.in/juntai";
	private String  urlxml=""; 	
	private String  strproduct="";
	public  static final String xmlFilePath="/data/data/update.xml";
	public  static final String UpdatexmlPath = "/data/data/download.xml";
	//public  static final String xmlFilePath="/data/data/com.UpdateService/files/update.xml";
	private File Filexml = new File(xmlFilePath);
 	private List<Firmware>  downware=null;
 	private List<Firmware>  localware=null;
 	private List<Firmware>  updateware=null;
 	private Firmware firmware=null;
 	private String tmpString="";	
 	private String strfirmware="";
 	private FTPClient ftpClient = new FTPClient();
 	XmlParse xmlparse=new XmlParse();
 	private int versionmask = 0x01;
 	private String version = "";
 	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        displaybar();                
        mContext = this;
        manager =(DownloadManager)getSystemService(DOWNLOAD_SERVICE);        
        tmpString=SystemProperties.get( "ro.hwsuzhiguo."+ this.getString(R.string.hwmodel) ); 
		/*Log.d("111","tempstr="+tmpString);
		if(!tmpString.equalsIgnoreCase(this.getString(R.string.strtest)) ) {
        	OtaProc(false);
        	return;
        }

		tmpString = SystemProperties.get("ro.szjt.author");
		Log.d("222","tempstr="+tmpString);
		if(!tmpString.equalsIgnoreCase("juntaishuma"))
		{
			OtaProc(false);
        	return;
		}*/

		//add by lrx 
		version = SystemProperties.get("ro.szjt.version");
		tmpString = SystemProperties.get("ro.szjt.mask");
		//versionmask = Integer.parseInt(tmpString); 
        
        ButtonOtaNetwork = ((Button)findViewById(R.id.check_network));
    	ButtonOtaNetwork.setOnClickListener(new View.OnClickListener()  {
    		public void onClick(View paramView)   {          	            	 
    			
    		        	new AlertDialog.Builder(UpdateService.this)
    		            //.setIconAttribute(android.R.attr.alertDialogIcon)
    		            .setTitle(R.string.softupdate_title)
    		            .setMessage(R.string.softupdate_content)
    		            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
    		                public void onClick(DialogInterface dialog, int whichButton) {
    		                    deleteFile(UpdatexmlPath);
    	    		        	//deleteFile(FileTarPath); 
    	    		        	dialog.dismiss();
    	    		        	mHandler.sendEmptyMessage(0);
    		                }
    		            })
    		            .setNeutralButton(R.string.wifi_settings, new DialogInterface.OnClickListener() {
    		                public void onClick(DialogInterface dialog, int whichButton) {
    		                	startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
    		                }
    		            })
    		            .setNegativeButton(R.string.BtnCancel, new DialogInterface.OnClickListener() {
    		                public void onClick(DialogInterface dialog, int whichButton) {
    		                	dialog.cancel();
    		                	checkQuit(true);
    		                }
    		            })
    		            .create()
    		            .show();
    		       
    	        //}
        	}
    	});
    	
    	ButtonOtaSdcard = ((Button)findViewById(R.id.check_sdcard));
    	ButtonOtaSdcard.setOnClickListener(new View.OnClickListener()  {
    		public void onClick(View paramView)   {
				 SystemUpdate mSystemUpdate= new SystemUpdate();
				 mSystemUpdate.OtaClick();
    			//SystemUpdate.setContext(UpdateService.this,getApplicationContext());
    			//SystemUpdate.OtaClick(UpdateService.this);
    		}
    	});
        
    }
    
    Handler mHandler = new Handler() {
    	public void handleMessage (Message msg) {
			switch(msg.what){
			case 0:
				OtaClick();
				break;
			case 1:
				NetWorkUpdate();
				break;
			case 2:
				dealresult();
				break;
			case 3:
				showDowndialg();
				break;
			default:
				break;
			}
		}
    };
    
    private void launchcal(Context context) {
		Intent newintent = new Intent(context,ReceiverProc.class);
		newintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(newintent);
	}	
    
    public boolean deleteFile(String fileName) {     
    	File file = new File(fileName);     
    	if(file.isFile() && file.exists()) {     
    		file.delete();     
    		System.out.println("delete file "+fileName+"finished");     
    		return true;     
    	}else{     
    		System.out.println("deleer file"+fileName+"error");     
    		return false;     
    	}     
    } 

	private long press_count =0;
	private ProgressDialog mxProgressDialog = null;
	public void showDowndialg(){
		
		mxProgressDialog = new ProgressDialog(UpdateService.this);
        //mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
        mxProgressDialog.setTitle("Network Download");
        mxProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mxProgressDialog.setMessage("Start download update file "+firmware.getdesc()+",pls wait...");
		mxProgressDialog.setCancelable(false);
		//mxProgressDialog.setIndeterminate(false);
        mxProgressDialog.show();

		new Thread() {   
			@Override  
			public void run() {   
				try { 
					mxProgressDialog.setProgress(0);
					while (sysstatus ==0) {
						File f = new File(FileotaPath);
						press_count = (f.length()*100)/firmware.getsize();
						//Log.d("ftp","count "+f.length()+"press_count="+press_count);
						if(press_count>100)press_count=100;
						int a = Integer.parseInt(String.valueOf(press_count));
						if(mxProgressDialog.isShowing())
						mxProgressDialog.setProgress(a);  
						Thread.sleep(300); 
					}	 		
					
				} catch (Exception e) {   
					//progressDialog.cancel();   
				}   
			} 
		}.start();
	}

	public void closeDownDialog(){
			if(mxProgressDialog!=null)
			{
				if(mxProgressDialog.isShowing())
				mxProgressDialog.cancel();

			}
		}

	public void dealresult(){
		Builder dialog=new AlertDialog.Builder(this);
		closeDownDialog();
		if(sysstatus!=3)
		{
			dialog.setTitle("warning!");
			dialog.setMessage("download err");
    		dialog.setPositiveButton( R.string.BtnYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {            	            	
                	checkQuit(true);
                } 	
            });
		}
		else
		{
			dialog.setTitle("update file");
		    dialog.setMessage("now update system!");
			dialog.setPositiveButton( R.string.BtnYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {            	            	
                	//checkQuit(true);
                	xmlparse.AddfirwareToFile(xmlFilePath, firmware);
					 SystemUpdate mSystemUpdate= new SystemUpdate();
				 	mSystemUpdate.UpdateFile();
					
                } 	
            });
			dialog.setNegativeButton(R.string.BtnNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // No, hence do nothing   
                	checkQuit(true);
                }
            });    
		}
		dialog.show();
	}

	
    
    private int sysstatus = 0;
	private int sysreload = 0;
    public void startDownload() {
       
		

        sysstatus = 0;
		sysreload = 0;
		 new Thread(new Runnable(){
   		 @Override
   		 public void run() {
   		 
   			String temp = firmware.getdir();
    		String newurl="/juntai"+temp;
    		boolean connect = connectftpserver();
			deleteFile(FileotaPath);
			mHandler.sendEmptyMessage(3);
	    	if(connect== false)
	    	{
	    		Log.d("ftp","connect err");
				closeftpconnect();
				sysstatus = 1;
	    		
	    	}
			else
	   		{
	   			    connect=loadFile(newurl,FileotaPath);
	   				
	   				if(connect)
	   				{
	   					sysstatus = 3;
	   					
	   				}
	   				else
	   				{
	   					sysstatus = 2; 
	   				}

					closeftpconnect();
					Log.d("ftp","download "+connect);
   	     	}

			if(sysstatus!=3)	
			while(sysreload<3)
			{
				connect = connectftpserver();
				if(connect== false)
		    	{
		    		Log.d("ftp","connect err");
					closeftpconnect();
					sysstatus = 1;
					break;
		    		
		    	}
				 connect=loadFile(newurl,FileotaPath);
	   				
   				if(connect)
   				{
   					sysstatus = 3;
   					
   				}
   				else
   				{
   					sysstatus = 2; 
   				}
				closeftpconnect();
				sysreload++;
			}
   			
   			mHandler.sendEmptyMessage(2);
   			}
		}).start(); 

		
    	/*Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).mkdirs();
        DownloadManager.Request down=new DownloadManager.Request(uri);     
        down.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE|DownloadManager.Request.NETWORK_WIFI);   
        down.setTitle("System Update");
        down.setDescription(SaveFileName);
        tmpString=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getName();
        tmpString = "/sdcard/"+tmpString+"/"+SaveFileName;
        deleteFile(tmpString);
        down.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS,SaveFileName);
        //down.setNotificationVisibility(false);
        //down.setShowRunningNotification(true);
        down.setShowRunningNotification(true);      
        down.setVisibleInDownloadsUi(true);   
    	lastDownload=manager.enqueue(down);
         if( lastDownload >0 ) {
    			//ReadWriteXml(true);
    	}
		checkQuit(true);*/
    }
    
    private void checkQuit(boolean paramBoolean)   {
        super.finish();
    }   
    
    /*public void UpdateFile() {			
			File updateFile = new File(FileTarPath);
			if (updateFile.exists()){
			try {
					RecoverySystem.installPackage(getApplicationContext(), updateFile); 
				} catch (IOException e) {
					e.printStackTrace(); 
				}
			}
		}*/	 
    
    public void OtaProc(boolean bProc) {
    	Builder dialog=new AlertDialog.Builder(this);
    	dialog.setTitle(R.string.otatitle);
    	if( bProc ) {
    		dialog.setMessage(R.string.otamessage);
    		dialog.setPositiveButton( R.string.BtnYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {            	            	
    				//if(!BarProc() ) {
    					//NetWorkUpdate();
						
    				//}

					dialog.dismiss();
    	    		mHandler.sendEmptyMessage(1);
                } 	
            });
    		dialog.setNegativeButton(R.string.BtnNo, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    // No, hence do nothing   
                	checkQuit(true);
                }
            });            	
    	} else {
    		dialog.setMessage(R.string.nofile);
    		dialog.setPositiveButton( R.string.BtnYes, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {            	            	
                	checkQuit(true);
                } 	
            });
    	}
    	dialog.show();
    }
    
    private boolean bRet=false;
    public void OtaClick()  {
    	//if( FileSrc.exists() ) { 
    	//	bRet = true;
    	//	OtaProc(bRet);
    	//} else {  
    		if( !CheckNetwork(UpdateService.this) ) return;
    		new OtaTask().execute();
    	//}
    }
    public boolean isVaildRet=false;

    private class OtaTask extends AsyncTask<Integer, Integer, Boolean> {

    	private ProgressDialog mProgressDialog = null;
    	//private boolean isVaildRet = false;
    	
    	@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(UpdateService.this);
            //mProgressDialog.setIconAttribute(android.R.attr.alertDialogIcon);
            mProgressDialog.setTitle(R.string.softupdate_title);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setMessage(UpdateService.this.getString(R.string.connecting));
			mProgressDialog.setCancelable(false);
			//mxProgressDialog.setIndeterminate(false);
            mProgressDialog.show();
		}

		@Override
		protected Boolean doInBackground(Integer... params) {
			// TODO Auto-generated method stub
			isVaildRet = false;
			/*if(Filexml.exists() ) {			
    			isVaild(urlxml,false);
    		}	else {
    			Log.d("update service","xmlurl="+urlxml);
    			isVaild(urlxml,true);
    		}*/
			isVaild(urlxml);
    		
    		for(int i=0;i<300;i++) {
    			SystemClock.sleep(100);
    			if(isVaildRet ) break;
    		}
    		publishProgress(0);
			return isVaildRet;
		}
    	
		@Override
		protected void onPostExecute(Boolean isVaildRet) {
			// TODO Auto-generated method stub
			super.onPostExecute(isVaildRet);
			if (mProgressDialog != null && mProgressDialog.isShowing()) {
				mProgressDialog.cancel();
				//mProgressDialog =null;
			    Log.d("update service","finish isVaildRet="+isVaildRet);
				if( !isVaildRet ) {
					Log.d("update service","isVaildRet="+isVaildRet);
	    			new AlertDialog.Builder(UpdateService.this)
	    	    	.setTitle(R.string.otatitle)
	    	    	.setMessage(R.string.no_update_available)  
	    	    	.setNeutralButton(R.string.BtnYes, new DialogInterface.OnClickListener() {   
	    	    			public void onClick(DialogInterface dialog, int whichButton) {   
	    	    				dialog.cancel();   
	    	    			}   
	    	    	})		    	
	    	    	.show();   
	    		}
				else
				{
					downware=xmlparse.ReadWriteXml(UpdatexmlPath);
					/*if(downware!=null)
					{
						for(int i=0; i<downware.size(); i++)
						{
							firmware = downware.get(i);
							Log.d("ftp","id="+firmware.getid()+"dir"+firmware.getdir()+"size"+firmware.getsize());
						}
					}*/
					localware= xmlparse.ReadWriteXml(xmlFilePath);
					if(localware!=null)
					Log.d("local","local="+localware.size());
					updateware = xmlparse.CompareXml(downware, localware, versionmask, version);
					/*if(updateware!=null)
					{
						for(int i=0; i<updateware.size(); i++)
						{
							firmware = updateware.get(i);
							Log.d("ftp","id="+firmware.getid()+"dir"+firmware.getdir()+"size"+firmware.getsize());
						}
					}
					else
					{
						Log.d("ftp","update is null");
					}*/
					if(updateware!=null)
					{
						firmware = xmlparse.XmlStillHaveDown(updateware);
						
						if(firmware!=null)
						{
							Log.d("local","local="+firmware.getsize());
							bRet =true;
						}
						else
						{
							Log.d("ftp","firmware is null");
						}
					}
						
					//bRet = false;
				}
				OtaProc(bRet);
			}
		}
    }
       
	public void displaybar() {
 		xh_count = 0;      
	 	progressDialog = new ProgressDialog(UpdateService.this);    
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);     
		progressDialog.setTitle("dowload update file");   		  
		progressDialog.setMessage("dowload update file from network");         
		progressDialog.setIndeterminate(false);					 
		progressDialog.setMax(100); 
		progressDialog.setCancelable(false);		   					
		new Thread() {   
			@Override  
			public void run() {   
				try {   
					progressDialog.setProgress(0);
					while (xh_count <= progressDialog.getMax()) {										
						progressDialog.setProgress(xh_count);  
						Thread.sleep(100); 
					}	 		
					progressDialog.cancel();   
				} catch (Exception e) {   
					progressDialog.cancel();   
				}   
			} 
		}.start();	
		
		
	}
	/*private long countnum=0;
	private int readlen=0;
	private long copysize=0;	
	public void copyFile(File src,File tar) throws Exception   {                 		
        InputStream is=new FileInputStream(src);
        OutputStream op=new FileOutputStream(tar);
        bis=new BufferedInputStream(is);
        bos=new BufferedOutputStream(op);
        final byte[] bt=new byte[8192];
        copysize=FileSrc.length();
        readlen=bis.read(bt);     
        Thread thread=new Thread(){  
        	public void run(){  
        		try {  
        			while(readlen!=-1)  {        			
        				bos.write(bt,0,readlen);        				
        				countnum =countnum+readlen;
        				xh_count =(int)(countnum*100/copysize);
        				readlen = bis.read(bt);
        				Thread.sleep(1);          		
        			}   
        			bis.close();
        			bos.close(); 
        			progressDialog.cancel();
        			UpdateFile();
        		} catch (Exception e) {  
					progressDialog.cancel(); 
        		} 
        	}		             
		};
		thread.start(); 
  	}*/
	
	/*public boolean BarProc() {                 					
		boolean ret=false;
		if( FileSrc.exists() ) {     		  	
     		xh_count = 0;      
			progressDialog.setTitle(R.string.bartitle);
			progressDialog.setMessage("copy update file to system device"); 
			progressDialog.setMax(100);
			progressDialog.setProgress(xh_count); 
     		progressDialog.show(); 
			try	{				
				ret =true;
				copyFile(FileSrc,FileTar);				
			} catch (Exception e) { 		
				e.printStackTrace();
			}	
     	}
		return ret;
	}*/
	
	private static FTPClientConfig getFtpConfig(){

     FTPClientConfig ftpConfig=new FTPClientConfig(FTPClientConfig.SYST_UNIX/*SYST_UNIX*/);

     ftpConfig.setServerLanguageCode(FTP.DEFAULT_CONTROL_ENCODING);

     return ftpConfig;
     }

    public boolean connectftpserver()
    {
    	boolean ret=false;
    	int reply;
		int connect;
    	try{
    		
            //ftpClient.setDefaultPort(990);
            ftpClient.configure(getFtpConfig());
            //ftpClient.connect("d2m.youdomain.com",21);
           ftpClient.connect("f1.pubds.com",4502);
			//Log.d("ftp_test","connect finish");
			//ftpClient.login("d2m","ndSCHWnT");
			ftpClient.login("ftp_0_50003","547536");
			//Log.d("ftp_test","login");
			ftpClient.setDefaultPort(4502);                                
            reply = ftpClient.getReplyCode();
            //Log.d("ftp_test","reply="+reply);
            if (!FTPReply.isPositiveCompletion(reply)) {
            		
            		Log.d("ftp_test","err disconnect");
            }
            else
            {
            	ret = true;
            	ftpClient.enterLocalPassiveMode();
                ftpClient.setControlEncoding("gbk");
            }
    	}
    	catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return ret;
    }
    
    public void closeftpconnect()
    {
    	try{
    		ftpClient.logout();
    		ftpClient.disconnect();
    	}
    	catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
    }
    
   public  boolean loadFile(String remoteFileName,String localFileName){
        BufferedOutputStream buffOut=null;
        boolean ret=true;
        
        try{
		  ftpClient.enterLocalPassiveMode(); 

           ftpClient.setFileType(FTP.BINARY_FILE_TYPE); 

		   File f = new File(localFileName); 
		  
		  if(f.exists())
		  {
		  	
		  	 if(f.length() >= firmware.getsize()){ 
			 	return false;
		  	 }
			 buffOut=new BufferedOutputStream(new FileOutputStream(localFileName));

			 ftpClient.setRestartOffset(f.length()); 
             ftpClient.retrieveFile(remoteFileName, buffOut); 
			 buffOut.close();

		  }
		  else
		  {
          	 buffOut=new BufferedOutputStream(new FileOutputStream(localFileName));
          	 //Log.d("ftp test","start download");
          	 ftpClient.retrieveFile(remoteFileName, buffOut);
			 buffOut.close();
		  }
        }catch(Exception e){
        	//Log.e("ftp test","down fail");
            ret = false ;
    	   e.printStackTrace();

          }finally{
                 try{
                	 //Log.d("ftp test","down ok");
                      if(buffOut!=null) buffOut.close();
                    }catch(Exception e){
                        e.printStackTrace();
                     }
            }
          return ret;
}


	
	public boolean isVaild(final String downloadUrl) {		
		new Thread(new Runnable(){
   		 @Override
   		 public void run() {
   			 boolean connect =false;
   			 
   			 connect = connectftpserver();
   			 Log.d("ftp test","connect="+connect);
   			 if(connect)
   			 {
   				connect=loadFile("/juntai/update.xml",UpdatexmlPath);
   				//closeftpconnect();
   				Log.d("ftp test","connect="+connect);
   				if(connect)
   				{
   					isVaildRet = true;
   					
   				}
   				else
   				{
   	   				isVaildRet = false; 
   				}

				closeftpconnect();
   			 }
   			 else
   			 {
   				closeftpconnect();
   				isVaildRet = false; 
   			 }

   			/*try{
   				URL url = new URL(downloadUrl);
   				HttpURLConnection conn = (HttpURLConnection) url.openConnection();		
   				conn.setConnectTimeout(6*1000);
   				conn.setReadTimeout(6*1000);
   				conn.setRequestMethod("GET");
   				conn.setRequestProperty("Accept", "*");
   				conn.setRequestProperty("Accept-Language", "en-US");
   				conn.setRequestProperty("Referer", downloadUrl);
   				conn.setRequestProperty("Charset", "UTF-8");
   				conn.setRequestProperty("User-Agent", "Mozilla/5.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
   				conn.setRequestProperty("Connection", "Keep-Alive");			
   				conn.connect();
   				if (conn.getResponseCode()==200) {  
   					if( bxml ) {
   						int xmlsize = conn.getContentLength();
   						if (xmlsize <= 0) {
   							isVaildRet = false;	//throw new RuntimeException("");							
						} else {
							InputStream inStream = conn.getInputStream();
							byte[] buffer = new byte[1024];
							int readsie=inStream.read(buffer, 0, 1024);	
							File Filexml = new File(xmlFilePath);
							OutputStream bosxml=new FileOutputStream(Filexml);
							bosxml.write(buffer,0,readsie);
							bosxml.close();
							inStream.close();
							isVaildRet = true;						   									
						}
   				} else {
   						Log.d("update","getResponseCode=");
   						isVaildRet = true;
   				}
   				conn.disconnect();
   			} 
   		}catch (Exception e) {
   				isVaildRet =false;    				
   		}*/	
   	}
		}).start();    
		return isVaildRet;
	}
	

	
    private boolean CheckNetwork( final Context context) {   
    	boolean flag = false;   
    	ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);   
    	if (conManager.getActiveNetworkInfo() != null)   
    		flag = conManager.getActiveNetworkInfo().isAvailable();   
    	if (!flag) {   
    		new AlertDialog.Builder(context)
    		.setTitle(R.string.update)
    		.setMessage(R.string.connect_error)  
//    		.setPositiveButton("Ensure", new DialogInterface.OnClickListener() {   
//    			public void onClick(DialogInterface dialog, int whichButton) {   
//    				startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));    				    				
//    			}  
//    		})
    		.setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {   
    				public void onClick(DialogInterface dialog, int whichButton) {   
    					checkQuit(true);  
    				}   
    		})
    		.create()  
    		.show();   
    	}   
    	return flag;   
    }     
    private long readSDCard() {
    	long sdSpraeSize=0;
    	String state = Environment.getExternalStorageState();
    	if(Environment.MEDIA_MOUNTED.equals(state)) {
    		File sdcardDir = Environment.getExternalStorageDirectory();
    		StatFs sf = new StatFs(sdcardDir.getPath());
    		long blockSize = sf.getBlockSize();
    		long blockCount = sf.getBlockCount(); 
    		long availCount = sf.getAvailableBlocks();
    		sdSpraeSize = availCount*blockSize;
    		Log.d("", "block size:"+ blockSize+",block num:"+ blockCount+",total:"+blockSize*blockCount/1024+"KB");
    		Log.d("", "used block num:"+ availCount+", sprae size:"+ availCount*blockSize/1024+"KB"); 
    	}
    	return sdSpraeSize;
    }
    
    private void NetWorkUpdate()
	{
		long sdSpraeSize=readSDCard();
		Log.d("networkupdate","sdSpraeSize="+sdSpraeSize+"fir="+(firmware.getsize()+1024*12024*40));
		//if( sdSpraeSize >(firmware.getsize()+1024*12024*40) )  {
			Log.d("ftp test","sdSpraeSize="+sdSpraeSize);
			startDownload();
		//} else {
		//	Toast.makeText(UpdateService.this, "sdcard size is not enough",1).show();
		//}								
	}
	public class SystemUpdate {	
		private Button ButtonOta;
		private long filesize=0;
		private int  xh_count=0;
		private int  wait_count=0;
		private int len=0;
		private long count=0; 
		private ProgressDialog progressDialog;
		private ProgressDialog progressDialogWait;
		private BufferedInputStream bis;
		private BufferedOutputStream bos;
		//private String FileSrcPath="/mnt/extsd/update.zip";
		//private String FileSrcPath="/data/update.zip";//no sdcard for test wzb
		//private String FileTarPath="/data/data/update.zip";
		private String FileSrcPath="/mnt/sdcard2/update.zip"; //add by wzb for mtk tf card
		private String FileTarPath="/data/update.zip";
	 	private File FileSrc = null;
	 	private File FileTar = new File(FileTarPath);
		 /** Called when the activity is first created. */
	    public void SystemUpdate() {
	        //super.onCreate(savedInstanceState);
	        //setContentView(R.layout.main);    
	        //displaybar();
	        //ButtonOta = ((Button)findViewById(R.id.ota));
	        //ButtonOta.setOnClickListener(new View.OnClickListener()  {
	        //    	public void onClick(View paramView)   {          	            	 
	        //    		OtaClick();
	        //    	}
	        //    });         
	    }  
	    /*handleMessage()*/ 
		Handler handler= new Handler(){		
			public void handleMessage (Message msg) {
				switch(msg.what){
				case 0:
					Toast.makeText(UpdateService.this, "update file is error!",2).show();
					break;
				case 1:
					Toast.makeText(UpdateService.this, "sdcard size is not enough!",2).show();
					break;
				case 2:
		    		sdSpraeSize=readSDCard();
		    		filesize=FileSrc.length();
		    		//if( sdSpraeSize >(filesize+1024*12024*40) )  {
		         		xh_count = 0;      
		    			progressDialog.setTitle(R.string.bartitle);
		    			progressDialog.setMessage("copy update file to system device");     
		    			progressDialog.setProgress(xh_count); 
		    			progressDialog.show();   					
		    			try	{				
		    				copyFile(FileSrc,FileTar);
		    			} catch (Exception e) { 		
		    				e.printStackTrace();
		    			}	    	 	
		    		//} else {
		    		//	Toast.makeText(UpdateService.this, "sdcard size is not enough!",2).show();
		    		//}
					break;
				default:
					break;
				}
			}
		};
	    public boolean deleteFile(String fileName) {     
	    	File file = new File(fileName);     
	    	if(file.isFile() && file.exists()) {     
	    		file.delete();     
	    		System.out.println("delete"+fileName+"sucess");     
	    		return true;     
	    	}else{     
	    		System.out.println("delete"+fileName+"failure");     
	    		return false;     
	    	}     
	    }     
	    
	    //private void checkQuit(boolean paramBoolean)   {
	    //    super.finish();
	    //}    

	    public void OtaClick()    {
	    	displaybar();
	    	new AlertDialog.Builder(UpdateService.this)
	        .setTitle(R.string.otatitle)
	        .setMessage(R.string.otamessage)
	        .setPositiveButton(R.string.BtnYes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {            	            	            	
	            		BarProc();            	
	            	//checkQuit(true);
	            }
	        })
	        .setNegativeButton(R.string.BtnNo, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	                // No, hence do nothing   
	            	checkQuit(true);
	            }
	        })        
	        .show();
	    }    
	   
	    
		public void displaybar() {
	 		xh_count = 0;      
		 	progressDialog = new ProgressDialog(UpdateService.this);    
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);     
			progressDialog.setTitle("dowload update file");   		  
			progressDialog.setMessage("dowload update file from network");         
			//progressDialog.setIcon(R.drawable.img2);     							 
			//progressDialog.setMax(100); 
			//progressDialog.setProgress(100);
			progressDialog.setCancelable(false);
			progressDialog.setIndeterminate(false);
			//progressDialog.show();   					
			new Thread() {   
				@Override  
				public void run() {   
					try { 
						progressDialog.setProgress(0);
						while (xh_count <= 100) {										
							progressDialog.setProgress(xh_count);  
							Thread.sleep(100); 
						}	 		
						progressDialog.cancel();   
					} catch (Exception e) {   
						progressDialog.cancel();   
					}   
				} 
			}.start();			
		}

		public void displaywait() {     
			progressDialogWait = new ProgressDialog(UpdateService.this);    
			progressDialogWait.setProgressStyle(ProgressDialog.STYLE_SPINNER);     
			progressDialogWait.setTitle(" wait ");   		  
			progressDialogWait.setMessage("check update file, please wait!");         
			progressDialogWait.setCancelable(false);
			progressDialogWait.setIndeterminate(false);		 				
			new Thread() {   
				@Override  
				public void run() {   
					try { 
						progressDialogWait.setProgress(0);
						while (wait_count <= 100) {										
							progressDialogWait.setProgress(wait_count);  
							Thread.sleep(100); 
						}	 		
						progressDialogWait.cancel();   
					} catch (Exception e) {   
						progressDialogWait.cancel();   
					}   
				} 
			}.start();			
		}

		public void UpdateFile() {			
			File updateFile = new File(FileTarPath);
			if (updateFile.exists()){
				try {
						RecoverySystem.installPackage(getApplicationContext(), updateFile); 
					} catch (IOException e) {
						e.printStackTrace(); 
					}
			}
		}	
		private boolean bUnzip=false;
		private String strEntry; //save zip name	
		private String unzipFile; //save zip name
		private long sdSpraeSize;
		private void Unzip(String zipFile) {		    		    
				unzipFile = zipFile;
				displaywait();					   
				wait_count = 0;
				progressDialogWait.show(); 	        
		        Thread thread=new Thread() {  
					public void run(){  
		         	 try {  
				    	try {
				    	  FileInputStream fis = new FileInputStream(unzipFile);
				    	  ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
				    	  ZipEntry entry; 
				    	  while ((entry = zis.getNextEntry()) != null) {
				    		  try { 		
				    		     strEntry = entry.getName();
				    		     if( strEntry.equalsIgnoreCase("daliyuxin") ) {
				    		    	 bUnzip=true;	
				    		    	 Log.d(" ","find files");
				    		    	 break;
				    			 }
				    		   } catch (Exception ex) {
				    			   	bUnzip=false;
				    			   	Log.d(" ","can't zip");
				    		    	break;
				    		   }
				    		   Thread.sleep(1);
				    	  } 
				    	  zis.close();
				       } catch (Exception cwj) {
				    	   bUnzip=false;
				       }
					   Log.d(" ","bUnzip"+bUnzip);	                        		
				       progressDialogWait.cancel();
				       Log.d(" ","bUnzip"+bUnzip);
				      // if(bUnzip) {
				      if(true){// wzb for test
				       		
					    	  Message msg=new Message();
				    		  msg.what=2;	 
				    		  handler.sendMessage (msg);
				       } else {
				    	  Message msg=new Message();
			    		  msg.what=0;	 
			    		  handler.sendMessage (msg);			    	  			    	  
				       }
					} catch (Exception e) {   
						progressDialogWait.cancel();
		    			Message msg=new Message();
		    			msg.what=0;	 	    			
		    			handler.sendMessage (msg);					
					} 
				   }	 
				};
				thread.start();     
		}

		public void copyFile(File src,File tar) throws Exception
	    {                 
	          count = 0;
	          InputStream is=new FileInputStream(src);
	          OutputStream op=new FileOutputStream(tar);
	          bis=new BufferedInputStream(is);
	          bos=new BufferedOutputStream(op);
	          final byte[] bt=new byte[8192];
	          //filesize=FileSrc.length();
	          len = bis.read(bt);     
	          Thread thread=new Thread(){  
					public void run(){  
	         		try {  
			            while( len!=-1)  {
	           				bos.write(bt,0,len);
	           				count =count+len;
	           				len = bis.read(bt);            			
	            			xh_count =(int)(count*100/filesize);            				
	            			Thread.sleep(1);          		
						}   
						bis.close();
	          			bos.close(); 
	          			progressDialog.cancel();
	          			UpdateFile();
					} catch (Exception e) {  
						// TODO: handle exception  
	 					progressDialog.cancel(); 
					} 
			  }		             
			};
			thread.start();        	
	  	}
	  
		public void BarProc() 
		{
			if(FileSrc==null) FileSrc =  new File(FileSrcPath);
			if(FileSrc==null) Log.d(" ","can't new the file");                					
			if( FileSrc.exists()) {   
	     		Unzip(FileSrcPath);
		    	/*long sdSpraeSize=readSDCard();
		    	filesize=FileSrc.length();
		    		if( sdSpraeSize >(filesize+1024*12024*40) )  {
		         		xh_count = 0;      
		    			progressDialog.setTitle(R.string.bartitle);
		    			progressDialog.setMessage("copy update file to system device");     
		    			progressDialog.setProgress(xh_count); 
		    			progressDialog.show();   					
		    			try	{				
		    				copyFile(FileSrc,FileTar);
		    			} catch (Exception e) { 		
		    				e.printStackTrace();
		    			}	    	 	
		    		} else {
		    			Toast.makeText(SystemUpdate.this, "sdcard size is not enough!",2).show();
		    	}*/
	     	} else {
	     		Log.d(" ","can't find the file");
	     		new AlertDialog.Builder(UpdateService.this)
	     		.setTitle(R.string.nofiletitle)
	     		.setMessage(R.string.nofile)
	     		.setPositiveButton(R.string.BtnYes, new DialogInterface.OnClickListener() {
	     			public void onClick(DialogInterface dialog, int whichButton) {                           
	     				//checkQuit(true);
	     			}
	     		})           
	     		.show();     		
			}	
		}
		
	    private long readSDCard() {
	    	long sdSpraeSize=0;
	    	String state = Environment.getExternalStorageState();
	    	if(Environment.MEDIA_MOUNTED.equals(state)) {
	    		File sdcardDir = Environment.getExternalStorageDirectory();
	    		StatFs sf = new StatFs(sdcardDir.getPath());
	    		long blockSize = sf.getBlockSize();
	    		long blockCount = sf.getBlockCount(); 
	    		long availCount = sf.getAvailableBlocks();
	    		sdSpraeSize = availCount*blockSize;
	    		Log.d("", "block size:"+ blockSize+",block size:"+ blockCount+",total size:"+blockSize*blockCount/1024+"KB");
	    		Log.d("", "used block size:"+ availCount+",sprae size:"+ availCount*blockSize/1024+"KB"); 
	    	}
	    	return sdSpraeSize;
	    }    
	}
}
