package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.controler.MapControler;
import jason.wondermap.crash.CrashHandler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;

import java.io.File;

import android.content.Context;
import android.os.Bundle;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * 启动退出帮助类
 * 
 * @author liuzhenhui
 * 
 */
public class LaunchHelper {

	private Context mContext = WonderMapApplication.getInstance();;

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
				.writeDebugLogs() // Remove for release app
				.build();
		L.isDebug = true;
		ImageLoader.getInstance().init(config);// 全局初始化此配置
	}

	/**
	 * 启动时需要启动的资源，注意时序
	 */
	public void checkLaunch() {
		// 初始化图片加载库
		initImageLoader(WonderMapApplication.getInstance());
		// 加载联系人
		AccountUserManager.getInstance().loadLocalContact();
		// 更新最新信息
		AccountUserManager.getInstance().updateUserInfos();
		// 往底图添加用户，依赖地图控制器，bmob用户下载
		MapUserManager.getInstance();
		// // 日志抓取类
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(WonderMapApplication.getInstance());
		// 崩溃后上传日志
		MiStatInterface.enableExceptionCatcher(true);
		// 仅wifi下统计数据
		MiStatInterface.setUploadPolicy(
				MiStatInterface.UPLOAD_POLICY_WIFI_ONLY, 0);
		// 设置小米自动更新组件，仅wifi下更新
		XiaomiUpdateAgent.setCheckUpdateOnlyWifi(true);
		XiaomiUpdateAgent.update(mContext);
	}

	/**
	 * 退出时需要回收的资源，按初始化的相反方向
	 */
	public void checkExit() {
		// MapUserManager.getInstance();
		AccountUserManager.getInstance().destroy();
		ImageLoader.getInstance().destroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		MapControler.getInstance().unInit();
		WLocationManager.getInstance().stop();
	}

	public void checkIsNeedToConfirmInfo() {
		boolean isNeedTo = !AccountUserManager.getInstance().getUserManager()
				.getCurrentUser(User.class).isInfoIsSet();
		if (isNeedTo) {
			L.d(WModel.NeedToEditInfo, "需要确认信息");
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.USER_ID, AccountUserManager.getInstance()
					.getCurrentUserid());
			bundle.putBoolean(BundleTake.NeedToEditInfo, true);
			BaseFragment.getWMFragmentManager().showFragment(
					WMFragmentManager.TYPE_USERINFO, bundle);
		} else {
			L.d(WModel.NeedToEditInfo, "不需要确认");
		}
	}

}
