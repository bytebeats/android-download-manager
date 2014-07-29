package com.littlepanpc.downloader.util;

import java.util.UUID;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
	private static final String TAG = NetworkUtils.class.getSimpleName();
	
	public static boolean isNetworkAvailable(Context context) {
		LogUtils.i(TAG, "isNetworkAvailable--- start");
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			LogUtils.i(TAG, "isNetworkAvailable---1");
			return false;
		} else {
			LogUtils.i(TAG, "isNetworkAvailable---2");
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			LogUtils.i(TAG, "isNetworkAvailable---3");
			if (info != null) {
				LogUtils.i(TAG, "isNetworkAvailable---4");
				for (int i = 0; i < info.length; i++) {
					LogUtils.i(TAG, "isNetworkAvailable---5");
					if (info[i].getState() == NetworkInfo.State.CONNECTED
							|| info[i].getState() == NetworkInfo.State.CONNECTING) {
						LogUtils.i(TAG, "isNetworkAvailable---true");
						return true;
					}
				}
			}
		}
		LogUtils.i(TAG, "isNetworkAvailable---end");
		return false;
	}
	
	public static String getFileNameFromUrl(String url) {
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
