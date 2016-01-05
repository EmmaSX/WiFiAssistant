package edu.pku.xuehan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class Detailwifi extends Activity {
	final Context context = this;
	String wifiname;
	int wifiIndex;
	TextView dialog_detail_wifi_name;
	DialogWifi dialogWifi;
	TextView detail_wifi_name;
	ImageButton detail_wifi_back_arrow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.detail_wifi); // 显示WiFi详情页
		Bundle bundle = this.getIntent().getExtras(); // 取得第一頁傳遞過來的資料。

		wifiIndex = bundle.getInt("wifiIndex"); // 取出WiFiIndex
		wifiname = bundle.getString("SSID"); // 取出WiFiname
		int signal = bundle.getInt("signal"); // 取出信号强度
		String encryption = bundle.getString("encryption");// 取出加密方式
		String mac = bundle.getString("mac");// 取出mac地址

		detail_wifi_back_arrow = (ImageButton) findViewById(R.id.detail_wifi_back_arrow);

		dialog_detail_wifi_name = (TextView) findViewById(R.id.dialog_detail_wifi_name);

		detail_wifi_name = (TextView) findViewById(R.id.detail_wifi_name);
		detail_wifi_name.setText(wifiname);

		TextView signal_text = (TextView) findViewById(R.id.signal);
		signal_text.setText(String.valueOf(signal));

		TextView encry = (TextView) findViewById(R.id.encryption);
		encry.setText(encryption);

		TextView mac_text = (TextView) findViewById(R.id.mac);
		mac_text.setText(mac);

		ImageButton connect = (ImageButton) findViewById(R.id.connect); // 按鈕處理
		connect.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				// 弹出对话框
				dialog();
			}
		});

		//点击返回按钮回到主界面
		detail_wifi_back_arrow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setClass(Detailwifi.this, Main.class);
				startActivity(intent);
			}

		});
	}

	private void dialog() {
		dialogWifi = new DialogWifi(this, R.style.dialog_wifi, wifiname);
		EditText editText = (EditText) dialogWifi.getEditText();// 方法在CustomDialog中实现

		dialogWifi.setCanceledOnTouchOutside(true);// 设置点击Dialog外部任意区域关闭Dialog
		dialogWifi.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String password = dialogWifi.getEditText().getText().toString();
				Toast.makeText(Detailwifi.this, "正在连接，请稍后... ", Toast.LENGTH_LONG).show();
				new ConnectWifiThread().execute(String.valueOf(wifiIndex), password);
			}
		});
		dialogWifi.show();
	}

	class ConnectWifiThread extends AsyncTask<String, Integer, String> {

		@Override
		protected String doInBackground(String... params) {
			int index = Integer.parseInt(params[0]);
			WifiAdmin wifiadmin = WifiAdmin.getInstance();
			if (index > wifiadmin.getScanList().size()) {
				return null;
			}
			// 连接配置好指定ID的网络
			if (wifiadmin.connectWithIndex(index, params[1])) {
				return wifiadmin.getScanList().get(index).SSID;
			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			if (null != dialogWifi) {
				dialogWifi.dismiss();
			}
			if (null != result) {
				handler.sendEmptyMessage(0);
			} else {
				handler.sendEmptyMessage(1);
			}
			super.onPostExecute(result);
		}

	}

	class RefreshDetailView implements Runnable {

		@Override
		public void run() {
			boolean flag = true;
			while (flag) {
				if (WifiAdmin.getInstance().checkConnection(wifiname)) {
					break;
				}
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			handler.sendEmptyMessage(4);
		}
	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Toast.makeText(Detailwifi.this, "正在获取ip地址...", Toast.LENGTH_SHORT).show();
				// wifi_result_textview.setText("正在获取ip地址...");
				new Thread(new RefreshDetailView()).start();
				break;
			case 1:
				Toast.makeText(Detailwifi.this, "连接失败！", Toast.LENGTH_SHORT).show();
				break;
			case 3:

				// final EditText passowrdText = (EditText)
				// findViewById(R.id.dialog_password);
				// builder.setPositiveButton("连接", new
				// DialogInterface.OnClickListener() {
				//
				// @Override
				// public void onClick(DialogInterface dialog, int which) {
				// connetionConfiguration(wifiIndex,
				// passowrdText.getText().toString());
				// }
				// }).show();
				break;
			case 4:
				Toast.makeText(Detailwifi.this, "连接成功！", Toast.LENGTH_SHORT).show();
				detail_wifi_name.setText("当前网络：" + wifiname);
				break;
			}
			super.handleMessage(msg);
		}

	};

}
