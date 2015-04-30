package jason.wondermap.helper;

import com.xiaomi.market.sdk.XiaomiUpdateAgent;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import android.content.Context;
import android.os.Bundle;

public class LaunchHelper {
	public void checkLaunch(Context mContext) {
		checkCrashLog(mContext);
		checkIsNeedToConfirmInfo();
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

	private void checkIsNeedToConfirmInfo() {
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
