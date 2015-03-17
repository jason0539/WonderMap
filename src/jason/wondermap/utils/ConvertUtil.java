package jason.wondermap.utils;

import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;

public class ConvertUtil {
	/**
	 * 从收到的hello信息中提取该用户
	 * 
	 * @param msg
	 * @return
	 */
	public static User HelloMsgToUser(HelloMessage msg) {
		User u = new User(msg.getUserId(), msg.getChannelId(),
				msg.getNickname(), msg.getHeadIcon(), msg.getLat(),
				msg.getLng(), 0);
		return u;
	}
}
