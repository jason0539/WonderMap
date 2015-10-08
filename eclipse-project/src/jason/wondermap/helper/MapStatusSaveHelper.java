package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.controler.MapControler;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapStatus.Builder;
import com.baidu.mapapi.model.LatLng;

/**
 * 地图状态保存，用于恢复上次应用退出时地图的状态恢复
 * 
 * @author liuzhenhui
 * 
 */
public class MapStatusSaveHelper {
	private final String LAT = "status_lat";
	private final String LNG = "status_lng";
	private final String ZOOM = "status_zoom";
	private final String ROTATE = "status_rotate";
	private final String OVERLOOK = "status_overlook";
	private double lat;
	private double lng;
	private float zoom;
	private float rotate;
	private float overlook;
	private SharePreferenceUtil spUtil;

	public MapStatusSaveHelper() {
		spUtil = WonderMapApplication.getInstance().getSpUtil();
		String latString = WLocationManager.getInstance().getLatitude() + "";
		String lngString = WLocationManager.getInstance().getLongtitude() + "";
		// 获取本地保存的位置，如果本地保存为空则移动到当前定位到的位置，如果当前位置为空，则默认移动到北京
		lat = Double.valueOf(spUtil.getValue(LAT, latString));
		lng = Double.valueOf(spUtil.getValue(LNG, lngString));
		L.d("默认位置" + lat + "," + lng);
		zoom = spUtil.getValue(ZOOM, MapControler.ZoomLevelMin);
		rotate = spUtil.getValue(ROTATE, 0);
		overlook = spUtil.getValue(OVERLOOK, 0);
	}

	public MapStatusUpdate getStatus(BaiduMap map) {
		MapStatus ms = new Builder(map.getMapStatus())
				.target(new LatLng(lat, lng)).zoom(zoom).rotate(rotate)
				.overlook(overlook).build();
		return MapStatusUpdateFactory.newMapStatus(ms);
	}

	public void save(MapStatus ms) {
		if (overlook != ms.overlook || rotate != ms.rotate || zoom != ms.zoom
				|| lat != ms.target.latitude || lng != ms.target.longitude) {
			overlook = ms.overlook;
			rotate = ms.rotate;
			zoom = ms.zoom;
			lat = ms.target.latitude;
			lng = ms.target.longitude;
			spUtil.setValue(LAT, lat + "");
			spUtil.setValue(LNG, lng + "");
			spUtil.setValue(OVERLOOK, overlook);
			spUtil.setValue(ROTATE, rotate);
			spUtil.setValue(ZOOM, zoom);
		}
	}
}
