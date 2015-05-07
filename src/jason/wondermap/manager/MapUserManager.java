package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.controler.MapControler;
import jason.wondermap.helper.OnlineUserHelper;
import jason.wondermap.interfacer.GetOnlineUserListener;
import jason.wondermap.interfacer.MapUserTransferListener;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserTransferUtil;
import jason.wondermap.utils.WModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import cn.bmob.im.bean.BmobChatUser;

import com.baidu.mapapi.map.Marker;

/**
 * 地图显示的用户管理类，显示，位置更新，
 * 
 * @author liuzhenhui
 * 
 */
public class MapUserManager {
	public static final String ShowFriendOrAll = "sp_fiend_or_all";
	public static final int Period = 60 * 1000;// 每分钟更新一次好友信息
	private boolean isFriend = false;
	private Timer timer;
	private HashMap<String, MapUser> allMapUsers = new HashMap<String, MapUser>();
	private HashMap<String, BmobChatUser> lastAllMapUsers = new HashMap<String, BmobChatUser>();
	private HashMap<String, MapUser> friendMapUsers = new HashMap<String, MapUser>();
	private Map<String, BmobChatUser> lastFriendMaps = new HashMap<String, BmobChatUser>();

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 只显示好友
	 */
	public void showFriends() {
		isFriend = true;
		WonderMapApplication.getInstance().getSpUtil()
				.setValue(ShowFriendOrAll, isFriend);
		MapControler.getInstance().clearMarker();
		// 第一次切换到好友地图，好友为空，则从内存读取出来，添加到地图
		if (friendMapUsers.size() == 0) {
			lastFriendMaps = new HashMap<String, BmobChatUser>(
					AccountUserManager.getInstance().getContactList());
			ArrayList<BmobChatUser> list = CollectionUtils
					.map2arrayList(lastFriendMaps);
			int size = list.size();
			for (int i = 0; i < size; i++) {
				UserTransferUtil.FriendToMapUser(list.get(i),
						new MapUserTransferListener() {
							@Override
							public void onSuccess(MapUser user) {
								addUser(user);
							}
						});
			}
		}
		// 之后每次切换，都是直接恢复到地图上就行了
		else {
			onResumeAllUsersOnMap();
		}
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				HashMap<String, MapUser> users = null;
				synchronized (friendMapUsers) {
					users = new HashMap<String, MapUser>(friendMapUsers);
				}
				Iterator<Entry<String, MapUser>> iterator = users.entrySet()
						.iterator();
				Entry<String, MapUser> entry;
				while (iterator.hasNext()) {
					entry = iterator.next();
					entry.getValue().updateSelf();
				}
				// 每次更新后，检查是否有新好友添加进来
				Map<String, BmobChatUser> newestMap = AccountUserManager
						.getInstance().getContactList();// 最新好友列表
				L.d(WModel.UpdateFriend, "最新好友个数" + newestMap.size());
				// 查看原来的列表
				L.d(WModel.UpdateFriend, "原来好友个数" + lastFriendMaps.size());
				if (newestMap.size() > lastFriendMaps.size()) {// 有新好友
					List<BmobChatUser> lists = CollectionUtils
							.map2list(newestMap);
					// 遍历新列表，找出新好友并添加
					for (BmobChatUser bmobChatUser : lists) {
						if (!lastFriendMaps.containsKey(bmobChatUser
								.getObjectId())) {
							L.d(WModel.UpdateFriend, bmobChatUser.getUsername()
									+ "是新好友，加入成功");
							lastFriendMaps.put(bmobChatUser.getObjectId(),
									bmobChatUser);
							UserTransferUtil.FriendToMapUser(bmobChatUser,
									new MapUserTransferListener() {
										@Override
										public void onSuccess(MapUser user) {
											addUser(user);
										}
									});
						}
					}
				} else if (newestMap.size() < lastFriendMaps.size()) {
					// 还要考虑删除的情况
					List<BmobChatUser> lists = CollectionUtils
							.map2list(lastFriendMaps);
					// 遍历老列表，找出被删的好友，从涂上删除
					for (BmobChatUser bmobChatUser : lists) {
						if (!newestMap.containsKey(bmobChatUser.getObjectId())) {
							L.d(WModel.UpdateFriend, bmobChatUser.getUsername()
									+ "被删除了");
							String removedId = bmobChatUser.getObjectId();
							lastFriendMaps.remove(removedId);
							// 从图上删除的逻辑
							friendMapUsers.get(removedId).getMarker().remove();
							friendMapUsers.remove(removedId);
						}
					}
				}
			}
		}, 0, Period);
	}

	/**
	 * 只显示好友
	 */
	public void showOnLine() {
		isFriend = false;
		WonderMapApplication.getInstance().getSpUtil()
				.setValue(ShowFriendOrAll, isFriend);
		MapControler.getInstance().clearMarker();
		// 第一次切换到在线地图，好友为空，则从内存读取出来，添加到地图
		if (allMapUsers.size() == 0) {
			onlineUserHelper.getOnlineList(new GetOnlineUserListener() {
				@Override
				public void onSuccess(List<BmobChatUser> onlineUsers) {
					lastAllMapUsers = new HashMap<String, BmobChatUser>(
							CollectionUtils.list2map(onlineUsers));
					ArrayList<BmobChatUser> list = CollectionUtils
							.map2arrayList(lastAllMapUsers);
					int size =  list.size();
					for (int i = 0; i <size; i++) {
						UserTransferUtil.FriendToMapUser(list.get(i),
								new MapUserTransferListener() {
									@Override
									public void onSuccess(MapUser user) {
										addUser(user);
									}
								});
					}
				}
			});
		}
		// 之后每次切换，都是直接恢复到地图上就行了
		else {
			onResumeAllUsersOnMap();
		}
		if (timer != null) {
			timer.cancel();
		}
		timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				HashMap<String, MapUser> users = null;
				synchronized (allMapUsers) {
					users = new HashMap<String, MapUser>(allMapUsers);
				}
				Iterator<Entry<String, MapUser>> iterator = users.entrySet()
						.iterator();
				Entry<String, MapUser> entry;
				while (iterator.hasNext()) {
					entry = iterator.next();
					entry.getValue().updateSelf();
				}
				// 每次更新后，检查是否在线用户有变化
				onlineUserHelper.getOnlineList(new GetOnlineUserListener() {
					@Override
					public void onSuccess(List<BmobChatUser> onlineUsers) {
						Map<String, BmobChatUser> newestMap = CollectionUtils
								.list2map(onlineUsers);// 最新在线列表
						List<BmobChatUser> lists = CollectionUtils
								.map2list(newestMap);
						// 遍历新列表，找出新好友并添加
						for (BmobChatUser bmobChatUser : lists) {
							String objectId = bmobChatUser.getObjectId();
							if (!lastAllMapUsers.containsKey(objectId)) {
								// 之前添加过，下线的
								if (allMapUsers.containsKey(objectId)) {
									L.d(WModel.UpdateFriend,
											bmobChatUser.getUsername() + "被删除了");
									lastAllMapUsers.remove(objectId);
									// 从图上删除的逻辑
									allMapUsers.remove(objectId).getMarker()
											.remove();
								}
								// 之前不包含，新上线的
								else {
									L.d(WModel.UpdateFriend,
											bmobChatUser.getUsername()
													+ "是上线的，加入成功");
									lastAllMapUsers.put(objectId, bmobChatUser);
									UserTransferUtil.FriendToMapUser(
											bmobChatUser,
											new MapUserTransferListener() {
												@Override
												public void onSuccess(
														MapUser user) {
													addUser(user);
												}
											});
								}
							}
						}
						;
					}
				});
			}
		}, 0, Period);
	}

	/**
	 * 显示所有人
	 */
	public void showAll() {
		timer.cancel();
		isFriend = false;
		WonderMapApplication.getInstance().getSpUtil()
				.setValue(ShowFriendOrAll, isFriend);
		MapControler.getInstance().clearMarker();
		onResumeAllUsersOnMap();
	}

	/**
	 * activity退到后台进入时在onResume里面调用，恢复所有marker,以连表中存储的为准
	 */
	public void onResumeAllUsersOnMap() {
		L.d(WModel.EnsureEveryoneOnMap,
				"ensureAllUsersOnMap into,user count = " + allMapUsers.size());
		HashMap<String, MapUser> users = null;
		if (isFriend) {
			synchronized (friendMapUsers) {
				users = new HashMap<String, MapUser>(friendMapUsers);
				friendMapUsers.clear();
			}
		}
		// 恢复所有人的marker
		else {
			synchronized (allMapUsers) {
				users = new HashMap<String, MapUser>(allMapUsers);
				allMapUsers.clear();
			}
		}
		MapControler.getInstance().clearMarker();// 先把所有marker清除掉
		Iterator<Entry<String, MapUser>> iterator = users.entrySet().iterator();
		Entry<String, MapUser> entry;
		while (iterator.hasNext()) {
			entry = iterator.next();
			addUser(entry.getValue());
		}
		users = null;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝显示所有人的逻辑＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 收到推送消息后添加的
	 * 
	 * @param userId
	 */
	public void addUserFromUserId(String userId) {
		if (userId.equals(AccountUserManager.getInstance().getCurrentUserid())) {// 自己的消息忽略
			return;
		}
		if (isFriend) {
			// 好友模式时收到其他人，则转换成mapuser后存入list，不往地图显示，onresume的时候会显示
			UserTransferUtil.HelloMsgToUser(userId,
					new MapUserTransferListener() {
						@Override
						public void onSuccess(MapUser user) {
							// 只显示好友时，收到hello直接存储，不显示到地图，但是要在切换后显示出来
							allMapUsers.put(user.getObjectId(), user);
						}
					});
			return;
		}
		// 查询是否已经添加过该用户
		MapUser alreadExitsUser = getUser(userId);
		// 添加过则更新位置
		if (alreadExitsUser != null) {
			alreadExitsUser.updateSelf();
		}
		// 没添加则添加
		else {
			L.d(WModel.EnsureEveryoneOnMap, "添加用户" + userId);
			UserTransferUtil.HelloMsgToUser(userId,
					new MapUserTransferListener() {
						@Override
						public void onSuccess(MapUser user) {
							// 只显示好友时，收到hello直接存储，不显示到地图，但是要在切换后显示出来
							addUser(user);
						}
					});
		}
	}

	/**
	 * 向地图添加新用户，如果已经添加过则更新位置
	 */
	private void addUser(MapUser user) {
		Marker marker = MapControler.getInstance().addUser(user);
		user.setMarker(marker);
		// 好友
		if (isFriend) {
			friendMapUsers.put(user.getObjectId(), user);
		}
		// 所有人
		else {
			allMapUsers.put(user.getObjectId(), user);
		}
	}

	/**
	 * 获取目前应该显示的所有用户，用来给WMapControl判断点击的marker是那个用户的
	 */
	public synchronized HashMap<String, MapUser> getMapUsers() {
		if (isFriend) {
			return friendMapUsers;
		} else {
			return allMapUsers;
		}
	}

	// // ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 收到推送时，查找指定的用户，判断是否已经添加
	 */
	private MapUser getUser(String id) {
		if (isFriend) {
			if (friendMapUsers.containsKey(id)) {
				return friendMapUsers.get(id);
			}
		} else {
			if (allMapUsers.containsKey(id)) {
				return allMapUsers.get(id);
			}
		}
		return null;
	}

	public boolean isOnlyShowFriends() {
		return isFriend;
	}

	public void onPause() {
		L.d(WModel.UpdateFriend, "暂停更新用户位置");
		isPause = true;
		if (timer != null) {
			timer.cancel();
		}
	}

	/**
	 * 恢复好友
	 */
	public void onResume() {
		L.d(WModel.UpdateFriend, "恢复更新用户位置");
		isPause = false;
		if (isFriend) {
			showFriends();
		} else {
			showOnLine();
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private MapUserManager() {
		isFriend = WonderMapApplication.getInstance().getSpUtil()
				.getValue(ShowFriendOrAll, false);
		onlineUserHelper = new OnlineUserHelper(
				WonderMapApplication.getInstance());
		onResume();
	}

	private static MapUserManager instance = null;
	private OnlineUserHelper onlineUserHelper;
	private boolean isPause = false;

	public static MapUserManager getInstance() {
		if (instance == null) {
			instance = new MapUserManager();
		}
		return instance;
	}
}
