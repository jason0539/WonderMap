package jason.wondermap.fragment;

import java.util.HashMap;
import java.util.Map;

import cn.bmob.im.BmobChat;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.config.WMapConfig;
import jason.wondermap.helper.LaunchHelper;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.AnimationFactory;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;

public class SplashFragment extends ContentFragment {
	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;

	private DialogTips mExitAppDialog;
	private ViewGroup mRootViewGroup;
	private LaunchHelper launchHelper;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(R.layout.activity_splash,
				mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
	}

	// 退出登录后回到这里，onInitView只执行一次，所以放在onResume里面
	@Override
	public void onResume() {
		super.onResume();
		long t = System.currentTimeMillis();
		new Thread(new Runnable() {
			public void run() {
				if (AccountUserManager.getInstance().getCurrentUser() != null) {
					// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
					mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
				} else {// 前往登陆
					mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
				}
			}
		}).start();
		L.d(WModel.Time, "splash时间" + (System.currentTimeMillis() - t));
	}

	private Handler mHandler = new Handler(BaseFragment.getMainActivity()
			.getMainLooper()) {
		@Override
		public void handleMessage(Message msg) {
			// 检查是否显示提醒用户授权信息
			if (!WonderMapApplication.getInstance().getSpUtil().hasAccept()) {
				openUserAccessbleDialog(msg.what);
				return;
			}
			switch (msg.what) {
			case GO_HOME:
				goHomeFrag();
				break;
			case GO_LOGIN:
				goLoginFrag();
				break;
			}
		}
	};

	private void launch() {
		getMainActivity().releaseLaunchView();
		launchHelper = new LaunchHelper();
		launchHelper.checkLaunch();
		AccountUserManager.getInstance().updateUserInfos();
	}

	private void goHomeFrag() {
		launch();
		wmFragmentManager.showFragment(WMFragmentManager.TYPE_MAP_HOME);
	}

	private void goLoginFrag() {
		launch();
		wmFragmentManager.showFragment(WMFragmentManager.TYPE_LOGIN);
	}

	/**
	 * 打开提醒用户授权窗口，仅小米应用商店需要
	 * 
	 * @param what
	 */
	public void openUserAccessbleDialog(final int what) {
		mExitAppDialog = new DialogTips(getActivity(), "活点地图服务条款",
				getResources().getString(R.string.accept_tips), "接受", true,
				true);
		// 设置成功事件
		mExitAppDialog
				.SetOnSuccessListener(new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialogInterface,
							int userId) {
						WonderMapApplication.getInstance().getSpUtil()
								.setAccept(true);
						switch (what) {
						case GO_HOME:
							goHomeFrag();
							break;
						case GO_LOGIN:
							goLoginFrag();
							break;
						}

					}
				});
		mExitAppDialog.SetOnCancelListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				getMainActivity().finish();
			}
		});
		mExitAppDialog.setIsFullScreen(true);
		// 显示确认对话框
		mExitAppDialog.show();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝动画效果＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	protected Map<View, Animation> animationIn(long lastDuration,
			int fragmentType, boolean isBack) {
		Map<View, Animation> animMap = new HashMap<View, Animation>();
		animMap.put(mContentView, AnimationFactory.getAnimation(mContext,
				AnimationFactory.ANIM_POP_IN, lastDuration, 300));
		return animMap;
	}

	@Override
	protected Map<View, Animation> animationOut(int fragmentType, boolean isBack) {
		Map<View, Animation> animMap = new HashMap<View, Animation>();
		animMap.put(mContentView, AnimationFactory.getAnimation(mContext,
				AnimationFactory.ANIM_POP_OUT, -1, 300));
		return animMap;
	}
}