package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.adapter.ChatMessageAdapter;
import jason.wondermap.bean.ChatMessage;
import jason.wondermap.bean.HelloMessage;
import jason.wondermap.bean.User;
import jason.wondermap.dao.UserDB;
import jason.wondermap.interfacer.OnBaiduPushNewMessageListener;
import jason.wondermap.manager.PushMsgReceiveManager;
import jason.wondermap.task.SendMsgAsyncTask;
import jason.wondermap.utils.ConvertUtil;
import jason.wondermap.utils.NetUtil;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.StaticConstant;
import jason.wondermap.utils.T;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

public class ChatFragment extends ContentFragment implements
		OnBaiduPushNewMessageListener {
	private ViewGroup mRootView;
	private TextView mNickName;
	private EditText mMsgInput;
	private Button mMsgSend;

	private ListView mChatMessagesListView;
	private List<ChatMessage> mDatas = new ArrayList<ChatMessage>();
	private ChatMessageAdapter mAdapter;
	private WonderMapApplication mApplication;

	private User mFromUser;
	private UserDB mUserDB;
	private Gson mGson;
	private SharePreferenceUtil mSpUtil;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {

		mRootView = (ViewGroup) inflater.inflate(R.layout.main_chatting, null);
		initView();
		initEvent();
		return mRootView;
	}

	private void initView() {
		mChatMessagesListView = (ListView) mRootView
				.findViewById(R.id.id_chat_listView);
		mMsgInput = (EditText) mRootView.findViewById(R.id.id_chat_msg);
		mMsgSend = (Button) mRootView.findViewById(R.id.id_chat_send);
		mNickName = (TextView) mRootView.findViewById(R.id.id_nickname);

		mApplication = (WonderMapApplication) WonderMapApplication
				.getInstance();
		mUserDB = mApplication.getUserDB();
		mGson = mApplication.getGson();
		mSpUtil = mApplication.getSpUtil();
		// TODO 获取参数的方式改变
		String userId = mShowBundle.getString(StaticConstant.FragBundleUserId);
		Log.e("TAG", userId);

		if (TextUtils.isEmpty(userId)) {
			// finish();
		}
		if (mUserDB.selectInfo(userId) == null) {
			User user = ConvertUtil.GetUserFromBundle(mShowBundle);
			mUserDB.addUser(user);
		}
		mFromUser = mUserDB.getUser(userId);
		// 未读消息更新为已经读取
		mApplication.getMessageDB().updateReaded(userId);

		Log.e("TAG", mFromUser.toString());

		mNickName.setText(mFromUser.getNick());
		// 获取10条聊天记录
		mDatas = mApplication.getMessageDB().find(mFromUser.getUserId(), 1, 10);
		mAdapter = new ChatMessageAdapter(mContext, mDatas);
		mChatMessagesListView.setAdapter(mAdapter);
		mChatMessagesListView.setSelection(mDatas.size() - 1);

		PushMsgReceiveManager.msgListeners.add(this);
	}

	private void initEvent() {
		mMsgSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String msg = mMsgInput.getText().toString();
				if (TextUtils.isEmpty(msg)) {
					T.showShort(mApplication, "您还未填写消息呢!");
					return;
				}

				if (!NetUtil.isNetConnected(mApplication)) {
					T.showShort(mApplication, "当前无网络连接！");
					return;
				}
				HelloMessage message = new HelloMessage(System
						.currentTimeMillis(), msg);
				new SendMsgAsyncTask(mGson.toJson(message), mFromUser
						.getUserId()).send();

				ChatMessage chatMessage = new ChatMessage();
				chatMessage.setComing(false);
				chatMessage.setDate(new Date());
				chatMessage.setMessage(msg);
				chatMessage.setNickname(mSpUtil.getUserNick());
				chatMessage.setUserId(mSpUtil.getUserId());
				// 消息存入数据库
				mApplication.getMessageDB().add(mFromUser.getUserId(),
						chatMessage);

				mDatas.add(chatMessage);
				mAdapter.notifyDataSetChanged();
				mChatMessagesListView.setSelection(mDatas.size() - 1);
				mMsgInput.setText("");

				InputMethodManager imm = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				// 得到InputMethodManager的实例
				if (imm.isActive()) {
					// 如果开启,则关闭，个人感觉不关闭比较好
//					imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
//							InputMethodManager.HIDE_NOT_ALWAYS);
					// 关闭软键盘，开启方法相同，这个方法是切换开启与关闭状态的
				}

			}
		});
	}

	@Override
	public void onDestroyView() {
		PushMsgReceiveManager.msgListeners.remove(this);
		super.onDestroy();

	}

	@Override
	public void onNewMessage(HelloMessage message) {
		Log.e("TAG", "getMsg in chatActivity" + message.getNickname());

		// 获得回复的消息
		if (mFromUser.getUserId().equals(message.getUserId())) {
			ChatMessage chatMessage = new ChatMessage();
			chatMessage.setComing(true);
			chatMessage.setDate(new Date(message.getTimeSamp()));
			chatMessage.setMessage(message.getMessage());
			chatMessage.setUserId(message.getUserId());
			chatMessage.setNickname(message.getNickname());
			chatMessage.setReaded(true);
			mDatas.add(chatMessage);
			mAdapter.notifyDataSetChanged();
			mChatMessagesListView.setSelection(mDatas.size() - 1);
			// 存入数据库，当前聊天记录
			mApplication.getMessageDB().add(mFromUser.getUserId(), chatMessage);
		}
	}

	@Override
	protected void onInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBackPressed() {
		wmFragmentManager.back(null);
		return true;
	}
}
