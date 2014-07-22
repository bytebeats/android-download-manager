package com.littlepanpc.downloader.db;

import com.littlepanpc.downloader.util.LogUtils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @class: DownloadDBHelper
 * @Description: create or update database table---\"downloads.db\"
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-5-19 下午6:07:39
 * @since: 1.0.0
 *
 */
public class DownloadDBHelper extends SQLiteOpenHelper {
	private static final String TAG = DownloadDBHelper.class.getSimpleName();

	public static final String DB_NAME = "downloads.db";
	public static final String DB_TABLE_NAME = "download_task";
	public static final int DB_VERSION = 1;

	private static DownloadDBHelper instance;

	/**
	 * @Title: getInstance
	 * @Description: concurrent singleTon---double checking lock pattern
	 * @param context
	 * @return
	 * @return DownloadDBHelper
	 * @throws 
	 */
	public static DownloadDBHelper getInstance(Context context) {
		if (instance == null) {
			synchronized (DownloadDBHelper.class) {
				if (instance == null) {
					instance = new DownloadDBHelper(context);
				}
			}
		}
		LogUtils.i(TAG, "getInstance---"+instance);
		return instance;
	}

	/**
	 * <p>Title: </p>
	 * <p>Description: Constructor</p>
	 *
	 * @param context
	 */
	private DownloadDBHelper(Context context) {
		// TODO Auto-generated constructor stub
		super(context, DB_NAME, null, DB_VERSION);
		LogUtils.i(TAG, "DownloadDBHelper constructor");
	}
	
	/* (non-Javadoc)
	 * <p>Title: onCreate</p>
	 * <p>Description: create database table</p>
	 * @param db
	 * @see android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite.SQLiteDatabase)
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		LogUtils.i(TAG, "onCreate start");
		String createSQL = "create table " + DB_TABLE_NAME + "("
				+ "_id integer primary key autoincrement, "
				+ "url text unique, " + "parent_path text, "
				+ "file_name text, " + "downloaded_size integer, "
				+ "total_size integer, " + "status integer)";
		LogUtils.i(TAG, "createsql---"+createSQL);
		LogUtils.i(TAG, "onCreate before execute");
		db.execSQL(createSQL);
		LogUtils.i(TAG, "onCreate after execute");
		LogUtils.i(TAG, "onCreate finish");
	}

	/* (non-Javadoc)
	 * <p>Title: onUpgrade</p>
	 * <p>Description: update database table</p>
	 * @param db
	 * @param oldVersion
	 * @param newVersion
	 * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase, int, int)
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		String dropSQL = "drop table if exists " + DB_TABLE_NAME;
		LogUtils.i(TAG, "dropsql---"+dropSQL);
		LogUtils.i(TAG, "onUpgrade before execute");
		db.execSQL(dropSQL);
		LogUtils.i(TAG, "onUpgrade after execute");
		onCreate(db);
		LogUtils.i(TAG, "onUpgrade finish");
	}

}
