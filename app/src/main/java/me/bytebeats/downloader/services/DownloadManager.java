package me.bytebeats.downloader.services;

import me.bytebeats.downloader.utils.ConfigUtils;
import me.bytebeats.downloader.utils.IndexConstants;
import me.bytebeats.downloader.utils.NetworkUtils;
import me.bytebeats.downloader.utils.StorageUtils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @class: DownloadManager
 * @Description: Android Download Manager
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-5 下午6:03:23
 * @since: 1.0.0
 * 
 */
public class DownloadManager extends Thread {
	private static final String TAG = DownloadManager.class.getName();

	private static final int MAX_TASK_COUNT = 100;
	private static final int MAX_DOWNLOAD_THREAD_COUNT = 3;

	private Context mContext;

	private TaskQueue mTaskQueue;
	private List<DownloadTask> mDownloadingTasks;
	private List<DownloadTask> mPausingTasks;

	private Boolean isRunning = false;

	public DownloadManager(Context context) {
		Log.i(TAG, "Constructor");
		mContext = context;
		mTaskQueue = new TaskQueue();
		mDownloadingTasks = new ArrayList<DownloadTask>();
		mPausingTasks = new ArrayList<DownloadTask>();
	}

	public void startManage() {
		Log.i(TAG, "startManage");
		isRunning = true;
		if (!isAlive()) {
			start();
		}
		if (isInterrupted()) {
			resume();
		}
		checkUncompleteTasks();
	}

	public void finish() {
		Log.i(TAG, "finish");
		pauseAllTask();
		isRunning = false;
		if (isAlive()) {
			interrupt();
		}
	}

	public boolean isRunning() {
		Log.i(TAG, "isRunning");
		return isRunning;
	}

	@Override
	public void run() {
		Log.i(TAG, "run");
		while (isRunning) {
			DownloadTask task = mTaskQueue.poll();
			mDownloadingTasks.add(task);
			task.execute();
		}
	}

	public void addTask(String url) {
		Log.i(TAG, "addTask(String url)");
		if (!StorageUtils.isSdCardMounted()) {
			Toast.makeText(mContext, "SD卡不可用", Toast.LENGTH_LONG).show();
			return;
		}

		if (getTotalTaskCount() >= MAX_TASK_COUNT) {
			Toast.makeText(mContext, "任务列表已满", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			addTask(newDownloadTask(url));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	private void addTask(DownloadTask task) {
		Log.i(TAG, "addTask(DownloadTask task)");
		broadcastAddTask(task.getUrl());
		mTaskQueue.offer(task);
		if (!this.isAlive()) {
			this.startManage();
		}
	}

	private void broadcastAddTask(String url) {
		Log.i(TAG, "broadcastAddTask(String url)");
		broadcastAddTask(url, false);
	}

	private void broadcastAddTask(String url, boolean isInterrupt) {
		Log.i(TAG, "broadcastAddTask(String url, boolean isInterrupt)");
		Intent nofityIntent = new Intent(
				"me.bytebeats.download.activities.DownloadListActivity");
		nofityIntent.putExtra(IndexConstants.TYPE,
				IndexConstants.OperationIndex.ADD);
		nofityIntent.putExtra(IndexConstants.URL, url);
		nofityIntent.putExtra(IndexConstants.IS_PAUSED, isInterrupt);
		mContext.sendBroadcast(nofityIntent);
	}

	public void rebroadcastAddAllTask() {
		Log.i(TAG, "rebroadcastAddAllTask");
		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			broadcastAddTask(task.getUrl(), task.isInterrupted());
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			broadcastAddTask(task.getUrl());
		}
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			broadcastAddTask(task.getUrl());
		}
	}

	public boolean hasTask(String url) {
		Log.i(TAG, "hasTask");
		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task.getUrl().equals(url)) {
				return true;
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			if (task.getUrl().equals(url)) {
				return true;
			}
		}
		return false;
	}

	public DownloadTask getTask(int position) {
		Log.i(TAG, "getTask");
		if (position >= mDownloadingTasks.size()) {
			return mTaskQueue.get(position - mDownloadingTasks.size());
		} else {
			return mDownloadingTasks.get(position);
		}
	}

	private void checkUncompleteTasks() {
		Log.i(TAG, "checkUncompleteTasks");
		List<String> urlList = ConfigUtils.getURLArray(mContext);
		if (urlList.size() >= 0) {
			for (int i = 0; i < urlList.size(); i++) {
				addTask(urlList.get(i));
			}
		}
	}

	private void clearAllTaskRecords() {
		ConfigUtils.clear(mContext);
	}

	/*
	 * 经过这么久的测试，觉得线程的管理原本一件极其麻烦的事， 尤其是诸如全部暂停、全部开始这些东东，
	 * 对单个下载任务的开始、暂停、重新开始、删除等操作还是比较容易实现的。
	 */
	@Deprecated
	public/* synchronized */void pauseAllTask() {
		Log.i(TAG, "pauseAllTask");
		DownloadTask task;
		int size = mTaskQueue.size();
		for (int i = 0; i < size; i++) {
			mPausingTasks.add(mTaskQueue.poll());
		}

		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null) {
				pauseTask(task);
			}
		}
	}

	public/* synchronized */void pauseTask(String url) {
		Log.i(TAG, "pauseTask(String url)");
		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				pauseTask(task);
			}
		}
	}

	private synchronized void pauseTask(DownloadTask task) {
		Log.i(TAG, "pauseTask(DownloadTask task)");
		if (task != null) {
			task.onCancelled();
			// move to pausing list
			String url = task.getUrl();
			try {
				mDownloadingTasks.remove(task);
				task = newDownloadTask(url);
				mPausingTasks.add(task);
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
	}

	public/* synchronized */void resumeAllTask() {
		Log.i(TAG, "resumeAllTask");
		Iterator<DownloadTask> mPausingIter = mPausingTasks.iterator();
		while (mPausingIter.hasNext()) {
			DownloadTask pausingTask = mPausingIter.next();
			if (pausingTask != null) {
				mTaskQueue.offer(pausingTask);
				mPausingIter.remove();
			}
		}
	}

	public/* synchronized */void deleteAllTask() {
		Log.i(TAG, "deleteAllTask");
		mPausingTasks.clear();
		mTaskQueue.clear();
		File file;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			DownloadTask task = mDownloadingTasks.get(i);
			file = new File(StorageUtils.FILE_ROOT
					+ NetworkUtils.getFileNameFromUrl(task.getUrl()));
			if (file.exists()) {
				file.delete();
			}
			task.onCancelled();
			completeTask(task);
		}
		file = null;
		clearAllTaskRecords();
	}

	public/* synchronized */void deleteTask(String url) {
		Log.i(TAG, "deleteTask(String url)");
		DownloadTask task;
		for (int i = 0; i < mDownloadingTasks.size(); i++) {
			task = mDownloadingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				File file = new File(StorageUtils.FILE_ROOT
						+ NetworkUtils.getFileNameFromUrl(task.getUrl()));
				if (file.exists()) {
					file.delete();
				}
				task.onCancelled();
				completeTask(task);
				return;
			}
		}
		for (int i = 0; i < mTaskQueue.size(); i++) {
			task = mTaskQueue.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mTaskQueue.remove(task);
			}
		}
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				mPausingTasks.remove(task);
			}
		}
	}

	public/* synchronized */void continueTask(String url) {
		Log.i(TAG, "continueTask(String url)");
		DownloadTask task;
		for (int i = 0; i < mPausingTasks.size(); i++) {
			task = mPausingTasks.get(i);
			if (task != null && task.getUrl().equals(url)) {
				continueTask(task);
			}
		}
	}

	private/* synchronized */void continueTask(DownloadTask task) {
		Log.i(TAG, "continueTask(DownloadTask task)");
		if (task != null) {
			mPausingTasks.remove(task);
			mTaskQueue.offer(task);
		}
	}

	private/* synchronized */void completeTask(DownloadTask task) {
		Log.i(TAG, "completeTask");
		if (mDownloadingTasks.contains(task)) {
			ConfigUtils.clearURL(mContext, mDownloadingTasks.indexOf(task));
			mDownloadingTasks.remove(task);

			// notify list changed
			Intent nofityIntent = new Intent(
					"me.bytebeats.download.activities.DownloadListActivity");
			nofityIntent.putExtra(IndexConstants.TYPE,
					IndexConstants.OperationIndex.COMPLETE);
			nofityIntent.putExtra(IndexConstants.URL, task.getUrl());
			mContext.sendBroadcast(nofityIntent);
		}
	}

	/**
	 * Create a new download task with default config
	 * 
	 * @param url
	 * @return
	 * @throws MalformedURLException
	 */
	private DownloadTask newDownloadTask(String url)
			throws MalformedURLException {
		Log.i(TAG, "newDownloadTask");
		DownloadTaskListener taskListener = new DownloadTaskListener() {

			@Override
			public void updateProcess(DownloadTask task) {

				Intent updateIntent = new Intent(
						"me.bytebeats.download.activities.DownloadListActivity");
				updateIntent.putExtra(IndexConstants.TYPE,
						IndexConstants.OperationIndex.PROCESS);
				updateIntent.putExtra(
						IndexConstants.PROCESS_SPEED,
						task.getDownloadSpeed() + "kbps | "
								+ task.getDownloadSize() + " / "
								+ task.getTotalSize());
				updateIntent.putExtra(IndexConstants.PROCESS_PROGRESS,
						task.getDownloadPercent() + "");
				updateIntent.putExtra(IndexConstants.URL, task.getUrl());
				mContext.sendBroadcast(updateIntent);
			}

			@Override
			public void preDownload(DownloadTask task) {

				ConfigUtils.storeURL(mContext, mDownloadingTasks.indexOf(task),
						task.getUrl());
			}

			@Override
			public void finishDownload(DownloadTask task) {

				completeTask(task);
			}

			@Override
			public void errorDownload(DownloadTask task, Throwable error) {

				if (error != null) {
					Toast.makeText(mContext, "Error: " + error.getMessage(),
							Toast.LENGTH_LONG).show();
				}

			}
		};
		return new DownloadTask(mContext, url, StorageUtils.FILE_ROOT,
				taskListener);
	}

	public int getQueueTaskCount() {

		return mTaskQueue.size();
	}

	public int getDownloadingTaskCount() {

		return mDownloadingTasks.size();
	}

	public int getPausingTaskCount() {

		return mPausingTasks.size();
	}

	public int getTotalTaskCount() {

		return getQueueTaskCount() + getDownloadingTaskCount()
				+ getPausingTaskCount();
	}

	/**
	 * @class: TaskQueue
	 * @Description: A obstructed task queue
	 * @author: Peter Pan
	 * @email: happychinapc@gmail.com
	 * @date: 2014-8-5 下午6:03:09
	 * @since: 1.0.0
	 * 
	 */
	private class TaskQueue {// 复合模式优于继承
		private Queue<DownloadTask> taskQueue;

		public TaskQueue() {
			taskQueue = new LinkedList<DownloadTask>();
		}

		public void offer(DownloadTask task) {
			taskQueue.offer(task);
		}

		public DownloadTask poll() {

			DownloadTask task = null;
			while (mDownloadingTasks.size() >= MAX_DOWNLOAD_THREAD_COUNT
					|| (task = taskQueue.poll()) == null) {
				try {
					Thread.sleep(1000); // sleep
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return task;
		}

		public DownloadTask get(int position) {

			if (position >= size()) {
				return null;
			}
			return ((LinkedList<DownloadTask>) taskQueue).get(position);
		}

		public int size() {
			return taskQueue.size();
		}

		public void clear() {
			taskQueue.clear();
		}

		/**
		 * @Title: remove
		 * @Description: remove the element at the index of position and return
		 *               the old value
		 * @param position
		 * @return DownloadTask
		 * @throws null
		 */
		@SuppressWarnings("unused")
		public DownloadTask remove(int position) {
			DownloadTask task = get(position);
			taskQueue.remove(task);
			return task;
		}

		public boolean remove(DownloadTask task) {
			if (task != null) {
				return taskQueue.remove(task);
			} else {
				return false;
			}
		}
	}

}
