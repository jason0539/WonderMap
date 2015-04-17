package jason.wondermap.fragment;

import jason.wondermap.LoginActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.UserInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;

public class MineFragment extends ContentFragment implements OnClickListener {
	Button btn_logout;
	TextView tv_set_name;
	RelativeLayout layout_info, rl_switch_notification, rl_switch_voice,
			rl_switch_vibrate, layout_blacklist, layout_feedback;

	ImageView iv_open_notification, iv_close_notification, iv_open_voice,
			iv_close_voice, iv_open_vibrate, iv_close_vibrate;

	View view1, view2;
	SharePreferenceUtil mSharedUtil;
	ViewGroup mRootView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_set,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		mSharedUtil = WonderMapApplication.getInstance().getSpUtil();
		initTopBarForLeft(mRootView, "设置");
		// 黑名单列表
		layout_blacklist = (RelativeLayout) mRootView
				.findViewById(R.id.layout_blacklist);
		layout_feedback = (RelativeLayout) mRootView
				.findViewById(R.id.layout_feedback);
		layout_info = (RelativeLayout) mRootView.findViewById(R.id.layout_info);
		rl_switch_notification = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_notification);
		rl_switch_voice = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_voice);
		rl_switch_vibrate = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_vibrate);
		rl_switch_notification.setOnClickListener(this);
		rl_switch_voice.setOnClickListener(this);
		rl_switch_vibrate.setOnClickListener(this);

		iv_open_notification = (ImageView) mRootView
				.findViewById(R.id.iv_open_notification);
		iv_close_notification = (ImageView) mRootView
				.findViewById(R.id.iv_close_notification);
		iv_open_voice = (ImageView) mRootView.findViewById(R.id.iv_open_voice);
		iv_close_voice = (ImageView) mRootView
				.findViewById(R.id.iv_close_voice);
		iv_open_vibrate = (ImageView) mRootView
				.findViewById(R.id.iv_open_vibrate);
		iv_close_vibrate = (ImageView) mRootView
				.findViewById(R.id.iv_close_vibrate);
		view1 = (View) mRootView.findViewById(R.id.view1);
		view2 = (View) mRootView.findViewById(R.id.view2);

		tv_set_name = (TextView) mRootView.findViewById(R.id.tv_set_name);
		btn_logout = (Button) mRootView.findViewById(R.id.btn_logout);

		// 初始化
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();

		if (isAllowNotify) {
			iv_open_notification.setVisibility(View.VISIBLE);
			iv_close_notification.setVisibility(View.INVISIBLE);
		} else {
			iv_open_notification.setVisibility(View.INVISIBLE);
			iv_close_notification.setVisibility(View.VISIBLE);
		}
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		if (isAllowVoice) {
			iv_open_voice.setVisibility(View.VISIBLE);
			iv_close_voice.setVisibility(View.INVISIBLE);
		} else {
			iv_open_voice.setVisibility(View.INVISIBLE);
			iv_close_voice.setVisibility(View.VISIBLE);
		}
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		if (isAllowVibrate) {
			iv_open_vibrate.setVisibility(View.VISIBLE);
			iv_close_vibrate.setVisibility(View.INVISIBLE);
		} else {
			iv_open_vibrate.setVisibility(View.INVISIBLE);
			iv_close_vibrate.setVisibility(View.VISIBLE);
		}
		btn_logout.setOnClickListener(this);
		layout_info.setOnClickListener(this);
		layout_blacklist.setOnClickListener(this);
		layout_feedback.setOnClickListener(this);
		initData();
	}

	private void initData() {
		tv_set_name.setText(BmobUserManager.getInstance(getActivity())
				.getCurrentUser().getUsername());
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.layout_blacklist:// 启动到黑名单页面
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_BLACK_LIST);
			break;
		case R.id.layout_feedback:
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_FEEDBACK);
			break;
		case R.id.layout_info:// 启动到个人资料页面
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.FROM, "me");
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_USERINFO,
					bundle);
			break;
		case R.id.btn_logout:
			WonderMapApplication.getInstance().logout();
			getActivity().finish();
			startActivity(new Intent(getActivity(), LoginActivity.class));
			break;
		case R.id.rl_switch_notification:
			if (iv_open_notification.getVisibility() == View.VISIBLE) {
				iv_open_notification.setVisibility(View.INVISIBLE);
				iv_close_notification.setVisibility(View.VISIBLE);
				mSharedUtil.setPushNotifyEnable(false);
				rl_switch_vibrate.setVisibility(View.GONE);
				rl_switch_voice.setVisibility(View.GONE);
				view1.setVisibility(View.GONE);
				view2.setVisibility(View.GONE);
			} else {
				iv_open_notification.setVisibility(View.VISIBLE);
				iv_close_notification.setVisibility(View.INVISIBLE);
				mSharedUtil.setPushNotifyEnable(true);
				rl_switch_vibrate.setVisibility(View.VISIBLE);
				rl_switch_voice.setVisibility(View.VISIBLE);
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.VISIBLE);
			}

			break;
		case R.id.rl_switch_voice:
			if (iv_open_voice.getVisibility() == View.VISIBLE) {
				iv_open_voice.setVisibility(View.INVISIBLE);
				iv_close_voice.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVoiceEnable(false);
			} else {
				iv_open_voice.setVisibility(View.VISIBLE);
				iv_close_voice.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVoiceEnable(true);
			}

			break;
		case R.id.rl_switch_vibrate:
			if (iv_open_vibrate.getVisibility() == View.VISIBLE) {
				iv_open_vibrate.setVisibility(View.INVISIBLE);
				iv_close_vibrate.setVisibility(View.VISIBLE);
				mSharedUtil.setAllowVibrateEnable(false);
			} else {
				iv_open_vibrate.setVisibility(View.VISIBLE);
				iv_close_vibrate.setVisibility(View.INVISIBLE);
				mSharedUtil.setAllowVibrateEnable(true);
			}
			break;

		}
	}

	public void onPause() {
		super.onPause();
	};

	@Override
	public void onDestroyView() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		super.onDestroyView();
	}

}
