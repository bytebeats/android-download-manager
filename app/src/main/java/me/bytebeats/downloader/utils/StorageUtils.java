package me.bytebeats.downloader.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;

public class StorageUtils {
	private static final String TAG = StorageUtils.class.getName();
	private static final String SDCARD_ROOT = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/";
	public static final String FILE_ROOT = SDCARD_ROOT + "MyDownloads/";
	public static final String FILE_ROOT_TEMP = SDCARD_ROOT
			+ "MyDownloads/tmp/";

	private static final long LOW_STORAGE_THRESHOLD = 1024 * 1024 * 10;// (10M)

	public static boolean isSdCardMounted() {
		Log.i(TAG, "isSdCardMounted");
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		}
		return false;
	}

	public static long getAvailableStorage() {
		Log.i(TAG, "getAvailableStorage");
		String storageDirectory = null;
		storageDirectory = Environment.getExternalStorageDirectory().toString();

		try {
			StatFs stat = new StatFs(storageDirectory);
			@SuppressWarnings("deprecation")
			long avaliableSize = ((long) stat.getAvailableBlocks() * (long) stat
					.getBlockSize());
			return avaliableSize;
		} catch (RuntimeException ex) {
			return 0;
		}
	}

	public static boolean isStorageAdequate() {
		Log.i(TAG, "isStorageAdequate");
		if (getAvailableStorage() < LOW_STORAGE_THRESHOLD) {
			return false;
		}
		return true;
	}

	public static void mkdir() throws IOException {
		Log.i(TAG, "mkdir");
		File file = new File(FILE_ROOT);
		if (!file.exists() || !file.isDirectory()) {
			file.mkdir();
		}
		File tmpFile = new File(FILE_ROOT_TEMP);
		if (!tmpFile.exists() || !tmpFile.isDirectory()) {
			tmpFile.mkdir();
		}
	}

	public static Bitmap getLoacalBitmap(String url) {
		Log.i(TAG, "getLoacalBitmap");
		try {
			FileInputStream fis = new FileInputStream(url);
			return BitmapFactory.decodeStream(fis); // /把流转化为Bitmap图片

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String size(long size) {
		Log.i(TAG, "size(long size)");
		if (size / (1024 * 1024) > 0) {
			float tmpSize = (float) (size) / (float) (1024 * 1024);
			DecimalFormat df = new DecimalFormat("#.##");
			return "" + df.format(tmpSize) + "MB";
		} else if (size / 1024 > 0) {
			return "" + (size / (1024)) + "KB";
		} else
			return "" + size + "B";
	}

	public static void installAPK(Context context, final String url) {
		Log.i(TAG, "installAPK");
		Intent intent = new Intent(Intent.ACTION_VIEW);
		String fileName = FILE_ROOT + NetworkUtils.getFileNameFromUrl(url);
		intent.setDataAndType(Uri.fromFile(new File(fileName)),
				"application/vnd.android.package-archive");
		intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
		intent.setClassName("com.android.packageinstaller",
				"com.android.packageinstaller.PackageInstallerActivity");
		context.startActivity(intent);
	}

	public static boolean delete(File path) {
		Log.i(TAG, "delete(File path)");
		boolean result = true;
		if (path.exists()) {
			if (path.isDirectory()) {
				for (File child : path.listFiles()) {
					result &= delete(child);
				}
				result &= path.delete(); // Delete empty directory.
			}
			if (path.isFile()) {
				result &= path.delete();
			}
			if (!result) {
				Log.i(TAG, "Delete failed;");
			}
			return result;
		} else {
			Log.i(TAG, "File does not exist.");
			return false;
		}
	}
}
