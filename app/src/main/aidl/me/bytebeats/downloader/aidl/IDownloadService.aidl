package me.bytebeats.downloader.aidl;

interface IDownloadService {
	
	void startManage();
	
	void addTask(String url);
	
	void pauseTask(String url);
	
	void deleteTask(String url);
	
	void continueTask(String url);
	
	void pauseAllTask();
	
	void resumeAllTask();
	
	void deleteAllTask();
	
	void finish();
}
