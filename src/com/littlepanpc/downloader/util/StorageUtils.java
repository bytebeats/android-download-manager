package com.littlepanpc.downloader.util;

import android.os.Environment;
import android.os.StatFs;
/**
 * @class: StorageUtils
 * @Description: Storage Utility.
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-7-30 上午10:33:07
 * @since: 1.0.0
 *
 */
public class StorageUtils {
	/**
	 * @Field String DOWNLOAD_DIRECTORY: store directory.
	 */
	public static final String DOWNLOAD_DIRECTORY = Environment
			.getExternalStorageDirectory() + "/downloads";
	/**
	 * @Field String DOWNLOAD_DIRECTORY_TEMP: temporary store directory.
	 */
	public static final String DOWNLOAD_DIRECTORY_TEMP = Environment
			.getExternalStorageDirectory() + "/downloads/tmps";
	/**
	 * @Title: getAvailableStorage
	 * @Description: obtain available storage
	 * @return long
	 * @throws 
	 */
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
