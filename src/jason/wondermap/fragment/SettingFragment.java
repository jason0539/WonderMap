package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.utils.SharePreferenceUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.RelativeLayout;

public class SettingFragment extends ContentFragment implements OnClickListener {
	private RelativeLayout layout_switch_notification, layout_switch_voice,
			layout_switch_vibrate;
	private CheckBox ck_notify, ck_voice, ck_vibrate;
	private SharePreferenceUtil mSharedUtil;
	private View view1, view2;
	private ViewGroup mRootView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		findViews();
		setListenter();
		mSharedUtil = WonderMapApplication.getInstance().getSpUtil();
		initTopBarForLeft(mRootView, "通知设置");
		refreshView();
	}

	private void refreshView() {
		boolean isAllowNotify = mSharedUtil.isAllowPushNotify();
		ck_notify.setChecked(isAllowNotify);
		boolean isAllowVoice = mSharedUtil.isAllowVoice();
		ck_voice.setChecked(isAllowVoice);
		boolean isAllowVibrate = mSharedUtil.isAllowVibrate();
		ck_vibrate.setChecked(isAllowVibrate);
		if (!isAllowNotify) {
			layout_switch_vibrate.setVisibility(View.GONE);
			layout_switch_voice.setVisibility(View.GONE);
			view1.setVisibility(View.GONE);
			view2.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
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
		layout_switch_notification = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_notification);
		layout_switch_voice = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_voice);
		layout_switch_vibrate = (RelativeLayout) mRootView
				.findViewById(R.id.rl_switch_vibrate);
		view1 = (View) mRootView.findViewById(R.id.view1);
		view2 = (View) mRootView.findViewById(R.id.view2);
		ck_notify = (CheckBox) mRootView.findViewById(R.id.ck_set_notification);
		ck_voice = (CheckBox) mRootView.findViewById(R.id.ck_set_voice);
		ck_vibrate = (CheckBox) mRootView.findViewById(R.id.ck_set_vibrate);
	}

	private void setListenter() {
		layout_switch_notification.setOnClickListener(this);
		layout_switch_voice.setOnClickListener(this);
		layout_switch_vibrate.setOnClickListener(this);
	}
}
