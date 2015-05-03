package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.L;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.widget.Toast;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 管理登陆和登陆用户的账户相关信息
 * 
 * @author liuzhenhui
 * 
 */
public class AccountUserManager {
	private WonderMapApplication mApplication;
	private Map<String, BmobChatUser> contactList = new HashMap<String, BmobChatUser>();;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 退出登录,清空缓存数据,推到启动页
	 */
	public void logout() {
		BmobUserManager.getInstance(mApplication).logout();
		setContactList(null);
		BaseFragment.getWMFragmentManager().backTo(
				WMFragmentManager.TYPE_SPLASH, null);
	}

	/**
	 * 下载好友信息
	 */
	public void downloadContact() {
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (BmobUserManager.getInstance(mApplication).getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = CollectionUtils.list2map(BmobDB.create(mApplication)
					.getContactList());
		}
	}

	/**
	 * 获取内存中好友user list
	 */
	public Map<String, BmobChatUser> getContactList() {
		return contactList;
	}

	/**
	 * 设置好友user list到内存中
	 * 
	 * @param contactList
	 */
	public void setContactList(Map<String, BmobChatUser> contactList) {
		if (this.contactList != null) {
			this.contactList.clear();
		}
		this.contactList = contactList;
	}

	/**
	 * 除登陆注册和欢迎页面外-用于检测是否有其他设备登录了同一账号
	 */
	public void checkLogin() {
		if (BmobUserManager.getInstance(mApplication).getCurrentUser() == null) {
			Toast.makeText(mApplication, "您的账号已在其他设备上登录!", Toast.LENGTH_SHORT)
					.show();
			// TODO 下线提醒
			BaseFragment.getWMFragmentManager().showFragment(
					WMFragmentManager.TYPE_LOGIN);
			// 这里有一句，以后处理
			// MainActivity.finish();
		}
	}

	/**
	 * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 */
	public void updateUserInfos() {
		// 更新地理位置信息
		// updateUserLocation();
		// 查询该用户的好友列表(这个好友列表是去除黑名单用户的哦),目前支持的查询好友个数为100，如需修改请在调用这个方法前设置BmobConfig.LIMIT_CONTACTS即可。
		// 这里默认采取的是登陆成功之后即将好于列表存储到数据库中，并更新到当前内存中,
		BmobUserManager.getInstance(mApplication).queryCurrentContactList(
				new FindListener<BmobChatUser>() {

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
						// 保存到application中方便比较
						setContactList(CollectionUtils.list2map(arg0));
					}
				});
	}

	/**
	 * 更新自己的经纬度信息
	 */
	public void updateUserLocation(BmobGeoPoint point) {
		if (getCurrentUser() == null) {
			return;
		}
		User user = getCurrentUser();
		user.setLocation(point);
		user.update(mApplication, new UpdateListener() {
			@Override
			public void onSuccess() {
				L.d("更新经纬度信息到服务器成功");
			}

			@Override
			public void onFailure(int code, String msg) {
				L.d("更新经纬度信息到服务器失败");
			}
		});
	}

	public User getCurrentUser() {
		return BmobUserManager.getInstance(mApplication).getCurrentUser(
				User.class);
	}

	public String getCurrentUserName() {
		return BmobUserManager.getInstance(mApplication).getCurrentUserName();
	}

	public BmobUserManager getUserManager() {
		return BmobUserManager.getInstance(mApplication);
	}

	public String getCurrentUserid() {
		return BmobUserManager.getInstance(mApplication)
				.getCurrentUserObjectId();
	}

	public void updateCurrentUserSex(boolean se, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User user = getCurrentUser();
		user.setSex(se);
		user.update(mApplication, listener);
	}

	public void updateCurrentUserName(String name, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User user = getCurrentUser();
		user.setUsername(name);
		user.update(mApplication, listener);
	}

	public void updateCurrentUserAge(String age, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User currUser = getCurrentUser();

		currUser.setAge(Integer.valueOf(age));
		currUser.update(mApplication, listener);
	}

	public void updateCurrentUserSign(String sign, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User currUser = getCurrentUser();

		currUser.setSignature(sign);
		currUser.update(mApplication, listener);
	}

	public void updateCurrentUserPhone(String phone, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User currUser = getCurrentUser();

		currUser.setPhone(phone);
		currUser.update(mApplication, listener);
	}

	public void confirmCurrentUserInfo(UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User currUser = getCurrentUser();

		currUser.setInfoIsSet(true);
		currUser.update(mApplication, listener);
	}

	public void updateCurrentUserAvatar(String url, UpdateListener listener) {
		if (getCurrentUser() == null) {
			return;
		}
		User currUser = getCurrentUser();
		currUser.setAvatar(url);
		currUser.update(mApplication, listener);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static AccountUserManager instance = null;

	private AccountUserManager() {
		mApplication = WonderMapApplication.getInstance();
	}

	public void destroy() {
		contactList = null;
	}

	public static AccountUserManager getInstance() {
		if (instance == null) {
			instance = new AccountUserManager();
		}
		return instance;
	}
}
