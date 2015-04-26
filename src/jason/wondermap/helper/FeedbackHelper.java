package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.FeedBack;
import jason.wondermap.manager.AccountUserManager;
import android.util.Log;
import cn.bmob.v3.listener.SaveListener;

/**
 * 负责用户意见反馈的上传
 * 
 * @author liuzhenhui
 * 
 */
public class FeedbackHelper {

	/**
	 * 保存反馈信息到Bmob云数据库中
	 */
	public void saveFeedbackMsg(String msg, final SaveListener listener) {
		FeedBack feedback = new FeedBack(AccountUserManager.getInstance()
				.getCurrentUser(), msg);
		feedback.save(WonderMapApplication.getInstance(), new SaveListener() {

			@Override
			public void onSuccess() {
				Log.i("bmob", "反馈信息已保存到服务器");
				listener.onSuccess();
			}

			@Override
			public void onFailure(int code, String arg0) {
				Log.e("bmob", "保存反馈信息失败：" + arg0);
				listener.onFailure(code, arg0);
			}
		});
	}
}
