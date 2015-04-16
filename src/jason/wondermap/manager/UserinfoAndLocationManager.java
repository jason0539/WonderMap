package jason.wondermap.manager;

import jason.wondermap.LoginActivity;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.L;

import java.util.List;

import android.content.Intent;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

public class UserinfoAndLocationManager {
	BmobUserManager userManager;
	BmobChatManager manager;

	WonderMapApplication mApplication;

	/**
	 * 除登陆注册和欢迎页面外-用于检测是否有其他设备登录了同一账号
	 */
	public void checkLogin() {
		BmobUserManager userManager = BmobUserManager.getInstance(mApplication);
		if (userManager.getCurrentUser() == null) {
			Toast.makeText(mApplication, "您的账号已在其他设备上登录!", Toast.LENGTH_SHORT)
					.show();
			mApplication.startActivity(new Intent(mApplication, LoginActivity.class));
			// 这里有一句，以后处理
//			MainActivity.finish();
		}
	}

	/**
	 * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 */
	public void updateUserInfos() {
		// 更新地理位置信息
		updateUserLocation();
		// 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		// 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		userManager.queryCurrentContactList(new FindListener<BmobChatUser>() {

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				if (arg0 == BmobConfig.CODE_COMMON_NONE) {
					L.d(arg1);
				} else {
					L.d("查询好友列表失败：" + arg1);
				}
			}

			@Override
			public void onSuccess(List<BmobChatUser> arg0) {
				// TODO Auto-generated method stub
				// 保存到application中方便比较
				WonderMapApplication.getInstance().setContactList(
						CollectionUtils.list2map(arg0));
			}
		});
	}

	/**
	 * 更新用户的经纬度信息
	 */
	public void updateUserLocation() {
		if (WMapLocationManager.getInstance().getLastPoint() != null) {
			String saveLatitude = WMapLocationManager.getInstance()
					.getSavedLatitude();
			String saveLongtitude = WMapLocationManager.getInstance()
					.getSavedLongtitude();
			String newLat = String.valueOf(WMapLocationManager.getInstance()
					.getLatitude());
			String newLong = String.valueOf(WMapLocationManager.getInstance()
					.getLongtitude());
			if (!saveLatitude.equals(newLat) || !saveLongtitude.equals(newLong)) {// 只有位置有变化就更新当前位置，达到实时更新的目的
				User u = (User) userManager.getCurrentUser(User.class);
				final User user = new User();
				user.setLocation(WMapLocationManager.getInstance()
						.getLastPoint());
				user.setObjectId(u.getObjectId());
				user.update(WonderMapApplication.getInstance(),
						new UpdateListener() {
							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								// 下面两句貌似没用
								WMapLocationManager.getInstance().saveLatitude(
										String.valueOf(user.getLocation()
												.getLatitude()));
								WMapLocationManager.getInstance()
										.saveLongtitude(
												String.valueOf(user
														.getLocation()
														.getLongitude()));
								// ShowLog("经纬度更新成功");
							}

							@Override
							public void onFailure(int code, String msg) {
								// TODO Auto-generated method stub
								// ShowLog("经纬度更新 失败:"+msg);
							}
						});
			} else {
				// ShowLog("用户位置未发生过变化");
			}
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static UserinfoAndLocationManager instance = null;

	private UserinfoAndLocationManager() {
		userManager = BmobUserManager.getInstance(WonderMapApplication
				.getInstance());
		manager = BmobChatManager.getInstance(WonderMapApplication
				.getInstance());
	}

	public static UserinfoAndLocationManager getInstance() {
		if (instance == null) {
			instance = new UserinfoAndLocationManager();
		}
		return instance;
	}
}
