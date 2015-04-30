package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.RecommendFriendAdapter;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.User;
import jason.wondermap.helper.PhoneRecommendHelper;
import jason.wondermap.interfacer.PhoneNumberRecommendListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.sns.TencentShare;
import jason.wondermap.sns.TencentShareEntity;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.WModel;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;

public class FriendRecommendFragment extends ContentFragment {
	private ViewGroup mRootViewGroup;
	private Button bn_recommend;
	private PhoneRecommendHelper phoneRecommendHelper;
	private ProgressDialog dialog;
	private ListView listView;
	RecommendFriendAdapter adapter;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_friend_recommend, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForLeft(mRootViewGroup, "好友推荐");
		bn_recommend = (Button) mRootViewGroup
				.findViewById(R.id.btn_recommend_friend);
		bn_recommend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				T.showShort(mContext, "推荐给好友一起玩");
				final TencentShare tencentShare = new TencentShare(BaseFragment
						.getMainActivity(), getQQShareEntity());
				tencentShare.shareToQQ();
			}
		});
		listView = (ListView) mRootViewGroup.findViewById(R.id.list_recommend);
		L.d(WModel.PhoneNumber, "手机号");
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("正在搜索,不要退出");
		dialog.setCanceledOnTouchOutside(false);
		dialog.show();
		phoneRecommendHelper = new PhoneRecommendHelper(mContext);
		phoneRecommendHelper
				.fetchRecommendFriends(new PhoneNumberRecommendListener() {

					@Override
					public void onSuccess(HashMap<String, User> recommedFriends) {
						dialog.dismiss();
						adapter = new RecommendFriendAdapter(
								phoneRecommendHelper);
						listView.setAdapter(adapter);
					}

					@Override
					public void onProgress(int total, int progress) {
						L.d(WModel.PhoneNumber, "进度：" + progress + "/" + total);
						dialog.setMessage("进度：" + progress + "/" + total);
					}
				});
	}

	private TencentShareEntity getQQShareEntity() {
		String title = "活点地图，随时随地看见TA";
		String comment = "快来加入活点地图,看看TA在哪里";
		String img = null;
		// img =
		// "http://file.bmob.cn/M01/B2/14/oYYBAFVBexuALTLNAAC3_XgW_sY104.png";
		img = "http://file.bmob.cn/M00/69/6C/oYYBAFU5-R6AHUciAADjtQ_g_-8687.jpg";
		String summary = "在活点地图里我叫“"
				+ AccountUserManager.getInstance().getCurrentUserName()
				+ "”，快来找我吧";
		String targetUrl = "http://huodianditu.bmob.cn";
		TencentShareEntity entity = new TencentShareEntity(title, img,
				targetUrl, summary, comment);
		return entity;
	}
}
