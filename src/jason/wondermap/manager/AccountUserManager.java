package jason.wondermap.manager;

import jason.wondermap.LoginActivity;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.User;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.L;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.widget.Toast;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 管理登陆和登陆用户的账户相关信息
 * 
 * @author liuzhenhui
 * 
 */
public class AccountUserManager {
	private WonderMapApplication mApplication;
	private BmobUserManager userManager;// 用户管理
	private BmobChatManager chatManager;// 聊天管理
	private BmobDB bmobDB;// 数据库
	private Map<String, BmobChatUser> contactList;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 下载好友信息
	 */
	public void downloadContact() {
		// 若用户登陆过，则先从好友数据库中取出好友list存入内存中
		if (userManager.getCurrentUser() != null) {
			// 获取本地好友user list到内存,方便以后获取好友list
			contactList = CollectionUtils.list2map(bmobDB.getContactList());
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
		if (userManager.getCurrentUser() == null) {
			Toast.makeText(mApplication, "您的账号已在其他设备上登录!", Toast.LENGTH_SHORT)
					.show();
			mApplication.startActivity(new Intent(mApplication,
					LoginActivity.class));
			// 这里有一句，以后处理
			// MainActivity.finish();
		}
	}

	public void login(User user, final SaveListener saveListener) {
		userManager.login(user, new SaveListener() {

			@Override
			public void onSuccess() {
				saveListener.onSuccess();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				saveListener.onFailure(arg0, arg1);
			}
		});
	}

	public BmobUser getCurrentUser() {
		return userManager.getCurrentUser();
	}

	public String getCurrentUserName() {
		return userManager.getCurrentUserName();
	}

	public BmobUserManager getUserManager() {
		return userManager;
	}

	/**
	 * 用于登陆或者自动登陆情况下的用户资料及好友资料的检测更新
	 */
	public void updateUserInfos() {
		// 更新地理位置信息
		// updateUserLocation();
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
				// 保存到application中方便比较
				setContactList(CollectionUtils.list2map(arg0));
			}
		});
	}

	/**
	 * 更新自己的经纬度信息
	 * 
	 * @param bmobGeoPoint
	 */
	public void updateUserLocation(BmobGeoPoint bmobGeoPoint) {
		User u = (User) userManager.getCurrentUser(User.class);
		final User user = new User();
		user.setLocation(bmobGeoPoint);
		user.setObjectId(u.getObjectId());
		user.update(WonderMapApplication.getInstance(), new UpdateListener() {
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

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static AccountUserManager instance = null;

	private AccountUserManager() {
		mApplication = WonderMapApplication.getInstance();
		userManager = BmobUserManager.getInstance(mApplication);
		chatManager = BmobChatManager.getInstance(mApplication);
		bmobDB = BmobDB.create(mApplication);
		contactList = new HashMap<String, BmobChatUser>();
	}

	public static AccountUserManager getInstance() {
		if (instance == null) {
			instance = new AccountUserManager();
		}
		return instance;
	}
}
