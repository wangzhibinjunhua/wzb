package anti.drop.device.utils;

import anti.drop.device.BaseApplication;

/**
 * 带有开关的Log
 * @author LuoYong
 */
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

}
