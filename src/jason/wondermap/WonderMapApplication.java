package jason.wondermap;

import jason.wondermap.config.WMapConstants;
import jason.wondermap.utils.SharePreferenceUtil;
import android.app.Application;

/**
 * 应用上下文
 * 
 * @author liuzhenhui
 */
public class WonderMapApplication extends Application {

	private static WonderMapApplication mApplication;
	private SharePreferenceUtil mSpUtil;

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
	}

	/**
	 * 获取应用的全局上下文
	 */
	public static WonderMapApplication getInstance() {
		return mApplication;
	}

	/**
	 * 获取本地存储帮助类
	 */
	public SharePreferenceUtil getSpUtil() {
		if (mSpUtil == null) {
			// 原来是每个用户都不同的本地存储，导致tips服务协议等弹窗重复弹出，现在去掉这个设定，以后有需要再改
			// String currentId = BmobUserManager.getInstance(
			// getApplicationContext()).getCurrentUserObjectId();
			// String sharedName = currentId + WMapConstants.PREFERENCE_NAME;
			String sharedName = WMapConstants.PREFERENCE_NAME;
			mSpUtil = new SharePreferenceUtil(this, sharedName);
		}
		return mSpUtil;
	}

}
