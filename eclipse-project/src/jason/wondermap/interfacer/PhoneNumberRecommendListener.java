package jason.wondermap.interfacer;

import jason.wondermap.bean.User;

import java.util.HashMap;

public interface PhoneNumberRecommendListener {
	public void onProgress(int total,int progress);
	public void onSuccess(HashMap<String, User> recommedFriends);
}
