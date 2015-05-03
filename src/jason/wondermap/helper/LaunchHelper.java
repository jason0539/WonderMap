package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.config.WMapConfig;
import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import android.content.Context;
import android.os.Bundle;
import cn.bmob.im.BmobChat;

import com.xiaomi.market.sdk.XiaomiUpdateAgent;

/**
 * 启动退出帮助类
 * 
 * @author liuzhenhui
 * 
 */
public class LaunchHelper {
	/**
	 * 退出时需要回收的资源，按初始化的相反方向
	 */
	public void checkExit() {
		WLocationManager.getInstance().stop();
		AccountUserManager.getInstance().destroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		MapControler.getInstance().unInit();
		// ImageLoader.getInstance().destroy();
		// MapUserManager.getInstance().
	}

	/**
	 * 启动时需要启动的资源，注意时序
	 */
	public void checkLaunch(Context mContext) {
		// 初始化bmob相关，定位一旦成功就要发送消息，没有依赖
		BmobChat.DEBUG_MODE = true;
		BmobChat.getInstance(mContext).init(WMapConfig.applicationId);
		AccountUserManager.getInstance().downloadContact();
		// 初始化地图用户管理，依赖MapControl和bmob获取联系人
		MapUserManager.getInstance();
		// 定位，一旦开始就使用push发送消息，依赖Bmob Push服务，挪到MapHomeFrag里面，保证在服务协议之后显示
//		WLocationManager.getInstance().start();
		checkCrashLog(mContext);
		// 设置小米自动更新组件，仅wifi下更新
		XiaomiUpdateAgent.setCheckUpdateOnlyWifi(true);
		XiaomiUpdateAgent.update(mContext);
	}

	/**
	 * 检查是否有crash 日志信息需要上传，如果有且当前为wifi环境则上传
	 * 
	 * @param context
	 */
	private void checkCrashLog(final Context context) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				if (!CommonUtils.isWifi(context)) {// 仅wifi环境下上传
					return;
				}
				if (WonderMapApplication.getInstance().getSpUtil()
						.hasCrashLog()) {
					CrashLogHelper crashLogManager = new CrashLogHelper();
					crashLogManager.uploadLog();
					L.d(WModel.CrashUpload, "存在crash 文件");
				} else {
					L.d(WModel.CrashUpload, "没有crash");
				}
			}
		}.start();
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
