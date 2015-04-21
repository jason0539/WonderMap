package jason.wondermap.fragment;

import jason.wondermap.LoginActivity;
import jason.wondermap.R;
import jason.wondermap.adapter.FootblogCommentAdapter;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.BlogComment;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.dao.DatabaseUtil;
import jason.wondermap.sns.TencentShare;
import jason.wondermap.sns.TencentShareEntity;
import jason.wondermap.utils.ActivityUtil;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.UserInfo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FootblogCommentFragment extends ContentFragment implements
		OnClickListener {
	String TAG = "FootblogCommentFragment";
	private ListView commentList;
	private TextView footer;

	private EditText commentContent;
	private Button commentCommit;

	private TextView userName;
	private TextView commentItemContent;
	private ImageView commentItemImage;

	private ImageView userLogo;
	private ImageView myFav;
	private TextView comment;
	private TextView share;
	private TextView love;
	private TextView hate;

	private Blog qiangYu;
	private String commentEdit = "";

	private FootblogCommentAdapter mAdapter;

	private List<BlogComment> comments = new ArrayList<BlogComment>();

	private int pageNum;
	private ViewGroup mRootViewGroup;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.activity_comment, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForLeft(mRootViewGroup, "评论");
		commentList = (ListView) mRootViewGroup.findViewById(R.id.comment_list);
		footer = (TextView) mRootViewGroup.findViewById(R.id.loadmore);

		commentContent = (EditText) mRootViewGroup
				.findViewById(R.id.comment_content);
		commentCommit = (Button) mRootViewGroup
				.findViewById(R.id.comment_commit);

		userName = (TextView) mRootViewGroup.findViewById(R.id.user_name);
		commentItemContent = (TextView) mRootViewGroup
				.findViewById(R.id.content_text);
		commentItemImage = (ImageView) mRootViewGroup
				.findViewById(R.id.content_image);

		userLogo = (ImageView) mRootViewGroup.findViewById(R.id.user_logo);
		myFav = (ImageView) mRootViewGroup.findViewById(R.id.item_action_fav);
		comment = (TextView) mRootViewGroup
				.findViewById(R.id.item_action_comment);
		share = (TextView) mRootViewGroup.findViewById(R.id.item_action_share);
		love = (TextView) mRootViewGroup.findViewById(R.id.item_action_love);
		hate = (TextView) mRootViewGroup.findViewById(R.id.item_action_hate);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		qiangYu = (Blog) mShowBundle
				.getSerializable(BundleTake.CommentItemData);// MyApplication.getInstance().getCurrentQiangYu();
		pageNum = 0;

		mAdapter = new FootblogCommentAdapter(mContext, comments);
		commentList.setAdapter(mAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
//				T.showShort(mContext, "po" + position);
				//之后假如@功能，评论中可以@
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);

		initMoodView(qiangYu);
		footer.setOnClickListener(this);
		commentCommit.setOnClickListener(this);

		userLogo.setOnClickListener(this);
		myFav.setOnClickListener(this);
		love.setOnClickListener(this);
		hate.setOnClickListener(this);
		share.setOnClickListener(this);
		comment.setOnClickListener(this);
		fetchData();
	}

	private void initMoodView(Blog mood2) {
		// TODO Auto-generated method stub
		if (mood2 == null) {
			return;
		}
		userName.setText(qiangYu.getAuthor().getUsername());
		commentItemContent.setText(qiangYu.getContent());
		if (null == qiangYu.getContentfigureurl()) {
			commentItemImage.setVisibility(View.GONE);
		} else {
			commentItemImage.setVisibility(View.VISIBLE);
			ImageLoader
					.getInstance()
					.displayImage(
							qiangYu.getContentfigureurl().getFileUrl(mContext) == null ? ""
									: qiangYu.getContentfigureurl().getFileUrl(
											mContext), commentItemImage,
							ActivityUtil.getOptions(R.drawable.bg_pic_loading),
							new SimpleImageLoadingListener() {

								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									// TODO Auto-generated method stub
									super.onLoadingComplete(imageUri, view,
											loadedImage);
									float[] cons = ActivityUtil
											.getBitmapConfiguration(
													loadedImage,
													commentItemImage, 1.0f);
									RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
											(int) cons[0], (int) cons[1]);
									layoutParams.addRule(RelativeLayout.BELOW,
											R.id.content_text);
									commentItemImage
											.setLayoutParams(layoutParams);
								}

							});
		}

		love.setText(qiangYu.getLove() + "");
		if (qiangYu.getMyLove()) {
			love.setTextColor(Color.parseColor("#D95555"));
		} else {
			love.setTextColor(Color.parseColor("#000000"));
		}
		hate.setText(qiangYu.getHate() + "");
		if (qiangYu.getMyFav()) {
			myFav.setImageResource(R.drawable.ic_action_fav_choose);
		} else {
			myFav.setImageResource(R.drawable.ic_action_fav_normal);
		}

		User user = qiangYu.getAuthor();
		// 此处获取头像方式不知是否正确
		// BmobFile avatar = new BmobFile(new File(user.getAvatar()));
		// if (null != avatar) {
		// ImageLoader.getInstance().displayImage(avatar.getFileUrl(mContext),
		// userLogo,
		// ActivityUtil.getOptions(R.drawable.content_image_default),
		// new SimpleImageLoadingListener() {
		//
		// @Override
		// public void onLoadingComplete(String imageUri,
		// View view, Bitmap loadedImage) {
		// // TODO Auto-generated method stub
		// super.onLoadingComplete(imageUri, view, loadedImage);
		// L.i(TAG, "load personal icon completed.");
		// }
		//
		// });
		// }
		String avatar = user.getAvatar();
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, userLogo,
					ImageLoadOptions.getOptions());
		} else {
			userLogo.setImageResource(R.drawable.default_head);
		}

	}

	private void fetchComment() {
		BmobQuery<BlogComment> query = new BmobQuery<BlogComment>();
		query.addWhereRelatedTo("relation", new BmobPointer(qiangYu));
		query.include("user");
		query.order("createdAt");
		query.setLimit(WMapConstants.NUMBERS_PER_PAGE);
		query.setSkip(WMapConstants.NUMBERS_PER_PAGE * (pageNum++));
		query.findObjects(mContext, new FindListener<BlogComment>() {

			@Override
			public void onSuccess(List<BlogComment> data) {
				// TODO Auto-generated method stub
				L.i(TAG, "get comment success!" + data.size());
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					if (data.size() < WMapConstants.NUMBERS_PER_PAGE) {
						T.showShort(mContext, "已加载完所有评论~");
						footer.setText("暂无更多评论~");
					}

					mAdapter.getDataList().addAll(data);
					mAdapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(commentList);
					L.i(TAG, "refresh");
				} else {
					T.showShort(mContext, "暂无更多评论~");
					footer.setText("暂无更多评论~");
					pageNum--;
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				T.showShort(mContext, "获取评论失败。请检查网络~");
				pageNum--;
			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.user_logo:
			onClickUserLogo();
			break;
		case R.id.loadmore:
			onClickLoadMore();
			break;
		case R.id.comment_commit:
			onClickCommit();
			break;
		case R.id.item_action_fav:
			onClickFav(v);
			break;
		case R.id.item_action_love:
			onClickLove();
			break;
		case R.id.item_action_hate:
			onClickHate();
			break;
		case R.id.item_action_share:
			onClickShare();
			break;
		case R.id.item_action_comment:
			onClickComment();
			break;
		default:
			break;
		}
	}

	private void onClickUserLogo() {
		// 跳转到个人信息界面
		User currentUser = BmobUser.getCurrentUser(mContext, User.class);
		if (currentUser != null) {// 已登录
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.USER_ID,
					currentUser.getObjectId());
			BaseFragment.getWMFragmentManager().showFragment(
					WMFragmentManager.TYPE_USERINFO, bundle);
		} else {// 未登录
			T.showShort(mContext, "请先登录。");
			Activity mainActivity = BaseFragment.getMainActivity();
			mainActivity.startActivity(new Intent(mainActivity,
					LoginActivity.class));
			mainActivity.finish();
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterAndLoginActivity.class);
			// startActivityForResult(intent, Constant.GO_SETTINGS);
		}
	}

	private void fetchData() {
		fetchComment();
	}

	private void onClickLoadMore() {
		// TODO Auto-generated method stub
		fetchData();
	}

	private void onClickCommit() {
		// TODO Auto-generated method stub
		User currentUser = BmobUser.getCurrentUser(mContext, User.class);
		if (currentUser != null) {// 已登录
			commentEdit = commentContent.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit)) {
				T.showShort(mContext, "评论内容不能为空。");
				return;
			}
			// comment now
			publishComment(currentUser, commentEdit);
		} else {// 未登录
			T.showShort(mContext, "发表评论前请先登录。");
			Activity mainActivity = BaseFragment.getMainActivity();
			mainActivity.startActivity(new Intent(mainActivity,
					LoginActivity.class));
			mainActivity.finish();
			// T.showShort(mContext, "发表评论前请先登录。");
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterAndLoginActivity.class);
			// startActivityForResult(intent, Constant.PUBLISH_COMMENT);
		}

	}

	private void publishComment(User user, String content) {

		final BlogComment comment = new BlogComment();
		comment.setUser(user);
		comment.setCommentContent(content);
		comment.save(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				T.showShort(mContext, "评论成功");
				if (mAdapter.getDataList().size() < WMapConstants.NUMBERS_PER_PAGE) {
					mAdapter.getDataList().add(comment);
					mAdapter.notifyDataSetChanged();
					setListViewHeightBasedOnChildren(commentList);
				}
				commentContent.setText("");
				hideSoftInput();

				// 将该评论与强语绑定到一起
				BmobRelation relation = new BmobRelation();
				relation.add(comment);
				qiangYu.setRelation(relation);
				qiangYu.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						// TODO Auto-generated method stub
						L.i(TAG, "更新评论成功。");
						// fetchData();
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						// TODO Auto-generated method stub
						L.i(TAG, "更新评论失败。" + arg1);
					}
				});

			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub
				T.showShort(mContext, "评论失败。请检查网络~");
			}
		});
	}

	private void onClickFav(View v) {
		// TODO Auto-generated method stub

		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user != null && user.getSessionToken() != null) {
			BmobRelation favRelaton = new BmobRelation();
			qiangYu.setMyFav(!qiangYu.getMyFav());
			if (qiangYu.getMyFav()) {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_choose);
				favRelaton.add(qiangYu);
				T.showShort(mContext, "收藏成功。");
			} else {
				((ImageView) v)
						.setImageResource(R.drawable.ic_action_fav_normal);
				favRelaton.remove(qiangYu);
				T.showShort(mContext, "取消收藏。");
			}

			user.setFavorite(favRelaton);
			user.update(mContext, new UpdateListener() {

				@Override
				public void onSuccess() {
					// TODO Auto-generated method stub
					L.i(TAG, "收藏成功。");
					T.showShort(mContext, "收藏成功。");
					// try get fav to see if fav success
					// getMyFavourite();
				}

				@Override
				public void onFailure(int arg0, String arg1) {
					// TODO Auto-generated method stub
					L.i(TAG, "收藏失败。请检查网络~");
					T.showShort(mContext, "收藏失败。请检查网络~" + arg0);
				}
			});
		} else {
			// 前往登录注册界面
			T.showShort(mContext, "收藏前请先登录。");
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterAndLoginActivity.class);
			// startActivityForResult(intent, Constant.SAVE_FAVOURITE);
			Activity mainActivity = BaseFragment.getMainActivity();
			mainActivity.startActivity(new Intent(mainActivity,
					LoginActivity.class));
			mainActivity.finish();
		}

	}

	private void getMyFavourite() {
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user != null) {
			BmobQuery<Blog> query = new BmobQuery<Blog>();
			query.addWhereRelatedTo("favorite", new BmobPointer(user));
			query.include("user");
			query.order("createdAt");
			query.setLimit(WMapConstants.NUMBERS_PER_PAGE);
			query.findObjects(mContext, new FindListener<Blog>() {

				@Override
				public void onSuccess(List<Blog> data) {
					// TODO Auto-generated method stub
					L.i(TAG, "get fav success!" + data.size());
					T.showShort(mContext, "fav size:" + data.size());
				}

				@Override
				public void onError(int arg0, String arg1) {
					// TODO Auto-generated method stub
					T.showShort(mContext, "获取收藏失败。请检查网络~");
				}
			});
		} else {
			// 前往登录注册界面
			// T.showShort(mContext, "获取收藏前请先登录。");
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterAndLoginActivity.class);
			// startActivityForResult(intent, Constant.GET_FAVOURITE);
			Activity mainActivity = BaseFragment.getMainActivity();
			mainActivity.startActivity(new Intent(mainActivity,
					LoginActivity.class));
			mainActivity.finish();
		}
	}

	boolean isFav = false;

	private void onClickLove() {
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user == null) {
			// 前往登录注册界面
			T.showShort(mContext, "请先登录");
			Activity mainActivity = BaseFragment.getMainActivity();
			mainActivity.startActivity(new Intent(mainActivity,
					LoginActivity.class));
			mainActivity.finish();
			// Intent intent = new Intent();
			// intent.setClass(this, RegisterAndLoginActivity.class);
			// startActivity(intent);
			return;
		}
		if (qiangYu.getMyLove()) {
			T.showShort(mContext, "您已经赞过啦");
			return;
		}
		isFav = qiangYu.getMyFav();
		if (isFav) {
			qiangYu.setMyFav(false);
		}
		qiangYu.setLove(qiangYu.getLove() + 1);
		love.setTextColor(Color.parseColor("#D95555"));
		love.setText(qiangYu.getLove() + "");
		qiangYu.increment("love", 1);
		qiangYu.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				qiangYu.setMyLove(true);
				qiangYu.setMyFav(isFav);
				DatabaseUtil.getInstance(mContext).insertFav(qiangYu);

				T.showShort(mContext, "点赞成功~");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
			}
		});
	}

	private void onClickHate() {
		// TODO Auto-generated method stub
		qiangYu.setHate(qiangYu.getHate() + 1);
		hate.setText(qiangYu.getHate() + "");
		qiangYu.increment("hate", 1);
		qiangYu.update(mContext, new UpdateListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				T.showShort(mContext, "点踩成功~");
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void onClickShare() {
		// TODO Auto-generated method stub
		T.showShort(mContext, "share to ...");
		final TencentShare tencentShare = new TencentShare(
				BaseFragment.getMainActivity(), getQQShareEntity(qiangYu));
		tencentShare.shareToQQ();
	}

	private TencentShareEntity getQQShareEntity(Blog qy) {
		// TODO 分享到qq时显示的内容
		String title = "这里好多美丽的风景";
		String comment = "来领略最美的风景吧";
		String img = null;
		if (qy.getContentfigureurl() != null) {
			img = qy.getContentfigureurl().getFileUrl(mContext);
		} else {
			img = "http://www.codenow.cn/appwebsite/website/yyquan/uploads/53af6851d5d72.png";
		}
		String summary = qy.getContent();

		String targetUrl = "http://huodianditu.bmob.cn";
		TencentShareEntity entity = new TencentShareEntity(title, img,
				targetUrl, summary, comment);
		return entity;
	}

	private void onClickComment() {
		// TODO Auto-generated method stub
		commentContent.requestFocus();

		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.showSoftInput(commentContent, 0);
	}

	private void hideSoftInput() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(commentContent.getWindowToken(), 0);
	}

	// TODO 下一版本调整为可以无账号预览，点击时提醒注册登陆，
	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// // TODO Auto-generated method stub
	// super.onActivityResult(requestCode, resultCode, data);
	// if (resultCode == Activity.RESULT_OK) {
	// switch (requestCode) {
	// case Constant.PUBLISH_COMMENT:
	// // 登录完成
	// commentCommit.performClick();
	// break;
	// case Constant.SAVE_FAVOURITE:
	// myFav.performClick();
	// break;
	// case Constant.GET_FAVOURITE:
	//
	// break;
	// case Constant.GO_SETTINGS:
	// userLogo.performClick();
	// break;
	// default:
	// break;
	// }
	// }
	//
	// }

	/***
	 * 动态设置listview的高度 item 总布局必须是linearLayout
	 * 
	 * @param listView
	 */
	public void setListViewHeightBasedOnChildren(ListView listView) {
		ListAdapter listAdapter = listView.getAdapter();
		if (listAdapter == null) {
			return;
		}
		int totalHeight = 0;
		for (int i = 0; i < listAdapter.getCount(); i++) {
			View listItem = listAdapter.getView(i, null, listView);
			listItem.measure(0, 0);
			totalHeight += listItem.getMeasuredHeight();
		}
		ViewGroup.LayoutParams params = listView.getLayoutParams();
		params.height = totalHeight
				+ (listView.getDividerHeight() * (listAdapter.getCount() - 1))
				+ 15;
		listView.setLayoutParams(params);
	}

}
