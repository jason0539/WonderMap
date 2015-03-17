package jason.wondermap.manager;

import java.util.ArrayList;

import com.baidu.mapapi.map.Marker;

import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.utils.L;

/**
 * 地图显示的用户管理类，负责用户管理去重和地图显示位置更新，区别于好友
 * 
 * @author liuzhenhui
 * 
 */
public class WMapUserManager {
	private ArrayList<User> mapUsers = new ArrayList<User>();

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 是否已经显示用户
	 */
	public boolean containsUser(String userId) {
		for (User user : mapUsers) {
			if (user.getUserId().equals(userId)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 向地图添加新用户，如果已经添加过则更新位置
	 */
	public void addUser(User user) {
		Marker marker = WMapControler.getInstance().addUser(user);
		user.setMarker(marker);
		mapUsers.add(user);
	}

	/**
	 * 根据收到的hellomsg更新user的位置信息,
	 */
	public void updateUser(HelloMessage msg) {
		User oldUser = getUser(msg.getUserId());
		if (oldUser != null) {
			// oldUser.setChannelId(msg.getChannelId());
			// oldUser.setGroup(msg.getGroup());
			// oldUser.setHeadIcon(msg.getHeadIcon());
			oldUser.setLat(msg.getLat());
			oldUser.setLng(msg.getLng());
			// oldUser.setNick(msg.getNick());
			WMapControler.getInstance().updateUserPosition(oldUser);
		} else {
			L.e("WMapUserManager:要更新位置的用户不存在");
			return;
		}
	}

	public User getUser(String userId) {
		for (User usr : mapUsers) {
			if (usr.getUserId().equals(userId)) {
				return usr;
			}
		}
		return null;
	}

	public synchronized ArrayList<User> getMapUsers() {
		return mapUsers;
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
