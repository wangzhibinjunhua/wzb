package com.eken.rcservice;


public class MyLib{

	public MyLib(){

	}

	static{
		System.loadLibrary("writeevent");
	}
	public native int writeevent(int[] ints);
	public native int openvmouse();
	public native int closevmouse();
	public native int writegsensor(float[] fa);
}
