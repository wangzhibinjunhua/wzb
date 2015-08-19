package com.tvtelecontroller.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tvtelecontroller.BaseApplication;

import android.annotation.SuppressLint;
import android.util.Log;

public class Log4L {
	
	public static void v(String tag, String msg) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.v(tag, msg);
		}
	}

	public static void d(String tag, String msg) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.d(tag, msg);
		}
	}

	public static void i(String tag, String msg) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.i(tag, msg);
		}
	}

	public static void w(String tag, String msg) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.w(tag, msg);
		}
	}

	public static void e(String tag, String msg) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.e(tag, msg);
		}
	}

	public static void e(String tag, String msg, Throwable e) {

		if (BaseApplication.isOpenLog()) {
			android.util.Log.e(tag, msg, e);
		}
	}
	
	public static void print(Object object){
		
		if (BaseApplication.isOpenLog()) {
			System.out.println(object);
		}
		
	}

	/**
	 * 打印固定格式的时间
	 * @param tag
	 * @param time
	 */
	@SuppressLint("SimpleDateFormat")
	public static void printTime(String tag, long time) {

		if (BaseApplication.isOpenLog()) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(time);
			String format = "yy-MM-dd HH:mm:ss";

			DateFormat datefFormat = new SimpleDateFormat(format);
			Log4L.v(tag, datefFormat.format(calendar.getTime()));
		}
	}
	
	public static void print(String tag, long time){
		if (BaseApplication.isOpenLog()) {
			Log4L.v(tag, String.valueOf(time));
		}
	}

}
