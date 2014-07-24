package com.littlepanpc.downloader.meta;

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
}
