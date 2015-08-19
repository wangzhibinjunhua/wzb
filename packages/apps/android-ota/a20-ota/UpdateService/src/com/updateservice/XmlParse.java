package com.updateservice;

import com.updateservice.Firmware;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import android.util.Xml;

public class XmlParse {	
	String TAG = "XmlParse";
	public List<Firmware> readXML(InputStream inStream) { 
		XmlPullParser parser = Xml.newPullParser(); 
		try { 
			parser.setInput(inStream, "UTF-8"); 
			int eventType = parser.getEventType(); 
			Firmware currentfirmware = null; 
			List<Firmware> firmwareupdate = null; 
			while (eventType != XmlPullParser.END_DOCUMENT) { 
				switch (eventType) { 
					case XmlPullParser.START_DOCUMENT:
						//Log.d(TAG,"XmlPullParser.START_DOCUMENT");
						firmwareupdate = new ArrayList<Firmware>(); 
						break; 
					case XmlPullParser.START_TAG:
						String name = parser.getName(); 
						//Log.d(TAG,"XmlPullParser.START_TAG"+name);
						if (name.equalsIgnoreCase("firmware")) { 
							currentfirmware = new Firmware();
							currentfirmware.setid(parser.getAttributeValue(null, "id")); 
							//currentfirmware.setId(new Integer(parser.getAttributeValue(null, "id"))); 
						} else if (currentfirmware != null) { 
							if (name.equalsIgnoreCase("md5")){ 
								currentfirmware.setmd5(parser.nextText());
							} 
							else if (name.equalsIgnoreCase("size")) { 
								currentfirmware.setsize(new Integer(parser.nextText())); 
							} 
							else if (name.equalsIgnoreCase("dirname"))
							{
								currentfirmware.setdir(parser.nextText());
							}
							else if (name.equalsIgnoreCase("version"))
							{
								currentfirmware.setversion(parser.nextText());						
							} 
							else if (name.equalsIgnoreCase("versionmask"))
							{
								currentfirmware.setmask(new Integer(parser.nextText())); 
							}
							else if (name.equalsIgnoreCase("desc"))
							{
								currentfirmware.setdesc(parser.nextText());
							} 
						}	 
						break; 
					case XmlPullParser.END_TAG:
						if (parser.getName().equalsIgnoreCase("firmware") && currentfirmware != null) { 
							firmwareupdate.add(currentfirmware); 
							currentfirmware = null; 
						} 
						break; 
				} 
				eventType = parser.next(); 
			} 
			inStream.close(); 
			return firmwareupdate; 
		} catch (Exception e) { 
			e.printStackTrace(); 
		} 
		return null; 
	} 
	
	public String writeXML(List<Firmware> firmwareupdate, Writer writer){ 
	    XmlSerializer serializer = Xml.newSerializer(); 
	    try { 
	        serializer.setOutput(writer); 
	        serializer.startDocument("UTF-8", true); 	      
	        serializer.startTag("", "firmwareupdate"); 
	        for (Firmware firmware : firmwareupdate){ 
	            serializer.startTag("", "firmware"); 
	            serializer.attribute("", "id", firmware.getid()); 
	            serializer.startTag("", "md5"); 
	            serializer.text(firmware.getmd5()); 
	            serializer.endTag("", "md5"); 
	            serializer.startTag("", "size"); 
	            serializer.text(firmware.getsize().toString()); 
	            serializer.endTag("", "size"); 
	            serializer.startTag("", "dirname"); 
	            serializer.text(firmware.getdir()); 
	            serializer.endTag("", "dirname"); 
	            serializer.startTag("", "versionmask"); 
	            serializer.text(firmware.getmask().toString()); 
	            serializer.endTag("", "versionmask"); 	           
	            serializer.startTag("", "version"); 
	            serializer.text(firmware.getversion()); 
	            serializer.endTag("", "version");
	            serializer.startTag("", "desc"); 
	            serializer.text(firmware.getdesc()); 
	            serializer.endTag("", "desc"); 
	            serializer.startTag("", "downid"); 
	            serializer.text(firmware.getdownid().toString()); 
	            serializer.endTag("", "downid");
	            serializer.endTag("", "firmware"); 
	        } 
	        serializer.endTag("", "firmwareupdate"); 
	        serializer.endDocument(); 
	        return writer.toString(); 
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    } 
	    return null; 
	}
	
	public void xmlWriteToFile(List<Firmware> firmwareupdate,String path) {		
		File xmlFile = new File(path); 
		try {
			FileOutputStream outStream = new FileOutputStream(xmlFile); 
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(outStream, "UTF-8"); 
			BufferedWriter writer = new BufferedWriter(outStreamWriter); 
			writeXML(firmwareupdate, writer); 
			writer.flush(); 
			writer.close(); 
		}catch (Exception e) {   
	    	e.printStackTrace();   
	    }
	}
	
	
	//add by lrx 
	public List<Firmware> ReadWriteXml(String path){	
		List<Firmware> firmwareupdate = null; 
		File fl = new File(path); 
		if(fl==null) return null;
		InputStream xmlis;
		if(fl.exists() ) { 
			try {
				xmlis = new FileInputStream(fl);
				Log.d(TAG,"FileInputStream");
				firmwareupdate = readXML(xmlis);
				
			} catch (FileNotFoundException e) {			
				firmwareupdate = null;
			}
		}	
		return firmwareupdate;
	}
	
	public void AddfirwareToFile(String path,Firmware af) {
		List<Firmware> firmwareupdate = null;
		firmwareupdate = ReadWriteXml(path);
		if(firmwareupdate==null)
		{
			firmwareupdate = new ArrayList<Firmware>(); 
			firmwareupdate.add(af);
		}
		else
		{
			firmwareupdate.add(af);
		}
		xmlWriteToFile(firmwareupdate,path);
		
	}

	
	//find have update in fir1
	public List<Firmware> CompareXml(List<Firmware> fir1,List<Firmware> fir2,int mask,String version)
	{
		int i;
		Firmware firmware=null;
		List<Firmware> firmwareupdate = null; 
		boolean find = false;
		
		if(fir1==null)
		{
			Log.d(TAG,"fir1 is Null");
			return null;
		}
		
		for(i=0; i<fir1.size(); i++)
		{
			firmware = fir1.get(i);
			if(firmware==null)
			{
				Log.d(TAG,"firmware is Null");
				continue;
			}
			if((firmware.getmask()&mask)==0)
			{
				Log.d(TAG,"mask = 0x"+firmware.getmask());
				continue;
			}
			int a = Integer.parseInt(firmware.getversion());
			Log.d(TAG,"a="+a);
			if(a<=Integer.parseInt(version))
			{
				continue;
			}
			//find firware in fir1;
			
			find = false;
			if(fir2!=null)
			{
				for(int j =0; j<fir2.size(); j++)
				{
					Firmware oldfir = fir2.get(j);
					//Log.d(TAG,"fir2 "+j+"name="+oldfir.getdir());
					if(oldfir.getid().equalsIgnoreCase(firmware.getid()))
					{
						find =true;
						break;
					}
				}
			}
			if(find==false)
			{
				if(firmwareupdate==null)
				{
					firmwareupdate = new ArrayList<Firmware>(); 
				}
				if(firmwareupdate!=null)
				{
					Firmware temp = new Firmware();
					temp = firmware;
					firmwareupdate.add(firmware);
					temp = null;
				}
			}
			
		}
			
		return firmwareupdate;
	}
	//if still have any download
	public Firmware XmlStillHaveDown(List<Firmware> fir)
	{
		Firmware firmware=null;
		if(fir==null)
		return null;
		
		for(int i=0; i<fir.size(); i++)
		{
			firmware = fir.get(i);
			if(firmware.getdownid()==0) return firmware;
		}
		return null;
	}
	
	//set download state
	public void XmlSetDownLoad(List<Firmware> fir,Firmware firmware)
	{
		if(fir==null) return ;
		
		for(int i=0; i<fir.size(); i++)
		{
			Firmware temp = fir.get(i);
			if(firmware.getid()==temp.getid())
			{
				temp.setdownid(1);
				return ;
			}
		}
		
	}
	
	public List<Firmware> MergeXml(List<Firmware> fir1,List<Firmware> fir2)
	{
		List<Firmware> firmwareupdate = null; 
		
		if(fir1==null) return fir2;
		if(fir2==null) return fir1;
		if(firmwareupdate==null)
		{
			firmwareupdate = new ArrayList<Firmware>(); 
		}
		for(int i=0; i<fir1.size(); i++)
		{
			Firmware firmware=fir1.get(i);
			if(firmwareupdate!=null)
			{
				Firmware temp = new Firmware();
				temp = firmware;
				firmwareupdate.add(firmware);
				temp = null;
			}
		}
		
		for(int i=0; i<fir2.size(); i++)
		{
			Firmware firmware=fir2.get(i);
			if(firmwareupdate!=null)
			{
				Firmware temp = new Firmware();
				temp = firmware;
				firmwareupdate.add(firmware);
				temp = null;
			}
		}
		
		return firmwareupdate;
	}
	
}
