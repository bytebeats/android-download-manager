package me.bytebeats.downloader.activities;

import me.bytebeats.downloader.R;
import me.bytebeats.downloader.aidl.IDownloadService;
import me.bytebeats.downloader.services.DownloadService;
import me.bytebeats.downloader.services.TrafficCounterService;
import me.bytebeats.downloader.utils.IndexConstants;
import me.bytebeats.downloader.utils.StorageUtils;
import me.bytebeats.downloader.utils.Utils;
import me.bytebeats.downloader.widgets.DownloadListAdapter;
import me.bytebeats.downloader.widgets.ViewHolder;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;

/**
 * @class: DownloadListActivity
 * @Description: TODO
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-18 上午10:06:41
 * @since: 1.0.0
 *
 */
public class DownloadListActivity extends Activity implements OnClickListener,
		ServiceConnection {
	private static final String TAG = DownloadListActivity.class
			.getSimpleName();

	private ListView downloadList;
	private Button addButton;
	private Button pauseAllButton;
	private Button resumeAllButton;
	private Button deleteAllButton;
	private Button trafficButton;
	private Button finishButton;

	private DownloadListAdapter downloadListAdapter;
	private MyReceiver mReceiver;

	private int urlIndex = 0;

	private Intent downloadIntent;
	private IDownloadService downloadService;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.download_list_activity);

		if (!StorageUtils.isSdCardMounted()) {
			Toast.makeText(this, "SD卡不可用", Toast.LENGTH_LONG).show();
			return;
		}

		try {
			StorageUtils.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}

		downloadIntent = new Intent(
				"me.bytebeats.download.services.DownloadService");
        downloadIntent.setPackage(getPackageName());
		bindService(downloadIntent, this, Context.BIND_AUTO_CREATE);

		downloadList = (ListView) findViewById(R.id.download_list);

		addButton = (Button) findViewById(R.id.btn_add);
		pauseAllButton = (Button) findViewById(R.id.btn_pause_all);
		resumeAllButton = (Button) findViewById(R.id.btn_resume_all);
		deleteAllButton = (Button) findViewById(R.id.btn_delete_all);
		trafficButton = (Button) findViewById(R.id.btn_traffic);
		finishButton = (Button) findViewById(R.id.service_finish);

		addButton.setOnClickListener(this);
		pauseAllButton.setOnClickListener(this);
		resumeAllButton.setOnClickListener(this);
		deleteAllButton.setOnClickListener(this);
		trafficButton.setOnClickListener(this);
		finishButton.setOnClickListener(this);

		Intent trafficIntent = new Intent(this, TrafficCounterService.class);
		startService(trafficIntent);

		mReceiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("me.bytebeats.download.activities.DownloadListActivity");
		registerReceiver(mReceiver, filter);

	}

	@Override
	protected void onDestroy() {
		Log.i(TAG, "onDestroy");
		unbindService(this);
		unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	public class MyReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.i(TAG, "onReceive");
			handleIntent(intent);
		}

		private void handleIntent(Intent intent) {
			if (intent != null
					&& intent
							.getAction()
							.equals("me.bytebeats.download.activities.DownloadListActivity")) {
				int type = intent.getIntExtra(IndexConstants.TYPE, -1);
				String url;
				Log.i(TAG, "handleIntent---" + type);
				switch (type) {
				case IndexConstants.OperationIndex.ADD:
					url = intent.getStringExtra(IndexConstants.URL);
					boolean isPaused = intent.getBooleanExtra(
							IndexConstants.IS_PAUSED, false);
					if (!TextUtils.isEmpty(url)) {
						downloadListAdapter.addItem(url, isPaused);
					}
					break;
				case IndexConstants.OperationIndex.COMPLETE:
					url = intent.getStringExtra(IndexConstants.URL);
					if (!TextUtils.isEmpty(url)) {
						downloadListAdapter.removeItem(url);
					}
					break;
				case IndexConstants.OperationIndex.PROCESS:
					url = intent.getStringExtra(IndexConstants.URL);
					View taskListItem = downloadList.findViewWithTag(url);
					ViewHolder viewHolder = new ViewHolder(taskListItem);
					viewHolder
							.setData(
									url,
									intent.getStringExtra(IndexConstants.PROCESS_SPEED),
									intent.getStringExtra(IndexConstants.PROCESS_PROGRESS));
					break;
				case IndexConstants.OperationIndex.ERROR:
					url = intent.getStringExtra(IndexConstants.URL);
					break;
				default:
					break;
				}
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onClick---" + v.getId());
		try {
			switch (v.getId()) {
			case R.id.btn_pause_all:
				downloadService.pauseAllTask();
				break;
			case R.id.btn_resume_all:
				downloadService.resumeAllTask();
				break;
			case R.id.btn_delete_all:
				downloadService.deleteAllTask();
				downloadListAdapter.deleteAllItems();
				break;
			case R.id.btn_add:
				downloadService.addTask(Utils.URLS[urlIndex]);
				urlIndex++;
				if (urlIndex >= Utils.URLS.length) {
					urlIndex = 0;
				}
				break;
			case R.id.btn_traffic:
				Intent intent = new Intent(DownloadListActivity.this,
						me.bytebeats.downloader.activities.TrafficStatActivity.class);
				startActivity(intent);
				break;
			case R.id.service_finish:
				downloadService.finish();
				break;

			default:
				break;
			}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void onServiceConnected(ComponentName name, IBinder service) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onServiceConnected");
		try {
			downloadService = IDownloadService.Stub.asInterface(service);
			downloadService.startManage();
			downloadListAdapter = new DownloadListAdapter(this, downloadService);
			downloadList.setAdapter(downloadListAdapter);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplication(),
					"Remote Connection interrupted exceptionally!!!",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onServiceDisconnected(ComponentName name) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onServiceDisconnected");
		try {
			downloadService.finish();
			downloadService = null;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			Toast.makeText(getApplication(),
					"Remote Connection interrupted exceptionally!!!",
					Toast.LENGTH_SHORT).show();
		}
	}

}
