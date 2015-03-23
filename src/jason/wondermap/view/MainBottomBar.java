package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.interfacer.OnUnReadMessageUpdateListener;
import jason.wondermap.manager.PushMsgReceiveManager;
import jason.wondermap.manager.UnReadMsgManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainBottomBar implements OnUnReadMessageUpdateListener {

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

	public MainBottomBar(View rootView) {
		// L.d("bottomBar构造器");
		fragmentManager = BaseFragment.getWMFragmentManager();
		initViews(rootView);
	}

	@Override
	public void unReadMessageUpdate(int count) {
		L.d(WModel.BottomBarNum, "增加数目："+count);
		if (mBadgeViewforMsg.isShown()) {
			mBadgeViewforMsg.increment(count);
		} else {
			mBadgeViewforMsg.setText(0+"");
			mBadgeViewforMsg.show();
			mBadgeViewforMsg.increment(count);
		}
	}

	/**
	 * MapHomeFragment中只构造一次bottombar，所以实例都是存在的,在这里更新红点
	 */
	public void onResume() {
		int mUnReadedMsgs = UnReadMsgManager.getInstance().getUnreadMsgNum();
		if (mUnReadedMsgs > 0) {
			mBadgeViewforMsg.setText(mUnReadedMsgs + "");
			mBadgeViewforMsg.show();
		} else {
			mBadgeViewforMsg.hide();
		}
		PushMsgReceiveManager.unReadListeners.add(this);
	}

	/**
	 * 销毁bottomBar中的资源
	 */
	public void onDestroyView() {
		PushMsgReceiveManager.unReadListeners.remove(this);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private void initViews(View view) {
		mRootView = view;
		msgButton = (Button) mRootView.findViewById(R.id.btn_main_msg);
		friendButton = (Button) mRootView.findViewById(R.id.btn_main_friend);
		discoverButton = (Button) mRootView
				.findViewById(R.id.btn_main_discover);
		mineButton = (Button) mRootView.findViewById(R.id.btn_main_mine);
		msgButton.setOnClickListener(getOnClickLis());
		mineButton.setOnClickListener(getOnClickLis());
		friendButton.setOnClickListener(getOnClickLis());
		discoverButton.setOnClickListener(getOnClickLis());
		mBadgeViewforMsg = new BadgeViewForButton(
				WonderMapApplication.getInstance(), msgButton);
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
