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

	private String SHARED_KEY_NOTIFY = "shared_key_notify";
	private String SHARED_KEY_VOICE = "shared_key_sound";
	private String SHARED_KEY_VIBRATE = "shared_key_vibrate";
	private String SHARED_KEY_CRASH = "shared_key_crash";

	// 是否允许推送通知
	public boolean isAllowPushNotify() {
		return mSharedPreferences.getBoolean(SHARED_KEY_NOTIFY, true);
	}

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
		return mSharedPreferences.getBoolean(SHARED_KEY_CRASH, false);
	}

	// 设置是否有crash文件，发生crash后设为true，上传完成设为false
	public void setCrashLog(boolean hasCrash) {
		editor.putBoolean(SHARED_KEY_CRASH, hasCrash);
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
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝qq登陆，用来保存用户名等信息，确认信息的时候取出＝＝＝＝＝＝＝＝

}
