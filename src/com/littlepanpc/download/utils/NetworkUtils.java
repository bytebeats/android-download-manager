package com.littlepanpc.download.utils;

import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
/**
 * @class: NetworkUtils
 * @Description: TODO
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-20 上午11:07:39
 * @since: 1.0.0
 *
 */
public class NetworkUtils {
	private static final String TAG = NetworkUtils.class.getName();

	public static boolean isNetworkAvailable(Context context) {
		Log.i(TAG, "isNetworkAvailable");
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public static String getFileNameFromUrl(String url) {
		Log.i(TAG, "isNetworkAvailable");
		// 通过 ‘？’ 和 ‘/’ 判断文件名
		int index = url.lastIndexOf('?');
		String filename;
		if (index > 1) {
			filename = url.substring(url.lastIndexOf('/') + 1, index);
		} else {
			filename = url.substring(url.lastIndexOf('/') + 1);
		}

		if (filename == null || "".equals(filename.trim())) {// 如果获取不到文件名称
			filename = UUID.randomUUID() + ".apk";// 默认取一个文件名
		}
		return filename;
	}
}
