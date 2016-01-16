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

				// ��ת������ҳ
				// ����
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
		// ��ȡLocationManager�ؼ�
		myLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		// ��ȡlocation�ؼ�
		mLocation01 = getLocationPrivider(myLocationManager01);
		if (mLocation01 != null) {
			// ��ȡ��ǰλ��
			processLocationUpdated(mLocation01);
		} else {
			gps.setText("error");
		}
		// ͨ��ָ����strLocationPrivider�����Եػ�ȡ��λ��Ϣ��������mLocationListener01������
		myLocationManager01.requestLocationUpdates(strLocationPrivider, 100000, 10, mLocationListener01);

	}

	public void scan() {
		scanList = mWifiAdmin.getScanList();
		listView.setAdapter(new EfficientAdapter(this, scanList)); // ��ʾ���ݣ�ָ����һ����������
	}

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

	// �ӿ�LocationListener��������λ��Ϣ
	public final LocationListener mLocationListener01 = new LocationListener() {
		@Override
		// ����λλ�øı�ʱ��ϵͳ���ô˷������д���
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

	// ��д�������趨Location
	public Location getLocationPrivider(LocationManager lm) {
		Location retLocation = null;
		try {
			// Criteria�࣬�����䷽��������LocationProvider��ص�����
			Criteria mCriteria01 = new Criteria();
			// ���öԾ��ȵ�Ҫ��
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			// ���öԸ߶ȵ�Ҫ��
			mCriteria01.setAltitudeRequired(false);
			// ���öԷ�λ��Ҫ��
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			// ���úĵ�������Ϣ
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			// ��÷���ָ��������LocationPrVider
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			// ��GPS��ȡ����Ķ�λ��Ϣ
			retLocation = lm.getLastKnownLocation(strLocationPrivider);
		} catch (Exception e) {
			gps.setText(e.toString());
			e.printStackTrace();
		}
		return retLocation;
	}
	
	//������Ķ�λ��Ϣת���ɵ���λ�������
		private GeoPoint getGeoByLocation(Location location) {
			GeoPoint gp = null;
			try {
				if (location != null) {
					//�����صĵ�ǰγ������ת����double����
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