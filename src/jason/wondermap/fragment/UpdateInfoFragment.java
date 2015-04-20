package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.im.BmobUserManager;
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
	ViewGroup mRootView;
	BmobUserManager userManager;
	String infoToEdit;
	Info toEditInfo;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		mRootView = (ViewGroup) inflater.inflate(
				R.layout.activity_set_updateinfo, mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		infoToEdit = mShowBundle.getString(BundleTake.InfoToEdit);
		if (infoToEdit != null && infoToEdit.equals(UserInfo.SIGN)) {
			toEditInfo = Info.sign;
		} else if (infoToEdit != null && infoToEdit.equals(UserInfo.NICK)) {
			toEditInfo = Info.nick;
		}
		userManager = BmobUserManager.getInstance(mContext);
		et_edit_info = (EditText) mRootView.findViewById(R.id.et_edit_info);
		tv_edit_info = (TextView) mRootView.findViewById(R.id.tv_edit_info);
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
		case nick:
			et_edit_info.setHint("请输入昵称");
			tv_edit_info.setText("昵称");
			initTopBarForBoth(mRootView, "修改昵称",
					R.drawable.base_action_bar_true_bg_selector,
					new onRightImageButtonClickListener() {

						@Override
						public void onClick() {
							String nick = et_edit_info.getText().toString();
							if (nick.equals("")) {
								ShowToast("请填写昵称!");
								return;
							}
							updateNick(nick);
						}
					});
			break;
		default:
			break;
		}
	}

	/**
	 * 修改资料 updateInfo
	 */
	private void updateNick(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		u.setObjectId(user.getObjectId());
		u.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				// 修改成功直接返回
				wmFragmentManager.back(null);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}

	private void updateSign(String signString) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setSignature(signString);
		u.setObjectId(user.getObjectId());
		u.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				// 修改成功直接返回
				wmFragmentManager.back(null);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("修改失败:" + arg1);
			}
		});
	}

	enum Info {
		nick, sign
	};
}
