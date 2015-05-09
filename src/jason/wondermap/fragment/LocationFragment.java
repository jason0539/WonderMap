package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.controler.MapControler;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cn.bmob.im.util.BmobLog;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * 发送位置和位置浏览
 * 
 * @author liuzhenhui
 * 
 */
public class LocationFragment extends ContentFragment implements
		OnGetGeoCoderResultListener {
	private static final String TAG = LocationFragment.class.getSimpleName();
	private MapStatus lastMapStatus;
	private BDLocation lastLocation = null;
	private ViewGroup mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d(TAG, "onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (view != null) {
			view.setClickable(false); // 允许地图可点击
		}
		return view;
	}

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		L.d(TAG, "onCreateContentView");
		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_location,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		L.d(TAG, "onInitView");
		MapControler.getInstance().clearMarker();
		MapUserManager.getInstance().setNeedToUpdate();
		lastMapStatus = MapControler.getInstance().getMapStatus();
		initBaiduMap();
	}

	private void initBaiduMap() {
		WLocationManager.getInstance().setOnGetGeoCodeResultListener(this);
		// 注册 SDK 广播监听者
		String type = mShowBundle.getString(UserInfo.TYPE);
		if (type.equals("select")) {// 选择发送位置
			initTopBarForBoth(mRootView, "位置", R.drawable.btn_login_selector,
					"发送", new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							gotoChatPage();
						}
					});
			MapControler.getInstance().moveToMylocation();
			lastLocation = WLocationManager.getInstance().getBDLocation();
			String address = lastLocation.getAddrStr();
			if (address != null && !address.equals("")) {
				lastLocation.setAddrStr(address);
			} else {
				// 反Geo搜索
				WLocationManager.getInstance().reverseGeoCode(new ReverseGeoCodeOption()
						.location(new LatLng(lastLocation.getLatitude(),
								lastLocation.getLongitude())));
			}
		} else {// 查看当前位置
			initTopBarForLeft(mRootView, "位置");
			double lat = mShowBundle.getDouble(UserInfo.LATITUDE);
			double lng = mShowBundle.getDouble(UserInfo.LONGTITUDE);
			L.d("lat :" + lat + ",lng:" + lng);
			LatLng latlng = new LatLng(lat, lng);// 维度在前，经度在后
			MapControler.getInstance().setMapStatus(latlng);
			MapControler.getInstance().addMarker(lat,lng);
			// 显示当前位置图标
			// OverlayOptions ooA = new MarkerOptions().position(latlng)
			// .icon(bdgeo).zIndex(9);
			// mBaiduMap.addOverlay(ooA);
		}
	}

	/**
	 * 回到聊天界面
	 */
	private void gotoChatPage() {
		if (lastLocation != null) {
			Bundle bundle = new Bundle();
			bundle.putInt(UserInfo.INTENT,
					WMapConstants.REQUESTCODE_TAKE_LOCATION);
			bundle.putDouble(UserInfo.X, lastLocation.getLongitude());
			bundle.putDouble(UserInfo.Y, lastLocation.getLatitude());
			bundle.putString(UserInfo.ADDRESS, lastLocation.getAddrStr());
			wmFragmentManager.back(bundle);
		} else {
			ShowToast("获取地理位置信息失败!");
		}
	}

	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {

	}

	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			ShowToast("抱歉，未能找到结果");
			return;
		}
		lastLocation.setAddrStr(result.getAddress());
		BmobLog.i("反编码得到的地址：" + result.getAddress());
	}

	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "位置发送浏览页");
	}
	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		MapControler.getInstance().setMapStatus(lastMapStatus);
		
	}

}
