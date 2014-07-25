package com.littlepanpc.downloader.util;

/**
 * @class: DownloadingStatusCode
 * @Description: downloading error code. Show the reason of bad downloading.
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-7-25 上午10:19:57
 * @since: 1.0.0
 *
 */
public class DownloadingStatusCode {
	/** complete file exists already */
	public static final String ERROR_FILE_EXIST = "100";
	/** bad URL */
	public static final String ERROR_URL = "101";
	/** low memory on external storage error */
	public static final String ERROR_NOMEMORY = "102";
	/**
	 * connection timeout or socket timeout during downloading. click to
	 * continue when it happens.
	 */
	public static final String ERROR_DOWNLOAD_INTERRUPT = "103";
}
