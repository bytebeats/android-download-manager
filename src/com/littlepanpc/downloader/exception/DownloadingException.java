package com.littlepanpc.downloader.exception;

/**
 * @class: DownloadingException
 * @Description: throw DownloadException when downloading error.
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-7-28 上午9:05:17
 * @since: 1.0.0
 *
 */
public class DownloadingException extends RuntimeException {

	/**
	 * @Field long serialVersionUID: RMI Usage
	 */
	private static final long serialVersionUID = 7449248441719450018L;

	public DownloadingException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DownloadingException(String detailMessage) {
		super(detailMessage);
		// TODO Auto-generated constructor stub
	}

}
