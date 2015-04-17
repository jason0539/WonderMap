package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.NewFriendAdapter;
import jason.wondermap.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.db.BmobDB;

public class NewFriendFragment extends ContentFragment implements
		OnItemLongClickListener {
	ListView listview;

	NewFriendAdapter adapter;

	String from = "";
	ViewGroup mRootView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_new_friend,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		initTopBarForLeft(mRootView, "新朋友");
		listview = (ListView) mRootView.findViewById(R.id.list_newfriend);
		listview.setOnItemLongClickListener(this);
		adapter = new NewFriendAdapter(mContext, BmobDB.create(mContext)
				.queryBmobInviteList());
		listview.setAdapter(adapter);
		if (from == null) {// 若来自通知栏的点击，则定位到最后一条
			listview.setSelection(adapter.getCount());
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
			int position, long arg3) {
		// TODO Auto-generated method stub
		BmobInvitation invite = (BmobInvitation) adapter.getItem(position);
		showDeleteDialog(position, invite);
		return true;
	}

	public void showDeleteDialog(final int position, final BmobInvitation invite) {
		DialogTips dialog = new DialogTips(getActivity(), invite.getFromname(),
				"删除好友请求", "确定", true, true);
		// 设置成功事件
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				deleteInvite(position, invite);
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	/**
	 * 删除请求 deleteRecent
	 * 
	 * @param @param recent
	 * @return void
	 * @throws
	 */
	private void deleteInvite(int position, BmobInvitation invite) {
		adapter.remove(position);
		BmobDB.create(mContext).deleteInviteMsg(invite.getFromid(),
				Long.toString(invite.getTime()));
	}

	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		if (from == null) {//  如果是点击通知栏进来的，则返回时回到主页面,这里定为返回到地图首页
			wmFragmentManager.backTo(WMFragmentManager.TYPE_MAP_HOME, null);
		}
	}
}
