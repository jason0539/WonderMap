package jason.wondermap.interfacer;

import jason.wondermap.bean.MapUser;

import java.util.HashMap;

public interface FriendToMapUserListener {
	public void onSuccess(HashMap<String, MapUser> maps);
}
