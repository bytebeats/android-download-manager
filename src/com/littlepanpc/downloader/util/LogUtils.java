
package com.littlepanpc.downloader.util;

import android.util.Log;

/**
 * @class: LogUtils
 * @Description: to print logging
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-5-19 下午6:01:11
 * @since: 1.0.0
 *
 */
public class LogUtils {

	private final static boolean PRINTABLE = true;
	
	public static void i(String tag, String msg) {
		if(PRINTABLE) {
			Log.i(tag, msg);
		}
	}
	
	public static void i(String tag, Object msg) {
		if(PRINTABLE) {
			Log.i(tag, msg.toString());
		}
	}
	
	public static void w(String tag, String msg) {
		if(PRINTABLE) {
			Log.w(tag, msg);
		}
	}

	public static void w(String tag, Object msg) {
		if(PRINTABLE) {
			Log.w(tag, msg.toString());
		}
	}
	
	public static void e(String tag, String msg) {
		if(PRINTABLE) {
			Log.e(tag, msg);
		}
	}

	public static void e(String tag, Object msg) {
		if(PRINTABLE) {
			Log.e(tag, msg.toString());
		}
	}
	
	public static void d(String tag, String msg) {
		if(PRINTABLE) {
			Log.d(tag, msg);
		}
	}
	
	public static void d(String tag, Object msg) {
		if(PRINTABLE) {
			Log.d(tag, msg.toString());
		}
	}
	
	public static void v(String tag, String msg) {
		if(PRINTABLE) {
			Log.v(tag, msg);
		}
	}
	
	public static void v(String tag, Object msg) {
		if(PRINTABLE) {
			Log.v(tag, msg.toString());
		}
	}
}
