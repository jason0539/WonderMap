package jason.wondermap;

import jason.sdk.dialog.JasonDialog;
import jason.wondermap.controler.WMapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.fragment.ContentFragment;
import jason.wondermap.fragment.WMFragmentManager;
import jason.wondermap.manager.FeedbackManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.MessageManager;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import com.baidu.mapapi.map.MapView;

public class MainActivity extends FragmentActivity {
	private WMFragmentManager fragmentManager;
	private View mForbidTouchView; // 禁止触摸的空视图
	private JasonDialog mExitAppDialog = null;
	private MapView mMapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.d(WModel.MainActivity, "onCreate");
		setContentView(R.layout.activity_main);
		initView();
		WMapControler.getInstance().init(mMapView);
		WLocationManager.getInstance().init();// 开始定位,之后最好移到application里面，启动就完成
		fragmentManager = new WMFragmentManager(this);
		BaseFragment.initBeforeAll(this);
		fragmentManager.showFragment(WMFragmentManager.TYPE_MAP, null);
		MessageManager.getInstance();// 开始接收消息
//		添加检查log，上传到服务器
		CommonUtils.checkCrashLog();
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
		L.d(WModel.MainActivity, "onPause");
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		L.d(WModel.MainActivity, "onResume");
		mMapView.onResume();
		// 确保所有用户都在地图上显示出来,activity进入onPause之后marker都消失了
		MapUserManager.getInstance().onResumeAllUsersOnMap();// TODO 本来有的，注释掉了。
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		L.d(WModel.MainActivity, "onDestroy");
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		WMapControler.getInstance().unInit();
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
