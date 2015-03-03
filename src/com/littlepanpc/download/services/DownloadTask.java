package com.littlepanpc.download.services;

import com.littlepanpc.download.error.FileAlreadyExistException;
import com.littlepanpc.download.error.NoMemoryException;
import com.littlepanpc.download.https.AndroidHttpClient;
import com.littlepanpc.download.utils.NetworkUtils;
import com.littlepanpc.download.utils.StorageUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;

import android.accounts.NetworkErrorException;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @class: DownloadTask
 * @Description: AsyncTask to download
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-18 上午10:09:20
 * @since: 1.0.0
 * 
 */
public class DownloadTask extends AsyncTask<Void, Integer, Long> {
	private static final String TAG = DownloadTask.class.getName();

	public final static int TIME_OUT = 30000;
	private final static int BUFFER_SIZE = 1024 * 8;

	private static final boolean DEBUG = true;
	private static final String TEMP_SUFFIX = ".downloading";

	private URL URL;
	private File file;
	private File tempFile;
	private String url;
	private RandomAccessFile outputStream;
	private DownloadTaskListener listener;
	private Context context;

	private long downloadSize;
	private long previousFileSize;
	private long totalSize;
	private long downloadPercent;
	private long networkSpeed;
	private long previousTime;
	private long totalTime;
	private Throwable error = null;
	private boolean isInterrupted = false;

	private final class ProgressReportingRandomAccessFile extends
			RandomAccessFile {
		private final String TAG2 = ProgressReportingRandomAccessFile.class
				.getName();
		private int progress = 0;

		public ProgressReportingRandomAccessFile(File file, String mode)
				throws FileNotFoundException {

			super(file, mode);
		}

		@Override
		public void write(byte[] buffer, int offset, int count)
				throws IOException {
			Log.i(TAG2, "write");
			super.write(buffer, offset, count);
			progress += count;
			publishProgress(progress);
		}
	}

	public DownloadTask(Context context, String url, String path)
			throws MalformedURLException {
		this(context, url, path, null);
		Log.i(TAG, "Constructor");
	}

	public DownloadTask(Context context, String url, String path,
			DownloadTaskListener listener) throws MalformedURLException {

		super();
		this.url = url;
		this.URL = new URL(url);
		this.listener = listener;
		String fileName = new File(URL.getFile()).getName();
		this.file = new File(path, fileName);
		this.tempFile = new File(StorageUtils.FILE_ROOT_TEMP,
				(url + fileName).hashCode() + TEMP_SUFFIX);
		this.context = context;
	}

	public String getUrl() {

		return url;
	}

	public boolean isInterrupted() {

		return isInterrupted;
	}

	public long getDownloadPercent() {
		return downloadPercent;
	}

	public long getDownloadSize() {
		return downloadSize + previousFileSize;
	}

	public long getTotalSize() {
		return totalSize;
	}

	public long getDownloadSpeed() {
		return this.networkSpeed;
	}

	public long getTotalTime() {
		return this.totalTime;
	}

	public DownloadTaskListener getListener() {
		return this.listener;
	}

	@Override
	protected void onPreExecute() {
		Log.i(TAG, "onPreExecute");
		previousTime = System.currentTimeMillis();
		if (listener != null)
			listener.preDownload(this);
	}

	@Override
	protected Long doInBackground(Void... params) {
		Log.i(TAG, "doInBackground");
		long result = -1;
		try {
			result = download();
		} catch (NetworkErrorException e) {
			error = e;
		} catch (FileAlreadyExistException e) {
			error = e;
		} catch (NoMemoryException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		// Log.i(TAG, "onProgressUpdate");
		if (progress.length > 1) {
			totalSize = progress[1];
			if (totalSize == -1) {
				if (listener != null)
					listener.errorDownload(this, error);
			} else {

			}
		} else {
			totalTime = System.currentTimeMillis() - previousTime;
			downloadSize = progress[0];
			downloadPercent = (downloadSize + previousFileSize) * 100
					/ totalSize;
			networkSpeed = downloadSize / totalTime;
			if (listener != null)
				listener.updateProcess(this);
		}
	}

	@Override
	protected void onPostExecute(Long result) {
		Log.i(TAG, "onPostExecute");
		if (result == -1 || isInterrupted || error != null) {
			if (DEBUG && error != null) {
				Log.v(TAG, "Download failed." + error.getMessage());
			}
			if (listener != null) {
				listener.errorDownload(this, error);
			}
			return;
		}
		// finish download
		tempFile.renameTo(file);
		if (listener != null) {
			listener.finishDownload(this);
		}
	}

	@Override
	public void onCancelled() {
		super.onCancelled();
		isInterrupted = true;
		Log.i(TAG, "onCancelled");
	}

	private AndroidHttpClient client;
	private HttpGet httpGet;
	private HttpResponse response;

	private long download() throws NetworkErrorException, IOException,
			FileAlreadyExistException, NoMemoryException {
		Log.i(TAG, "download");
		if (DEBUG) {
			Log.v(TAG, "totalSize: " + totalSize);
		}

		/*
		 * check net work
		 */
		if (!NetworkUtils.isNetworkAvailable(context)) {
			throw new NetworkErrorException("Network blocked.");
		}

		/*
		 * check file length
		 */
		client = AndroidHttpClient.newInstance("DownloadTask");
		httpGet = new HttpGet(url);
		response = client.execute(httpGet);
		totalSize = response.getEntity().getContentLength();

		if (file.exists() && totalSize == file.length()) {
			if (DEBUG) {
				Log.v(null, "Output file already exists. Skipping download.");
			}

			throw new FileAlreadyExistException(
					"Output file already exists. Skipping download.");
		} else if (tempFile.exists()) {
			httpGet.addHeader("Range", "bytes=" + tempFile.length() + "-");
			previousFileSize = tempFile.length();

			client.close();
			client = AndroidHttpClient.newInstance("DownloadTask");
			response = client.execute(httpGet);

			if (DEBUG) {
				Log.v(TAG, "File is not complete, download now.");
				Log.v(TAG, "File length:" + tempFile.length() + " totalSize:"
						+ totalSize);
			}
		}

		/*
		 * check memory
		 */
		long storage = StorageUtils.getAvailableStorage();
		if (DEBUG) {
			Log.i(null, "storage:" + storage + " totalSize:" + totalSize);
		}

		if (totalSize - tempFile.length() > storage) {
			throw new NoMemoryException("SD card low memory.");
		}

		/*
		 * start download
		 */
		outputStream = new ProgressReportingRandomAccessFile(tempFile, "rw");

		publishProgress(0, (int) totalSize);

		InputStream input = response.getEntity().getContent();
		int bytesCopied = copy(input, outputStream);

		if ((previousFileSize + bytesCopied) != totalSize && totalSize != -1
				&& !isInterrupted) {
			throw new IOException("Download incomplete: " + bytesCopied
					+ " != " + totalSize);
		}

		if (DEBUG) {
			Log.v(TAG, "Download completed successfully.");
		}

		return bytesCopied;

	}

	public int copy(InputStream input, RandomAccessFile out)
			throws IOException, NetworkErrorException {
		Log.i(TAG, "copy");
		if (input == null || out == null) {
			return -1;
		}

		byte[] buffer = new byte[BUFFER_SIZE];

		BufferedInputStream in = new BufferedInputStream(input, BUFFER_SIZE);
		if (DEBUG) {
			Log.v(TAG, "length" + out.length());
		}

		int count = 0, n = 0;
		long errorBlockTimePreviousTime = -1, expireTime = 0;

		try {

			out.seek(out.length());

			while (!isInterrupted) {
				n = in.read(buffer, 0, BUFFER_SIZE);
				if (n == -1) {
					break;
				}
				out.write(buffer, 0, n);
				count += n;

				/*
				 * check network
				 */
				if (!NetworkUtils.isNetworkAvailable(context)) {
					throw new NetworkErrorException("Network blocked.");
				}

				if (networkSpeed == 0) {
					if (errorBlockTimePreviousTime > 0) {
						expireTime = System.currentTimeMillis()
								- errorBlockTimePreviousTime;
						if (expireTime > TIME_OUT) {
							throw new ConnectTimeoutException(
									"connection time out.");
						}
					} else {
						errorBlockTimePreviousTime = System.currentTimeMillis();
					}
				} else {
					expireTime = 0;
					errorBlockTimePreviousTime = -1;
				}
			}
		} finally {
			client.close(); // must close client first
			client = null;
			out.close();
			in.close();
			input.close();
		}
		return count;
	}

	@Override
	public String toString() {
		Log.i(TAG, "toString");
		return "DownloadTask [file=" + file + ", tempFile=" + tempFile
				+ ", url=" + url + ", downloadSize=" + downloadSize
				+ ", previousFileSize=" + previousFileSize + ", totalSize="
				+ totalSize + ", downloadPercent=" + downloadPercent
				+ ", networkSpeed=" + networkSpeed + "]";
	}

}
