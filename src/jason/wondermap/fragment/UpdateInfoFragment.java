package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.config.BundleTake;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.PushMsgSendManager;
import jason.wondermap.utils.StringUtils;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;

import java.util.List;

import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 设置昵称和性别
 * 
 * @author liuzhenhui
 * 
 */
public class UpdateInfoFragment extends ContentFragment {
	EditText et_edit_info;
	TextView tv_edit_info;
	TextView tv_edit_tips_name;
	ViewGroup mRootView;
	BmobUserManager userManager;
	String infoToEdit;
	Info toEditInfo;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(
				R.layout.activity_set_updateinfo, mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		infoToEdit = mShowBundle.getString(BundleTake.InfoToEdit);
		if (infoToEdit != null && infoToEdit.equals(UserInfo.SIGN)) {
			toEditInfo = Info.sign;
		} else if (infoToEdit != null && infoToEdit.equals(UserInfo.AGE)) {
			toEditInfo = Info.age;
		} else if (infoToEdit != null && infoToEdit.equals(UserInfo.USER_NAME)) {
			toEditInfo = Info.name;
		} else if (infoToEdit != null
				&& infoToEdit.equals(UserInfo.USER_PHONENUMBER)) {
			toEditInfo = Info.phone;
		}
		userManager = BmobUserManager.getInstance(mContext);
		et_edit_info = (EditText) mRootView.findViewById(R.id.et_edit_info);
		tv_edit_info = (TextView) mRootView.findViewById(R.id.tv_edit_info);
		tv_edit_tips_name = (TextView) mRootView
				.findViewById(R.id.tv_updateinfo_nametips);
		switch (toEditInfo) {
		case sign:
			et_edit_info.setHint("请输入签名");
			tv_edit_info.setText("签名");
			initTopBarForBoth(mRootView, "修改签名",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							String nick = et_edit_info.getText().toString();
							if (nick.equals("")) {
								ShowToast("请填写签名!");
								return;
							}
							updateSign(nick);
						}
					});
			break;
		case age:
			et_edit_info.setHint("请输入年龄");
			et_edit_info.setInputType(InputType.TYPE_CLASS_NUMBER);
			tv_edit_info.setText("年龄");
			initTopBarForBoth(mRootView, "修改年龄",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							String nick = et_edit_info.getText().toString();
							if (nick.equals("")
									|| !TextUtils.isDigitsOnly(nick)
									|| nick.length() > 2) {
								ShowToast("请正确填写年龄!");
								return;
							}
							updateAge(nick);
						}
					});
			break;
		case name:
			et_edit_info.setHint("请输入名字");
			tv_edit_info.setText("名字");
			tv_edit_tips_name.setVisibility(View.VISIBLE);
			initTopBarForBoth(mRootView, "修改名字",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							String nick = et_edit_info.getText().toString();
							if (nick.equals("")) {
								ShowToast("请填写名字!");
								return;
							}
							updateName(nick);
						}
					});
			break;
		case phone:
			et_edit_info.setHint("请输入手机号");
			et_edit_info.setInputType(InputType.TYPE_CLASS_NUMBER);
			tv_edit_info.setText("手机");
			initTopBarForBoth(mRootView, "修改手机号",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							String nick = et_edit_info.getText().toString();
							if (!StringUtils.isPhoneNumber(nick)) {
								ShowToast("请正确填写手机号!");
								return;
							}
							updatePhone(nick);
						}
					});
		default:
			break;
		}
	}

	/**
	 * 修改资料 updateInfo
	 */
	private void updateAge(String nick) {
		AccountUserManager.getInstance().updateCurrentUserAge(nick,
				new UpdateListener() {

					@Override
					public void onSuccess() {
						// 修改成功直接返回
						ShowToast("修改成功");
						wmFragmentManager.back(null);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("修改失败" + arg1);
					}
				});
	}

	private void updateSign(String signString) {
		AccountUserManager.getInstance().updateCurrentUserSign(signString,
				new UpdateListener() {

					@Override
					public void onSuccess() {
						// 修改成功直接返回
						ShowToast("修改成功");
						wmFragmentManager.back(null);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("修改失败:" + arg1);
					}
				});
	}

	private void updatePhone(String signString) {
		AccountUserManager.getInstance().updateCurrentUserPhone(signString,
				new UpdateListener() {

					@Override
					public void onSuccess() {
						// 修改成功直接返回
						ShowToast("修改成功");
						wmFragmentManager.back(null);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("修改失败:" + arg1);
					}
				});
	}

	/**
	 * 修改资料 updateInfo
	 */
	private void updateName(final String nick) {
		AccountUserManager.getInstance().getUserManager()
				.queryUserByName(nick, new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, String arg1) {

					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// 用户名可能已经被人使用
						if (arg0.size() > 0) {
							ShowToast("用户名已被抢占，请换一个重试");
						} else {
							// 用户名无人使用才进行更改
							AccountUserManager.getInstance()
									.updateCurrentUserName(nick,
											new UpdateListener() {

												@Override
												public void onSuccess() {
//													// 修改成功直接返回
//													PushMsgSendManager
//															.getInstance()
//															.sayHello();
													ShowToast("修改成功");
													wmFragmentManager
															.back(null);
												}

												@Override
												public void onFailure(int arg0,
														String arg1) {
													ShowToast("修改失败，请稍后重试");
												}
											});
						}
					}
				});

	}

	enum Info {
		age, sign, name, phone
	};
}
