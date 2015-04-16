package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.adapter.BlackListAdapter;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.view.HeaderLayout;
import jason.wondermap.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.db.BmobDB;
import cn.bmob.v3.listener.UpdateListener;

public class BlackListFragment extends ContentFragment implements
		OnItemClickListener {
	ListView listview;
	BlackListAdapter adapter;
	ViewGroup mRootView;
	BmobUserManager userManager;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_blacklist,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		userManager = BmobUserManager.getInstance(mContext);
		mHeaderLayout = (HeaderLayout) mRootView
				.findViewById(R.id.common_actionbar);
		initTopBarForLeft(mRootView, "黑名单");
		adapter = new BlackListAdapter(mContext, BmobDB.create(mContext)
				.getBlackList());
		listview = (ListView) mRootView.findViewById(R.id.list_blacklist);
		listview.setOnItemClickListener(this);
		listview.setAdapter(adapter);
	}

	/**
	 * 显示移除黑名单对话框
	 * 
	 * @Title: showRemoveBlackDialog
	 * @Description: TODO
	 * @param @param position
	 * @param @param invite
	 * @return void
	 * @throws
	 */
	public void showRemoveBlackDialog(final int position,
			final BmobChatUser user) {
		DialogTips dialog = new DialogTips(mContext, "移出黑名单", "你确定将"
				+ user.getUsername() + "移出黑名单吗?", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				adapter.remove(position);
				userManager.removeBlack(user.getUsername(),
						new UpdateListener() {

							@Override
							public void onSuccess() {
								// TODO Auto-generated method stub
								ShowToast("移出黑名单成功");
								// 重新设置下内存中保存的好友列表
								WonderMapApplication.getInstance()
										.setContactList(
												CollectionUtils.list2map(BmobDB
														.create(mContext)
														.getContactList()));
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								// TODO Auto-generated method stub
								ShowToast("移出黑名单失败:" + arg1);
							}
						});
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		BmobChatUser invite = (BmobChatUser) adapter.getItem(arg2);
		showRemoveBlackDialog(arg2, invite);
	}

}
