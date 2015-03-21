package jason.wondermap.utils;

import android.os.Bundle;
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
				msg.getLng(), "");
		return u;
	}

	public static Bundle UserPutInBundle(User user) {
		Bundle bundle = new Bundle();
		bundle.putString(StaticConstant.FragBundleUserId, user.getUserId());
		bundle.putString(StaticConstant.FragBundleChannelId,
				user.getChannelId());
		bundle.putString(StaticConstant.FragBundleNick, user.getNick());
		bundle.putString(StaticConstant.FragBundleHeadIcon, user.getHeadIcon());
		bundle.putString(StaticConstant.FragBundleGroup, user.getGroup());
		bundle.putDouble(StaticConstant.FragBundleLat, user.getLat());
		bundle.putDouble(StaticConstant.FragBundleLng, user.getLng());
		return bundle;
	}

	public static User GetUserFromBundle(Bundle b) {
		User user = new User(b.getString(StaticConstant.FragBundleUserId),
				b.getString(StaticConstant.FragBundleChannelId),
				b.getString(StaticConstant.FragBundleNick),
				b.getString(StaticConstant.FragBundleHeadIcon),
				b.getDouble(StaticConstant.FragBundleLat),
				b.getDouble(StaticConstant.FragBundleLng),
				b.getString(StaticConstant.FragBundleGroup));
		return user;

	}
}
