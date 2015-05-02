package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.UserInfo;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 我的页面
 * 
 * @author liuzhenhui
 * 
 */
public class MineFragment extends ContentFragment implements OnClickListener {
	private TextView btn_logout;
	private TextView tv_set_name;
	private RelativeLayout layout_info, layout_blacklist, layout_feedback,
			layout_favourite, layout_about,layout_setting;
	private ViewGroup mRootView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_set,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		findViews();
		setListenter();
		initTopBarForLeft(mRootView, "设置");
		refreshView();
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshView();
	}

	private void refreshView() {
		tv_set_name.setText(AccountUserManager.getInstance()
				.getCurrentUserName());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_blacklist:// 启动到黑名单页面
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_BLACK_LIST);
			break;
		case R.id.layout_favourite:
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_FAVOURITE);
			break;
		case R.id.layout_feedback:
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_FEEDBACK);
			break;
		case R.id.layout_info:// 启动到个人资料页面
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.USER_ID, AccountUserManager.getInstance()
					.getCurrentUserid());
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_USERINFO,
					bundle);
			break;
		case R.id.layout_about:
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_ABOUT);
			break;
		case R.id.btn_logout:
			AccountUserManager.getInstance().logout();
			break;
		case R.id.layout_setting:
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_SETTING);
			break;
		}
	}

	private void findViews() {
		layout_blacklist = (RelativeLayout) mRootView
				.findViewById(R.id.layout_blacklist);
		layout_favourite = (RelativeLayout) mRootView
				.findViewById(R.id.layout_favourite);
		layout_feedback = (RelativeLayout) mRootView
				.findViewById(R.id.layout_feedback);
		layout_info = (RelativeLayout) mRootView.findViewById(R.id.layout_info);
		layout_about = (RelativeLayout) mRootView
				.findViewById(R.id.layout_about);
		layout_setting = (RelativeLayout )mRootView.findViewById(R.id.layout_setting);
		tv_set_name = (TextView) mRootView.findViewById(R.id.tv_set_name);
		btn_logout = (TextView) mRootView.findViewById(R.id.btn_logout);
	}

	private void setListenter() {
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);
		layout_favourite.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		layout_about.setOnClickListener(this);
		layout_setting.setOnClickListener(this);
	}

}
