package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import android.content.Context;
import android.os.Bundle;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 启动退出帮助类
 * 
 * @author liuzhenhui
 * 
 */
public class LaunchHelper {

	private Context mContext = WonderMapApplication.getInstance();;

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
