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
		setContentView(R.layout.detail_wifi); // ��ʾWiFi����ҳ
		Bundle bundle = this.getIntent().getExtras(); // ȡ�õ�һ퓂��f�^����Y�ϡ�

		wifiIndex = bundle.getInt("wifiIndex"); // ȡ��WiFiIndex
		wifiname = bundle.getString("SSID"); // ȡ��WiFiname
		int signal = bundle.getInt("signal"); // ȡ���ź�ǿ��
		String encryption = bundle.getString("encryption");// ȡ�����ܷ�ʽ
		String mac = bundle.getString("mac");// ȡ��mac��ַ

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

		ImageButton connect = (ImageButton) findViewById(R.id.connect); // ���o̎��
		connect.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {

				// �����Ի���
				dialog();
			}
		});

		//������ذ�ť�ص�������
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
		EditText editText = (EditText) dialogWifi.getEditText();// ������CustomDialog��ʵ��

		dialogWifi.setCanceledOnTouchOutside(true);// ���õ��Dialog�ⲿ��������ر�Dialog
		dialogWifi.setOnPositiveListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String password = dialogWifi.getEditText().getText().toString();
				Toast.makeText(Detailwifi.this, "�������ӣ����Ժ�... ", Toast.LENGTH_LONG).show();
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
			// �������ú�ָ��ID������
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
				Toast.makeText(Detailwifi.this, "���ڻ�ȡip��ַ...", Toast.LENGTH_SHORT).show();
				// wifi_result_textview.setText("���ڻ�ȡip��ַ...");
				new Thread(new RefreshDetailView()).start();
				break;
			case 1:
				Toast.makeText(Detailwifi.this, "����ʧ�ܣ�", Toast.LENGTH_SHORT).show();
				break;
			case 3:

				// final EditText passowrdText = (EditText)
				// findViewById(R.id.dialog_password);
				// builder.setPositiveButton("����", new
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
				Toast.makeText(Detailwifi.this, "���ӳɹ���", Toast.LENGTH_SHORT).show();
				detail_wifi_name.setText("��ǰ���磺" + wifiname);
				break;
			}
			super.handleMessage(msg);
		}

	};

}
