package jason.wondermap.fragment;

import jason.wondermap.LoginActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;

public class MineFragment extends ContentFragment implements OnClickListener {
	private TextView btn_logout;
	private TextView tv_set_name;
	private RelativeLayout layout_info, layout_blacklist, layout_feedback,
			layout_favourite, layout_switch_notification, layout_switch_voice,
			layout_switch_vibrate;
	private CheckBox ck_notify, ck_voice, ck_vibrate;
	private View view1, view2;
	private SharePreferenceUtil mSharedUtil;
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
		mSharedUtil = WonderMapApplication.getInstance().getSpUtil();
		initTopBarForLeft(mRootView, "设置");
		refreshView();
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		refreshView();
	}

	private void refreshView() {
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();
		ck_notify.setChecked(isAllowNotify);
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		ck_voice.setChecked(isAllowVoice);
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		ck_vibrate.setChecked(isAllowVibrate);
		tv_set_name.setText(AccountUserManager.getInstance()
				.getCurrentUserName());
		if (!isAllowNotify) {
			layout_switch_vibrate.setVisibility(View.GONE);
			layout_switch_voice.setVisibility(View.GONE);
			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
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
			bundle.putString(UserInfo.USER_NAME, AccountUserManager
					.getInstance().getCurrentUserName());
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_USERINFO,
					bundle);
			break;
		case R.id.btn_logout:
			WonderMapApplication.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if (ck_notify.isChecked()) {
				ck_notify.setChecked(false);
				mSharedUtil.setPushNotifyEnable(false);
				layout_switch_vibrate.setVisibility(View.GONE);
				layout_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				ck_notify.setChecked(true);
				mSharedUtil.setPushNotifyEnable(true);
				layout_switch_vibrate.setVisibility(View.VISIBLE);
				layout_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rl_switch_voice:
			if (ck_voice.isChecked()) {
				ck_voice.setChecked(false);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				ck_voice.setChecked(true);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (ck_vibrate.isChecked()) {
				ck_vibrate.setChecked(false);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				ck_vibrate.setChecked(true);
				mSharedUtil.setAllowVibrateEnable(true);
			}
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
		layout_switch_notification = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_notification);
		layout_switch_voice = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_voice);
		layout_switch_vibrate = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_vibrate);
		view1 = (View) mRootView.findViewById(R.id.view1);
		view2 = (View) mRootView.findViewById(R.id.view2);
		tv_set_name = (TextView) mRootView.findViewById(R.id.tv_set_name);
		btn_logout = (TextView) mRootView.findViewById(R.id.btn_logout);
		ck_notify = (CheckBox) mRootView.findViewById(R.id.ck_set_notification);
		ck_voice = (CheckBox) mRootView.findViewById(R.id.ck_set_voice);
		ck_vibrate = (CheckBox) mRootView.findViewById(R.id.ck_set_vibrate);
	}

	private void setListenter() {
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);
		layout_favourite.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		layout_switch_notification.setOnClickListener(this);
		layout_switch_voice.setOnClickListener(this);
		layout_switch_vibrate.setOnClickListener(this);
	}

}
