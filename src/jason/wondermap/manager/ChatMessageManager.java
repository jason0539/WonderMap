package jason.wondermap.manager;

import jason.wondermap.MainActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.receiver.MyMessageReceiver;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobNotifyManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.inteface.EventListener;

public class ChatMessageManager implements EventListener {

	// ＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public void unInit() {
		mContext.unregisterReceiver(newReceiver);
		mContext.unregisterReceiver(userReceiver);
		// 取消定时检测服务
		// BmobChat.getInstance(this).stopPollService();
	}

	@Override
	public void onMessage(BmobMsg message) {
		// TODO Auto-generated method stub
		refreshNewMsg(message);
	}

	@Override
	public void onNetChange(boolean isNetConnected) {
		// TODO Auto-generated method stub
		if (isNetConnected) {
			// ShowToast(R.string.network_tips);
		}
	}

	@Override
	public void onAddUser(BmobInvitation message) {
		// TODO Auto-generated method stub
		refreshInvite(message);
	}

	@Override
	public void onOffline() {
		// TODO Auto-generated method stub
		// showOfflineDialog(this);
	}

	@Override
	public void onReaded(String conversionId, String msgTime) {
		// TODO Auto-generated method stub
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	NewBroadcastReceiver newReceiver;
	TagBroadcastReceiver userReceiver;

	/**
	 * 新消息广播接收者
	 */
	private class NewBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 刷新界面
			refreshNewMsg(null);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	private void initNewMessageBroadCast() {
		// 注册接收消息广播
		newReceiver = new NewBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_NEW_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		mContext.registerReceiver(newReceiver, intentFilter);
	}

	/**
	 * 刷新界面
	 */
	private void refreshNewMsg(BmobMsg message) {
		// 声音提示
		boolean isAllow = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			WonderMapApplication.getInstance().getMediaPlayer().start();
		}
		// 更新ui,聊天界面的红点之类的
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(mContext).saveReceiveMessage(true,
					message);
		}
		if (BaseFragment.getWMFragmentManager().getCurrentFragmentType() == WMFragmentManager.TYPE_CHAT) {
			// 当前页面如果为会话页面，刷新此页面
			if (BaseFragment.getWMFragmentManager().getCurrentFragment() != null) {
				// BaseFragment.getWMFragmentManager().getCurrentFragment().refresh();
			}
		}
	}

	private void initTagMessageBroadCast() {
		// 注册接收消息广播
		userReceiver = new TagBroadcastReceiver();

		IntentFilter intentFilter = new IntentFilter(
				BmobConfig.BROADCAST_ADD_USER_MESSAGE);
		// 优先级要低于ChatActivity
		intentFilter.setPriority(3);
		mContext.registerReceiver(userReceiver, intentFilter);
	}

	/**
	 * 标签消息广播接收者
	 */
	private class TagBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			BmobInvitation message = (BmobInvitation) intent
					.getSerializableExtra("invite");
			refreshInvite(message);
			// 记得把广播给终结掉
			abortBroadcast();
		}
	}

	/**
	 * 刷新好友请求
	 */
	private void refreshInvite(BmobInvitation message) {
		boolean isAllow = WonderMapApplication.getInstance().getSpUtil()
				.isAllowVoice();
		if (isAllow) {
			WonderMapApplication.getInstance().getMediaPlayer().start();
		}
		// iv_contact_tips.setVisibility(View.VISIBLE);
		if (BaseFragment.getWMFragmentManager().getCurrentFragmentType() == WMFragmentManager.TYPE_CONTACT) {
			if (BaseFragment.getWMFragmentManager().getCurrentFragment() != null) {
				// BaseFragment.getWMFragmentManager().getCurrentFragment().refresh();
			}
		} else {
			// 同时提醒通知
			String tickerText = message.getFromname() + "请求添加好友";
			boolean isAllowVibrate = WonderMapApplication.getInstance()
					.getSpUtil().isAllowVibrate();
			BmobNotifyManager.getInstance(mContext).showNotify(isAllow,
					isAllowVibrate, R.drawable.ic_launcher, tickerText,
					message.getFromname(), tickerText.toString(),
					MainActivity.class);
			// 此处应该为NewFriendActivity
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static ChatMessageManager instance = null;
	private Context mContext;

	private ChatMessageManager() {
		mContext = WonderMapApplication.getInstance();
		// 开启定时检测服务（单位为秒）-在这里检测后台是否还有未读的消息，有的话就取出来
		// 如果你觉得检测服务比较耗流量和电量，你也可以去掉这句话-同时还有onDestory方法里面的stopPollService方法
		// BmobChat.getInstance(this).startPollService(30);
		// 开启广播接收器
		initNewMessageBroadCast();
		initTagMessageBroadCast();
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空 每次mainactivity进入onresume的时候，清空消息数量
		MyMessageReceiver.mNewNum = 0;
	}

	public static ChatMessageManager getInstance() {
		if (instance == null) {
			instance = new ChatMessageManager();
		}
		return instance;
	}
}
