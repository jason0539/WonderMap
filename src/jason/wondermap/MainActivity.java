package jason.wondermap;

import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.ContentFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.helper.LaunchHelper;
import jason.wondermap.manager.ChatMessageManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.dialog.DialogTips;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.baidu.mapapi.map.MapView;

public class MainActivity extends FragmentActivity {
	private WMFragmentManager fragmentManager;
	private View mForbidTouchView; // 禁止触摸的空视图
	private DialogTips appDialog = null;
	private MapView mMapView = null;
	private LaunchHelper launchHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d(WModel.MainActivity, "onCreate");
		setContentView(R.layout.activity_main);
		fragmentManager = new WMFragmentManager(this);
		BaseFragment.initBeforeAll(this, fragmentManager);
		initView();
		// 要先初始化地图，否则定位开始了，网上加位置的时候markerView为空
		MapControler.getInstance().init(mMapView);
		WLocationManager.getInstance().start();// 开始定位,之后最好移到application里面，启动就完成
		fragmentManager.showFragment(WMFragmentManager.TYPE_MAP_HOME, null);
		ChatMessageManager.getInstance();// 开始接收消息
		// 添加检查log，上传到服务器
		launchHelper = new LaunchHelper();
		launchHelper.checkLaunch(this);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 禁止点击事件
	 * 
	 * @param forbid
	 *            是否禁止
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
		appDialog = new DialogTips(this, "退出", getResources().getString(
				R.string.exit_tips), "确定", true, true);
		// 设置成功事件
		appDialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				exitApp();
			}
		});
		// 显示确认对话框
		appDialog.show();

		appDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				appDialog = null;
			}
		});

		if (!appDialog.isShowing()) {
			try {
				appDialog.show();
			} catch (Exception e) {
			}
		}
	}

	/** 是否正在显示退出应用对话框 */
	public boolean isShowingExitAppDialog() {
		if (appDialog != null && appDialog.isShowing()) {
			return true;
		}
		return false;
	}

	public void exitApp() {
		// 做一些销毁操作
		finish();
		// new Handler().post(new Runnable() {
		// @Override
		// public void run() {
		android.os.Process.killProcess(android.os.Process.myPid());
		// }
		// });
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
		mMapView = (MapView) findViewById(R.id.bmapView);
		mForbidTouchView = findViewById(R.id.view_main_forbid_touch);
		mForbidTouchView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		forbidTouch(false);// 默认不禁止触摸
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

	@Override
	protected void onDestroy() {
		L.d(WModel.MainActivity, "onDestroy");
		WLocationManager.getInstance().stop();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		MapControler.getInstance().unInit();
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
