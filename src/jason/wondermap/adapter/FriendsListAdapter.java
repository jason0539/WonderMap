package jason.wondermap.adapter;

import jason.wondermap.R;
import jason.wondermap.bean.User;
import jason.wondermap.view.BadgeViewForListAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Application;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 消息页面list适配器
 * 
 * @author liuzhenhui
 * 
 */
public class FriendsListAdapter extends BaseAdapter {
	/**
	 * 所有的用户
	 */
	private List<User> mDatas;
	private LayoutInflater mInflater;
	private Application mApplication;
	/**
	 * 存储userId-新来消息的个数
	 */
	private Map<String, Integer> mUserMessages = new HashMap<String, Integer>();

	public FriendsListAdapter(List<User> userLists, LayoutInflater inflater,
			Map<String, Integer> userMsg, Application application) {
		mDatas = userLists;
		mInflater = inflater;
		mUserMessages = userMsg;
		mApplication = application;
	}

	@Override
	public int getCount() {
		return mDatas.size();
	}

	@Override
	public Object getItem(int position) {
		return mDatas.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		User user = mDatas.get(position);
		String userId = user.getUserId();

		ViewHolder holder = null;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.main_tab_weixin_info_item,
					parent, false);
			holder = new ViewHolder();
			holder.mNickname = (TextView) convertView
					.findViewById(R.id.id_nickname);
			holder.mUserId = (TextView) convertView
					.findViewById(R.id.id_userId);
			holder.mWapper = (RelativeLayout) convertView
					.findViewById(R.id.id_item_ly);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		// 如果存在新的消息，则设置BadgeView
		if (mUserMessages.containsKey(userId)) {
			if (holder.mBadgeView == null)
				holder.mBadgeView = new BadgeViewForListAdapter(mApplication);
			holder.mBadgeView.setTargetView(holder.mWapper);
			holder.mBadgeView.setBadgeGravity(Gravity.CENTER_VERTICAL
					| Gravity.RIGHT);
			holder.mBadgeView.setBadgeMargin(0, 0, 8, 0);
			holder.mBadgeView.setBadgeCount(mUserMessages.get(userId));
		} else {
			if (holder.mBadgeView != null)
				holder.mBadgeView.setVisibility(View.GONE);
		}

		holder.mNickname.setText(mDatas.get(position).getNick());
		holder.mUserId.setText(userId);

		return convertView;
	}

	private final class ViewHolder {
		TextView mNickname;
		TextView mUserId;
		RelativeLayout mWapper;
		BadgeViewForListAdapter mBadgeView;
	}

}
