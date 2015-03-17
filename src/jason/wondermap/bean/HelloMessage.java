package jason.wondermap.bean;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.utils.SharePreferenceUtil;

import java.io.Serializable;

import com.google.gson.annotations.Expose;

/* 新人加入，广播自己的位置和信息
 * 不转换没有 @Expose 注解的字段
 */
public class HelloMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	@Expose
	private String userId;
	@Expose
	private String channelId;
	@Expose
	private String nickname;
	@Expose
	private String headIcon;
	@Expose
	private long timeSamp;
	@Expose
	private String message;
	@Expose
	private double lat;
	@Expose
	private double lng;
	/**
	 * 新人第一次加入时，会广播这个字段，且值为hello
	 */
	@Expose
	private String hello;
	/**
	 * 接收到新人的hello时，自动回复此字段且值为world
	 */
	@Expose
	private String world;

	public HelloMessage(long time_samp, String message) {
		super();
		SharePreferenceUtil spUtil = WonderMapApplication.getInstance()
				.getSpUtil();
		this.userId = spUtil.getUserId();// 用户id
		this.channelId = spUtil.getChannelId();// 设备id
		this.nickname = spUtil.getUserNick();// 昵称
		this.headIcon = spUtil.getUserHeadPicUrl();// 头像
		this.timeSamp = time_samp;// 时间戳
		this.message = message;// 信息
		this.lat = WMapControler.getInstance().getLat();
		this.lng = WMapControler.getInstance().getLng();
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getChannelId() {
		return channelId;
	}

	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getHeadIcon() {
		return headIcon;
	}

	public void setHeadIcon(String headIcon) {
		this.headIcon = headIcon;
	}

	public long getTimeSamp() {
		return timeSamp;
	}

	public void setTimeSamp(long timeSamp) {
		this.timeSamp = timeSamp;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getHello() {
		return hello;
	}

	public void setHello(String hello) {
		this.hello = hello;
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
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

}
