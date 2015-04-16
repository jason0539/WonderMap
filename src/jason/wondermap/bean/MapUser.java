package jason.wondermap.bean;

import com.baidu.mapapi.map.Marker;

public class MapUser {
	private String name;
	private double lat;
	private double lng;
	private Marker mMarker;

	public MapUser(String name, double lat, double lng) {
		this.name = name;
		this.lat = lat;
		this.lng = lng;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getLat() {
		return lat;
	}

	public void setLat(double lat) {
		this.lat = lat;
	}

	public double getLng() {
		return lng;
	}

	public void setLng(double lng) {
		this.lng = lng;
	}

	public Marker getMarker() {
		return mMarker;
	}

	public void setMarker(Marker mMarker) {
		this.mMarker = mMarker;
	}

}
