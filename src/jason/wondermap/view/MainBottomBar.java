package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.receiver.MyMessageReceiver;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;

public class MainBottomBar implements EventListener {

	private View mRootView;
	private Button mineButton;
	private Button msgButton;
	private Button friendButton;
	private Button discoverButton;
	private WMFragmentManager fragmentManager;
	/**
	 * 分别为每个TabIndicator创建一个BadgeView
	 */
	private BadgeViewForButton mBadgeViewforMsg;
	private BadgeViewForButton mBadgeViewforContact;

	public MainBottomBar(View rootView) {
		// L.d("bottomBar构造器");
		fragmentManager = BaseFragment.getWMFragmentManager();
		initViews(rootView);
	}

	/**
	 * MapHomeFragment中只构造一次bottombar，所以实例都是存在的,在这里更新红点
	 */
	public void onResume() {
		int num = BmobDB.create(BaseFragment.getMainActivity())
				.getAllUnReadCount();
		boolean hasUnread = num > 0 ? true : false;
		if (hasUnread) {
			mBadgeViewforMsg.setText(num + "");
			mBadgeViewforMsg.show();
		} else {
			mBadgeViewforMsg.hide();
		}
		queryMyfriends();
		MyMessageReceiver.ehList.add(this);
	}

	public void onPause() {
		// 防止onResume中添加多次监听器
		MyMessageReceiver.ehList.remove(this);
	}

	/**
	 * 销毁bottomBar中的资源
	 */
	public void onDestroyView() {
		mBadgeViewforMsg.hide();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private void initViews(View view) {
		mRootView = view;
		msgButton = (Button) mRootView.findViewById(R.id.btn_main_recent);
		friendButton = (Button) mRootView.findViewById(R.id.btn_main_contact);
		discoverButton = (Button) mRootView
				.findViewById(R.id.btn_main_discover);
		mineButton = (Button) mRootView.findViewById(R.id.btn_main_mine);
		msgButton.setOnClickListener(getOnClickLis());
		mineButton.setOnClickListener(getOnClickLis());
		friendButton.setOnClickListener(getOnClickLis());
		discoverButton.setOnClickListener(getOnClickLis());
		mBadgeViewforMsg = new BadgeViewForButton(
				WonderMapApplication.getInstance(), msgButton);
		mBadgeViewforContact = new BadgeViewForButton(
				WonderMapApplication.getInstance(), friendButton);
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
				case R.id.btn_main_recent:
					clickType = WMFragmentManager.TYPE_RECENT;
					break;
				case R.id.btn_main_contact:
					clickType = WMFragmentManager.TYPE_CONTACT;
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

	// 监听事件＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝从MainActivity移植过来的＝＝＝＝＝＝＝之后重构
	@Override
	public void onAddUser(BmobInvitation arg0) {
		queryMyfriends();
	}

	@Override
	public void onMessage(BmobMsg message) {
		L.d(WModel.BottomBarNum, "收到消息");
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
		if (!mBadgeViewforMsg.isShown()) {
			L.d(WModel.BottomBarNum, "角标未显示，设为1并显示");
			mBadgeViewforMsg.setText(1 + "");
			mBadgeViewforMsg.show();
		} else {
			L.d(WModel.BottomBarNum, "角标显示，加1");
			mBadgeViewforMsg.increment(1);
		}

	}

	private void queryMyfriends() {
		// 是否有新的好友请求
		if (BmobDB.create(BaseFragment.getMainActivity()).hasNewInvite()) {
			mBadgeViewforContact.setText(" ");
			mBadgeViewforContact.show();
		} else {
			mBadgeViewforContact.hide();
		}
	}
}
