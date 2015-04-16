package jason.wondermap.manager;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
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

import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.listener.PushListener;

public class PushMsgSendManager {
	// private Timer mTimer;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void sayHello() {
		// 开启一个15秒后超时的Callback，一直尝试发送，直到成功／／修正，一直发送可能发出多个，地图点重复，不再重复发送
		// mTimer.schedule(mConnTimeoutCallback, 15000);
		if (CommonUtils.isNetworkAvailable(WonderMapApplication.getInstance())) {// 如果网络可用
			Map<String, String> map = new HashMap<String, String>();
			map.put(UserInfo.TAG, UserInfo.HELLO);
			map.put(UserInfo.USER_NAME,
					BmobUserManager.getInstance(
							WonderMapApplication.getInstance())
							.getCurrentUserName());
			map.put(UserInfo.LATITUDE, WMapLocationManager.getInstance()
					.getLatitude() + "");
			map.put(UserInfo.LONGTITUDE, WMapLocationManager.getInstance()
					.getLongtitude() + "");
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
							sayHello();
						}
					}, 100);
				}
			});
		} else {
			T.showLong(WonderMapApplication.getInstance(),
					R.string.network_tips);
		}
	}

	public void sayWorld() {
		if (CommonUtils.isNetworkAvailable(WonderMapApplication.getInstance())) {// 如果网络可用
			Map<String, String> map = new HashMap<String, String>();
			map.put(UserInfo.TAG, UserInfo.WORLD);
			map.put(UserInfo.USER_NAME,
					BmobUserManager.getInstance(
							WonderMapApplication.getInstance())
							.getCurrentUserName());
			map.put(UserInfo.LATITUDE, WMapLocationManager.getInstance()
					.getLatitude() + "");
			map.put(UserInfo.LONGTITUDE, WMapLocationManager.getInstance()
					.getLongtitude() + "");
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
							sayHello();
						}
					}, 100);
				}
			});
		} else {
			T.showLong(WonderMapApplication.getInstance(),
					R.string.network_tips);
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
	}

	private static PushMsgSendManager instance = null;

	public static PushMsgSendManager getInstance() {
		if (instance == null) {
			instance = new PushMsgSendManager();
		}
		return instance;
	}
}
