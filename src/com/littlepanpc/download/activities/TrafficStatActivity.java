package com.littlepanpc.download.activities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.TrafficStats;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.littlepanpc.download.utils.ConfigUtils;
import com.littlepanpc.download.utils.StorageUtils;
import com.littlepanpc.android_download_manager.R;
/**
 * @class: TrafficStatActivity
 * @Description: Show the information of networks
 * @author: Peter Pan
 * @email: happychinapc@gmail.com
 * @date: 2014-8-15 上午9:35:53
 * @since: 1.0.0
 *
 */
public class TrafficStatActivity extends Activity {
	private static final String TAG = TrafficStatActivity.class.getSimpleName();

	private TextView netText;
	private TextView appWifiText;
	private TextView appGprsText;
	private TextView testText;
	private Button clearButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "onCreate");
		setContentView(R.layout.traffic_stat_activity);

		netText = (TextView) findViewById(R.id.net_text);
		appWifiText = (TextView) findViewById(R.id.app_wifi_text);
		appGprsText = (TextView) findViewById(R.id.app_gprs_text);
		testText = (TextView) findViewById(R.id.test_text);
		clearButton = (Button) findViewById(R.id.btn_clear);

		clearButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				clearTrafficStats();
			}
		});

		getTrafficStats();
	}

	private void clearTrafficStats() {
		Log.i(TAG, "clearTrafficStats");
		ConfigUtils.setLong(this, ConfigUtils.KEY_RX_MOBILE, 0L);
		ConfigUtils.setLong(this, ConfigUtils.KEY_RX_WIFI, 0L);
		ConfigUtils.setLong(this, ConfigUtils.KEY_TX_MOBILE, 0L);
		ConfigUtils.setLong(this, ConfigUtils.KEY_TX_WIFI, 0L);
		ConfigUtils.setString(this, ConfigUtils.KEY_Network_Operator_Name, "");

		getTrafficStats();
	}
	/**
	 * @Title: getTrafficStats
	 * @Description: get information about rate of network links
	 * @return void
	 * @throws 
	 */
	@TargetApi(Build.VERSION_CODES.FROYO)
	private void getTrafficStats() {
		Log.i(TAG, "getTrafficStats");
		ConnectivityManager conn = (ConnectivityManager) this
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (conn.getActiveNetworkInfo() == null) {
			netText.setText("当前联网方式: 未连接"
					+ "\n运营商："
					+ ConfigUtils.getString(this,
							ConfigUtils.KEY_Network_Operator_Name));
		} else {
			netText.setText("当前联网方式:"
					+ conn.getActiveNetworkInfo().getTypeName()
					+ "\n运营商："
					+ ConfigUtils.getString(this,
							ConfigUtils.KEY_Network_Operator_Name));
		}

		long mobileRx = ConfigUtils.getLong(this, ConfigUtils.KEY_RX_MOBILE);
		long mobileTx = ConfigUtils.getLong(this, ConfigUtils.KEY_TX_MOBILE);
		appGprsText.setText("接收 " + StorageUtils.size(mobileRx) + " / 发送 "
				+ StorageUtils.size(mobileTx));

		long wifiRx = ConfigUtils.getLong(this, ConfigUtils.KEY_RX_WIFI);
		long wifiTx = ConfigUtils.getLong(this, ConfigUtils.KEY_TX_WIFI);
		appWifiText.setText("接收 " + StorageUtils.size(wifiRx) + " / 发送 "
				+ StorageUtils.size(wifiTx));

		try {
			PackageManager packageManager = this.getPackageManager();
			ApplicationInfo info = packageManager.getApplicationInfo(
					this.getPackageName(), PackageManager.GET_META_DATA);
			testText.setText("Totoal: " + TrafficStats.getUidRxBytes(info.uid)
					+ " / " + "Totoal: " + TrafficStats.getUidTxBytes(info.uid));
		} catch (Exception e) {
			// TODO: handle exception
		}

	}
}
