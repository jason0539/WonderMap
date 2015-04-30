package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.adapter.QiangContentAdapter;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DiscoveryFragment extends ContentFragment implements
		OnPageChangeListener {
	ViewGroup mRootViewGroup;
	private View contentView;
	private ViewPager mViewPager;
	private QiangContentAdapter mAdapter;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_discover, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForBoth(mRootViewGroup, "足迹",
				R.drawable.btn_footblog_add_blog_selector,
				editFootLogClickListener);
		mViewPager = (ViewPager) mRootViewGroup.findViewById(R.id.viewpager);
		mAdapter = new QiangContentAdapter(getActivity()
				.getSupportFragmentManager());
		mViewPager.setAdapter(mAdapter);
		mViewPager.setOnPageChangeListener(this);
		mViewPager.setOffscreenPageLimit(4);
	}

	onRightImageButtonClickListener editFootLogClickListener = new onRightImageButtonClickListener() {

		@Override
		public void onClick() {
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_NEW_FOOTBLOG,
					null);
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

	// OnPageChangeListener方法
	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {

	}

}
