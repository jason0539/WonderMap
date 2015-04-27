package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.controler.MapControler;
import jason.wondermap.interfacer.MapUserTransferListener;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.json.JSONObject;

import cn.bmob.im.util.BmobJsonUtil;

import com.baidu.mapapi.map.Marker;

/**
 * 地图显示的用户管理类，显示，位置更新，
 * 
 * @author liuzhenhui
 * 
 */
public class MapUserManager {
	private HashMap<String, MapUser> mapUsers = new HashMap<String, MapUser>();

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void addUserFromPushMsg(JSONObject msg) {
		String userId = BmobJsonUtil.getString(msg, UserInfo.USER_ID);
		String nameString = BmobJsonUtil.getString(msg, UserInfo.USER_NAME);
		if (userId.equals(AccountUserManager.getInstance().getCurrentUserid())) {// 自己的消息忽略
			return;
		}
		MapUser alreadExitsUser = getUser(userId);
		// 添加过则更新位置
		if (alreadExitsUser != null) {
			L.d(WModel.EnsureEveryoneOnMap, "更新位置" + nameString);
			updateUser(alreadExitsUser, msg);
			L.d(nameString + "已经添加过，更新位置");
		}
		// 没添加则添加
		else {
			L.d(WModel.EnsureEveryoneOnMap, "添加用户" + nameString);
			CommonUtils.HelloMsgToUser(msg, new MapUserTransferListener() {

				@Override
				public void onSuccess(MapUser user) {
					addUser(user);
					T.showShort(WonderMapApplication.getInstance(),
							user.getName() + "加入");					
				}
			});
		}
	}

	/**
	 * activity退到后台进入时在onResume里面调用，恢复所有marker
	 */
	public void onResumeAllUsersOnMap() {
		L.d(WModel.EnsureEveryoneOnMap,
				"ensureAllUsersOnMap into,user count = " + mapUsers.size());
		HashMap<String, MapUser> users = new HashMap<String, MapUser>(mapUsers);
		mapUsers.clear();
		MapControler.getInstance().clearMarker();// 先把所有marker清除掉
		Iterator<Entry<String, MapUser>> iterator = users.entrySet()
				.iterator();
		Entry<String, MapUser> entry;
		while (iterator.hasNext()) {
			entry = iterator.next();
			addUser(entry.getValue());
		}
		users = null;
	}

	/**
	 * 获取目前应该显示的所有用户，用来给WMapControl判断点击的marker是那个用户的
	 * 
	 * @return
	 */
	public synchronized HashMap<String, MapUser> getMapUsers() {
		return mapUsers;
	}

	// // ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 向地图添加新用户，如果已经添加过则更新位置
	 */
	private void addUser(MapUser user) {
		Marker marker = MapControler.getInstance().addUser(user);
		user.setMarker(marker);
		mapUsers.put(user.getObjectId(), user);
	}

	/**
	 * 收到的helloMsg，用户已存在，更新user的位置信息
	 */
	private void updateUser(MapUser alreadExitsUser, JSONObject msg) {
		MapUser oldUser = alreadExitsUser;
		double lat = Double.valueOf(BmobJsonUtil.getString(msg,
				(UserInfo.LATITUDE)));
		double lng = Double.valueOf(BmobJsonUtil.getString(msg,
				UserInfo.LONGTITUDE));
		String username = BmobJsonUtil.getString(msg, UserInfo.USER_NAME);
		oldUser.setName(username);
		oldUser.setLat(lat);
		oldUser.setLng(lng);
		MapControler.getInstance().updateUserPosition(oldUser);
	}

	/**
	 * 查找指定的用户
	 */
	private MapUser getUser(String id) {
		if (mapUsers.containsKey(id)) {
			return mapUsers.get(id);
		}
		return null;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private MapUserManager() {
	}

	private static MapUserManager instance = null;

	public static MapUserManager getInstance() {
		if (instance == null) {
			instance = new MapUserManager();
		}
		return instance;
	}
}
