package com.littlepanpc.download.error;

/**
 * @class: FileAlreadyExistException
 * @Description: TODO
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-13 上午10:21:28
 * @since: 1.0.0
 */
public class FileAlreadyExistException extends DownloadException {

	/**
	 * @Field long serialVersionUID: serialization
	 */
	private static final long serialVersionUID = 1L;

	public FileAlreadyExistException(String message) {
		super(message);
	}

}
