package jason.wondermap.adapter;

import jason.wondermap.R;
import jason.wondermap.bean.BlogComment;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.ActivityUtil;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

/**评论页，评论条目适配器
 * @author liuzhenhui
 */
public class FootblogCommentAdapter extends BaseContentAdapter<BlogComment> {

	public FootblogCommentAdapter(Context context, List<BlogComment> list) {
		super(context, list);
	}

	@Override
	public View getConvertView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.comment_item, null);
			viewHolder.userHead = (ImageView) convertView
					.findViewById(R.id.comment_item_user_head);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.userName_comment);
			viewHolder.userSex = (CheckBox) convertView
					.findViewById(R.id.comment_item_user_sex);
			viewHolder.userAge = (TextView) convertView
					.findViewById(R.id.comment_item_user_age);
			viewHolder.commentContent = (TextView) convertView
					.findViewById(R.id.content_comment);
			viewHolder.index = (TextView) convertView
					.findViewById(R.id.index_comment);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		final BlogComment comment = dataList.get(position);
		final User user = comment.getUser();
		if (comment.getUser() != null) {
			viewHolder.userName.setText(user.getUsername());
			L.i("CommentActivity", "NAME:" + user.getUsername());
		} else {
			viewHolder.userName.setText("墙友");
		}
		// 头像
		String avatarUrl = null;
		if (user.getAvatar() != null) {
			avatarUrl = user.getAvatar();
		}
		ImageLoader.getInstance().displayImage(avatarUrl, viewHolder.userHead,
				ActivityUtil.getOptions(R.drawable.user_icon_default_main));
		viewHolder.userHead.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 点击头像，如果未登陆则跳转到登陆页面
				if (AccountUserManager.getInstance().getCurrentUser() == null) {
					T.showShort(mContext, "请先登录。");
					BaseFragment.getWMFragmentManager().showFragment(WMFragmentManager.TYPE_LOGIN);
					return;
				}
				Bundle bundle = new Bundle();
				bundle.putSerializable(UserInfo.USER_ID, user.getObjectId());
				BaseFragment.getWMFragmentManager().showFragment(
						WMFragmentManager.TYPE_USERINFO, bundle);
			}
		});
		// 性别
		boolean man = user.getSex();
		if (!man) {
			viewHolder.userSex.setChecked(false);
			viewHolder.userAge.setTextColor(Color.parseColor("#EC197D"));
		} else {
			viewHolder.userSex.setChecked(true);
			viewHolder.userAge.setTextColor(Color.parseColor("#2BA2E5"));
		}
		// 年龄
		viewHolder.userAge.setText(user.getAge() + "");
		viewHolder.index.setText((position + 1) + "楼");
		viewHolder.commentContent.setText(comment.getCommentContent());

		return convertView;
	}

	public static class ViewHolder {
		public ImageView userHead;
		public TextView userName;
		public CheckBox userSex;
		public TextView userAge;

		public TextView commentContent;
		public TextView index;
	}
}
