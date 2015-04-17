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
import com.baidu.mapapi.map.MyLocationData;

/**
 * 用户位置管理类
 * 
 * @author liuzhenhui
 * 
 */
public class WLocationManager {
	public final String PREF_LATITUDE = "latitude";// 经度
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private final int LOCATION_SCAN_SPAN = 30 * 1000;// 定位间隔1秒
	private WonderMapApplication mApplication;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private boolean isFirstLoc = true; // 是否首次定位
	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;

	private double latitude;
	private double longtitude;
	private double lastLatitude;
	private double lastLongtitude;

	// ＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void start() {
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

	public void stop() {
		mLocationClient.unRegisterLocationListener(mMyLocationListener);
		mLocationClient.stop();
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			// 收到新位置
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(latitude).longitude(longtitude)
					.build();
			WMapControler.getInstance().setMyLocationData(locData);
			saveLocation();// 保存当前位置
			if (isFirstLoc) {// 第一次定位成功，移动地图，发送hello消息
				isFirstLoc = false;
				WMapControler.getInstance().moveToLoc(latitude, longtitude);
				// hello消息发送
				PushMsgSendManager.getInstance().sayHello();
			}
		}

	}

	private void saveLocation() {
		// 位置有变化才更新
		if (latitude != lastLatitude || longtitude != lastLongtitude) {
			// 更新位置到服务器
			AccountUserManager.getInstance().updateUserLocation(
					new BmobGeoPoint(longtitude, latitude));
			// 保存到本地
			saveLatitude(latitude + "");
			saveLongtitude(longtitude + "");
			lastLatitude = latitude;
			lastLongtitude = longtitude;
		}

	}

	// 本地存储经纬度的代码，之后考虑重构
	/**
	 * 获取经度
	 */
	private String getSavedLongtitude() {
		String longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude.equals("") ? "0" : longtitude;
	}

	/**
	 * 设置经度
	 */
	private void saveLongtitude(String lon) {
		editor.putString(PREF_LONGTITUDE, lon).commit();
	}

	/**
	 * 获取纬度
	 */
	private String getSavedLatitude() {
		String latitude = preferences.getString(PREF_LATITUDE, "");
		return latitude.equals("") ? "0" : latitude;
	}

	/**
	 * 设置维度
	 */
	private void saveLatitude(String lat) {
		editor.putString(PREF_LATITUDE, lat).commit();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private static WLocationManager instance = null;

	private WLocationManager() {
		mApplication = WonderMapApplication.getInstance();
		preferences = PreferenceManager
				.getDefaultSharedPreferences(mApplication);
		editor = preferences.edit();
		latitude = lastLatitude = Double.valueOf(getSavedLatitude());// 初始默认为上次位置
		longtitude = lastLongtitude = Double.valueOf(getSavedLongtitude());
	}

	public static WLocationManager getInstance() {
		if (instance == null) {
			instance = new WLocationManager();
		}
		return instance;
	}

	// =＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝get set＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public double getLongtitude() {
		return longtitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLastLatitude() {
		return lastLatitude;
	}

	public double getLastLongtitude() {
		return lastLongtitude;
	}

}