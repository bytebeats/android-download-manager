package com.littlepanpc.downloader.util;

public class DownloadingStatusCode {
	public static final String ERROR_FILE_EXIST = "100";
	/** bad URL */
	public static final String ERROR_URL = "101";
	public static final String ERROR_NOMEMORY = "102";
	/**
	 * connection timeout or socket timeout during downloading. click to
	 * continue when it happens.
	 */
	public static final String ERROR_DOWNLOAD_INTERRUPT = "103";
}
