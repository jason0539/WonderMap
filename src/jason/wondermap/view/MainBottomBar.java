package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.utils.L;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainBottomBar {

	private View mRootView;
	private Button mineButton;
	private Button msgButton;
	private Button friendButton;
	private Button discoverButton;
	private Context mContent;
	private WMFragmentManager fragmentManager;
	private int typeOfFragment;

	public MainBottomBar(View rootView, int type) {
		mContent = WonderMapApplication.getInstance();
		fragmentManager = BaseFragment.getWMFragmentManager();
		typeOfFragment = type;
		initViews(rootView);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	private void initViews(View view) {
		mRootView = view;
		msgButton = (Button) mRootView.findViewById(R.id.btn_main_msg);
		friendButton = (Button) mRootView.findViewById(R.id.btn_main_friend);
		discoverButton = (Button) mRootView
				.findViewById(R.id.btn_main_discover);
		mineButton = (Button) mRootView.findViewById(R.id.btn_main_mine);
		initListener();
	}

	private void initListener() {
		msgButton.setOnClickListener(getOnClickLis());
		mineButton.setOnClickListener(getOnClickLis());
		friendButton.setOnClickListener(getOnClickLis());
		discoverButton.setOnClickListener(getOnClickLis());
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
				if (clickType == typeOfFragment) {
					return;
				}
				fragmentManager.showFragment(clickType);
			}
		};
	}
}
