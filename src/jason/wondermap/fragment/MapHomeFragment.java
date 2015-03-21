package jason.wondermap.fragment;

import jason.wondermap.R;
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_map, null);
		bottomBar = new MainBottomBar(
				viewGroup.findViewById(R.id.main_bottom_bar), getType());
		return viewGroup;
	}

	@Override
	protected void onInitView() {
		L.d(TAG + ":onInitView");
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
		super.onResume();
	}

	public void onPause() {
		L.d(TAG + ":onPause");
		super.onPause();
	};

	@Override
	public void onDestroyView() {
		L.d(TAG + ":onDestroyView");

		super.onDestroyView();
	}

}
