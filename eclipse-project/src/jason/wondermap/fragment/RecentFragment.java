package jason.wondermap.fragment;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

import jason.wondermap.R;
import jason.wondermap.adapter.MessageRecentAdapter;
import jason.wondermap.manager.ChatMessageManager;
import jason.wondermap.receiver.MyMessageReceiver;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.view.ClearEditText;
import jason.wondermap.view.dialog.DialogTips;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.bean.BmobRecent;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

/**
 * 最近会话
 * 
 * @author liuzhenhui
 * 
 */
public class RecentFragment extends ContentFragment implements
		OnItemClickListener, OnItemLongClickListener, EventListener {

	private final static String TAG = RecentFragment.class.getSimpleName();
	private ViewGroup mContainer;
	private ViewGroup mRootView;
	private boolean hidden;
	private ListView listview;
	private MessageRecentAdapter adapter;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝UI相关 生命周期＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d(TAG, "onCreateView");
		mContainer = container;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		L.d(TAG, "onCreateContentView");
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_recent,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		L.d(TAG, "onInitView");
		// 开启广播接收器==重构时要考虑同一管理
		initNewMessageBroadCast();
		initTopBarForLeft(mRootView, "会话");
		listview = (ListView) mRootView.findViewById(R.id.list);
		listview.setOnItemClickListener(this);
		listview.setOnItemLongClickListener(this);
		adapter = new MessageRecentAdapter(mContext,
				R.layout.item_conversation, BmobDB.create(getActivity())
						.queryRecents());
		listview.setAdapter(adapter);

	}

	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
		MyMessageReceiver.ehList.remove(this);// 取消监听推送的消息
	}

	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "最近会话页");
		if (!hidden) {
			refresh();
		}
		// 监听消息＝＝＝＝start
		MyMessageReceiver.ehList.add(this);// 监听推送的消息
		// 清空
		ChatMessageManager.mNewNum = 0;
		// 监听消息＝＝＝＝＝end
	}

	@Override
	public void onDestroyView() {
		Log.e(TAG, "onDestroyView");
		super.onDestroyView();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		// TODO Auto-generated method stub
		BmobRecent recent = adapter.getItem(position);
		// 重置未读消息
		BmobDB.create(getActivity()).resetUnread(recent.getTargetid());
		// 组装聊天对象
		Bundle bundle = new Bundle();
		bundle.putString(UserInfo.AVATAR, recent.getAvatar());
		bundle.putString(UserInfo.USER_NAME, recent.getUserName());
		bundle.putString(UserInfo.USER_ID, recent.getTargetid());
		wmFragmentManager.showFragment(WMFragmentManager.TYPE_CHAT, bundle);
		// 以下是获取代码
	}

	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					adapter = new MessageRecentAdapter(getActivity(),
							R.layout.item_conversation, BmobDB.create(
									getActivity()).queryRecents());
					listview.setAdapter(adapter);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部方法＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		BmobRecent recent = adapter.getItem(position);
		showDeleteDialog(recent);
		return true;
	}

	private void showDeleteDialog(final BmobRecent recent) {
		DialogTips dialog = new DialogTips(getActivity(), recent.getUserName(),
				"删除会话", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteRecent(recent);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	/**
	 * 删除会话 deleteRecent
	 */
	private void deleteRecent(BmobRecent recent) {
		adapter.remove(recent);
		BmobDB.create(getActivity()).deleteRecent(recent.getTargetid());
		BmobDB.create(getActivity()).deleteMessages(recent.getTargetid());
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	// 监听事件＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝从MainActivity移植过来的＝＝＝＝＝＝＝之后重构
	@Override
	public void onAddUser(BmobInvitation arg0) {

	}

	@Override
	public void onMessage(BmobMsg message) {
		refreshNewMsg(message);
	}

	@Override
	public void onNetChange(boolean arg0) {

	}

	@Override
	public void onOffline() {
	}

	@Override
	public void onReaded(String arg0, String arg1) {

	}

	private void refreshNewMsg(BmobMsg message) {
		// 也要存储起来
		if (message != null) {
			BmobChatManager.getInstance(mContext).saveReceiveMessage(false,
					message);
		}
		// 当前页面如果为会话页面，刷新此页面
		refresh();
	}

	NewBroadcastReceiver newReceiver;

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
	 * 新消息广播接收者
	 * 
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
}
