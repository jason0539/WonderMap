package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.RecommendFriendAdapter;
import jason.wondermap.bean.User;
import jason.wondermap.helper.PhoneRecommendHelper;
import jason.wondermap.interfacer.PhoneNumberRecommendListener;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;

import java.util.HashMap;

import android.app.ProgressDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
		listView = (ListView) mRootViewGroup.findViewById(R.id.list_recommend);
		L.d(WModel.PhoneNumber, "手机号");
		dialog = new ProgressDialog(getActivity());
		dialog.setMessage("正在搜索,不要退出");
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();
		phoneRecommendHelper = new PhoneRecommendHelper(mContext);
		phoneRecommendHelper
				.fetchRecommendFriends(new PhoneNumberRecommendListener() {

					@Override
					public void onSuccess(HashMap<String, User> recommedFriends) {
						dialog.dismiss();
						adapter = new RecommendFriendAdapter(phoneRecommendHelper);
						listView.setAdapter(adapter);
					}

					@Override
					public void onProgress(int total, int progress) {
						L.d(WModel.PhoneNumber, "进度：" + progress + "/" + total);
						dialog.setMessage("进度：" + progress + "/" + total);
					}
				});
	}
}
