package jason.wondermap;

import jason.wondermap.config.WMapConfig;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.crash.CrashHandler;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.OsUtils;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.WModel;

import java.io.File;

import android.app.Application;
import android.content.Context;
import cn.bmob.im.BmobChat;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
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
		// L.d(WModel.Time, "进程名称" + processName);
		if (processName != null) {
			boolean defaultProcess = processName
					.equals(WMapConstants.REAL_PACKAGE_NAME);
			if (defaultProcess) {
				L.d(WModel.Time, "初始化" + processName);
				long t = System.currentTimeMillis();
				// 初始化百度地图SDK，setContentView就需要地图控件
				SDKInitializer.initialize(WonderMapApplication.getInstance());
				// 初始化bmob服务
				// BmobChat.DEBUG_MODE = true;
				BmobChat.getInstance(this).init(WMapConfig.applicationId);
				// 初始化图片加载库
				initImageLoader(WonderMapApplication.getInstance());
				// 加载联系人
				AccountUserManager.getInstance().loadLocalContact();
				// 更新最新信息
				AccountUserManager.getInstance().updateUserInfos();
				// 开始定位，依赖地图初始化，定位成功要存储，依赖bmob服务，
				WLocationManager.getInstance().start();
				// 小米集成统计
				MiStatInterface.initialize(this, WMapConfig.MiAppId,
						WMapConfig.MiAppKey, WMapConfig.CHANNEL_MI);
				// 异常处理类，提示用户应用即将退出
				CrashHandler crashHandler = CrashHandler.getInstance();
				crashHandler.init(WonderMapApplication.getInstance());
				// 崩溃后上传日志
				MiStatInterface.enableExceptionCatcher(true);
				// 仅wifi下统计数据
				MiStatInterface.setUploadPolicy(
						MiStatInterface.UPLOAD_POLICY_WIFI_ONLY, 0);
				L.d(WModel.Time, "APPLICATION 时间"
						+ (System.currentTimeMillis() - t));
			}
		}

	}

	/** 初始化ImageLoader */
	private void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				WMapConstants.CACHE_DIR);// 获取到缓存的目录地址
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				// 将保存的时候的URI名称用MD5 加密
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// .writeDebugLogs() // Remove for release app
				.build();
		L.isDebug = true;
		ImageLoader.getInstance().init(config);// 全局初始化此配置
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
