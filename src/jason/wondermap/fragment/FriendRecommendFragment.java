package jason.wondermap.fragment;

import jason.wondermap.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendRecommendFragment extends ContentFragment {
	private ViewGroup mRootViewGroup;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_friend_recommend, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForLeft(mRootViewGroup, "好友推荐");
	}

}
