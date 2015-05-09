package jason.wondermap;

import jason.wondermap.config.WMapConfig;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.OsUtils;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.WModel;
import android.app.Application;
import cn.bmob.im.BmobChat;

import com.baidu.mapapi.SDKInitializer;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

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
		String processName = OsUtils.getProcessName(this,
				android.os.Process.myPid());
		L.d(WModel.Time, "进程名称" + processName);
		if (processName != null) {
			boolean defaultProcess = processName
					.equals(WMapConstants.REAL_PACKAGE_NAME);
			if (defaultProcess) {
				L.d(WModel.Time, "初始化" + processName);
				long t = System.currentTimeMillis();
				// 初始化地图，setContentView需要地图控件
				SDKInitializer.initialize(WonderMapApplication.getInstance());
				// 初始化bmob服务
				// BmobChat.DEBUG_MODE = true;
				BmobChat.getInstance(this).init(WMapConfig.applicationId);
				// 开始定位，依赖地图初始化，bmob服务
				WLocationManager.getInstance().start();
				// 小米集成统计
				MiStatInterface.initialize(this, WMapConfig.MiAppId,
						WMapConfig.MiAppKey, WMapConfig.CHANNEL_MI);
				L.d(WModel.Time, "APPLICATION 时间"
						+ (System.currentTimeMillis() - t));
			}
		}

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
