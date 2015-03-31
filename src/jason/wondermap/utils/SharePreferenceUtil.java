package jason.wondermap.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.baidu.frontia.FrontiaUser;

/**
 * 本地存储的帮助类
 */
public class SharePreferenceUtil {
	// 声音设置和通知设置
	public static final String MESSAGE_NOTIFY_KEY = "message_notify";
	public static final String MESSAGE_SOUND_KEY = "message_sound";
	public static final String SHOW_HEAD_KEY = "show_head";
	public static final String PULLREFRESH_SOUND_KEY = "pullrefresh_sound";
	// 标志是否登陆
	public static final String HAS_LOGIN = "has_login";
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;

	/**
	 * 本地存储的帮助类
	 * 
	 */
	public SharePreferenceUtil(Context context, String file) {
		sp = context.getSharedPreferences(file, Context.MODE_PRIVATE);
		editor = sp.edit();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝标志是否登陆＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void login() {
		editor.putBoolean(HAS_LOGIN, true);
		editor.commit();
	}

	public boolean hasLogin() {
		return sp.getBoolean(HAS_LOGIN, false);
	}

	public void logout() {
		editor.putBoolean(HAS_LOGIN, false);
		editor.commit();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝绑定账号授权成功后返回的信息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * @param social_id
	 *            绑定的社交账号id
	 */
	public void setSocialId(String social_id) {
		editor.putString(AccountInfo.SOCIAL_ID, social_id);
		editor.commit();
	}

	public String getSocialId() {
		return sp.getString(AccountInfo.SOCIAL_ID, "");
	}

	/**
	 * @param accessToken
	 *            社交账号返回的token
	 */
	public void setAccessToken(String accessToken) {
		editor.putString(AccountInfo.ACCESS_TOKEN, accessToken);
		editor.commit();
	}

	public String getAccessToken() {
		return sp.getString(AccountInfo.ACCESS_TOKEN, "");
	}

	public void setExpiresIn(long expiresIn) {
		editor.putLong(AccountInfo.EXPIRES_IN, expiresIn);
		editor.commit();
	}

	public String getExpiresIn() {
		return sp.getString(AccountInfo.EXPIRES_IN, "");
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝获取到的用户信息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// 昵称
	public void setUserNick(String nick) {
		editor.putString(AccountInfo.USER_NICK, nick);
		editor.commit();
	}

	public String getUserNick() {
		return sp.getString(AccountInfo.USER_NICK, "");
	}

	// 头像
	public void setUserHeadPicUrl(String url) {
		editor.putString(AccountInfo.USER_HEADPIC_URL, url);
		editor.commit();
	}

	public String getUserHeadPicUrl() {
		return sp.getString(AccountInfo.USER_HEADPIC_URL, "");
	}

	// 生日
	public void setUserBirthday(String birthday) {
		editor.putString(AccountInfo.USER_BIRTHDAY, birthday);
		editor.commit();
	}

	public String getUserBirthdayl() {
		return sp.getString(AccountInfo.USER_BIRTHDAY, "");
	}

	// 城市
	public void setUserCity(String city) {
		editor.putString(AccountInfo.USER_CITY, city);
		editor.commit();
	}

	public String getUserCity() {
		return sp.getString(AccountInfo.USER_CITY, "");
	}

	// 省份
	public void setUserProvince(String province) {
		editor.putString(AccountInfo.USER_PROVINCE, province);
		editor.commit();
	}

	public String getUserProvince() {
		return sp.getString(AccountInfo.USER_PROVINCE, "");
	}

	// 性别
	public void setUserSex(int sex) {
		editor.putInt(AccountInfo.USER_SEX, sex);
		editor.commit();
	}

	public FrontiaUser.SEX getUserSex() {
		FrontiaUser.SEX sex = FrontiaUser.SEX.create(sp.getInt(
				AccountInfo.USER_SEX, 0));
		return sex;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝绑定云推送成功后返回的信息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * @param appid
	 *            应用ID，服务端用来识别本应用，绑定推送时返回
	 */
	public void setAppId(String appid) {
		editor.putString(AccountInfo.APP_ID, appid);
		editor.commit();
	}

	public String getAppId() {
		return sp.getString(AccountInfo.APP_ID, "");
	}

	/**
	 * @param userid
	 *            应用的用户ID，一个应用在多个端，可以都属于同一用户
	 */
	public void setUserId(String userId) {
		editor.putString(AccountInfo.USER_ID, userId);
		editor.commit();
	}

	/**
	 * 获取用户的user_id，根据这里判断是否已经登陆
	 */
	public String getUserId() {
		return sp.getString(AccountInfo.USER_ID, "");
	}

	/**
	 * @param channelId
	 *            ： 推送通道ID，通常指一个终端，如一台android系统手机。客户端绑定调用返回值中可获得。
	 */
	public void setChannelId(String channelId) {
		editor.putString(AccountInfo.CHANNEL_ID, channelId);
		editor.commit();
	}

	public String getChannelId() {
		return sp.getString(AccountInfo.CHANNEL_ID, "");
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝云推送和一些无用的设置，暂时搁置＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// 设置Tag,之后可见权限等通过tag操作
	public void setTag(String tag) {
		editor.putString("tag", tag);
		editor.commit();
	}

	public String getTag() {
		return sp.getString("tag", "");
	}

	// 是否通知
	public boolean getMsgNotify() {
		return sp.getBoolean(MESSAGE_NOTIFY_KEY, true);
	}

	public void setMsgNotify(boolean isChecked) {
		editor.putBoolean(MESSAGE_NOTIFY_KEY, isChecked);
		editor.commit();
	}

	// 新消息是否有声音
	public boolean getMsgSound() {
		return sp.getBoolean(MESSAGE_SOUND_KEY, true);
	}

	public void setMsgSound(boolean isChecked) {
		editor.putBoolean(MESSAGE_SOUND_KEY, isChecked);
		editor.commit();
	}

	// 刷新是否有声音
	public boolean getPullRefreshSound() {
		return sp.getBoolean(PULLREFRESH_SOUND_KEY, true);
	}

	public void setPullRefreshSound(boolean isChecked) {
		editor.putBoolean(PULLREFRESH_SOUND_KEY, isChecked);
		editor.commit();
	}

	// 是否显示自己头像
	public boolean getShowHead() {
		return sp.getBoolean(SHOW_HEAD_KEY, true);
	}

	public void setShowHead(boolean isChecked) {
		editor.putBoolean(SHOW_HEAD_KEY, isChecked);
		editor.commit();
	}

	// 表情翻页效果
	public int getFaceEffect() {
		return sp.getInt("face_effects", 3);
	}

	public void setFaceEffect(int effect) {
		if (effect < 0 || effect > 11)
			effect = 3;
		editor.putInt("face_effects", effect);
		editor.commit();
	}

}
