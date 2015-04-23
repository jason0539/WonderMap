package jason.wondermap.bean;

import cn.bmob.v3.datatype.BmobGeoPoint;

public class WMapGeoPoint extends BmobGeoPoint {

	private static final long serialVersionUID = -8787246015009895184L;
	public WMapGeoPoint(){
		super();
	}
	public WMapGeoPoint(double lng,double lat){
		super(lng, lat);
	}
}
