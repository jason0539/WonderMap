package jason.wondermap.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * 首选项管理
 * 
 * @ClassName: SharePreferenceUtil
 * @Description: TODO
 * @author smile
 * @date 2014-6-10 下午4:20:14
 */
@SuppressLint("CommitPrefEdits")
public class SharePreferenceUtil {
	private SharedPreferences mSharedPreferences;
	private static SharedPreferences.Editor editor;

	public SharePreferenceUtil(Context context, String name) {
		mSharedPreferences = context.getSharedPreferences(name,
				Context.MODE_PRIVATE);
		editor = mSharedPreferences.edit();
	}

	private String SHARED_KEY_NOTIFY = "shared_key_notify";// 是否允许通知
	private String SHARED_KEY_VOICE = "shared_key_sound";// 是否允许声音
	private String SHARED_KEY_VIBRATE = "shared_key_vibrate";// 是否允许震动
	private String SHARED_KEY_CRASH = "shared_key_crash";// 是否有crash日志信息
	private String SHARED_KEY_ACCEPT = "shared_key_accept";// 接受用户协议，小米的
	private String SHARED_KEY_ACCEPT_PHONE = "shared_key_accept_phone";// 接受读取通讯录
	private String SHARED_KEY_LOCATION = "shared_key_location";//
	private String SHARED_KEY_SMALL = "shared_key_small";//
	private String SHARED_KEY_BIG = "shared_key_big";//
	private String SHARED_KEY_MAPTYPE = "shared_key_maptype";//
	private String SHARED_KEY_ALLORFRIEND = "shared_key_allorfriend";//

	// 是否允许推送通知
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

	/**
	 * 是否允许通知 策略：此项代表消息总控制，如果关闭，则通知栏、声音、震动都关闭， 如果开启，则通知栏肯定有通知，声音和震动通过另外两个开关控制
	 */
	public void setPushNotifyEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_NOTIFY, isChecked);
		editor.commit();
	}

	// 允许声音
	public boolean isAllowVoice() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VOICE, true);
	}

	public void setAllowVoiceEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VOICE, isChecked);
		editor.commit();
	}

	// 允许震动
	public boolean isAllowVibrate() {
		return mSharedPreferences.getBoolean(SHARED_KEY_VIBRATE, true);
	}

	public void setAllowVibrateEnable(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_VIBRATE, isChecked);
		editor.commit();
	}

	// 是否发生过crash，存储文件名
	public boolean hasCrashLog() {
		boolean hasLog = mSharedPreferences.getBoolean(SHARED_KEY_CRASH, false);
		return hasLog;
	}

	// 设置是否有crash文件，发生crash后设为true，上传完成设为false
	public void setCrashLog(boolean hasCrash) {
		editor.putBoolean(SHARED_KEY_CRASH, hasCrash);
		editor.commit();
	}

	// 用户是否同意授权使用位置信息
	public boolean hasAccept() {
		boolean hasLog = mSharedPreferences
				.getBoolean(SHARED_KEY_ACCEPT, false);
		return hasLog;
	}

	// 设置用户是否同意授权
	public void setAccept(boolean hasAccept) {
		editor.putBoolean(SHARED_KEY_ACCEPT, hasAccept);
		editor.commit();
	}
	// 用户是否同意授权读取通讯录
	public boolean hasAcceptPhone() {
		boolean hasLog = mSharedPreferences
				.getBoolean(SHARED_KEY_ACCEPT_PHONE, false);
		return hasLog;
	}
	
	// 设置用户是否同意授权读取通讯录
	public void setAcceptPhone(boolean hasAccept) {
		editor.putBoolean(SHARED_KEY_ACCEPT_PHONE, hasAccept);
		editor.commit();
	}

	// 第一次定位
	public boolean isFirstLocation() {
		return mSharedPreferences.getBoolean(SHARED_KEY_LOCATION, true);
	}

	public void setFirstLocation(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_LOCATION, isChecked);
		editor.commit();
	}

	// 第一次卫星地图
	public boolean isFirstChangeMapType() {
		return mSharedPreferences.getBoolean(SHARED_KEY_MAPTYPE, true);
	}

	public void setFirstChangeMapType(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_MAPTYPE, isChecked);
		editor.commit();
	}

	// 是否第一次切换好友
	public boolean isFirstChangeFriends() {
		return mSharedPreferences.getBoolean(SHARED_KEY_ALLORFRIEND, true);
	}

	public void setFirstChangeFriends(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_ALLORFRIEND, isChecked);
		editor.commit();
	}

	// 是否第一次放大

	public boolean isFirstBig() {
		return mSharedPreferences.getBoolean(SHARED_KEY_BIG, true);
	}

	public void setFirstBig(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_BIG, isChecked);
		editor.commit();
	}

	// 是否第一次缩小

	public boolean isFirstSmall() {
		return mSharedPreferences.getBoolean(SHARED_KEY_SMALL, true);
	}

	public void setFirstSmall(boolean isChecked) {
		editor.putBoolean(SHARED_KEY_SMALL, isChecked);
		editor.commit();
	}

	// 第三方分享
	// Delete
	public void remove(String key) {
		editor.remove(key);
		editor.commit();
	}

	// String
	public String getValue(String key, String defaultValue) {
		return mSharedPreferences.getString(key, defaultValue);
	}

	// String
	public void setValue(String key, String value) {
		editor.putString(key, value);
		editor.commit();
	}

	// float
	public float getValue(String key, float defaultValue) {
		return mSharedPreferences.getFloat(key, defaultValue);
	}

	// float
	public void setValue(String key, float value) {
		editor.putFloat(key, value);
		editor.commit();
	}

	// boolean
	public boolean getValue(String key, boolean defaultValue) {
		return mSharedPreferences.getBoolean(key, defaultValue);
	}

	// boolean
	public void setValue(String key, boolean value) {
		editor.putBoolean(key, value);
		editor.commit();
	}
}
