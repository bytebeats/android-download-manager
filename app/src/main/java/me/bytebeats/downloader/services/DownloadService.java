package me.bytebeats.downloader.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import me.bytebeats.downloader.aidl.IDownloadService;

/**
 * @class: DownloadService
 * @Description: Service to manage download tasks
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-19 上午9:08:44
 * @since: 1.0.0
 *
 */
public class DownloadService extends Service {
	private static final String TAG = DownloadService.class.getName();
	private DownloadManager mDownloadManager;

	@Override
	public IBinder onBind(Intent intent) {
		Log.i(TAG, "onBind");
		return new DownloadServiceBinder();
	}

	@Override
	public void onCreate() {
		Log.i(TAG, "onCreate");
		super.onCreate();
		mDownloadManager = new DownloadManager(this);
	}

	public class DownloadServiceBinder extends IDownloadService.Stub {
		private final String TAG2 = DownloadServiceBinder.class.getName();

		@Override
		public void startManage() throws RemoteException {
			Log.i(TAG2, "startManage");
			mDownloadManager.startManage();
		}

		@Override
		public void addTask(String url) throws RemoteException {
			Log.i(TAG2, "addTask");
			mDownloadManager.addTask(url);
		}

		@Override
		public void pauseTask(String url) throws RemoteException {
			Log.i(TAG2, "pauseTask");
			mDownloadManager.pauseTask(url);
		}

		@Override
		public void deleteTask(String url) throws RemoteException {
			Log.i(TAG2, "deleteTask");
			mDownloadManager.deleteTask(url);
		}

		@Override
		public void continueTask(String url) throws RemoteException {
			Log.i(TAG2, "continueTask");
			mDownloadManager.continueTask(url);
		}

		@Override
		public void pauseAllTask() throws RemoteException {
			Log.i(TAG2, "pauseAllTask");
			mDownloadManager.pauseAllTask();
		}

		@Override
		public void resumeAllTask() throws RemoteException {
			Log.i(TAG2, "resumeAllTask");
			mDownloadManager.resumeAllTask();
		}

		@Override
		public void deleteAllTask() throws RemoteException {
			Log.i(TAG2, "deleteAllTask");
			mDownloadManager.deleteAllTask();
		}

		@Override
		public void finish() throws RemoteException {
			// TODO Auto-generated method stub
			mDownloadManager.finish();
		}

	}

}
