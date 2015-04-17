package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NewFootblogFragment extends ContentFragment {
	private ViewGroup mRootViewGroup;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_newfootblog, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForBoth(mRootViewGroup, "发布足迹",
				R.drawable.btn_chat_send_selector, newFootblogSend);
	}

	onRightImageButtonClickListener newFootblogSend = new onRightImageButtonClickListener() {

		@Override
		public void onClick() {

		}
	};
}
