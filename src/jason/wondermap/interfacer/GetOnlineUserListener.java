package jason.wondermap.interfacer;

import java.util.List;

import cn.bmob.im.bean.BmobChatUser;

public interface GetOnlineUserListener {
	void onSuccess(List<BmobChatUser> onlineUsers);
}
