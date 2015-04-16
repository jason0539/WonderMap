package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.bean.User;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import cn.bmob.im.BmobUserManager;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 设置昵称和性别
 * 
 * @author liuzhenhui
 * 
 */
public class UpdateInfoFragment extends ContentFragment {
	EditText edit_nick;
	ViewGroup mRootView;
	BmobUserManager userManager;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		mRootView = (ViewGroup) inflater.inflate(
				R.layout.activity_set_updateinfo, mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		userManager = BmobUserManager.getInstance(mContext);
		initTopBarForBoth(mRootView, "修改昵称",
				R.drawable.base_action_bar_true_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						// TODO Auto-generated method stub
						String nick = edit_nick.getText().toString();
						if (nick.equals("")) {
							ShowToast("请填写昵称!");
							return;
						}
						updateInfo(nick);
					}
				});
		edit_nick = (EditText)mRootView. findViewById(R.id.edit_nick);
	}

	/**
	 * 修改资料 updateInfo
	 */
	private void updateInfo(String nick) {
		final User user = userManager.getCurrentUser(User.class);
		User u = new User();
		u.setNick(nick);
		u.setHight(110);
		u.setObjectId(user.getObjectId());
		u.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				final User c = userManager.getCurrentUser(User.class);
				ShowToast("修改成功:" + c.getNick() + ",height = " + c.getHight());
				//  修改成功直接返回
				 wmFragmentManager.back(null);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				ShowToast("onFailure:" + arg1);
			}
		});
	}
}
