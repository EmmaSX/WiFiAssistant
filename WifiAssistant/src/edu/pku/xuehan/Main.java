package edu.pku.xuehan;

import java.util.List;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import edu.pku.xuehan.db.DBHelper;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends MapActivity {

	ListView listView;
	List<ScanResult> scanList;
	private ImageButton refresh;

	private String mScanResults;
	private WifiAdmin mWifiAdmin;

	private TextView gps;
	// private MapView myMapView01;
	private GeoPoint currentGeoPoint;
	private LocationManager myLocationManager01;
	private String strLocationPrivider = "";
	private Location mLocation01 = null;
	private int intZoomLevel = 12;
	private Double lon;
	private Double lat;
	
	private DBHelper dbHelper;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		refresh = (ImageButton) findViewById(R.id.refresh);
		listView = (ListView) findViewById(R.id.wifi_list);
		WifiAdmin.init(Main.this);
		mWifiAdmin = WifiAdmin.getInstance();
		
		dbHelper = new DBHelper(this);

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

		gps = (TextView) findViewById(R.id.gps);
		// myMapView01 = (MapView)findViewById(R.id.mapview);
		// 获取LocationManager控件
		myLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// 获取location控件
		mLocation01 = getLocationPrivider(myLocationManager01);
		if (mLocation01 != null) {
			// 获取当前位置
			processLocationUpdated(mLocation01);
		} else {
			gps.setText("error");
		}
		// 通过指定的strLocationPrivider周期性地获取定位信息，并触发mLocationListener01触发器
		myLocationManager01.requestLocationUpdates(strLocationPrivider, 100000, 10, mLocationListener01);

	}

	public void scan() {
		scanList = mWifiAdmin.getScanList();
		listView.setAdapter(new EfficientAdapter(this, scanList)); // 显示内容，指定另一个函数处理
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// 接口LocationListener，监听定位信息
	public final LocationListener mLocationListener01 = new LocationListener() {
		@Override
		// 当定位位置改变时，系统调用此方法进行处理
		public void onLocationChanged(Location location) {
			processLocationUpdated(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}
	};

	// 新写方法来设定Location
	public Location getLocationPrivider(LocationManager lm) {
		Location retLocation = null;
		try {
			// Criteria类，调用其方法设置与LocationProvider相关的条件
			Criteria mCriteria01 = new Criteria();
			// 设置对精度的要求
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			// 设置对高度的要求
			mCriteria01.setAltitudeRequired(false);
			// 设置对方位的要求
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			// 设置耗电量的信息
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			// 获得符合指定条件的LocationPrVider
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			// 从GPS获取最近的定位信息
			retLocation = lm.getLastKnownLocation(strLocationPrivider);
		} catch (Exception e) {
			gps.setText(e.toString());
			e.printStackTrace();
		}
		return retLocation;
	}
	
	//将传入的定位信息转换成地理位置坐标点
		private GeoPoint getGeoByLocation(Location location) {
			GeoPoint gp = null;
			try {
				if (location != null) {
					//将返回的当前纬度坐标转换成double类型
					double geoLatitude = location.getLatitude() * 1E6;
					double geoLongitude = location.getLongitude() * 1E6;
					gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return gp;
		}

		private void processLocationUpdated(Location location) {

			currentGeoPoint = getGeoByLocation(location);

			// refreshMapViewByGeoPoint(currentGeoPoint, myMapView01, intZoomLevel,
			// true);
			
			lon = currentGeoPoint.getLongitudeE6() / 1E6;
			lat = currentGeoPoint.getLatitudeE6() / 1E6;
			dbHelper.saveLocation(scanList, lat, lon);
			gps.setText("Your location is \n long:" + String.valueOf((int) currentGeoPoint.getLongitudeE6() / 1E6)
					+ "\n lat:" + String.valueOf((int) currentGeoPoint.getLatitudeE6() / 1E6) + "\n"

			);
		}
}