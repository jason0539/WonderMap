package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.controler.WMapControler;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MyLocationData;

public class WMapLocationManager {
	public final String PREF_LATITUDE = "latitude";// 经度
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	public final int LOCATION_SCAN_SPAN = 1000;// 定位间隔1秒
	private double latitude = 0.0;
	private double longtitude = 0.0;
	private BmobGeoPoint lastPoint = null;// 上一次定位到的经纬度

	private boolean isFirstLoc = true; // 是否首次定位
	public LocationClient mLocationClient;
	public MyLocationListener mMyLocationListener;

	// ＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void init() {
		mLocationClient = new LocationClient(WonderMapApplication.getInstance());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 要加入定位开始的逻辑
		LocationClientOption option = new LocationClientOption();// 定位选项
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(LOCATION_SCAN_SPAN);// 设置扫描间隔
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	public void unInit() {
		mLocationClient.stop();
		mLocationClient.unRegisterLocationListener(mMyLocationListener);
	}

	public BmobGeoPoint getLastPoint() {
		return lastPoint;
	}

	/**
	 * 获取经度
	 */
	public double getLongtitude() {
		return longtitude;
	}

	/**
	 * 获取纬度
	 */
	public double getLatitude() {
		return latitude;
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null)
				return;
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			lastPoint = new BmobGeoPoint(longtitude, latitude);
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(latitude).longitude(longtitude)
					.build();
			WMapControler.getInstance().setMyLocationData(locData);
			if (isFirstLoc) {// 第一次定位成功，移动地图，发送hello消息
				isFirstLoc = false;
				WMapControler.getInstance().moveToLoc(latitude, longtitude);
				// hello消息发送
				PushMsgSendManager.getInstance().sayHello();
			}
		}
	}

	// 本地存储经纬度的代码，之后考虑重构
	/**
	 * 获取经度
	 * 
	 * @return
	 */
	public String getSavedLongtitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(WonderMapApplication.getInstance());
		String longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude;
	}

	/**
	 * 设置经度
	 * 
	 * @param pwd
	 */
	public void saveLongtitude(String lon) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(WonderMapApplication.getInstance());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_LONGTITUDE, lon).commit();
	}

	/**
	 * 获取纬度
	 * 
	 * @return
	 */
	public String getSavedLatitude() {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(WonderMapApplication.getInstance());
		String latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude;
	}

	/**
	 * 设置维度
	 * 
	 * @param pwd
	 */
	public void saveLatitude(String lat) {
		SharedPreferences preferences = PreferenceManager
				.getDefaultSharedPreferences(WonderMapApplication.getInstance());
		SharedPreferences.Editor editor = preferences.edit();
		editor.putString(PREF_LATITUDE, lat).commit();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private static WMapLocationManager instance = null;

	private WMapLocationManager() {
	}

	public static WMapLocationManager getInstance() {
		if (instance == null) {
			instance = new WMapLocationManager();
		}
		return instance;
	}
}
