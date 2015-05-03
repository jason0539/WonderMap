package jason.wondermap;

import jason.wondermap.config.WMapConstants;
import jason.wondermap.crash.CrashHandler;
import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;

import java.io.File;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import cn.bmob.im.BmobUserManager;

import com.baidu.mapapi.SDKInitializer;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * 应用上下文
 * 
 * @author liuzhenhui
 * 
 */
public class WonderMapApplication extends Application {

	private static WonderMapApplication mApplication;
	private SharePreferenceUtil mSpUtil;
	private NotificationManager mNotificationManager;
	private MediaPlayer mMediaPlayer;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	public void onCreate() {
		super.onCreate();
		// 将crash 的log抓取存储在sd卡的crash目录
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		mApplication = this;
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(WonderMapApplication.getInstance());
		initImageLoader(getApplicationContext());
	}

	/** 初始化ImageLoader */
	private void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				WMapConstants.CACHE_DIR);// 获取到缓存的目录地址
		// 创建配置ImageLoader(所有的选项都是可选的,只使用那些你真的想定制)，这个可以设定在APPLACATION里面，设置为全局的配置参数
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// 线程池内加载的数量
				.threadPoolSize(3).threadPriority(Thread.NORM_PRIORITY - 2)
				.memoryCache(new WeakMemoryCache())
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				// 将保存的时候的URI名称用MD5 加密
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCache(new UnlimitedDiscCache(cacheDir))// 自定义缓存路径
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				.writeDebugLogs() // Remove for release app
				.build();
		L.isDebug = true;
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 获取应用的全局上下文
	 */
	public synchronized static WonderMapApplication getInstance() {
		return mApplication;
	}

	/**
	 * 获取通知管理工具
	 */
	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	public synchronized MediaPlayer getMediaPlayer() {
		if (mMediaPlayer == null)
			mMediaPlayer = MediaPlayer.create(this, R.raw.notify);
		return mMediaPlayer;
	}

	/**
	 * 获取本地存储帮助类
	 */
	public synchronized SharePreferenceUtil getSpUtil() {
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
