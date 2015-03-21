package jason.wondermap;

import com.baidu.mapapi.map.MapView;

import jason.sdk.dialog.JasonDialog;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.ContentFragment;
import jason.wondermap.fragment.WMFragmentManager;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class MainActivity extends FragmentActivity {
	private WMFragmentManager fragmentManager;
	private View mForbidTouchView; // 禁止触摸的空视图
	private JasonDialog mExitAppDialog = null;
	private MapView mMapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initView();
		WMapControler.getInstance().init(mMapView);
		fragmentManager = new WMFragmentManager(this);
		BaseFragment.initBeforeAll(this);
		fragmentManager.showFragment(WMFragmentManager.TYPE_MAP, null);
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

	/**
	 * 获取fragmentManager
	 * 
	 * @return
	 */
	public WMFragmentManager getWMFragmentManager() {
		return fragmentManager;
	}

	public void openExitAppDialog() {
		mExitAppDialog = new JasonDialog(this).setTitle("退出")
				.setMessage("确定退出").setLeftText("取消")
				.setRightOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						exitApp();
					}
				}).setRightText("退出");

		mExitAppDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface dialog) {
				mExitAppDialog = null;
			}
		});

		if (!mExitAppDialog.isShow()) {
			try {
				mExitAppDialog.show();
			} catch (Exception e) {
			}
		}
	}

	/** 是否正在显示退出应用对话框 */
	public boolean isShowingExitAppDialog() {
		if (mExitAppDialog != null && mExitAppDialog.isShow()) {
			return true;
		}
		return false;
	}

	/**
	 * 退出应用
	 */
	public void exitApp() {
		finish();
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}, 100);
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
		mMapView.onPause();

		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		WMapControler.getInstance().unInit();
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
