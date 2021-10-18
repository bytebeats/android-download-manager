package me.bytebeats.downloader.utils;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.text.TextUtils;
import android.util.Log;
/**
 * @class: ConfigUtils
 * @Description: TODO
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-20 上午11:06:04
 * @since: 1.0.0
 *
 */
public class ConfigUtils {
	private static final String TAG = ConfigUtils.class.getName();
	public static final String PREFERENCE_NAME = "android-download-manager";

	public static void clear(Context context) {
		Log.i(TAG, "clear");
		// Take care of commit()!!!
		getPreferences(context).edit().clear().commit();
	}

	public static SharedPreferences getPreferences(Context context) {
		// Log.i(TAG, "getPreferences");
        return context.getSharedPreferences(PREFERENCE_NAME,
				Context.MODE_PRIVATE);
	}

	public static String getString(Context context, String key) {
		// Log.i(TAG, "getString");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getString(key, "");
		else
			return "";
	}

	public static void setString(Context context, String key, String value) {
		// Log.i(TAG, "setString");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putString(key, value);
			editor.commit();
		}
	}

	public static final int URL_COUNT = 3;
	public static final String KEY_URL = "url";

	public static void storeURL(Context context, int index, String url) {
		// Log.i(TAG, "storeURL");
		setString(context, KEY_URL + index, url);
	}

	public static void clearURL(Context context, int index) {
		// Log.i(TAG, "clearURL");
		setString(context, KEY_URL + index, "");
	}

	public static String getURL(Context context, int index) {
		// Log.i(TAG, "getURL");
		return getString(context, KEY_URL + index);
	}

	public static List<String> getURLArray(Context context) {
		// Log.i(TAG, "getURLArray");
		List<String> urlList = new ArrayList<String>();
		for (int i = 0; i < URL_COUNT; i++) {
			if (!TextUtils.isEmpty(getURL(context, i))) {
				urlList.add(getString(context, KEY_URL + i));
			}
		}
		return urlList;
	}

	public static final String KEY_RX_WIFI = "rx_wifi";
	public static final String KEY_TX_WIFI = "tx_wifi";
	public static final String KEY_RX_MOBILE = "tx_mobile";
	public static final String KEY_TX_MOBILE = "tx_mobile";
	public static final String KEY_Network_Operator_Name = "operator_name";

	public static int getInt(Context context, String key) {
		// Log.i(TAG, "getInt");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getInt(key, 0);
		else
			return 0;
	}

	public static void setInt(Context context, String key, int value) {
		// Log.i(TAG, "setInt");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putInt(key, value);
			editor.commit();
		}
	}

	public static long getLong(Context context, String key) {
		// Log.i(TAG, "getLong");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null)
			return preferences.getLong(key, 0L);
		else
			return 0L;
	}

	public static void setLong(Context context, String key, long value) {
		// Log.i(TAG, "setLong");
		SharedPreferences preferences = getPreferences(context);
		if (preferences != null) {
			Editor editor = preferences.edit();
			editor.putLong(key, value);
			editor.commit();
		}
	}

	public static void addLong(Context context, String key, long value) {
		// Log.i(TAG, "addLong");
		setLong(context, key, getLong(context, key) + value);
	}
}
