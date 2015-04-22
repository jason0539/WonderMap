package jason.wondermap.controler;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Button;

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
import com.baidu.mapapi.map.InfoWindow.OnInfoWindowClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
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
public class WMapControler {
	private Context mContext;
	private MapView mMapView;// 地图图层
	private BaiduMap mBaiduMap;// 地图控制
	private LatLng currentPt;// 当前触摸地点
	private String touchType;// 触摸事件类型
	private InfoWindow mInfoWindow;// 点击用户图标弹出窗

	private LocationMode mCurrentMode; // 定位模式（普通、跟随、罗盘）
	private BitmapDescriptor mCurrentMarker;// 定位图标样式

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 处理缩放 sdk 缩放级别范围： [3.0,19.0]，越大越详细
	 */
	public void perfomZoom(float zoomLevel) {
		if (zoomLevel < 3 || zoomLevel > 19) {
			L.d("请输入正确的缩放级别");
			return;
		}
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoomLevel);
		mBaiduMap.animateMapStatus(u);
	}

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
		// customMarker - 设置用户自定义定位图标，可以为 null
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	/**
	 * 自定义定位图标的样式
	 * 
	 * @param id
	 *            R.drawable.icon_geo，传入null则恢复默认
	 */
	public void changeLocationMarker(int id) {
		// 修改为自定义marker
		mCurrentMarker = BitmapDescriptorFactory.fromResource(id);
		mBaiduMap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));
	}

	public void setMyLocationData(MyLocationData myLocData) {
		if (mBaiduMap == null) {
			L.d("mBaiduMap is null");
			return;
		}
		mBaiduMap.setMyLocationData(myLocData);
	}

	public void moveToLoc(double lat, double lng) {
		LatLng ll = new LatLng(lat, lng);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(ll, 19);// 默认以当前坐标为中心，最大化显示
		mBaiduMap.animateMapStatus(u);
	}

	/**
	 * 地图上面显示marker，指定图标
	 */
	public Marker addMarker(double lat, double lng) {
		BitmapDescriptor bd = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
		LatLng ll = new LatLng(lat, lng);
		OverlayOptions oo = new MarkerOptions().position(ll).icon(bd);
		return (Marker) (mBaiduMap.addOverlay(oo));
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
	public void clearMarker(){
		mBaiduMap.clear();
	}
	/**
	 * 添加用户在地图上的地标
	 * 
	 * @param user
	 * @return 返回该用户的地标，唯一，位置变动则更新该地标位置
	 */
	public Marker addUser(MapUser user) {
		Marker marker = addMarker(user.getLat(), user.getLng());
		return marker;
	}

	/**
	 * 更新用户位置
	 * 
	 * @param oldUser
	 */
	public void updateUserPosition(MapUser oldUser) {
		L.d("updateUserPosition lat :" + oldUser.getLat() + ",lng :"
				+ oldUser.getLng());
		Marker marker = oldUser.getMarker();
		L.d("updateUserPosition position :" + marker.getPosition());
		marker.setPosition(new LatLng(oldUser.getLat(), oldUser.getLng()));
		mBaiduMap.hideInfoWindow();
	}

	OnMarkerClickListener onMarkerClickListener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker mark) {
			// 地图marker被点击，从WMapUserManager取出目前所有用户，判断点击的是那个
			ArrayList<MapUser> mapUsers = MapUserManager.getInstance()
					.getMapUsers();
			for (MapUser user : mapUsers) {
				if (user.getMarker() == mark) {
					onMyMapMarkerClick(user, mark);
				}
			}
			return true;
		}
	};

	/**
	 * 点击地图marker的事件处理
	 * 
	 * @param user
	 * @param marker
	 */
	private void onMyMapMarkerClick(final MapUser user, Marker marker) {
		Button button = new Button(WonderMapApplication.getInstance());
		button.setBackgroundResource(R.drawable.popup);
		//TODO 点击之后实时获取用户姓名，有可能用户有更改
		button.setText(user.getName());
		OnInfoWindowClickListener listener = null;
		listener = new OnInfoWindowClickListener() {
			public void onInfoWindowClick() {
				Bundle bundle = new Bundle();
				bundle.putString(UserInfo.USER_ID, user.getObjectId());
				BaseFragment.getWMFragmentManager().showFragment(
						WMFragmentManager.TYPE_USERINFO, bundle);
			}
		};
		mInfoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(button),
				marker.getPosition(), -47, listener);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}

	/**
	 * 监听地图的各种触摸事件，之后应该实现为多个回调都可以接收到事件，暂时不使用
	 * 
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
		// L.d(state);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	public void init(MapView mapView) {
		mContext = WonderMapApplication.getInstance();
		mMapView = mapView;
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setOnMarkerClickListener(onMarkerClickListener);//
		// 用户的marker点击监听 
		mCurrentMode = LocationMode.NORMAL;
		mCurrentMarker = null;// null则为默认
		mBaiduMap.setMyLocationEnabled(true); // 开启定位图层
		initListener();
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
				updateMapState();
			}

			public boolean onMapPoiClick(MapPoi poi) {
				return false;
			}
		});
		mBaiduMap.setOnMapLongClickListener(new OnMapLongClickListener() {
			public void onMapLongClick(LatLng point) {
				touchType = "长按";
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapDoubleClickListener(new OnMapDoubleClickListener() {
			public void onMapDoubleClick(LatLng point) {
				touchType = "双击";
				currentPt = point;
				updateMapState();
			}
		});
		mBaiduMap.setOnMapStatusChangeListener(new OnMapStatusChangeListener() {
			public void onMapStatusChangeStart(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChangeFinish(MapStatus status) {
				updateMapState();
			}

			public void onMapStatusChange(MapStatus status) {
				updateMapState();
			}
		});
	}

	public void unInit() {
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
	}

	private static WMapControler instance = null;

	private WMapControler() {
	}

	public static WMapControler getInstance() {
		if (instance == null) {
			instance = new WMapControler();
		}
		return instance;
	}
}
