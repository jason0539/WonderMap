package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.AIContentAdapter;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.manager.FootblogManager;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

/**
 * @author liuzhenhui
 * 
 */
public class PersonalFootblogFragment extends ContentFragment implements
		OnClickListener {
	private String TAG = "PersonalFootblogFragment";
	private ImageView personalIcon;
	private TextView personalName;
	private TextView personalSign;
	private ViewGroup mRootViewGroup;
	private ImageView goSettings;

	private TextView personalTitle;
	private PullToRefreshListView mPullToRefreshListView;
	private ListView mListView;

	private ArrayList<Blog> mQiangYus;
	private AIContentAdapter mAdapter;

	private User mUser;

	private int pageNum;

	public static final int EDIT_USER = 1;

	public enum RefreshType {
		REFRESH, LOAD_MORE
	}

	private RefreshType mRefreshType = RefreshType.LOAD_MORE;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_personal, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		personalIcon = (ImageView) mRootViewGroup
				.findViewById(R.id.personal_icon);
		personalName = (TextView) mRootViewGroup
				.findViewById(R.id.personl_name);
		personalSign = (TextView) mRootViewGroup
				.findViewById(R.id.personl_signature);

		goSettings = (ImageView) mRootViewGroup.findViewById(R.id.go_settings);

		personalTitle = (TextView) mRootViewGroup
				.findViewById(R.id.personl_title);

		mPullToRefreshListView = (PullToRefreshListView) mRootViewGroup
				.findViewById(R.id.pull_refresh_list_personal);
		mUser = (User) mShowBundle.getSerializable(BundleTake.FootblogOfUser);

		updatePersonalInfo(mUser);

		initMyPublish();
		setListener();
		fetchData();
	}

	private void setListener() {
		// TODO Auto-generated method stub
		personalIcon.setOnClickListener(this);
		personalSign.setOnClickListener(this);
		personalTitle.setOnClickListener(this);
		goSettings.setOnClickListener(this);
	}

	private void fetchData() {
		// TODO Auto-generated method stub
		getPublishion();
	}

	private void initMyPublish() {
		if (isCurrentUser(mUser)) {
			personalTitle.setText("我发表过的");
			initTopBarForLeft(mRootViewGroup, "我的足迹");
			goSettings.setVisibility(View.VISIBLE);
			User user = BmobUser.getCurrentUser(mContext, User.class);
			updatePersonalInfo(user);
		} else {
			goSettings.setVisibility(View.GONE);
			if (mUser != null && !mUser.getSex()) {
				personalTitle.setText("她发表过的");
				initTopBarForLeft(mRootViewGroup, "她的足迹");
			} else if (mUser != null && mUser.getSex()) {
				personalTitle.setText("他发表过的");
				initTopBarForLeft(mRootViewGroup, "他的足迹");
			}
		}

		mPullToRefreshListView.setMode(Mode.BOTH);
		mPullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						String label = DateUtils.formatDateTime(getActivity(),
								System.currentTimeMillis(),
								DateUtils.FORMAT_SHOW_TIME
										| DateUtils.FORMAT_SHOW_DATE
										| DateUtils.FORMAT_ABBREV_ALL);
						refreshView.getLoadingLayoutProxy()
								.setLastUpdatedLabel(label);
						mRefreshType = RefreshType.REFRESH;
						pageNum = 0;
						fetchData();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						mRefreshType = RefreshType.LOAD_MORE;
						fetchData();
					}
				});
		mPullToRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub

					}
				});
		mListView = mPullToRefreshListView.getRefreshableView();
		mQiangYus = new ArrayList<Blog>();
		//个人足迹页不显示分享按钮
		mAdapter = new AIContentAdapter(mContext, mQiangYus,false);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				// MyApplication.getInstance().setCurrentQiangYu(mQiangYus.get(position-1));
				// Intent intent = new Intent();
				// intent.setClass(getActivity(), CommentActivity.class);
				// intent.putExtra("data", mQiangYus.get(position - 1));
				// startActivity(intent);
				FootblogManager.getInstance().setCurrentBlog(
						mQiangYus.get(position - 1));
				wmFragmentManager
						.showFragment(WMFragmentManager.TYPE_FOOTBLOG_COMMENT);
			}
		});
	}

	private void updatePersonalInfo(User user) {
		if (user ==null) {
			L.d(PersonalFootblogFragment.class.getSimpleName()+"中updatePersonalInfo user 为空");
		}
		personalName.setText(user.getUsername());
		String signString = user.getSignature();
		if (signString != null && !signString.equals("")) {
			personalSign.setText(signString);
		}

		if (user.getAvatar() != null) {
			// ImageLoader.getInstance().displayImage(
			// user.getAvatar().getFileUrl(),
			// personalIcon,
			// MyApplication.getInstance().getOptions(
			// R.drawable.content_image_default),
			// new SimpleImageLoadingListener() {
			//
			// @Override
			// public void onLoadingComplete(String imageUri,
			// View view, Bitmap loadedImage) {
			// // TODO Auto-generated method stub
			// super.onLoadingComplete(imageUri, view, loadedImage);
			// LogUtils.i(TAG, "load personal icon completed.");
			// }
			//
			// });
			ImageLoader.getInstance().displayImage(user.getAvatar(),
					personalIcon, ImageLoadOptions.getOptions());

		}
	}

	/**
	 * 判断点击条目的用户是否是当前登录用户
	 * 
	 * @return
	 */
	private boolean isCurrentUser(User user) {
		if (null != user) {
			User cUser = BmobUser.getCurrentUser(mContext, User.class);
			if (cUser != null && cUser.getObjectId().equals(user.getObjectId())) {
				return true;
			}
		}
		return false;
	}

	private void getPublishion() {
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.setLimit(WMapConstants.NUMBERS_PER_PAGE);
		query.setSkip(WMapConstants.NUMBERS_PER_PAGE * (pageNum++));
		query.order("-createdAt");
		query.include("author");
		query.addWhereEqualTo("author", mUser);
		query.findObjects(mContext, new FindListener<Blog>() {

			@Override
			public void onSuccess(List<Blog> data) {
				if (data.size() != 0 && data.get(data.size() - 1) != null) {
					if (mRefreshType == RefreshType.REFRESH) {
						mQiangYus.clear();
					}

					if (data.size() < WMapConstants.NUMBERS_PER_PAGE) {
						T.showShort(mContext, "已加载完所有数据~");
					}

					mQiangYus.addAll(data);
					mAdapter.notifyDataSetChanged();
					mPullToRefreshListView.onRefreshComplete();
				} else {
					T.showShort(mContext, "暂无更多数据~");
					pageNum--;
					mPullToRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onError(int arg0, String msg) {
				// TODO Auto-generated method stub
				L.i(TAG, "find failed." + msg);
				pageNum--;
				mPullToRefreshListView.onRefreshComplete();
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.personal_icon:
		case R.id.personl_signature:
		case R.id.go_settings:
			if (isCurrentUser(mUser)) {
				// Intent intent = new Intent();
				// intent.setClass(mContext, SettingsActivity.class);
				// startActivityForResult(intent, EDIT_USER);
				// TODO 点击前往个人信息页面，可以修改个人信息，修改之后返回本页，自动刷新资料
				// L.i(TAG, "current user edit...");
			}
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.USER_ID, mUser.getObjectId());
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_USERINFO,
					bundle);
			break;
		case R.id.personl_title:

			break;
		default:
			break;
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case EDIT_USER:
				getCurrentUserInfo();
				pageNum = 0;
				mRefreshType = RefreshType.REFRESH;
				getPublishion();
				break;

			default:
				break;
			}
		}
	}

	/**
	 * 查询当前用户具体信息
	 */
	private void getCurrentUserInfo() {
		User user = BmobUser.getCurrentUser(mContext, User.class);
		L.i(TAG, "sign:" + user.getSignature() + "sex:" + user.getSex());
		updatePersonalInfo(user);
		T.showShort(mContext, "更新信息成功。");
	}
	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "个人足迹页");
	}
	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
	}
}
