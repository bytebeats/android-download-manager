package com.littlepanpc.download.widgets;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.littlepanpc.download.aidl.IDownloadService;
import com.littlepanpc.android_download_manager.R;

public class DownloadListAdapter extends BaseAdapter {
	private static final String TAG = DownloadListAdapter.class.getName();
	private Context mContext;
	private IDownloadService downloadService;
	private ArrayList<HashMap<Integer, String>> dataList;

	public DownloadListAdapter(Context context, IDownloadService downloadService) {
		mContext = context;
		this.downloadService = downloadService;
		dataList = new ArrayList<HashMap<Integer, String>>();
	}

	@Override
	public int getCount() {
		Log.i(TAG, "getCount");
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {
//		Log.i(TAG, "getItem");
		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {
//		Log.i(TAG, "getItemId");
		return position;
	}

	public void addItem(String url) {
		Log.i(TAG, "addItem(String)");
		addItem(url, false);
	}

	public void addItem(String url, boolean isPaused) {
		Log.i(TAG, "addItem(String, boolean)");
		HashMap<Integer, String> item = ViewHolder.getItemDataMap(url, null,
				null, isPaused + "");
		dataList.add(item);
		this.notifyDataSetChanged();
	}

	public void deleteAllItems() {
		Log.i(TAG, "deleteAllItems");
		dataList.clear();
		notifyDataSetChanged();
	}

	public void removeItem(String url) {
		Log.i(TAG, "removeItem");
		String tmp;
		for (int i = 0; i < dataList.size(); i++) {
			tmp = dataList.get(i).get(ViewHolder.KEY_URL);
			if (tmp.equals(url)) {
				dataList.remove(i);
				this.notifyDataSetChanged();
			}
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
//		Log.i(TAG, "getView");
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.download_list_item, null);
		}

		HashMap<Integer, String> itemData = dataList.get(position);
		String url = itemData.get(ViewHolder.KEY_URL);
		convertView.setTag(url);

		ViewHolder viewHolder = new ViewHolder(convertView);
		viewHolder.setData(itemData);
		DownloadBtnListener listener = new DownloadBtnListener(url, viewHolder);
		viewHolder.continueButton.setOnClickListener(listener);
		viewHolder.pauseButton.setOnClickListener(listener);
		viewHolder.deleteButton.setOnClickListener(listener);

		return convertView;
	}
        /**
	 * @class: DownloadBtnListener
	 * @Description: Listener of Events of Click of Buttons about downloading 
	 * @author: Peter Pan
	 * @email: happychinapc@gmail.com
	 * @date: 2014-8-14 上午9:21:39
	 * @since: 1.0.0
	 *
	 */
	private class DownloadBtnListener implements View.OnClickListener {
		private String url;
		private ViewHolder mViewHolder;

		public DownloadBtnListener(String url, ViewHolder viewHolder) {
			this.url = url;
			this.mViewHolder = viewHolder;
		}

		@Override
		public void onClick(View v) {
			Log.i(TAG, "onClick---" + v.getId());
			try {
				switch (v.getId()) {
				case R.id.btn_continue:
					downloadService.continueTask(url);

					mViewHolder.continueButton.setVisibility(View.GONE);
					mViewHolder.pauseButton.setVisibility(View.VISIBLE);
					break;
				case R.id.btn_pause:
					downloadService.pauseTask(url);

					mViewHolder.continueButton.setVisibility(View.VISIBLE);
					mViewHolder.pauseButton.setVisibility(View.GONE);
					break;
				case R.id.btn_delete:
					downloadService.deleteTask(url);

					removeItem(url);
					break;
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
