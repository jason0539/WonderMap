package jason.wondermap.bean;

import java.io.Serializable;

public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	private String UserId;// 用户id
	private String channelId;// 设备id
	private String nick;// 昵称
	private String headIcon;// 头像
	private int group; // 分组

	/**
	 * @param UserId 
	 * @param channelId
	 * @param nick
	 * @param headIcon
	 * @param group 推送中的TAG实现分组发送
	 */
	public User(String UserId, String channelId, String nick, String headIcon,
			int group) {
		this.UserId = UserId;
		this.channelId = channelId;
		this.nick = nick;
		this.headIcon = headIcon;
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

	public int getGroup() {
		return group;
	}

	public void setGroup(int group) {
		this.group = group;
	}

	@Override
	public String toString() {
		return "User [UserId=" + UserId + ", channelId=" + channelId
				+ ", nick=" + nick + ", headIcon=" + headIcon + ", group="
				+ group + "]";
	}

}
