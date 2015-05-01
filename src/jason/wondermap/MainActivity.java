package jason.wondermap;

import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.ContentFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.helper.LaunchHelper;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.dialog.DialogTips;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends FragmentActivity {
	private WMFragmentManager fragmentManager;
	private View mForbidTouchView; // 禁止触摸的空视图
	private DialogTips appDialog = null;
	private LaunchHelper launchHelper;
	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d(WModel.MainActivity, "onCreate");
		initView();
		launchHelper = new LaunchHelper();
		launchHelper.checkLaunch(this);
		// 注册地图 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
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
//		appDialog = new DialogTips(this, "退出", getResources().getString(
//				R.string.exit_tips), "确定", true, true);
//		// 设置成功事件
//		appDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
//			public void onClick(DialogInterface dialogInterface, int userId) {
//				exitApp();
//			}
//		});
//		// 显示确认对话框
//		appDialog.show();
//
//		appDialog.setOnDismissListener(new OnDismissListener() {
//
//			@Override
//			public void onDismiss(DialogInterface dialog) {
//				appDialog = null;
//			}
//		});
//
//		if (!appDialog.isShowing()) {
//			try {
//				appDialog.show();
//			} catch (Exception e) {
//			}
//		}
		//没有处理好退出进入的逻辑，总是crash，暂时默认返回直接推到后台，不让退出
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

	private void initView() {
		setContentView(R.layout.activity_main);
		fragmentManager = new WMFragmentManager(this);
		BaseFragment.initBeforeAll(this, fragmentManager);
		fragmentManager.showFragment(WMFragmentManager.TYPE_SPLASH);
		// 初始化地图,定位一旦开始就要使用地图，没有依赖
		MapControler.getInstance().init((MapView) findViewById(R.id.bmapView));
		mForbidTouchView = findViewById(R.id.view_main_forbid_touch);
		mForbidTouchView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		forbidTouch(false);// 默认不禁止触摸
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(MainActivity.this,
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置",
						Toast.LENGTH_SHORT).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(MainActivity.this, "当前网络连接不稳定，请检查您的网络设置!",
						Toast.LENGTH_SHORT).show();
			}
		}
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
		launchHelper.checkExit();
		unregisterReceiver(mReceiver);
		L.d(WModel.MainActivity, "onDestroy out");
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
