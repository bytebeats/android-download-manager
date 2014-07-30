package com.littlepanpc.downloader.util;

import android.os.Environment;
import android.os.StatFs;

public class StorageUtils {
	public static final String DOWNLOAD_DIRECTORY = Environment
			.getExternalStorageDirectory() + "/downloads";
	public static final String DOWNLOAD_DIRECTORY_TEMP = Environment
			.getExternalStorageDirectory() + "/downloads/tmps";

	public static long getAvailableStorage() {

		String storageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		try {
			StatFs stat = new StatFs(storageDirectory);
			long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat
					.getBlockSize());
			return avaliableSize;
		} catch (RuntimeException ex) {
			return 0;
		}
	}

}
