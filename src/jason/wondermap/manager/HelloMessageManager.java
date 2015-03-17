package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.interfacer.OnHelloMessageSendSuccessListener;
import jason.wondermap.task.SendMsgAsyncTask;
import jason.wondermap.utils.L;

import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

public class HelloMessageManager {
	private SendMsgAsyncTask task;
	private Gson mGson;
//	private Timer mTimer;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void sayHello() {
		// 开启一个15秒后超时的Callback，一直尝试发送，直到成功／／修正，一直发送可能发出多个，地图点重复，不再重复发送
//		mTimer.schedule(mConnTimeoutCallback, 15000);
		HelloMessage firstSendMsg = new HelloMessage(
				System.currentTimeMillis(), "");
		firstSendMsg.setHello("hello");
		task = new SendMsgAsyncTask(mGson.toJson(firstSendMsg), "");
		task.setOnHelloMessageSendScuessListener(new OnHelloMessageSendSuccessListener() {
			@Override
			public void sendScuess() {
//				mTimer.cancel();
				L.d("Hello 发送成功");
			}
		});
		task.send();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 默认15秒hello消息发送失败，则先停止发送,然后重试
	 */
	private TimerTask mConnTimeoutCallback = new TimerTask() {

		@Override
		public void run() {
			if (task != null) {
				task.stop();
			}
			L.d("发送hello消息超时，请重试");
			// sayHello();
		}
	};

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private HelloMessageManager() {
		init();
	}

	private void init() {
		mGson = WonderMapApplication.getInstance().getGson();
//		mTimer = new Timer();
	}

	private void unInit() {
		if (task != null)
			task.stop();
	}

	private static HelloMessageManager instance = null;

	public static HelloMessageManager getInstance() {
		if (instance == null) {
			instance = new HelloMessageManager();
		}
		return instance;
	}
}
