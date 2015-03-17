package jason.wondermap.receiver;

import jason.wondermap.MainActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.ChatMessage;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.dao.UserDB;
import jason.wondermap.interfacer.OnBaiduPushNewFriendListener;
import jason.wondermap.interfacer.OnBaiduPushNewMessageListener;
import jason.wondermap.interfacer.OnNetChangeListener;
import jason.wondermap.interfacer.onBaiduPushBindListener;
import jason.wondermap.task.SendMsgAsyncTask;
import jason.wondermap.utils.L;
import jason.wondermap.utils.NetUtil;
import jason.wondermap.utils.PreUtils;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.StaticConstant;
import jason.wondermap.utils.T;
import jason.wondermap.utils.TimeUtil;

import java.util.ArrayList;
import java.util.List;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;
import com.baidu.frontia.api.FrontiaPush;
import com.baidu.frontia.api.FrontiaPushMessageReceiver;

public class BDPushMessageReceiver extends FrontiaPushMessageReceiver {
	public static final int NOTIFY_ID = 0x000;
	public static int mNewNum = 0;// 通知栏新消息条目，我只是用了一个全局变量，

	/**
	 * 新消息的监听
	 */
	public static ArrayList<OnBaiduPushNewMessageListener> msgListeners = new ArrayList<OnBaiduPushNewMessageListener>();
	/**
	 * 新用户加入的监听
	 */
	public static ArrayList<OnBaiduPushNewFriendListener> friendListeners = new ArrayList<OnBaiduPushNewFriendListener>();
	/**
	 * 网络的监听
	 */
	public static ArrayList<OnNetChangeListener> netListeners = new ArrayList<OnNetChangeListener>();
	/**
	 * 接收绑定结果的监听器
	 */
	public static ArrayList<onBaiduPushBindListener> bindListeners = new ArrayList<onBaiduPushBindListener>();

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

	/*
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝收到百度服务端推送过来的消息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 */
	@Override
	public void onMessage(Context context, String message,
			String customContentString) {
		String messageString = "收到消息 message=\"" + message
				+ "\" customContentString=" + customContentString;
		L.i(messageString);
		//还没登陆的情况下不处理消息
		if(!WonderMapApplication.getInstance().getSpUtil().hasLogin()){
			return;
		}
		HelloMessage receivedMsg = WonderMapApplication.getInstance().getGson()
				.fromJson(message, HelloMessage.class);
		parseMessage(receivedMsg);
	}

	/**
	 * 对收到的消息进行分析，1，新人消息 2，自动回复消息 3，普通消息
	 */
	private void parseMessage(HelloMessage msg) {
		L.d("parseMessage into");
		String userId = msg.getUserId();
		L.d("userId is " + userId);
		// 自己的消息
		if (userId.equals(WonderMapApplication.getInstance().getSpUtil()
				.getUserId())) {
			L.d("自己的消息，返回");
			return;
		}
		// 新人加入或者自动回复，都要在地图上显示
		if (checkHasNewFriend(msg) || checkAutoResponse(msg))
			return;
		// 普通消息
		UserDB userDB = WonderMapApplication.getInstance().getUserDB();
		User user = userDB.selectInfo(userId);
		// 漏网之鱼
		if (user == null) {
			user = new User(userId, msg.getChannelId(), msg.getNickname(), "",
					0);
			userDB.addUser(user);
			// 通知监听的面板,新人加入
			for (OnBaiduPushNewFriendListener listener : friendListeners)
				listener.onNewFriend(user);
		}
		if (msgListeners.size() > 0) {// 有监听的时候，传递下去
			for (int i = 0; i < msgListeners.size(); i++)
				msgListeners.get(i).onNewMessage(msg);
		} else
		// 当前没有任何监听，即处理后台状态
		{
			// 将新来的消息进行存储
			ChatMessage chatMessage = new ChatMessage(msg.getMessage(), true,
					userId, msg.getHeadIcon(), msg.getNickname(), false,
					TimeUtil.getTime(msg.getTimeSamp()));
			WonderMapApplication.getInstance().getMessageDB()
					.add(userId, chatMessage);
			showNotify(msg);
		}
	}

	/**
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝检测是否是新人加入，代表的是老人＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * 
	 * @param msg
	 */
	private boolean checkHasNewFriend(HelloMessage msg) {
		L.d("checkHasNewFriend into");
		String userId = msg.getUserId();
		L.d("userId is "+userId);
		String hello = msg.getHello();
		// 新人发送的消息
		if (!TextUtils.isEmpty(hello)) {
			L.d("新人，要发送消息回复");
			// 新人
			User u = new User(userId, msg.getChannelId(), msg.getNickname(),
					msg.getHeadIcon(), 0);
			WonderMapApplication.getInstance().getUserDB().addUser(u);// 存入或更新好友
			T.showShort(WonderMapApplication.getInstance(), u.getNick() + "加入");
			// 给新人回复一个应答
			HelloMessage message = new HelloMessage(System.currentTimeMillis(),
					"");
			message.setWorld(StaticConstant.SayWorld);
			new SendMsgAsyncTask(WonderMapApplication.getInstance().getGson()
					.toJson(message), userId).send();
			// 通知监听的面板，新人加入
			for (OnBaiduPushNewFriendListener listener : friendListeners)
				listener.onNewFriend(u);
			// 地图显示新人位置
			WMapControler.getInstance().addMarker(msg.getLat(), msg.getLng());
			return true;
		}
		return false;
	}

	/**
	 * ＝＝＝＝＝＝＝＝＝＝＝＝＝＝检测是否是自动回复，代表的是新人＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	 * 
	 * @param msg
	 */
	private boolean checkAutoResponse(HelloMessage msg) {
		String world = msg.getWorld();
		String userId = msg.getUserId();
		if (!TextUtils.isEmpty(world)) {
			User u = new User(userId, msg.getChannelId(), msg.getNickname(),
					msg.getHeadIcon(), 0);
			WonderMapApplication.getInstance().getUserDB().addUser(u);// 存入或更新好友
			T.showShort(WonderMapApplication.getInstance(),"收到"+ u.getNick() + "回复");
			// 通知监听的面板
			for (OnBaiduPushNewFriendListener listener : friendListeners)
				listener.onNewFriend(u);
			// 添加好友位置
			WMapControler.getInstance().addMarker(msg.getLat(), msg.getLng());
			return true;
		}
		return false;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝暂时无用＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private void showNotify(HelloMessage message) {
		mNewNum++;
		// 更新通知栏
		WonderMapApplication application = WonderMapApplication.getInstance();

		int icon = R.drawable.copyright;
		CharSequence tickerText = message.getNickname() + ":"
				+ message.getMessage();
		long when = System.currentTimeMillis();
		Notification notification = new Notification(icon, tickerText, when);
		notification.flags = Notification.FLAG_NO_CLEAR;
		// 设置默认声音
		// notification.defaults |= Notification.DEFAULT_SOUND;
		// 设定震动(需加VIBRATE权限)
		notification.defaults |= Notification.DEFAULT_VIBRATE;
		notification.contentView = null;

		Intent intent = new Intent(application, MainActivity.class);
		// 当点击通知时，我们让原有的Activity销毁，重新实例化一个
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(application, 0,
				intent, 0);
		notification.setLatestEventInfo(WonderMapApplication.getInstance(),
				application.getSpUtil().getUserNick() + " (" + mNewNum
						+ "条新消息)", tickerText, contentIntent);
		application.getNotificationManager().notify(NOTIFY_ID, notification);// 通知一下才会生效哦
	}

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
