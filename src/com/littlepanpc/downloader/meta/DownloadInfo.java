package com.littlepanpc.downloader.meta;

/**
 * @class: DownloadInfo
 * @Description: some information about download task.
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-7-24 上午9:34:01
 * @since: 1.0.0
 * 
 */
public class DownloadInfo {
	public String url;
	public String name;
	public String parentPath;
	public int downloadedSize;
	public int totalSize;
	public int status;

	@Override
	public String toString() {
		return "DownloadMeta [url=" + url + ", name=" + name + ", parentPath="
				+ parentPath + ", downloadedSize=" + downloadedSize
				+ ", totalSize=" + totalSize + ", status=" + status + "]";
	}

	@Override
	public boolean equals(Object o) {
		// TODO Auto-generated method stub
		if (o == this) {
			return true;
		}
		if (o instanceof DownloadInfo) {
			DownloadInfo meta = (DownloadInfo) o;
			return url.equals(meta.url) && name.equals(meta.name);
		}
		return false;
	}
	
	
	private volatile int hash = 0;

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		int result = hash;
		if (result == 0) {
			result = 17;
			result += 31 * result + url.hashCode();
			result += 31 * result + name.hashCode();
		}
		return result;
	}
}
