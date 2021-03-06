package jason.wondermap.manager;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.receiver.MyMessageReceiver;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONObject;

import android.os.Looper;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.listener.PushListener;

/**
 * 负责推送hello和world消息 目前全部推送，之后修改为world推送给指定用户，
 * 或者每隔几分钟推送一次hello，更新位置，长时间没有hello的，认为下线，心跳策略
 * 
 * @author liuzhenhui
 * 
 */
public class PushMsgSendManager {

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * mapHome进入时调用，wlocation定位时调用
	 */
	public void sayHello() {
		if (AccountUserManager.getInstance().getCurrentUserid().equals("")) {
			return;
		}
		if (CommonUtils.isNetworkAvailable(WonderMapApplication.getInstance())) {// 如果网络可用
			Map<String, String> map = new HashMap<String, String>();
			map.put(UserInfo.TAG, UserInfo.HELLO);
			map.put(UserInfo.USER_ID, AccountUserManager.getInstance()
					.getCurrentUserid());
			JSONObject jsonObject = new JSONObject(map);
			pushManager.pushMessageAll(jsonObject, new PushListener() {
				@Override
				public void onSuccess() {

				}

				@Override
				public void onFailure(int arg0, String arg1) {
					failReSendTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							L.i("resend msg...");
//							sayHello();
						}
					}, 100);
				}
			});
		} else {
			T.showLong(BaseFragment.getMainActivity(), R.string.network_tips);
		}
	}

	public void sayWorld() {
		if (CommonUtils.isNetworkAvailable(WonderMapApplication.getInstance())) {// 如果网络可用
			Map<String, String> map = new HashMap<String, String>();
			map.put(UserInfo.TAG, UserInfo.WORLD);
			map.put(UserInfo.USER_ID, AccountUserManager.getInstance()
					.getCurrentUserid());
			JSONObject jsonObject = new JSONObject(map);
			pushManager.pushMessageAll(jsonObject, new PushListener() {
				@Override
				public void onSuccess() {

				}

				@Override
				public void onFailure(int arg0, String arg1) {
					failReSendTimer.schedule(new TimerTask() {

						@Override
						public void run() {
							L.i("resend msg...");
//							sayWorld();
						}
					}, 100);
				}
			});
		} else {
			T.showLong(BaseFragment.getMainActivity(), R.string.network_tips);
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	BmobPushManager<MyMessageReceiver> pushManager = null;
	private Timer failReSendTimer;
	private String mUserId;

	private PushMsgSendManager() {
		pushManager = new BmobPushManager<MyMessageReceiver>(
				WonderMapApplication.getInstance());
		failReSendTimer = new Timer();
	}

	public void destroy() {
		failReSendTimer.cancel();
	}

	private static PushMsgSendManager instance = null;

	public static PushMsgSendManager getInstance() {
		if (instance == null) {
			instance = new PushMsgSendManager();
		}
		return instance;
	}
}
