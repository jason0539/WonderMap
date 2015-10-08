package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.adapter.UserFriendAdapter;
import jason.wondermap.bean.User;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.CharacterParser;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.PinyinComparator;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import jason.wondermap.view.MyLetterView;
import jason.wondermap.view.MyLetterView.OnTouchingLetterChangedListener;
import jason.wondermap.view.dialog.DialogTips;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

public class ContactFragment extends ContentFragment implements
		OnItemClickListener, OnItemLongClickListener {

	private final static String TAG = ContactFragment.class.getSimpleName();
	private ViewGroup mRootView;

	private ImageView iv_msg_tips;// 好友请求提示
	private LinearLayout layout_new;// 新朋友
	private LinearLayout layout_near;// 附近的人
	private LinearLayout layout_recommend;// 好友推荐

	private List<User> friends = new ArrayList<User>();
	private ListView list_friends;
	private UserFriendAdapter userAdapter;
	private CharacterParser characterParser;
	private MyLetterView right_letter;
	private TextView dialog;// 快速搜索侧边触摸后显示的字母

	// 根据拼音来排列ListView里面的数据类
	private PinyinComparator pinyinComparator;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_contacts,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		initTopBarForBoth(mRootView, "好友",
				R.drawable.base_action_bar_add_bg_selector,
				new onRightImageButtonClickListener() {

					@Override
					public void onClick() {
						wmFragmentManager
								.showFragment(WMFragmentManager.TYPE_ADD_FRIEND);
					}
				});
		characterParser = new CharacterParser();
		pinyinComparator = new PinyinComparator();
		initListView();
		initRightLetterView();
	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private void filledData(List<BmobChatUser> datas) {
		friends.clear();
		int total = datas.size();
		for (int i = 0; i < total; i++) {
			BmobChatUser user = datas.get(i);
			User sortModel = new User();
			sortModel.setAvatar(user.getAvatar());
			sortModel.setNick(user.getNick());
			sortModel.setUsername(user.getUsername());
			sortModel.setObjectId(user.getObjectId());
			sortModel.setContacts(user.getContacts());
			// 汉字转换成拼音
			String username = sortModel.getUsername();
			// 若没有username
			if (username != null) {
				String pinyin = characterParser.getSelling(sortModel
						.getUsername());
				String sortString = pinyin.substring(0, 1).toUpperCase();
				// 正则表达式，判断首字母是否是英文字母
				if (sortString.matches("[A-Z]")) {
					sortModel.setSortLetters(sortString.toUpperCase());
				} else {
					sortModel.setSortLetters("#");
				}
			} else {
				sortModel.setSortLetters("#");
			}
			friends.add(sortModel);
		}
		// 根据a-z进行排序
		Collections.sort(friends, pinyinComparator);
	}

	private void initListView() {
		list_friends = (ListView) mRootView.findViewById(R.id.list_friends);
		RelativeLayout headView = (RelativeLayout) mInflater.inflate(
				R.layout.include_new_friend, null);
		iv_msg_tips = (ImageView) headView.findViewById(R.id.iv_msg_tips);
		layout_new = (LinearLayout) headView.findViewById(R.id.layout_new);
		layout_near = (LinearLayout) headView.findViewById(R.id.layout_near);
		layout_recommend = (LinearLayout) headView
				.findViewById(R.id.layout_recommend);
		layout_new.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Bundle bundle = new Bundle();
				bundle.putString(UserInfo.FROM, "contact");
				wmFragmentManager.showFragment(
						WMFragmentManager.TYPE_NEW_FRIEND, bundle);
			}
		});
		layout_near.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				wmFragmentManager
						.showFragment(WMFragmentManager.TYPE_NEAR_PEOPLE);
			}
		});
		layout_recommend.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (TextUtils.isEmpty(AccountUserManager.getInstance()
						.getCurrentUser().getPhone())) {
					T.showLong(mContext, "您还没有填写自己的手机号，强烈建议填写，方便其他好友找到您");
				}
				if (WonderMapApplication.getInstance().getSpUtil()
						.hasAcceptPhone()) {
					wmFragmentManager
							.showFragment(WMFragmentManager.TYPE_FRIEND_RECOMMEND);
				} else {
					BaseFragment.getMainActivity().showMessage(
							"进入后需要读取你的通讯录数据，用来推荐好友，是否同意进入并读取通讯录",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									WonderMapApplication.getInstance()
											.getSpUtil().setAcceptPhone(true);
									wmFragmentManager
											.showFragment(WMFragmentManager.TYPE_FRIEND_RECOMMEND);

								}

							});
				}
			}
		});

		list_friends.addHeaderView(headView);
		userAdapter = new UserFriendAdapter(getActivity(), friends);
		list_friends.setAdapter(userAdapter);
		list_friends.setOnItemClickListener(this);
		list_friends.setOnItemLongClickListener(this);

		list_friends.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				hideSoftInputView();
				return false;
			}
		});

	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser) {
			queryMyfriends();
		}
		super.setUserVisibleHint(isVisibleToUser);
	}

	private void initRightLetterView() {
		right_letter = (MyLetterView) mRootView.findViewById(R.id.right_letter);
		dialog = (TextView) mRootView.findViewById(R.id.dialog);
		right_letter.setTextView(dialog);
		right_letter
				.setOnTouchingLetterChangedListener(new LetterListViewListener());
	}

	private class LetterListViewListener implements
			OnTouchingLetterChangedListener {

		@Override
		public void onTouchingLetterChanged(String s) {
			// 该字母首次出现的位置
			int position = userAdapter.getPositionForSection(s.charAt(0));
			if (position != -1) {
				list_friends.setSelection(position);
			}
		}
	}

	/**
	 * 获取好友列表 queryMyfriends
	 */
	private void queryMyfriends() {
		// 是否有新的好友请求
		if (BmobDB.create(getActivity()).hasNewInvite()) {
			iv_msg_tips.setVisibility(View.VISIBLE);
		} else {
			iv_msg_tips.setVisibility(View.GONE);
		}
		// 在这里再做一次本地的好友数据库的检查，是为了本地好友数据库中已经添加了对方，但是界面却没有显示出来的问题
		// 重新设置下内存中保存的好友列表
		AccountUserManager.getInstance().setContactList(
				CollectionUtils.list2map(BmobDB.create(getActivity())
						.getContactList()));

		Map<String, BmobChatUser> users = AccountUserManager.getInstance()
				.getContactList();
		// 组装新的User
		filledData(CollectionUtils.map2list(users));
		if (userAdapter == null) {
			userAdapter = new UserFriendAdapter(getActivity(), friends);
			list_friends.setAdapter(userAdapter);
		} else {
			userAdapter.notifyDataSetChanged();
		}

	}

	private boolean hidden;

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "好友页");
		if (!hidden) {
			refresh();
		}
	}
	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
	}
	public void refresh() {
		try {
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					queryMyfriends();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long arg3) {
		if (position == 0) {// 不处理头部view
			return;
		}
		User user = (User) userAdapter.getItem(position - 1);
		// 先进入好友的详细资料页面
		Bundle bundle = new Bundle();
		bundle.putString(UserInfo.USER_ID, user.getObjectId());
		wmFragmentManager.showFragment(WMFragmentManager.TYPE_USERINFO, bundle);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		if (position == 0) {// 不处理头部view
			return true;
		}
		User user = (User) userAdapter.getItem(position - 1);
		showDeleteDialog(user);
		return true;
	}

	public void showDeleteDialog(final User user) {
		DialogTips dialog = new DialogTips(getActivity(), user.getUsername(),
				"删除联系人", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteContact(user);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	/**
	 * 删除联系人 deleteContact
	 */
	private void deleteContact(final User user) {
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("正在删除...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		AccountUserManager.getInstance().getUserManager()
				.deleteContact(user.getObjectId(), new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("删除成功");
						// 删除内存
						AccountUserManager.getInstance().getContactList()
								.remove(user.getObjectId());
						// 更新界面
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								progress.dismiss();
								userAdapter.remove(user);
							}
						});
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("删除失败：" + arg1);
						progress.dismiss();
					}
				});
	}

}
