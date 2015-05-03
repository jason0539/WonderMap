package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.controler.MapControler;
import jason.wondermap.utils.L;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;

/**
 * 用户位置管理类
 * 
 * @author liuzhenhui 已经本地保存了上次位置，进入应用马上发送上次位置（假设还位于上次位置），等定位成功再判断是否需要发送新位置
 */
public class WLocationManager {
	public final String PREF_LATITUDE = "latitude";// 经度
	public final String PREF_LONGTITUDE = "longtitude";// 经度
	private final int LOCATION_SCAN_SPAN = 60 * 1000;// 定位间隔1分钟
	private WonderMapApplication mApplication;
	private SharedPreferences preferences;
	private SharedPreferences.Editor editor;

	private LocationClient mLocationClient;
	private MyLocationListener mMyLocationListener;
	private BDLocation lastBdLocation;
	private BmobGeoPoint lastGeoPoint;
	private GeoCoder mSearch = null;

	private double latitude;
	private double longtitude;
	private double lastLatitude;
	private double lastLongtitude;
	private boolean hasLocationChanged;

	// ＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 使用定位，bmobPush服务
	 */
	public void start() {
		mLocationClient = new LocationClient(WonderMapApplication.getInstance());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		// 要加入定位开始的逻辑
		LocationClientOption option = new LocationClientOption();// 定位选项
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(LOCATION_SCAN_SPAN);// 设置扫描间隔
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
		mLocationClient.start();
		mSearch = GeoCoder.newInstance();
	}

	public void stop() {
		//有可能在MainActivity还没进入MapHome，定位还没初始化，所以不能销毁
		if (mLocationClient == null) {
			return;
		}
		mLocationClient.unRegisterLocationListener(mMyLocationListener);
		mLocationClient.stop();
		PushMsgSendManager.getInstance().destroy();
		mSearch = null;
	}

	/**
	 * 定位SDK监听函数
	 */
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null || location.getLatitude() == 0
					|| location.getLongitude() == 0)
				return;
			lastBdLocation = location;
			lastGeoPoint = new BmobGeoPoint(location.getLongitude(),
					location.getLatitude());
			// 收到新位置
			latitude = location.getLatitude();
			longtitude = location.getLongitude();
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(latitude).longitude(longtitude)
					.build();
			MapControler.getInstance().setMyLocationData(locData);
			saveLocation();// 保存当前位置
		}
	}

	private void saveLocation() {
		// 位置有变化才更新,
		if (latitude != lastLatitude || longtitude != lastLongtitude) {
			hasLocationChanged = true;
			// 更新位置到服务器
			AccountUserManager.getInstance().updateUserLocation(lastGeoPoint);
			// 推送给其他人，每隔一段时间更新一次位置，以后陌生人不更新，好友实时更新
			PushMsgSendManager.getInstance().sayHello();
			// 保存到本地
			saveLatitude(latitude + "");
			saveLongtitude(longtitude + "");
			lastLatitude = latitude;
			lastLongtitude = longtitude;
		}

	}

	public boolean isLocationChanged() {
		if (hasLocationChanged) {
			hasLocationChanged = false;
			return true;
		}
		return false;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝GEO相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void setOnGetGeoCodeResultListener(
			OnGetGeoCoderResultListener listener) {
		mSearch.setOnGetGeoCodeResultListener(listener);
	}

	public void reverseGeoCode(ReverseGeoCodeOption option) {
		mSearch.reverseGeoCode(option);
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
		L.d("第一次获取到lat" + latitude + ";第一次lng" + longtitude);
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

	public LatLng getMyLocation() {
		return new LatLng(latitude, longtitude);
	}

	public BDLocation getBDLocation() {
		return lastBdLocation;
	}

	public BmobGeoPoint getBmobGeoPoint() {
		return lastGeoPoint;
	}

	// =＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝本地位置存取＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 获取经度
	 */
	private String getSavedLongtitude() {
		String longtitude = preferences.getString(PREF_LONGTITUDE, "");
		return longtitude.equals("") ? "116.402724" : longtitude;
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
		return latitude.equals("") ? "39.916439" : latitude;
	}

	/**
	 * 设置维度
	 */
	private void saveLatitude(String lat) {
		editor.putString(PREF_LATITUDE, lat).commit();
	}
}
