package com.littlepanpc.downloader.db;

import com.littlepanpc.downloader.meta.DownloadInfo;
import com.littlepanpc.downloader.task.AsyncDownloadTask.TaskStatus;
import com.littlepanpc.downloader.util.LogUtils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * @class: DownloadDBOperator
 * @Description: database manipulation
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-5-19 下午6:53:21
 * @since: 1.0.0
 * 
 */
public class DownloadDBOperator {
	private static final String TAG = DownloadDBOperator.class.getSimpleName();
	private DownloadDBHelper helper;

	public DownloadDBOperator(Context context) {
		LogUtils.i(TAG, "DownloadDBOperator constructor");
		helper = DownloadDBHelper.getInstance(context);
	}

	/**
	 * @Title: insert
	 * @Description: TODO
	 * @param url
	 * @param fileName
	 * @return void
	 * @throws
	 */
	public void insert(String url, String fileName) {
		LogUtils.i(TAG, "insert start");
		if (!exists(url)) {
			LogUtils.i(TAG, "not exist");
			SQLiteDatabase sd = helper.getWritableDatabase();
			String insertSQL = "insert into " + DownloadDBHelper.DB_TABLE_NAME
					+ " (url, file_name, status) values('" + url + "', '"
					+ fileName + "', " + TaskStatus.WAITING.getStatus() + ")";
			LogUtils.i(TAG, "insert before execute");
			LogUtils.i(TAG, "insert sql is---"+insertSQL);
			sd.execSQL(insertSQL);
			LogUtils.i(TAG, "insert after execute");
			sd.close();
			LogUtils.i(TAG, "insert close");
		}
	}

	/**
	 * @Title: updateProgress
	 * @Description: TODO
	 * @param url
	 * @param downloaded_size
	 * @param total_size
	 * @return void
	 * @throws
	 */
	public void updateProgress(String url, long downloaded_size, long total_size) {
		LogUtils.i(TAG, "");
		SQLiteDatabase sd = helper.getWritableDatabase();
		String updateSQL = "update " + DownloadDBHelper.DB_TABLE_NAME
				+ " set downloaded_size=?, total_size=?, status=? where url=?";
		LogUtils.i(TAG, "");
		sd.execSQL(updateSQL, new String[] { downloaded_size + "",
				total_size + "", TaskStatus.DOWNLOADING.getStatus() + "", url });
		LogUtils.i(TAG, "");
		sd.close();
		LogUtils.i(TAG, "");
	}

	/**
	 * @Title: updateTaskStatus
	 * @Description: TODO
	 * @param url
	 * @param status
	 * @return void
	 * @throws
	 */
	public void updateTaskStatus(String url, int status) {
		LogUtils.i(TAG, "");
		SQLiteDatabase sd = helper.getWritableDatabase();
		String updateStatusSQL = "update " + DownloadDBHelper.DB_TABLE_NAME
				+ " set status=" + status;
		LogUtils.i(TAG, "");
		sd.execSQL(updateStatusSQL);
		LogUtils.i(TAG, "");
		sd.close();
		LogUtils.i(TAG, "");
	}

	/**
	 * @Title: erase
	 * @Description: TODO
	 * @param url
	 * @return void
	 * @throws
	 */
	public void erase(String url) {
		LogUtils.i(TAG, "");
		SQLiteDatabase sd = helper.getWritableDatabase();
		String eraseSQL = "delete from " + DownloadDBHelper.DB_TABLE_NAME
				+ " where url=" + url;
		LogUtils.i(TAG, "");
		sd.execSQL(eraseSQL);
		LogUtils.i(TAG, "");
		sd.close();
		LogUtils.i(TAG, "");
	}

	/**
	 * @Title: eraseCompletely
	 * @Description: TODO
	 * @return void
	 * @throws
	 */
	public void eraseCompletely() {
		LogUtils.i(TAG, "");
		SQLiteDatabase sd = helper.getWritableDatabase();
		String eraseCompleteSQL = "delete from "
				+ DownloadDBHelper.DB_TABLE_NAME+" wheer status !="+TaskStatus.FINISHED.getStatus();
		LogUtils.i(TAG, "");
		sd.execSQL(eraseCompleteSQL);
		LogUtils.i(TAG, "");
		sd.close();
		LogUtils.i(TAG, "");
	}

	/**
	 * @Title: getTaskInfo
	 * @Description: TODO
	 * @param url
	 * @return
	 * @return DownloadInfo
	 * @throws
	 */
	public DownloadInfo getTaskInfo(String url) {
		LogUtils.i(TAG, "");
		DownloadInfo info = null;
		SQLiteDatabase sd = helper.getReadableDatabase();
		String querySQL = "select name, parent_path, downloaded_size, total_size, status from "
				+ DownloadDBHelper.DB_TABLE_NAME + " where url=" + url;
		LogUtils.i(TAG, "");
		Cursor cursor = sd.rawQuery(querySQL, null);
		LogUtils.i(TAG, "");
		if (cursor != null && cursor.moveToFirst()) {
			LogUtils.i(TAG, "");
			info = new DownloadInfo();
			info.name = cursor.getString(0);
			info.parentPath = cursor.getString(1);
			info.downloadedSize = cursor.getInt(2);
			info.totalSize = cursor.getInt(3);
			info.status = cursor.getInt(4);
			cursor.close();
		}
		LogUtils.i(TAG, "");
		sd.close();
		LogUtils.i(TAG, "");
		return info;
	}

	/**
	 * @Title: exists
	 * @Description: TODO
	 * @param url
	 * @return
	 * @return boolean
	 * @throws
	 */
	public boolean exists(String url) {
		LogUtils.i(TAG, "exists start");
		SQLiteDatabase sd = helper.getWritableDatabase();
		String sql = "select * from " + DownloadDBHelper.DB_TABLE_NAME
				+ " where url=?";
		LogUtils.i(TAG, "exists before execute");
		Cursor cursor = sd.rawQuery(sql, new String[] { url });
		boolean exist = false;
		LogUtils.i(TAG, "exists after execute");
		if (cursor != null && cursor.moveToFirst()) {
			LogUtils.i(TAG, "exist true");
			exist = true;
		}
		LogUtils.i(TAG, "exists finish");
		cursor.close();
		return exist;
	}
}
