package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.dao.MessageDB;
import jason.wondermap.dao.UserDB;
import jason.wondermap.utils.L;

import java.util.Map;

public class UnReadMsgManager {
	private MessageDB msgDb;
	private UserDB userDb;

	/**
	 * 获取所有未读消息数量
	 * 
	 * @return
	 */
	public int getUnreadMsgNum() {
		int mUnReadedMsgs = 0;
		Map<String, Integer> mUserMessages = getAllUsersUnreadMsg();
		for (Integer val : mUserMessages.values()) {
			mUnReadedMsgs += val;
		}
		L.d("未读消息数量" + mUnReadedMsgs);
		return mUnReadedMsgs;
	}

	/**
	 * 获取所有用户未读消息
	 * 
	 * @return
	 */
	public Map<String, Integer> getAllUsersUnreadMsg() {
		return msgDb.getUserUnReadMsgs(userDb.getUserIds());
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static UnReadMsgManager instance = null;

	private UnReadMsgManager() {
		msgDb = WonderMapApplication.getInstance().getMessageDB();
		userDb = WonderMapApplication.getInstance().getUserDB();
	}

	public static UnReadMsgManager getInstance() {
		if (instance == null) {
			instance = new UnReadMsgManager();
		}
		return instance;
	}
}
