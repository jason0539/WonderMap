package jason.wondermap.bean;

import java.io.Serializable;

import com.baidu.mapapi.map.Marker;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String UserId;// 用户id
	private String channelId;// 设备id
	private String nick;// 昵称
	private String headIcon;// 头像
	private double lat;
	private double lng;
	private Marker mMarker;
	private String group; // 分组

	/**
	 * @param UserId
	 * @param channelId
	 * @param nick
	 * @param headIcon
	 * @param lat
	 * @param lng
	 * @param mMarker
	 *            更新用户地图坐标的时候可能有用
	 * @param group
	 *            推送中的TAG实现分组发送
	 */
	public User(String UserId, String channelId, String nick, String headIcon,
			double lat, double lng, String group) {
		this.UserId = UserId;
		this.channelId = channelId;
		this.nick = nick;
		this.headIcon = headIcon;
		this.lat = lat;
		this.lng = lng;
		this.group = group;
	}

	public User() {

	}

	public String getUserId() {
		return UserId;
	}

	public void setUserId(String userId) {
		UserId = userId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
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

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "User [UserId=" + UserId + ", channelId=" + channelId
				+ ", nick=" + nick + ", headIcon=" + headIcon + ",lat =" + lat
				+ ",lng = " + lng + ", group=" + group + "]";
	}

}
