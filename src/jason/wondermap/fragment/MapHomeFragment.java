package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.controler.MapControler;
import jason.wondermap.helper.LaunchHelper;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.PushMsgSendManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.MainBottomBar;

import java.util.Map;

import B.t;
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
		new LaunchHelper().checkIsNeedToConfirmInfo();
		if (MapUserManager.getInstance().isOnlyShowFriends()) {
			initTopBarForOnlyTitle(mRootView, "好友地图");
		} else {
			initTopBarForOnlyTitle(mRootView, "在线地图");
		}
		bottomBar = new MainBottomBar(
				mRootView.findViewById(R.id.main_bottom_bar));
		locationView = mRootView.findViewById(R.id.tv_maphome_location);
		smallView = mRootView.findViewById(R.id.tv_maphome_small);
		bigView = mRootView.findViewById(R.id.tv_maphome_big);
		typeView = mRootView.findViewById(R.id.tv_maphome_type);
		friendView = mRootView.findViewById(R.id.tv_maphome_friend);
		initListener();
		PushMsgSendManager.getInstance().sayHello();
	}

	private void initListener() {
		locationView.setOnClickListener(getClickListener());
		smallView.setOnClickListener(getClickListener());
		bigView.setOnClickListener(getClickListener());
		friendView.setOnClickListener(getClickListener());
		locationView.setOnLongClickListener(getLongClickListener());
		smallView.setOnLongClickListener(getLongClickListener());
		typeView.setOnClickListener(getClickListener());
	}

	private OnClickListener getClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.tv_maphome_big:
					L.d(WModel.MapControl, "点击放大按钮");
					MapControler.getInstance().zoomIn(false);
					break;
				case R.id.tv_maphome_small:
					L.d(WModel.MapControl, "点击缩小按钮");
					if (WonderMapApplication.getInstance().getSpUtil()
							.isFirstSmall()) {
						BaseFragment.getMainActivity().showTips(
								R.string.tips_maphome_small);
						WonderMapApplication.getInstance().getSpUtil()
								.setFirstSmall(false);
					}
					MapControler.getInstance().zoomOut();
					break;
				case R.id.tv_maphome_location:
					L.d(WModel.MapControl, "点击定位按钮");
					if (WonderMapApplication.getInstance().getSpUtil()
							.isFirstLocation()) {
						BaseFragment.getMainActivity().showTips(
								R.string.tips_maphome_location);
						WonderMapApplication.getInstance().getSpUtil()
								.setFirstLocation(false);
					}

					MapControler.getInstance().moveToMylocation();
					break;
				case R.id.tv_maphome_type:
					if (WonderMapApplication.getInstance().getSpUtil()
							.isFirstChangeMapType()) {
						BaseFragment.getMainActivity().showTips(
								R.string.tips_maphome_maptype);
						WonderMapApplication.getInstance().getSpUtil()
								.setFirstChangeMapType(false);
					}

					MapControler.getInstance().changeMapType();
					break;
				case R.id.tv_maphome_friend:
					if (WonderMapApplication.getInstance().getSpUtil()
							.isFirstChangeFriends()) {
						BaseFragment.getMainActivity().showTips(
								R.string.tips_maphome_allorfriends);
						WonderMapApplication.getInstance().getSpUtil()
								.setFirstChangeFriends(false);
					}

					if (MapUserManager.getInstance().isOnlyShowFriends()) {
						T.showShort(getActivity(), "已切换到在线地图");
						initTopBarForOnlyTitle(mRootView, "在线地图");
						MapUserManager.getInstance().showAll();
					} else {
						T.showShort(getActivity(), "已切换到好友地图");
						initTopBarForOnlyTitle(mRootView, "好友地图");
						MapUserManager.getInstance().showFriends();
					}
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
					MapControler.getInstance().zoomOut(
							MapControler.ZoomLevelMin);
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
