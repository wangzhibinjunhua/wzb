package com.updateservice;
public class Firmware { 
		/*private int num=1; 
		private String name="juntai"; 
		private String desc="juntai ota"; 
		private String md5="c61ce360a734c018685fbf5e1662c0fd"; 
		private Integer size=8912;
		private String downloadurl="http://www.day-wish.com/Upload/DownFiles/update.zip";
		private String version="1.1";
		private String lastdownlaod="-1";

		public String getlastdownlaod() { 
			return this.lastdownlaod; 
		} 
		public void setlastdownlaod(String lastdownlaod) { 
			this.lastdownlaod = lastdownlaod; 
		}
		
		public String getversion() { 
			return this.version; 
		} 
		public void setversion(String version) { 
			this.version = version; 
		}
		public String getdesc() { 
			return this.desc; 
		} 
		public void setdesc(String desc) { 
			this.desc = desc; 
		}		
		public String getmd5() { 
			return this.md5; 
		} 
		public void setmd5(String md5) { 
			this.md5 = md5; 
		} 
		public String getdownloadurl() { 
			return this.downloadurl; 
		} 
		public void setdownloadurl(String downloadurl) { 
			this.downloadurl = downloadurl; 
		} 
		public Integer getsize() { 
			return this.size; 
		} 
		public void setsize(Integer size) { 
			this.size = size; 
		} */
	private String md5="c61ce360a734c018685fbf5e1662c0fd"; 
	private Integer size=8912;
	private String id ="";
	private String dirname = "";
	private String version="";
	private String desc=""; 
	private int versionmask =0;
	private int downid= 0; //0 no download 1 have download
	
	public String getdesc() { 
		return this.desc; 
	} 
	public void setdesc(String desc) { 
		this.desc = desc; 
	}
	
	public String getdir() { 
		return this.dirname; 
	} 
	public void setdir(String dir) { 
		this.dirname = dir; 
	}	
	
	public String getmd5() { 
		return this.md5; 
	} 
	public void setmd5(String md5) { 
		this.md5 = md5; 
	} 
	
	public String getid() { 
		return this.id; 
	} 
	public void setid(String id) { 
		this.id = id; 
	} 
	
	public Integer getsize() { 
		return this.size; 
	} 
	public void setsize(Integer size) { 
		this.size = size; 
	}
	
	public Integer getmask() { 
		return this.versionmask; 
	} 
	public void setmask(Integer mask) { 
		this.versionmask = mask; 
	}
	
	public Integer getdownid() { 
		return this.downid; 
	} 
	public void setdownid(Integer downid) { 
		this.downid = downid; 
	}
	
	public String getversion() { 
		return this.version; 
	} 
	public void setversion(String version) { 
		this.version = version; 
	}
}

