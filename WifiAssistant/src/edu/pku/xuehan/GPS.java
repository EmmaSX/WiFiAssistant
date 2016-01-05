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
			//设置对精度的要求
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			//设置对高度的要求
			mCriteria01.setAltitudeRequired(false);
			//设置对方位的要求
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			//设置耗电量的信息
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			//获得符合指定条件的LocationPrVider
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			//从GPS获取最近的定位信息
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

		gps.setText("Your location is \n long:" + String.valueOf((int) currentGeoPoint.getLongitudeE6() / 1E6)
				+ "\n lat:" + String.valueOf((int) currentGeoPoint.getLatitudeE6() / 1E6) + "\n"

		);
	}

	@Override
	//为了计算目的，需要知道是否正在显示任何一种路由信息，如果正在显示信息返回true
	protected boolean isRouteDisplayed() {
		return false;
	}
}