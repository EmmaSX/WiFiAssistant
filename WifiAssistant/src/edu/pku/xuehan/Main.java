package edu.pku.xuehan;

import java.util.List;

 

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

public class Main extends Activity {

	ListView listView;
	List<ScanResult> scanList;
	private ImageButton refresh;

	private String mScanResults;
	private WifiAdmin mWifiAdmin;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		refresh = (ImageButton) findViewById(R.id.refresh);
		listView = (ListView) findViewById(R.id.wifi_list);
		WifiAdmin.init(Main.this);
		mWifiAdmin = WifiAdmin.getInstance();
		
		refresh.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
         	   scan();                   	                     	
            }
        });

		
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

				
				Toast.makeText(getApplicationContext(), "You Selected Item " + Integer.toString(position),
						Toast.LENGTH_LONG).show();
				
				// 跳转到详情页
				// 代码
				Intent intent = new Intent();
				intent.setClass(Main.this, Detailwifi.class);

				Bundle bundle = new Bundle();
				bundle.putInt("wifiIndex", position);
				ScanResult scan = scanList.get(position);
				bundle.putString("SSID", scan.SSID);
				bundle.putInt("signal", scan.level);
				bundle.putString("encryption", scan.capabilities);
				bundle.putString("mac", scan.BSSID);
				intent.putExtras(bundle);

				startActivity(intent);
			}
		});
		
		scan();
	}
	
	public void scan(){
		scanList = mWifiAdmin.getScanList();  
		listView.setAdapter(new EfficientAdapter(this,scanList)); // 显示内容，指定另一个函数处理
	}

}