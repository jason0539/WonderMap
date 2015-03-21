package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.dao.MessageDB;
import jason.wondermap.dao.UserDB;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.interfacer.OnUnReadMessageUpdateListener;
import jason.wondermap.manager.PushMsgReceiveManager;
import jason.wondermap.utils.L;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainBottomBar implements OnUnReadMessageUpdateListener {

	private View mRootView;
	private Button mineButton;
	private TextView msgButton;
	private Button friendButton;
	private Button discoverButton;
	private Context mContent;
	private WMFragmentManager fragmentManager;
	private MessageDB msgDb;
	private UserDB userDb;
	/**
	 * 存储userId-新来消息的个数
	 */
	public Map<String, Integer> mUserMessages = new HashMap<String, Integer>();
	/**
	 * 未读消息总数
	 */
	private int mUnReadedMsgs;

	/**
	 * 分别为每个TabIndicator创建一个BadgeView
	 */
	private BadgeViewForButton mBadgeViewforMsg;

	public MainBottomBar(View rootView) {
		// L.d("bottomBar构造器");
		mContent = WonderMapApplication.getInstance();
		fragmentManager = BaseFragment.getWMFragmentManager();
		msgDb = WonderMapApplication.getInstance().getMessageDB();
		userDb = WonderMapApplication.getInstance().getUserDB();
		initViews(rootView);
	}

	@Override
	public void unReadMessageUpdate(int count) {
		if (mBadgeViewforMsg.isShown()) {
			mBadgeViewforMsg.increment(count);
		} else {
			mBadgeViewforMsg.show();
			mBadgeViewforMsg.increment(count);
		}
	}

	/**
	 * MapHomeFragment中只构造一次bottombar，所以实例都是存在的,在这里更新红点
	 */
	public void onResume() {
		mUnReadedMsgs = 0;
		mUserMessages = msgDb.getUserUnReadMsgs(userDb.getUserIds());
		for (Integer val : mUserMessages.values()) {
			mUnReadedMsgs += val;
		}
		L.d("未读消息数量" + mUnReadedMsgs);
		if (mUnReadedMsgs > 0) {
			mBadgeViewforMsg.setText(mUnReadedMsgs + "");
			mBadgeViewforMsg.show();
		} else {
			mBadgeViewforMsg.hide();
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private void initViews(View view) {
		mRootView = view;
		msgButton = (TextView) mRootView.findViewById(R.id.btn_main_msg);
		friendButton = (Button) mRootView.findViewById(R.id.btn_main_friend);
		discoverButton = (Button) mRootView
				.findViewById(R.id.btn_main_discover);
		mineButton = (Button) mRootView.findViewById(R.id.btn_main_mine);
		initListener();
		mBadgeViewforMsg = new BadgeViewForButton(
				WonderMapApplication.getInstance(), msgButton);
	}

	private void initListener() {
		msgButton.setOnClickListener(getOnClickLis());
		mineButton.setOnClickListener(getOnClickLis());
		friendButton.setOnClickListener(getOnClickLis());
		discoverButton.setOnClickListener(getOnClickLis());
		PushMsgReceiveManager.unReadListeners.add(this);
	}

	private OnClickListener getOnClickLis() {

		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				int clickType = WMFragmentManager.TYPE_NONE;
				switch (v.getId()) {
				case R.id.btn_main_discover:
					clickType = WMFragmentManager.TYPE_DISCOVER;
					break;
				case R.id.btn_main_msg:
					clickType = WMFragmentManager.TYPE_MESSAGE;
					break;
				case R.id.btn_main_friend:
					clickType = WMFragmentManager.TYPE_FRIEND;
					break;
				case R.id.btn_main_mine:
					clickType = WMFragmentManager.TYPE_MINE;
					break;
				default:
					break;
				}
				fragmentManager.showFragment(clickType);
			}
		};
	}

}
