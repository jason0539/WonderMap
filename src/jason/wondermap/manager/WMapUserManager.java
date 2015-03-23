package jason.wondermap.manager;

import java.util.ArrayList;
import java.util.List;

import android.widget.Gallery;

import com.baidu.location.m;
import com.baidu.mapapi.map.Marker;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.utils.WModel;
import jason.wondermap.utils.ConvertUtil;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;

/**
 * 地图显示的用户管理类，负责用户管理去重和地图显示位置更新，区别于好友
 * 
 * @author liuzhenhui
 * 
 */
public class WMapUserManager {
	private ArrayList<User> mapUsers = new ArrayList<User>();

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void addUserFromPushMsg(String userId, HelloMessage msg) {
		User alreadExitsUser = WMapUserManager.getInstance().getUser(userId);
		// 添加过则更新位置
		if (alreadExitsUser != null) {
			L.d(WModel.EnsureEveryoneOnMap, "更新位置" + userId);
			WMapUserManager.getInstance().updateUser(alreadExitsUser, msg);
			L.d(msg.getNickname() + "已经添加过，更新位置");
		}
		// 没添加则添加
		else {
			L.d(WModel.EnsureEveryoneOnMap, "添加用户" + userId);
			User u = ConvertUtil.HelloMsgToUser(msg);
			WMapUserManager.getInstance().addUser(u);
			// WonderMapApplication.getInstance().getUserDB().addUser(u);
			// 存入或更新好友，暂时不做好友功能
			T.showShort(WonderMapApplication.getInstance(), u.getNick() + "加入");
		}
	}

	/**
	 * activity退到后台进入时在onResume里面调用，恢复所有marker
	 */
	public void onResumeAllUsersOnMap() {
		L.d(WModel.EnsureEveryoneOnMap,
				"ensureAllUsersOnMap into,user count = " + mapUsers.size());
		ArrayList<User> arrayList = new ArrayList<User>(mapUsers);
		mapUsers.clear();
		for (User user : arrayList) {
			L.d(WModel.EnsureEveryoneOnMap, "userId is "+user.getUserId());
			addUser(user);
		}
		arrayList = null;
	}

	/**
	 * 获取目前应该显示的所有用户，用来给WMapControl判断点击的marker是那个用户的
	 * 
	 * @return
	 */
	public synchronized ArrayList<User> getMapUsers() {
		return mapUsers;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 向地图添加新用户，如果已经添加过则更新位置
	 */
	private void addUser(User user) {
		Marker marker = WMapControler.getInstance().addUser(user);
		user.setMarker(marker);
		mapUsers.add(user);
	}

	/**
	 * 收到的helloMsg，用户已存在，更新user的位置信息
	 * 
	 * @param alreadExitsUser
	 */
	private void updateUser(User alreadExitsUser, HelloMessage msg) {
		User oldUser = alreadExitsUser;
		// oldUser.setChannelId(msg.getChannelId());
		// oldUser.setGroup(msg.getGroup());
		// oldUser.setHeadIcon(msg.getHeadIcon());
		oldUser.setLat(msg.getLat());
		oldUser.setLng(msg.getLng());
		// oldUser.setNick(msg.getNick());
		WMapControler.getInstance().updateUserPosition(oldUser);
	}

	/**
	 * 查找指定id的用户
	 * 
	 * @param userId
	 * @return
	 */
	private User getUser(String userId) {
		for (User usr : mapUsers) {
			if (usr.getUserId().equals(userId)) {
				return usr;
			}
		}
		return null;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private WMapUserManager() {
	}

	private static WMapUserManager instance = null;

	public static WMapUserManager getInstance() {
		if (instance == null) {
			instance = new WMapUserManager();
		}
		return instance;
	}
}
