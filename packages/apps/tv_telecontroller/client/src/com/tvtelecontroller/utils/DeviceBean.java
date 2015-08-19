package com.tvtelecontroller.utils;

public class DeviceBean {
	
	public int _id;
	public String name;
	public String connIp;
	public String isConn;//0表示连接，1表示未连接
	
	public DeviceBean(){}
	
	public DeviceBean(String name,String connIp,String isConn){
		this.name = name;
		this.connIp = connIp;
		this.isConn = isConn;
	}
	
	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}



	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getConnIp() {
		return connIp;
	}
	public void setConnIp(String connIp) {
		this.connIp = connIp;
	}

	public String isConn() {
		return isConn;
	}

	public void setConn(String isConn) {
		this.isConn = isConn;
	}

}
