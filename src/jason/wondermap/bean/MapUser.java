package jason.wondermap.bean;

import com.baidu.mapapi.map.Marker;

public class MapUser {
	private User user;
	private Marker mMarker;

	public MapUser(User u) {
		user = u;
	}
	
	public String getObjectId() {
		return user.getObjectId();
	}

	public void setObjectId(String objectId) {
		user.setObjectId(objectId);
	}

	public String getName() {
		return user.getUsername();
	}

	public void setName(String name) {
		user.setUsername(name);
	}
	public String getAvatar(){
		return user.getAvatar();
	}
	
	public double getLat() {
		return user.getLat();
	}

	public void setLat(double lat) {
		user.setLat(lat);
	}

	public double getLng() {
		return user.getLng();
	}

	public void setLng(double lng) {
		user.setLng(lng);
	}

	public Marker getMarker() {
		return mMarker;
	}

	public void setMarker(Marker mMarker) {
		this.mMarker = mMarker;
	}

}
