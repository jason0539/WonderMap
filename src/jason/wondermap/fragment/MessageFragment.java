package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.adapter.FriendsListAdapter;
import jason.wondermap.bean.ChatMessage;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;
import jason.wondermap.interfacer.OnBaiduPushNewFriendListener;
import jason.wondermap.interfacer.OnBaiduPushNewMessageListener;
import jason.wondermap.manager.PushMsgReceiveManager;
import jason.wondermap.manager.UnReadMsgManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.StaticConstant;
import jason.wondermap.utils.T;
import jason.wondermap.utils.TimeUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.baidu.android.pushservice.PushManager;

public class MessageFragment extends ContentFragment implements
		OnBaiduPushNewFriendListener, OnBaiduPushNewMessageListener {

	private final static String TAG = MessageFragment.class.getSimpleName();
	private FriendsListAdapter mAdapter;
	private ListView mFrineds;
	private View mEmptyView;
	private WonderMapApplication mApplication;
	private SharePreferenceUtil mSpUtils;
	private ViewGroup mContainer;
	private LayoutInflater inflater;
	/**
	 * 存储userId-新来消息的个数
	 */
	public Map<String, Integer> mUserMessages = new HashMap<String, Integer>();

	/**
	 * 所有的用户
	 */
	private List<User> mUsersList;

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
		this.inflater = inflater;
		mApplication = WonderMapApplication.getInstance();
		mSpUtils = WonderMapApplication.getInstance().getSpUtil();

		// 聊天页面
		View view = inflater.inflate(R.layout.main_tab_weixin, mContainer,
				false);
		mFrineds = (ListView) view.findViewById(R.id.id_listview_friends);
		mEmptyView = inflater
				.inflate(R.layout.no_zuo_no_die, mContainer, false);
		mFrineds.setEmptyView(mEmptyView);
		return view;
	}

	@Override
	protected void onInitView() {
		L.d(TAG, "onInitView");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e(TAG, "onResume");
		if (!PushManager.isPushEnabled(getActivity()))
			PushManager.resumeWork(getActivity());
		// 更新用户列表
		mUsersList = mApplication.getUserDB().getUser();
		// 获取数据库中所有的用户
		mUserMessages = UnReadMsgManager.getInstance().getAllUsersUnreadMsg();
		mAdapter = new FriendsListAdapter(mUsersList, inflater, mUserMessages,
				mApplication);
		mFrineds.setAdapter(mAdapter);
		// 设置新朋友的监听
		PushMsgReceiveManager.friendListeners.add(this);
		// 设置新消息的监听
		PushMsgReceiveManager.msgListeners.add(this);
		mFrineds.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String userId = mUsersList.get(position).getUserId();
				if (userId.equals(mSpUtils.getUserId())) {
					T.showShort(getActivity(), "不能和自己聊天哈！");
					return;
				}
				if (mUserMessages.containsKey(userId)) {
					mUserMessages.remove(userId);
					mAdapter.notifyDataSetChanged();
				}
				Bundle bundle = new Bundle();
				bundle.putString(StaticConstant.FragBundleUserId, mUsersList
						.get(position).getUserId());
				wmFragmentManager.showFragment(WMFragmentManager.TYPE_CHAT,
						bundle);//
			}

		});
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.e(TAG, "onPause");
		/**
		 * 当onPause时，取消监听
		 */
		PushMsgReceiveManager.friendListeners.remove(this);
		PushMsgReceiveManager.msgListeners.remove(this);
	}

	/**
	 * 收到新消息时
	 */
	@Override
	public void onNewMessage(HelloMessage message) {
		// 如果是自己发送的，则直接返回
		if (message.getUserId() == mSpUtils.getUserId())
			return;
		// 如果该用户已经有未读消息，更新未读消息的个数，并通知更新未读消息接口，最后notifyDataSetChanged
		String userId = message.getUserId();
		if (mUserMessages.containsKey(userId)) {
			mUserMessages.put(userId, mUserMessages.get(userId) + 1);
		} else {
			mUserMessages.put(userId, 1);
		}
		// 将新来的消息进行存储
		ChatMessage chatMessage = new ChatMessage(message.getMessage(), true,
				userId, message.getHeadIcon(), message.getNickname(), false,
				TimeUtil.getTime(message.getTimeSamp()));
		mApplication.getMessageDB().add(userId, chatMessage);
		// 通知listview数据改变
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 监听新朋友到来的通知
	 */
	@Override
	public void onNewFriend(User u) {
		Log.e(TAG, "get a new friend :" + u.getUserId() + " , " + u.getNick());
		mUsersList.add(u);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroyView() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		Log.e(TAG, "onDestroyView");
		super.onDestroyView();
	}

}
