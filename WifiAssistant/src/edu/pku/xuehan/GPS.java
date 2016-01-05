package edu.pku.xuehan;

import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GPS extends MapActivity {
	private TextView gps;
	// private MapView myMapView01;
	private GeoPoint currentGeoPoint;
	private LocationManager myLocationManager01;
	private String strLocationPrivider = "";
	private Location mLocation01 = null;
	private int intZoomLevel = 12;

	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);

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
			//���öԾ��ȵ�Ҫ��
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			//���öԸ߶ȵ�Ҫ��
			mCriteria01.setAltitudeRequired(false);
			//���öԷ�λ��Ҫ��
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			//���úĵ�������Ϣ
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			//��÷���ָ��������LocationPrVider
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			//��GPS��ȡ����Ķ�λ��Ϣ
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

		gps.setText("Your location is \n long:" + String.valueOf((int) currentGeoPoint.getLongitudeE6() / 1E6)
				+ "\n lat:" + String.valueOf((int) currentGeoPoint.getLatitudeE6() / 1E6) + "\n"

		);
	}

	@Override
	//Ϊ�˼���Ŀ�ģ���Ҫ֪���Ƿ�������ʾ�κ�һ��·����Ϣ�����������ʾ��Ϣ����true
	protected boolean isRouteDisplayed() {
		return false;
	}
}