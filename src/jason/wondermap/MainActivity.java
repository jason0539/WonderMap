package jason.wondermap;

import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.ContentFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.helper.LaunchHelper;
import jason.wondermap.interfacer.LaunchInitListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;

import com.baidu.mapapi.map.MapView;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

public class MainActivity extends FragmentActivity {
	private WMFragmentManager fragmentManager;
	private View mForbidTouchView; // 禁止触摸的空视图
	private DialogTips appDialog = null;
	private View hideView;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝启动初始化＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d(WModel.MainActivity, "onCreate");
		long t = System.currentTimeMillis();
		setContentView(R.layout.activity_main);
		L.d(WModel.Time,
				"MainActivity onCreate时间" + (System.currentTimeMillis() - t));
		fragmentManager = new WMFragmentManager(this);
		BaseFragment.initBeforeAll(this, fragmentManager);
		mForbidTouchView = findViewById(R.id.view_main_forbid_touch);
		fragmentManager.showFragment(WMFragmentManager.TYPE_SPLASH);
	}

	private LaunchInitListener initListener;

	public void setOnLaunchInitLis(LaunchInitListener android) {
		initListener = android;
	}

	public void init() {
		// 设置小米自动更新组件，仅wifi下更新
		XiaomiUpdateAgent.setCheckUpdateOnlyWifi(true);
		XiaomiUpdateAgent.update(this);
		getWindow().getDecorView().setBackgroundDrawable(null);
		forbidTouch(false);// 默认不禁止触摸
		// 初始化地图显示,依赖地图sdk初始化
		MapControler.getInstance().init((MapView) findViewById(R.id.bmapView));
		new Thread(new Runnable() {
			public void run() {
				long t = System.currentTimeMillis();
				hideView = findViewById(R.id.layout_hide);
				// 初始化地图,定位一旦开始就要使用地图，没有依赖
				mForbidTouchView.setOnTouchListener(new OnTouchListener() {

					@Override
					public boolean onTouch(View v, MotionEvent event) {
						return true;
					}
				});
				initListener.OnFinished();
				initListener = null;
				L.d(WModel.Time,
						"MainActivity init时间"
								+ (System.currentTimeMillis() - t));
			}
		}).start();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 启动结束，释放遮挡地图的红色底图
	 */
	public void releaseHideView() {
		if (null != hideView) {
			try {
				ViewGroup vg = (ViewGroup) getWindow().getDecorView()
						.findViewById(android.R.id.content);
				if (null != vg) {
					vg.removeView(hideView);
				}
			} catch (Exception e) {
			}
			hideView.setBackgroundDrawable(null);
			hideView = null;
		}
	}

	/**
	 * 禁止点击事件，true禁止
	 */
	public void forbidTouch(boolean forbid) {
		if (forbid)
			mForbidTouchView.setVisibility(View.VISIBLE);
		else
			mForbidTouchView.setVisibility(View.GONE);
	}

	public void showTips(int sourseid) {
		appDialog = new DialogTips(this, "提示", getResources().getString(
				sourseid), "知道了", false, true);
		// 设置成功事件
		appDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				appDialog.dismiss();
			}
		});
		// 显示确认对话框
		appDialog.show();
	}

	public void showMessage(String msgString, OnClickListener onSuccessListener) {
		appDialog = new DialogTips(this, "提示", msgString, "确定", true, true);
		// 设置成功事件
		appDialog.SetOnSuccessListener(onSuccessListener);
		appDialog.SetOnCancelListener(new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				appDialog.dismiss();
			}
		});
		// 显示确认对话框
		appDialog.show();
	}

	public void openExitAppDialog() {
		// appDialog = new DialogTips(this, "退出", getResources().getString(
		// R.string.exit_tips), "确定", true, true);
		// // 设置成功事件
		// appDialog.SetOnSuccessListener(new DialogInterface.OnClickListener()
		// {
		// public void onClick(DialogInterface dialogInterface, int userId) {
		// exitApp();
		// }
		// });
		// // 显示确认对话框
		// appDialog.show();
		//
		// appDialog.setOnDismissListener(new OnDismissListener() {
		//
		// @Override
		// public void onDismiss(DialogInterface dialog) {
		// appDialog = null;
		// }
		// });
		//
		// if (!appDialog.isShowing()) {
		// try {
		// appDialog.show();
		// } catch (Exception e) {
		// }
		// }
		// 没有处理好退出进入的逻辑，总是crash，暂时默认返回直接推到后台，不让退出
		exitApp();
	}

	/** 是否正在显示退出应用对话框 */
	public boolean isShowingDialog() {
		if (appDialog != null && appDialog.isShowing()) {
			return true;
		}
		return false;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	@Override
	public void onBackPressed() {
		ContentFragment fragment = fragmentManager.getCurrentFragment();
		if (fragment != null && fragment.onBackPressed())
			return;
		if (fragmentManager.getFragmentStackSize() > 0)
			fragmentManager.back(null);
	}

	/**
	 * 显示下线的对话框 showOfflineDialog
	 */
	public void showOfflineDialog() {
		appDialog = new DialogTips(this, "您的账号已在其他设备上登录!", "重新登录");
		// 设置成功事件
		appDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				AccountUserManager.getInstance().logout();
				appDialog.dismiss();
			}
		});
		// 显示确认对话框
		appDialog.show();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	protected void onPause() {
		L.d(WModel.MainActivity, "onPause");
		MapControler.getInstance().onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		L.d(WModel.MainActivity, "onResume");
		MapControler.getInstance().onResume();
		super.onResume();
	}

	public void exitApp() {
		L.d(WModel.MainActivity, "exitApp into ");
		// // 做一些销毁操作
		// finish();
		// new Handler().postDelayed(new Runnable() {
		// @Override
		// public void run() {
		// android.os.Process.killProcess(android.os.Process.myPid());
		// L.d(WModel.MainActivity, "exitApp out ");
		// }
		// }, 1000);
		// super.onBackPressed();
		Intent i = new Intent(Intent.ACTION_MAIN);
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		i.addCategory(Intent.CATEGORY_HOME);
		startActivity(i);
	}

	@Override
	protected void onDestroy() {
		L.d(WModel.MainActivity, "onDestroy into");
		new LaunchHelper().checkExit();
		L.d(WModel.MainActivity, "onDestroy out");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		L.d(WModel.MainActivity, "onActivityResult");
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
