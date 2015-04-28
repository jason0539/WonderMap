package jason.wondermap.fragment;

import jason.wondermap.LoginActivity;
import jason.wondermap.R;
import jason.wondermap.adapter.AIContentAdapter;
import jason.wondermap.adapter.FootblogCommentAdapter;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.BlogComment;
import jason.wondermap.bean.User;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.manager.FootblogManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * 足迹评论详情页
 * 
 * @author liuzhenhui
 *         由于Blog加了BDLocation之后无法序列化，不再通过bundle传值，而是进入本页前统一存储到FootBlogManager中
 *         ，进入后取出
 */
public class FootblogCommentFragment extends ContentFragment implements
		OnClickListener {
	String TAG = "FootblogCommentFragment";
	private ListView blogEntity;
	AIContentAdapter aiContentAdapter;
	private ListView commentList;
	private TextView footer;

	private EditText commentContent;
	private Button commentCommit;

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
		blogEntity = (ListView) mRootViewGroup.findViewById(R.id.blog_entity);
		footer = (TextView) mRootViewGroup.findViewById(R.id.loadmore);
		commentContent = (EditText) mRootViewGroup
				.findViewById(R.id.comment_content);
		commentCommit = (Button) mRootViewGroup
				.findViewById(R.id.comment_commit);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
						| WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
		qiangYu = FootblogManager.getInstance().getCurrentBlog();
		pageNum = 0;

		mAdapter = new FootblogCommentAdapter(mContext, comments);
		commentList.setAdapter(mAdapter);
		List<Blog> blog = new ArrayList<Blog>();
		blog.add(qiangYu);
		aiContentAdapter = new AIContentAdapter(mContext, blog, this);
		blogEntity.setAdapter(aiContentAdapter);
		setListViewHeightBasedOnChildren(commentList);
		commentList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// T.showShort(mContext, "po" + position);
				// 之后假如@功能，评论中可以@
			}
		});
		commentList.setCacheColorHint(0);
		commentList.setScrollingCacheEnabled(false);
		commentList.setScrollContainer(false);
		commentList.setFastScrollEnabled(true);
		commentList.setSmoothScrollbarEnabled(true);

		footer.setOnClickListener(this);
		commentCommit.setOnClickListener(this);

		fetchData();
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
				if (data.size() != 0 && data.get(data.size() - 1) != null) {

					if (data.size() < WMapConstants.NUMBERS_PER_PAGE) {
						T.showShort(mContext, "已加载完所有评论~");
						footer.setText("暂无更多评论~");
					} else {
						footer.setText("点击加载更多评论");
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
				T.showShort(mContext, "获取评论失败。请检查网络~");
				pageNum--;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.loadmore:
			onClickLoadMore();
			break;
		case R.id.comment_commit:
			onClickCommit();
			break;
		case R.id.item_action_comment:
			onClickComment();
			break;
		default:
			break;
		}
	}

	private void fetchData() {
		fetchComment();
	}

	private void onClickLoadMore() {
		fetchData();
	}

	private void onClickCommit() {
		User currentUser = BmobUser.getCurrentUser(mContext, User.class);
		if (currentUser != null) {// 已登录
			commentEdit = commentContent.getText().toString().trim();
			if (TextUtils.isEmpty(commentEdit)) {
				T.showShort(mContext, "评论内容不能为空。");
				return;
			}
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
				qiangYu.setComment(qiangYu.getComment() + 1);
				qiangYu.increment("comment", 1);
				qiangYu.update(mContext, new UpdateListener() {

					@Override
					public void onSuccess() {
						L.i(TAG, "更新评论成功。");
						aiContentAdapter.notifyDataSetChanged();
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

	private void onClickComment() {
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
