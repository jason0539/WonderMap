package jason.wondermap.adapter;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.adapter.base.ViewHolder;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.helper.PhoneRecommendHelper;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;

import java.util.HashMap;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.v3.listener.PushListener;

import com.nostra13.universalimageloader.core.ImageLoader;

public class RecommendFriendAdapter extends BaseAdapter {
	Context mContext;
	LayoutInflater mInflater;
	private HashMap<String, User> recommedFriends;
	private HashMap<String, String> phoneNum;
	private List<User> useridList;

	public RecommendFriendAdapter(PhoneRecommendHelper phoneRecommendHelper) {
		mContext = BaseFragment.getMainActivity();
		mInflater = BaseFragment.getMainActivity().getLayoutInflater();
		recommedFriends = phoneRecommendHelper.getRecommedList();
		phoneNum = phoneRecommendHelper.getPhoneNumList();
		useridList = CollectionUtils.map2listOfUser(recommedFriends);
	}

	@Override
	public int getCount() {
		return useridList.size();
	}

	@Override
	public Object getItem(int position) {
		return useridList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.item_recommend_friend,
					null);
		}
		final User user = useridList.get(position);
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString(UserInfo.USER_ID, user.getObjectId());
				BaseFragment.getWMFragmentManager().showFragment(
						WMFragmentManager.TYPE_USERINFO, bundle);
			}
		});
		TextView name = ViewHolder.get(convertView, R.id.name);
		ImageView iv_avatar = ViewHolder.get(convertView, R.id.avatar);
		TextView tv_localTextView = ViewHolder.get(convertView, R.id.localname);
		Button btn_add = ViewHolder.get(convertView, R.id.btn_add);

		String avatar = user.getAvatar();

		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_avatar,
					ImageLoadOptions.getOptions());
		} else {
			iv_avatar.setImageResource(R.drawable.default_head);
		}

		name.setText(user.getUsername());
		tv_localTextView.setText(phoneNum.get(user.getPhone()));
		btn_add.setText("添加");
		btn_add.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				final ProgressDialog progress = new ProgressDialog(BaseFragment
						.getMainActivity());
				progress.setMessage("正在添加...");
				progress.setCanceledOnTouchOutside(false);
				progress.show();
				// 发送tag请求
				BmobChatManager.getInstance(WonderMapApplication.getInstance())
						.sendTagMessage(BmobConfig.TAG_ADD_CONTACT,
								user.getObjectId(), new PushListener() {

									@Override
									public void onSuccess() {
										progress.dismiss();
										T.showShort(mContext, "发送请求成功，等待对方验证!");
									}

									@Override
									public void onFailure(int arg0,
											final String arg1) {
										progress.dismiss();
										T.showShort(mContext, "发送请求失败，请重新添加!");
										L.d("发送请求失败:" + arg1);
									}
								});
				// test start
				// test end
			}
		});
		return convertView;
	}

}
