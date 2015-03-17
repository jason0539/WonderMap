package jason.wondermap.bean;

import java.text.SimpleDateFormat;
import java.util.Date;


public class ChatMessage {
	private String message;
	private boolean isComing;
	private Date date;
	private String userId;
	private String icon;

	public void setDateStr(String dateStr) {
		this.dateStr = dateStr;
	}

	private String nickname;
	private boolean readed;
	private String dateStr;

	public ChatMessage() {
	}

	public ChatMessage(String message, boolean isComing, String userId,
			String icon, String nickname, boolean readed, String dateStr) {
		super();
		this.message = message;
		this.isComing = isComing;
		this.userId = userId;
		this.icon = icon;
		this.nickname = nickname;
		this.readed = readed;
		this.dateStr = dateStr;
	}

	public String getDateStr() {
		return dateStr;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public boolean isComing() {
		return isComing;
	}

	public void setComing(boolean isComing) {
		this.isComing = isComing;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		this.dateStr = df.format(date);
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public boolean isReaded() {
		return readed;
	}

	public void setReaded(boolean readed) {
		this.readed = readed;
	}

}
