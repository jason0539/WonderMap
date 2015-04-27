package jason.wondermap.utils;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.bean.User;
import jason.wondermap.interfacer.MapUserDownLoadHeadListener;
import jason.wondermap.interfacer.MapUserTransferListener;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class UserTransferUtil {
	/**
	 * hello消息转换成MapUser
	 * 
	 * @param msg
	 */
	public static void HelloMsgToUser(String id,
			final MapUserTransferListener listener) {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						if (object.size() <= 0) {
							return;
						}
						MapUser.createMapuser(object.get(0),
								new MapUserDownLoadHeadListener() {

									@Override
									public void onSuccess(MapUser uMapUser) {
										listener.onSuccess(uMapUser);
									}
								});
					}

					@Override
					public void onError(int code, String msg) {
					}
				});
	}

	/**
	 * 普通好友转换成MapUser
	 */
	public static void FriendToMapUser(BmobChatUser user,
			final MapUserTransferListener listener) {
		String id = user.getObjectId();
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						if (object.size() <= 0) {
							return;
						}
						MapUser.createMapuser(object.get(0),
								new MapUserDownLoadHeadListener() {

									@Override
									public void onSuccess(MapUser uMapUser) {
										listener.onSuccess(uMapUser);
									}
								});
					}

					@Override
					public void onError(int code, String msg) {
					}
				});
	}

	public static void updateUserInfo(String id) {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						if (object.size() <= 0) {
							return;
						}
						object.get(0).getLat();
					}

					@Override
					public void onError(int code, String msg) {
					}
				});
	}
}
