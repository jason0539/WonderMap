package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscoveryFragment extends ContentFragment {
	ViewGroup mRootViewGroup;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_discover, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForBoth(mRootViewGroup, "足迹",
				R.drawable.btn_chat_add_camera_selector,
				editFootLogClickListener);
	}

	onRightImageButtonClickListener editFootLogClickListener = new onRightImageButtonClickListener() {

		@Override
		public void onClick() {
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_NEW_FOOTBLOG, null);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	};

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

}
