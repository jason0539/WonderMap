package jason.wondermap.manager;

import jason.wondermap.MainActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import android.content.Context;
import android.content.Intent;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;

/**
 * 常驻的消息管理
 * 
 * @author liuzhenhui
 * 
 */
public class ChatMessageManager {
	public static int mNewNum = 0;// 新到消息数量

	// ＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void notifyMsg(BmobMsg msg) {
		mNewNum++;
		// 更新ui,聊天界面的红点之类的
		// 也要存储起来
		if (msg != null) {
			BmobChatManager.getInstance(mContext)
					.saveReceiveMessage(false, msg);
		}
		showMsgNotify(msg);
	}

	/**
	 * 显示其他Tag的通知 showOtherNotify
	 */
	public void showOtherNotify(Context context, String username, String toId,
			String ticker, Class<?> cls) {
		boolean isAllow = WonderMapApplication.getInstance().getSpUtil()
				.isAllowPushNotify();
		boolean isAllowVoice = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVibrate();
		if (isAllow
				&& AccountUserManager.getInstance().getCurrentUser() != null
				&& AccountUserManager.getInstance().getCurrentUserid()
						.equals(toId)) {
			// 同时提醒通知,原来点击后跳转到好友添加请求页面，之后调整。／／NewFriendActivity
			BmobNotifyManager.getInstance(context).showNotify(isAllowVoice,
					isAllowVibrate, R.drawable.ic_app_icon, ticker, username,
					ticker.toString(), MainActivity.class);
		}
	}

	/**
	 * 显示与聊天消息的通知
	 */
	public void showMsgNotify(BmobMsg msg) {
		boolean isAllow = WonderMapApplication.getInstance().getSpUtil()
				.isAllowPushNotify();
		if (!isAllow) {// 关闭通知则一律不再通知，包括通知栏、声音、震动
			return;
		}
		// 更新通知栏
		int icon = R.drawable.ic_app_icon;
		String trueMsg = "";
		if (msg.getMsgType() == BmobConfig.TYPE_TEXT
				&& msg.getContent().contains("\\ue")) {
			trueMsg = "[表情]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_IMAGE) {
			trueMsg = "[图片]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_VOICE) {
			trueMsg = "[语音]";
		} else if (msg.getMsgType() == BmobConfig.TYPE_LOCATION) {
			trueMsg = "[位置]";
		} else {
			trueMsg = msg.getContent();
		}
		CharSequence tickerText = msg.getBelongUsername() + ":" + trueMsg;
		String contentTitle = msg.getBelongUsername() + " (" + mNewNum
				+ "条新消息)";

		Intent intent = new Intent(mContext, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

		boolean isAllowVoice = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVoice();
		boolean isAllowVibrate = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVibrate();

		BmobNotifyManager.getInstance(mContext).showNotifyWithExtras(
				isAllowVoice, isAllowVibrate, icon, tickerText.toString(),
				contentTitle, tickerText.toString(), intent);
	}

	public void onOffline() {
		// 下线
		BaseFragment.getMainActivity().showOfflineDialog();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static ChatMessageManager instance = null;
	private Context mContext;

	private ChatMessageManager() {
		mContext = WonderMapApplication.getInstance();
		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		// BmobChat.getInstance(this).startPollService(30);
	}

	public static ChatMessageManager getInstance() {
		if (instance == null) {
			instance = new ChatMessageManager();
		}
		return instance;
	}

}
