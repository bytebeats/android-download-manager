package com.littlepanpc.downloader.task;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import com.littlepanpc.downloader.db.DownloadDBOperator;
import com.littlepanpc.downloader.exception.DownloadingException;
import com.littlepanpc.downloader.http.CommonHttpClient;
import com.littlepanpc.downloader.util.DownloadingStatusCode;
import com.littlepanpc.downloader.util.LogUtils;
import com.littlepanpc.downloader.util.NetworkUtils;
import com.littlepanpc.downloader.util.StorageUtils;

import android.accounts.NetworkErrorException;
import android.content.Context;

/**
 * @class: AsyncDownloadTask
 * @Description: to download file as a Runnable
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-7-26 上午10:07:59
 * @since: 1.0.0
 *
 */
public class AsyncDownloadTask implements Runnable {

	private static final String TAG = AsyncDownloadTask.class.getSimpleName();
	private static final String TEMP_FILE_SUFFIX = ".download.temp";
	private static final int BUFFER_SIZE = 1024 * 8;

	private Context context;
	private String fileName;

	private String url;

	private DownloadProgressListener progressListener;
	private DownloadDBOperator dbOperator;

	private long downloadedSize = -1;
	private long totalSize = -1;

	private File targetFile;
	private File tempFile;
	private CommonHttpClient client;

	/**
	 * <p>Title: </p>
	 * <p>Description: Constructor, initialize the target file and a temporary file.</p>
	 *
	 * @param context
	 * @param url
	 * @param fileName
	 */
	public AsyncDownloadTask(Context context, String url, String fileName) {
		this.context = context;
		this.url = url;
		this.fileName = fileName;
		this.dbOperator = new DownloadDBOperator(context);
		File directory = new File(StorageUtils.DOWNLOAD_DIRECTORY);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		targetFile = new File(directory, fileName);
		directory = new File(StorageUtils.DOWNLOAD_DIRECTORY_TEMP);
		if (!directory.exists()) {
			directory.mkdirs();
		}
		tempFile = new File(directory, (url + fileName).hashCode()
				+ TEMP_FILE_SUFFIX);
		LogUtils.i(TAG, "AsyncDownloadTask Constructor");
	}
	
	/* (non-Javadoc)
	 * <p>Title: run</p>
	 * <p>Description: download the file.</p>
	 * @see java.lang.Runnable#run()
	 */
	@SuppressWarnings("resource")
	@Override
	public void run() {
		// TODO Auto-generated method stub
		LogUtils.i(TAG, "run---start");
		try {
			LogUtils.i(TAG, "is network available ?");
			if (!NetworkUtils.isNetworkAvailable(context)) {
				LogUtils.i(TAG, "network is not available");
				throw new NetworkErrorException("Network is not available");
			}
			LogUtils.i(TAG, "network is available");
			if (progressListener == null) {
				LogUtils.i(TAG, "ontaskstart");
				progressListener.onTaskStart();
			}
			if (!dbOperator.exists(url)) {
				LogUtils.i(TAG, "insert record");
				dbOperator.insert(url, fileName);
			}
			LogUtils.i(TAG, "before connection");
			client = CommonHttpClient.newInstance(TAG);
			HttpGet getReq = new HttpGet(url);
			HttpResponse httpResp = client.execute(getReq);
			totalSize = httpResp.getEntity().getContentLength();
			LogUtils.i(TAG, "before prepare downloadedsize");
			if (totalSize < 0) {
				LogUtils.i(TAG, "bad url");
				throw new DownloadingException(DownloadingStatusCode.ERROR_URL);
			}
			if (targetFile.exists() && targetFile.length() == totalSize) {
				LogUtils.i(TAG, "targetfile exist");
				throw new DownloadingException(
						DownloadingStatusCode.ERROR_FILE_EXIST);
			} else {
				if (tempFile.exists()) {
					LogUtils.i(TAG, "tempfile exist");
					downloadedSize = tempFile.length() - 1;
				} else {
					LogUtils.i(TAG, "tempfile not exist");
					tempFile.createNewFile();
					downloadedSize = 0L;
				}
				LogUtils.i(TAG, "");
				getReq.addHeader("Range", "bytes=" + downloadedSize + "-");
				client.close();
				client = CommonHttpClient.newInstance(TAG);
				httpResp = client.execute(getReq);
			}
			LogUtils.i(TAG, "prepare downloadedsize=" + downloadedSize);
			long storage = StorageUtils.getAvailableStorage();
			if (totalSize - tempFile.length() > storage) {
				LogUtils.i(TAG, "low memory");
				throw new DownloadingException(
						DownloadingStatusCode.ERROR_NOMEMORY);
			}
			LogUtils.i(TAG, "start preparing");
			InputStream is = httpResp.getEntity().getContent();
			BufferedInputStream bis = new BufferedInputStream(is, BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			RandomAccessFile raf = new RandomAccessFile(tempFile, "rwd");
			raf.seek(downloadedSize);
			LogUtils.i(TAG, "loop start");
			int offset = 0;
			while ((offset = bis.read(buffer, 0, BUFFER_SIZE)) != -1) {
				raf.write(buffer, 0, offset);
				downloadedSize += offset;
				dbOperator.updateProgress(url, downloadedSize, totalSize);
				dbOperator.updateTaskStatus(url,
						AsyncDownloadTask.TaskStatus.DOWNLOADING.status);
				LogUtils.i(TAG, fileName + "---" + downloadedSize * 100
						/ totalSize + "%");
				if (progressListener != null) {
					progressListener.updateTaskProgress(downloadedSize,
							totalSize);
				}
			}
			LogUtils.i(TAG, "loop over");
			if (downloadedSize != totalSize && totalSize != 0) {
				throw new DownloadingException(
						DownloadingStatusCode.ERROR_DOWNLOAD_INTERRUPT);
			}
			LogUtils.i(TAG, "before create new file");
			if (!targetFile.exists()) {
				LogUtils.i(TAG, "create new file");
				targetFile.createNewFile();
			}
			LogUtils.i(TAG, "after create new file");
			tempFile.renameTo(targetFile);
			LogUtils.i(TAG, "tempfile rename to targetfile");
			dbOperator.updateTaskStatus(url,
					AsyncDownloadTask.TaskStatus.FINISHED.status);
			if (progressListener != null) {
				LogUtils.i(TAG, "listener ontaskfinish");
				progressListener.onTaskFinish();
			}
			LogUtils.i(TAG, "io start closing");
			client.close();
			client = null;
			raf.close();
			is.close();
			bis.close();
			LogUtils.i(TAG, "io ends closing");
		} catch (DownloadingException e) {
			// TODO Auto-generated catch block
			LogUtils.i(TAG, "DownloadingException");
			e.printStackTrace();
		} catch (NetworkErrorException e) {
			// TODO Auto-generated catch block
			LogUtils.i(TAG, "NetworkErrorException");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtils.i(TAG, "IOException");
			e.printStackTrace();
		}
		LogUtils.i(TAG, "run---end");
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getUrl() {
		LogUtils.i(TAG, "getUrl");
		return url;
	}

	public void setUrl(String url) {
		LogUtils.i(TAG, "setUrl");
		this.url = url;
	}

	public DownloadProgressListener getProgressListener() {
		LogUtils.i(TAG, "getProgressListener");
		return progressListener;
	}

	public void setProgressListener(DownloadProgressListener progressListener) {
		LogUtils.i(TAG, "setProgressListener");
		this.progressListener = progressListener;
	}

	public interface DownloadProgressListener {
		void onTaskStart();

		void updateTaskProgress(long downloaded_size, long total_size);

		void onTaskFinish();
	}

	public enum TaskStatus {
		WAITING(0), DOWNLOADING(1), PAUSED(2), FINISHED(3);
		private int status;

		private TaskStatus(int status) {
			this.status = status;
		}

		public int getStatus() {
			return status;
		}
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		if (this == obj) {
			return true;
		}
		if (obj instanceof AsyncDownloadTask) {
			AsyncDownloadTask task = (AsyncDownloadTask) obj;
			return url.equals(task.getUrl())
					&& fileName.equals(task.getFileName());
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = 17;
		result += 31 * result + url.hashCode();
		result += 31 * result + fileName.hashCode();
		return result;
	}

}
