package jason.wondermap.receiver;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.interfacer.onBaiduPushBindListener;
import jason.wondermap.manager.PushMsgManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.NetUtil;
import jason.wondermap.utils.PreUtils;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.T;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;

public class BDPushMessageReceiver extends FrontiaPushMessageReceiver {
	/**
	 * 接收绑定结果的监听器
	 */
	public static ArrayList<onBaiduPushBindListener> bindListeners = new ArrayList<onBaiduPushBindListener>();

	/*
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝收到百度服务端推送过来的消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "收到消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		L.i(messageString);
		// 还没登陆的情况下不处理消息
		if (!WonderMapApplication.getInstance().getSpUtil().hasLogin()) {
			return;
		}
		HelloMessage receivedMsg = WonderMapApplication.getInstance().getGson()
				.fromJson(message, HelloMessage.class);
		PushMsgManager.getInstance().handleMsg(receivedMsg);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝绑定后的相关回调＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	public void onBind(final Context context, int errorCode, String appid,
			String userId, String channelId, String requestId) {
		String responseString = "onBind errorCode=" + errorCode + " appid="
				+ appid + " userId=" + userId + " channelId=" + channelId
				+ " requestId=" + requestId;
		L.d("收到绑定结果：" + responseString);
		if (errorCode == 0) {// 绑定成功
			SharePreferenceUtil spUtil = WonderMapApplication.getInstance()
					.getSpUtil();
			spUtil.setAppId(appid);
			spUtil.setChannelId(channelId);
			spUtil.setUserId(userId);
			spUtil.setTag("美女");// 之后用来通过tag控制权限，目前tag闲置
			L.d("绑定成功");
		} else
		// 如果网络正常，则重试
		{
			if (NetUtil.isNetConnected(context)) {
				T.showLong(context, "启动失败，正在重试...");
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						PushManager.startWork(context,
								PushConstants.LOGIN_TYPE_API_KEY,
								WonderMapApplication.API_KEY);
						// FrontiaPush frontiaPush = FrontiaPush
						// .newInstance(context);
						// frontiaPush.start(WonderMapApplication.getInstance()
						// .getSpUtil().getAccessToken());
					}
				}, 2000);// 两秒后重新开始验证
			} else {
				T.showLong(context, R.string.net_error_tip);
			}
		}
		// 回调函数
		for (int i = 0; i < bindListeners.size(); i++)
			bindListeners.get(i).onBind(userId, errorCode);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝暂时无用＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	@Override
	public void onNotificationClicked(Context context, String title,
			String description, String customContentString) {

		String notifyString = "通知点击 title=\"" + title + "\" description=\""
				+ description + "\" customContent=" + customContentString;
		L.i(notifyString);

	}

	@Override
	public void onSetTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onSetTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		L.i(responseString);

	}

	@Override
	public void onDelTags(Context context, int errorCode,
			List<String> sucessTags, List<String> failTags, String requestId) {
		String responseString = "onDelTags errorCode=" + errorCode
				+ " sucessTags=" + sucessTags + " failTags=" + failTags
				+ " requestId=" + requestId;
		L.i(responseString);

	}

	@Override
	public void onListTags(Context context, int errorCode, List<String> tags,
			String requestId) {
		String responseString = "onListTags errorCode=" + errorCode + " tags="
				+ tags;
		L.i(responseString);

	}

	@Override
	public void onUnbind(Context context, int errorCode, String requestId) {
		String responseString = "onUnbind errorCode=" + errorCode
				+ " requestId = " + requestId;
		L.i(responseString);

		// 解绑定成功，设置未绑定flag，
		if (errorCode == 0) {
			PreUtils.unbind(context);
		}
	}

}
