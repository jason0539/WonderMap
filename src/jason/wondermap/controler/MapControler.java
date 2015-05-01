package jason.wondermap.controler;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.helper.MapStatusSaveHelper;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.MapMarkerView;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapDoubleClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapLongClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMapStatusChangeListener;
import com.baidu.mapapi.map.BaiduMap.OnMapTouchListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatus.Builder;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**
 * 地图控制类，定位控制，视图控制
 * 
 * @author liuzhenhui
 * 
 */
public class MapControler {
	private Context mContext;
	private MapStatusSaveHelper statusSaveHelper;
	private MapView mMapView;// 地图图层
	private BaiduMap mBaiduMap;// 地图控制
	private LatLng currentPt;// 当前触摸地点
	private String touchType;// 触摸事件类型
	private InfoWindow mInfoWindow;// 点击用户图标弹出窗,暂时无用
	private MapStatus mapStatus;

	private LocationMode mCurrentMode; // 定位模式（普通、跟随、罗盘）
	private BitmapDescriptor mCurrentMarker;// 定位图标样式

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝地图动作控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// －－－－－－－－－－－－－－－－－－－－－放大缩小控制===================================
	public static final int Speed_Normal = 700;

	/**
	 * 默认缩小3倍
	 */
	public void zoomOut() {
		L.d(WModel.MapControl, "缩小");
		float level = 0;
		level = mBaiduMap.getMapStatus().zoom - 3;
		zoomOut(level < 3 ? 3 : level);
	}

	/**
	 * 缩小到指定级别
	 */
	public void zoomOut(float level) {
		zoomOut(mBaiduMap.getMapStatus().target, level);
	}

	/**
	 * 以某点为中心缩放到指定级别
	 */
	public void zoomOut(LatLng latLng, float level) {
		zoomOut(latLng, level, Speed_Normal);
	}

	/**
	 * 指定中心，以指定速度，缩放到级别level,最后一级慢速
	 */
	public void zoomOut(LatLng latLng, final float level, int speed) {
		if (level < 3 || level > 20) {
			L.d("请输入正确的缩放级别");
			return;
		}
		MapStatus ms = new Builder(mBaiduMap.getMapStatus()).target(latLng)
				.zoom(level).build();
		MapStatusUpdate update = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(update, speed);
	}

	/**
	 * 默认放大3倍
	 */
	public void zoomIn(boolean isCentreFirst) {
		if (mBaiduMap.getMapStatus().zoom == mBaiduMap.getMaxZoomLevel()) {
			return;
		}
		L.d(WModel.MapControl, "放大");
		float level = mBaiduMap.getMapStatus().zoom + 3;
		zoomIn(level > 20 ? 20 : level, isCentreFirst);
	}

	/**
	 * 放大到指定级别
	 */
	public void zoomIn(float level, boolean isCenterFirst) {
		zoomIn(mBaiduMap.getMapStatus().target, level, isCenterFirst);
	}

	/**
	 * 以某点为中心放大到指定级别
	 */
	public void zoomIn(LatLng latLng, float level, boolean isCenterFirst) {
		zoomIn(latLng, level, Speed_Normal, isCenterFirst);
	}

	/**
	 * 指定中心，以指定速度，放大到级别level isCenterFirst是否需要先移动到指定点，再放大
	 */
	public void zoomIn(LatLng latLng, final float level, final int speed,
			boolean isCenterFirst) {
		if (level < 3 || level > 20) {
			L.d("请输入正确的缩放级别");
			return;
		}
		if (isCenterFirst) {
			LatLng temp = new LatLng(mBaiduMap.getMapStatus().target.latitude,
					mBaiduMap.getMapStatus().target.longitude);
			java.text.DecimalFormat df = new java.text.DecimalFormat("#.000");
			L.d(WModel.MapControl, "地图现状：lat－" + temp.latitude + "；lng－"
					+ temp.longitude);
			L.d(WModel.MapControl, "要求状态：lat－" + latLng.latitude + "；lng－"
					+ latLng.longitude);
			// 如果已经位于中心，则直接缩放就可以，
			if (df.format(temp.latitude).equals(df.format(latLng.latitude))
					&& df.format(temp.longitude).equals(
							df.format(latLng.longitude))) {
				L.d(WModel.MapControl, "状态相同，直接缩放");
				mapStatus = new Builder(mBaiduMap.getMapStatus()).zoom(level)
						.build();
				MapStatusUpdate update = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				mBaiduMap.animateMapStatus(update, speed);
				return;
			} else {
				L.d(WModel.MapControl, "状态不同，直接缩放");
				mapStatus = new Builder(mBaiduMap.getMapStatus())
						.target(latLng).build();
				MapStatusUpdate update = MapStatusUpdateFactory
						.newMapStatus(mapStatus);
				mBaiduMap.animateMapStatus(update, speed);
				mBaiduMap
						.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {

							@Override
							public void onMapStatusChangeStart(MapStatus arg0) {

							}

							@Override
							public void onMapStatusChangeFinish(MapStatus arg0) {
								mapStatus = new Builder(mBaiduMap
										.getMapStatus()).zoom(level).build();
								MapStatusUpdate update = MapStatusUpdateFactory
										.newMapStatus(mapStatus);
								mBaiduMap.animateMapStatus(update, speed);
								mBaiduMap.setOnMapStatusChangeListener(null);
							}

							@Override
							public void onMapStatusChange(MapStatus arg0) {

							}
						});
			}
		} else {
			mapStatus = new Builder(mBaiduMap.getMapStatus()).target(latLng)
					.zoom(level).build();
			MapStatusUpdate update = MapStatusUpdateFactory
					.newMapStatus(mapStatus);
			mBaiduMap.animateMapStatus(update, speed);
		}

	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝旋转控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 处理旋转 旋转角范围： -180 ~ 180 , 单位：度 逆时针旋转
	 */
	public void perfomRotate(int rotateAngle) {
		if (rotateAngle < -180 || rotateAngle > 180) {
			L.d("请输入正确的旋转角度");
		}
		MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus()).rotate(
				rotateAngle).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(u);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝俯角控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 处理俯视 俯角范围： -45 ~ 0 , 单位： 度
	 */
	public void perfomOverlook(int overlookAngle) {
		if (overlookAngle < -45 || overlookAngle > 0) {
			L.d("请输入正确的俯角");
		}
		MapStatus ms = new MapStatus.Builder(mBaiduMap.getMapStatus())
				.overlook(overlookAngle).build();
		MapStatusUpdate u = MapStatusUpdateFactory.newMapStatus(ms);
		mBaiduMap.animateMapStatus(u);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝视图控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 切换普通地图和卫星地图
	 */
	public void changeMapType() {
		int type = mBaiduMap.getMapType();
		if (type == BaiduMap.MAP_TYPE_NORMAL) {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);
		} else {
			mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝方向 罗盘 控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 转化图层的显示方式，普通、方向、罗盘
	 * 
	 * @param mode
	 */
	public void changeLocationMode(LocationMode mode) {
		switch (mode) {
		case NORMAL:
			mCurrentMode = LocationMode.NORMAL;
			break;
		case FOLLOWING:
			mCurrentMode = LocationMode.FOLLOWING;
			break;
		case COMPASS:
			mCurrentMode = LocationMode.COMPASS;
			break;
		default:
			break;
		}
		// mode - 定位图层显示方式, 默认为 LocationMode.NORMAL 普通态
		// enableDirection - 是否允许显示方向信息
		// customMarker - 设置用户自定义定位图标，null则使用默认图标
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝显示位置控制＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 设置当前定位到的位置
	 * 
	 * @param myLocData
	 */
	public void setMyLocationData(MyLocationData myLocData) {
		if (mBaiduMap == null) {
			L.d("mBaiduMap is null");
			return;
		}
		mBaiduMap.setMyLocationData(myLocData);
	}

	public static final int ZoomLevelMax = 20;
	public static final int ZoomLevelSchool = 17;
	public static final int ZoomLevelDistrict = 14;
	public static final int ZoomLevelCity = 11;
	public static final int ZoomLevelProvince = 7;
	public static final int ZoomLevelCountry = 5;
	public static final int ZoomLevelMin = 3;

	/**
	 * 移动到我的位置
	 */
	public void moveToMylocation() {
		moveToLoc(new LatLng(WLocationManager.getInstance().getLatitude(),
				WLocationManager.getInstance().getLongtitude()));
	}

	/**
	 * 以当前缩放水平移动到指定地点
	 */
	public void moveToLoc(LatLng latLng) {
		moveToLoc(latLng, mBaiduMap.getMapStatus().zoom);
	}

	/**
	 * 地图移动到指定位置
	 */
	public void moveToLoc(LatLng latLng, float level) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, level);// 默认以当前坐标为中心，最大化显示
		mBaiduMap.animateMapStatus(u, Speed_Normal);
	}

	public void moveToMylocationLongPress() {
		// 缩放水平在校级，则直接移动过去
		if (mBaiduMap.getMapStatus().zoom > ZoomLevelSchool) {
			moveToMylocation();
		}
		// 否则放大到校级并且移动到当前位置
		else {
			zoomIn(new LatLng(WLocationManager.getInstance().getLatitude(),
					WLocationManager.getInstance().getLongtitude()),
					ZoomLevelSchool, true);
		}
	}

	/**
	 * 自定义定位图标的样式 R.drawable.icon_geo，传入null则恢复默认
	 */
	public void changeLocationMarker(int id) {
		// 修改为自定义marker
		mCurrentMarker = BitmapDescriptorFactory.fromResource(id);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	/**
	 * 设置地图显示位置
	 * 
	 * @param latlng
	 */
	public void setMapStatus(LatLng latlng) {
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(latlng));
	}

	public void setMapStatus(MapStatus status) {
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newMapStatus(status));
	}

	public MapStatus getMapStatus() {
		return mBaiduMap.getMapStatus();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝Marker相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 地图上面显示marker，默认图标
	 */
	public Marker addMarker(double lat, double lng) {
		return addMarker(lat, lng, R.drawable.icon_gcoding);
	}

	/**
	 * 地图上面显示marker，指定图标
	 */
	public Marker addMarker(double lat, double lng, int icon) {
		BitmapDescriptor bd = BitmapDescriptorFactory.fromResource(icon);
		LatLng ll = new LatLng(lat, lng);
		OverlayOptions oo = new MarkerOptions().position(ll).icon(bd);
		return (Marker) (mBaiduMap.addOverlay(oo));
	}

	public Marker addMarker(double lat, double lng, View view) {
		BitmapDescriptor bd = BitmapDescriptorFactory.fromView(view);
		LatLng ll = new LatLng(lat, lng);
		OverlayOptions oo = new MarkerOptions().position(ll).icon(bd);
		return (Marker) (mBaiduMap.addOverlay(oo));
	}

	public void clearMarker() {
		mBaiduMap.clear();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝MapUser相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 添加用户在地图上的地标
	 * 
	 * @param user
	 * @return 返回该用户的地标，唯一，位置变动则更新该地标位置
	 */
	public Marker addUser(MapUser user) {
		View view = MapMarkerView.createView(user);
		Marker marker = addMarker(user.getLat(), user.getLng(), view);
		return marker;
	}

	/**
	 * 更新用户位置
	 * 
	 * @param oldUser
	 */
	public void updateUserPosition(MapUser oldUser) {
		L.d(oldUser.getName() + "的新位置 lat :" + oldUser.getLat() + ",lng :"
				+ oldUser.getLng());
		Marker marker = oldUser.getMarker();
		marker.setPosition(new LatLng(oldUser.getLat(), oldUser.getLng()));
		mBaiduMap.hideInfoWindow();
	}

	/**
	 * 添加的Marker点击事件监听器
	 */
	OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker mark) {
			// 地图marker被点击，从WMapUserManager取出目前所有用户，判断点击的是那个
			HashMap<String, MapUser> mapUsers = MapUserManager.getInstance()
					.getMapUsers();
			Iterator<Entry<String, MapUser>> iterator = mapUsers.entrySet()
					.iterator();
			Entry<String, MapUser> entry;
			while (iterator.hasNext()) {
				entry = iterator.next();
				if (entry.getValue().getMarker() == mark) {
					onMyMapMarkerClick(entry.getValue(), mark);
				}
			}
			return true;
		};
	};

	/**
	 * 点击地图marker的事件处理
	 * 
	 * @param user
	 * @param marker
	 */
	private void onMyMapMarkerClick(final MapUser user, Marker marker) {
		// TODO 点击之后实时获取用户姓名，有可能用户有更改
		Bundle bundle = new Bundle();
		bundle.putString(UserInfo.USER_ID, user.getObjectId());
		BaseFragment.getWMFragmentManager().showFragment(
				WMFragmentManager.TYPE_USERINFO, bundle);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝点击事件相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 监听地图的各种触摸事件，之后应该实现为多个回调都可以接收到事件，暂时不使用
	 */
	public void setOnMapTouchListener(OnMapTouchListener listener) {
		mBaiduMap.setOnMapTouchListener(listener);
	}

	public void setOnMapClickListener(OnMapClickListener listener) {
		mBaiduMap.setOnMapClickListener(listener);
	}

	public void setOnMapLongClickListener(OnMapLongClickListener listener) {
		mBaiduMap.setOnMapLongClickListener(listener);
	}

	public void setOnMapDoubleClickListener(OnMapDoubleClickListener listener) {
		mBaiduMap.setOnMapDoubleClickListener(listener);
	}

	public void setOnMapStatusChangeListener(OnMapStatusChangeListener listener) {
		mBaiduMap.setOnMapStatusChangeListener(listener);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 更新地图状态输出log
	 */
	private void updateMapState() {
		String state = "";
		if (currentPt == null) {
			state = "点击、长按、双击地图以获取经纬度和地图状态";
		} else {
			state = String.format(touchType + ",当前经度： %f 当前纬度：%f",
					currentPt.longitude, currentPt.latitude);
		}
		state += "\n";
		MapStatus ms = mBaiduMap.getMapStatus();
		state += String.format("zoom=%.1f rotate=%d overlook=%d", ms.zoom,
				(int) ms.rotate, (int) ms.overlook);
		L.d(WModel.MapClick, state);
		statusSaveHelper.save(ms);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 初始化地图控制，没有依赖
	 */
	public void init(MapView mapView) {
		mContext = WonderMapApplication.getInstance();
		mMapView = mapView;
		mMapView.showScaleControl(false);// 隐藏比例尺
		mMapView.showZoomControls(false);// 隐藏缩放控件
		mMapView.removeViewAt(1);// 隐藏百度logo
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
		mBaiduMap.setMaxAndMinZoomLevel(20, (float) 4.5);
		// 用户的marker点击监听
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);//
		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = null;// null则为默认
		statusSaveHelper = new MapStatusSaveHelper();
		initListener();
		// 恢复到上次的地图状态
		mBaiduMap.animateMapStatus(statusSaveHelper.getStatus(mBaiduMap));
	}
	private boolean visible = false;
	public void onPause() {
		mMapView.onPause();
		visible = false;
	}

	public void onResume() {
		mMapView.onResume();
		visible = true;
	}
	public boolean isVisible(){
		return visible;
	}
	public void unInit() {
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
		mMapView.onDestroy();
	}

	// 初始化默认监听
	private void initListener() {
		mBaiduMap.setOnMapTouchListener(new OnMapTouchListener() {

			@Override
			public void onTouch(MotionEvent event) {

			}
		});

		mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
			public void onMapClick(LatLng point) {
				touchType = "单击";
				currentPt = point;
				// updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				touchType = "长按";
				currentPt = point;
				// updateMapState();
				float level = mBaiduMap.getMapStatus().zoom + 3;
				zoomIn(point, level > ZoomLevelMax ? ZoomLevelMax : level, true);
			}
		});
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				touchType = "双击";
				currentPt = point;
				// zoomIn(point);
				// updateMapState();
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				// updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				// updateMapState();
			}
		});
	}

	private static MapControler instance = null;

	private MapControler() {
	}

	public static MapControler getInstance() {
		if (instance == null) {
			instance = new MapControler();
		}
		return instance;
	}
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝废弃代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// /**
	// * 点击地图marker的事件处理
	// *
	// * @param user
	// * @param marker
	// */
	// private void onMyMapMarkerClick(final MapUser user, Marker marker) {
	// Button button = new Button(WonderMapApplication.getInstance());
	// button.setBackgroundResource(R.drawable.popup);
	// // TODO 点击之后实时获取用户姓名，有可能用户有更改
	// button.setText(user.getName());
	// OnInfoWindowClickListener listener = null;
	// listener = new OnInfoWindowClickListener() {
	// public void onInfoWindowClick() {
	// Bundle bundle = new Bundle();
	// bundle.putString(UserInfo.USER_ID, user.getObjectId());
	// BaseFragment.getWMFragmentManager().showFragment(
	// WMFragmentManager.TYPE_USERINFO, bundle);
	// }
	// };
	// mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button),
	// marker.getPosition(), -47, listener);
	// mBaiduMap.showInfoWindow(mInfoWindow);
	// }
}
