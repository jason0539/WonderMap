package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.controler.MapControler;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.MainBottomBar;

import java.util.Map;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;

public class MapHomeFragment extends ContentFragment {
	private final static String TAG = MapHomeFragment.class.getSimpleName();
	// bottomBar
	private MainBottomBar bottomBar;
	private View locationView;
	private View smallView;
	private View bigView;
	private View typeView;
	private View friendView;
	private ViewGroup mRootView;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d(TAG + ":onCreateView");
		View view = super.onCreateView(inflater, container, savedInstanceState);
		if (view != null) {
			view.setClickable(false); // 允许地图可点击
		}
		return view;
	}

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		L.d(TAG + ":onCreateContentView");
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_map_home,
				null);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		L.d(TAG + ":onInitView");
		initTopBarForOnlyTitle(mRootView, "活点地图");
		bottomBar = new MainBottomBar(
				mRootView.findViewById(R.id.main_bottom_bar));
		locationView = mRootView.findViewById(R.id.tv_maphome_location);
		smallView = mRootView.findViewById(R.id.tv_maphome_small);
		bigView = mRootView.findViewById(R.id.tv_maphome_big);
		typeView = mRootView.findViewById(R.id.tv_maphome_type);
		friendView = mRootView.findViewById(R.id.tv_maphome_friend);
		initListener();
	}

	private void initListener() {
		locationView.setOnClickListener(getClickListener());
		smallView.setOnClickListener(getClickListener());
		bigView.setOnClickListener(getClickListener());
		friendView.setOnClickListener(getClickListener());
		locationView.setOnLongClickListener(getLongClickListener());
		typeView.setOnClickListener(getClickListener());
	}

	private OnClickListener getClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_maphome_big:
					L.d(WModel.MapControl, "点击放大按钮");
					MapControler.getInstance().zoomIn();
					break;
				case R.id.tv_maphome_small:
					L.d(WModel.MapControl, "点击缩小按钮");
					MapControler.getInstance().zoomOut();
					break;
				case R.id.tv_maphome_location:
					L.d(WModel.MapControl, "点击定位按钮");
					MapControler.getInstance().moveToMylocation();
					break;
				case R.id.tv_maphome_type:
					MapControler.getInstance().changeMapType();
					break;
				case R.id.tv_maphome_friend:
					break;
				default:
					break;
				}
			}
		};
	}

	private OnLongClickListener getLongClickListener() {
		return new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				switch (v.getId()) {
				case R.id.tv_maphome_big:
					// L.d(WModel.MapControl, "点击放大按钮");
					// MapControler.getInstance().big();
					break;
				case R.id.tv_maphome_small:
					// L.d(WModel.MapControl, "点击缩小按钮");
					// MapControler.getInstance().small();
					break;
				case R.id.tv_maphome_location:
					L.d(WModel.MapControl, "点击定位按钮");
					MapControler.getInstance().moveToMylocationLongPress();
					break;
				default:
					break;
				}
				return true;
			}
		};
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
		// 确保消息未读数量正确
		bottomBar.onResume();
		// 确保所有用户都在地图上显示出来,activity进入onPause之后marker都消失了
		MapUserManager.getInstance().onResumeAllUsersOnMap();
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
		if (bottomBar != null) {
			bottomBar.onDestroyView();
		}
		super.onDestroyView();
	}

}
