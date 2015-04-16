package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.FeedBack;
import android.util.Log;
import cn.bmob.v3.listener.SaveListener;

/**
 * 负责用户意见反馈的上传
 * 
 * @author liuzhenhui
 * 
 */
public class FeedbackManager {

	/**
	 * 保存反馈信息到Bmob云数据库中
	 */
	public void saveFeedbackMsg(String msg) {
		FeedBack feedback = new FeedBack(UserinfoAndLocationManager
				.getInstance().getCurrentUserName(), msg);
		feedback.save(WonderMapApplication.getInstance(), new SaveListener() {

			@Override
			public void onSuccess() {
				Log.i("bmob", "反馈信息已保存到服务器");
			}

			@Override
			public void onFailure(int code, String arg0) {
				Log.e("bmob", "保存反馈信息失败：" + arg0);
			}
		});
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static FeedbackManager instance;

	private FeedbackManager() {
	}

	public static FeedbackManager getInstance() {
		if (instance == null) {
			instance = new FeedbackManager();
		}
		return instance;
	}
}
