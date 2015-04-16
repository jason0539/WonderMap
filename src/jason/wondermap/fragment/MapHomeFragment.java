package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.manager.WMapUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.view.MainBottomBar;

import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

public class MapHomeFragment extends ContentFragment {
	private final static String TAG = MapHomeFragment.class.getSimpleName();
	// bottomBar
	private MainBottomBar bottomBar;
	private ViewGroup mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d(TAG + ":onCreateView");
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (view != null) {
			view.setClickable(false); // 允许地图可点击
		}
		return view;
	}

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		L.d(TAG + ":onCreateContentView");
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_map, null);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		L.d(TAG + ":onInitView");
		bottomBar = new MainBottomBar(
				mRootView.findViewById(R.id.main_bottom_bar));
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	public boolean onBackPressed() {
		mActivity.openExitAppDialog();
		return true;

	}

	@Override
	protected Map<View, Animation> animationOut(int fragmentType, boolean isBack) {
		return null;
	}

	@Override
	protected Map<View, Animation> animationIn(long lastDuration,
			int fragmentType, boolean isBack) {
		return null;
	}

	@Override
	public void onResume() {
		L.d(TAG + ":onResume");
		//确保消息未读数量正确
		bottomBar.onResume();
		super.onResume();
	}

	public void onPause() {
		L.d(TAG + ":onPause");
		bottomBar.onPause();
		super.onPause();
	};

	@Override
	public void onDestroyView() {
		L.d(TAG + ":onDestroyView");
		bottomBar.onDestroyView();
		super.onDestroyView();
	}

}
