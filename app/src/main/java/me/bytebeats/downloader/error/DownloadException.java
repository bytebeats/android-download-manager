package me.bytebeats.downloader.error;

/**
 * @class: DownloadException
 * @Description: TODO
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-13 上午10:21:10
 * @since: 1.0.0
 */
public class DownloadException extends Exception {
	/**
	 * @Field long serialVersionUID: serialization
	 */
	private static final long serialVersionUID = 1L;

	private String mExtra;

	public DownloadException(String message) {

		super(message);
	}

	public DownloadException(String message, String extra) {

		super(message);
		mExtra = extra;
	}

	public String getExtra() {

		return mExtra;
	}
}
