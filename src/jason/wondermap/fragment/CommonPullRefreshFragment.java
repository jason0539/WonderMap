package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.BaseContentAdapter;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.User;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.FindListener;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

public abstract class CommonPullRefreshFragment extends ContentFragment {
	String TAG = "CommonPullRefreshFragment";
	private int pageNum;
	private String lastItemTime;

	protected ViewGroup contentView;
	protected ArrayList<Blog> mListItems;
	private PullToRefreshListView mPullRefreshListView;
	private BaseContentAdapter<Blog> mAdapter;
	private ListView actualListView;

	private TextView networkTips;
	private ProgressBar progressbar;
	private boolean pullFromUser;

	public enum RefreshType {
		REFRESH, LOAD_MORE
	}

	private RefreshType mRefreshType = RefreshType.LOAD_MORE;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		contentView = (ViewGroup) inflater.inflate(R.layout.fragment_pullrefresh_common,
				mContainer, false);
		return contentView;
	}

	@Override
	protected void onInitView() {
		pageNum = 0;
		lastItemTime = getCurrentTime();
		initTopBar();
		mPullRefreshListView = (PullToRefreshListView) contentView
				.findViewById(R.id.pull_refresh_list);
		networkTips = (TextView) contentView.findViewById(R.id.networkTips);
		progressbar = (ProgressBar) contentView.findViewById(R.id.progressBar);
		mPullRefreshListView.setMode(Mode.BOTH);
		mPullRefreshListView
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
						pullFromUser = true;
						mRefreshType = RefreshType.REFRESH;
						pageNum = 0;
						lastItemTime = getCurrentTime();
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
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub

					}
				});

		actualListView = mPullRefreshListView.getRefreshableView();
		mListItems = new ArrayList<Blog>();
		mAdapter = getAdapter();
		actualListView.setAdapter(mAdapter);

		if (mListItems.size() == 0) {
			fetchData();
		}
		actualListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				onListItemClick(parent, view, position, id);
			}
		});
	}

	private String getCurrentTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String times = formatter.format(new Date(System.currentTimeMillis()));
		return times;
	}

	public void fetchData() {
		setState(LOADING);
		User user = BmobUser.getCurrentUser(mContext, User.class);
		BmobQuery<Blog> query = new BmobQuery<Blog>();
		query.addWhereRelatedTo("favorite", new BmobPointer(user));
		query.order("-createdAt");
		query.setLimit(WMapConstants.NUMBERS_PER_PAGE);
		BmobDate date = new BmobDate(new Date(System.currentTimeMillis()));
		query.addWhereLessThan("createdAt", date);
		query.setSkip(WMapConstants.NUMBERS_PER_PAGE * (pageNum++));
		query.include("author");
		query.findObjects(getActivity(), new FindListener<Blog>() {

			@Override
			public void onSuccess(List<Blog> list) {
				// TODO Auto-generated method stub
				L.i(TAG, "find success." + list.size());
				if (list.size() != 0 && list.get(list.size() - 1) != null) {
					if (mRefreshType == RefreshType.REFRESH) {
						mListItems.clear();
					}
					if (list.size() < WMapConstants.NUMBERS_PER_PAGE) {
						T.showShort(mContext, "已加载完所有数据~");
					}
					mListItems.addAll(list);
					mAdapter.notifyDataSetChanged();

					L.i(TAG, "DD"
							+ (mListItems.get(mListItems.size() - 1) == null));
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				} else {
					T.showShort(mContext, "暂无更多数据~");
					if (list.size() == 0 && mListItems.size() == 0) {

						networkTips.setText("暂无收藏。快去首页收藏几个把~");
						setState(LOADING_FAILED);
						pageNum--;
						mPullRefreshListView.onRefreshComplete();

						L.i(TAG,
								"SIZE:" + list.size() + "ssssize"
										+ mListItems.size());
						return;
					}
					pageNum--;
					setState(LOADING_COMPLETED);
					mPullRefreshListView.onRefreshComplete();
				}
			}

			@Override
			public void onError(int arg0, String arg1) {
				// TODO Auto-generated method stub
				L.i(TAG, "find failed." + arg1);
				pageNum--;
				setState(LOADING_FAILED);
				mPullRefreshListView.onRefreshComplete();
			}
		});
	}

	private static final int LOADING = 1;
	private static final int LOADING_COMPLETED = 2;
	private static final int LOADING_FAILED = 3;
	private static final int NORMAL = 4;

	public void setState(int state) {
		switch (state) {
		case LOADING:
			if (mListItems.size() == 0) {
				mPullRefreshListView.setVisibility(View.GONE);
				progressbar.setVisibility(View.VISIBLE);
			}
			networkTips.setVisibility(View.GONE);

			break;
		case LOADING_COMPLETED:
			networkTips.setVisibility(View.GONE);
			progressbar.setVisibility(View.GONE);

			mPullRefreshListView.setVisibility(View.VISIBLE);
			mPullRefreshListView.setMode(Mode.BOTH);

			break;
		case LOADING_FAILED:
			if (mListItems.size() == 0) {
				mPullRefreshListView.setVisibility(View.VISIBLE);
				mPullRefreshListView.setMode(Mode.PULL_FROM_START);
				networkTips.setVisibility(View.VISIBLE);
			}
			progressbar.setVisibility(View.GONE);
			break;
		case NORMAL:

			break;
		default:
			break;
		}
	}

	public abstract BaseContentAdapter<Blog> getAdapter();

	public abstract void onListItemClick(AdapterView<?> parent, View view,
			int position, long id);

	protected void initTopBar() {

	}
}
