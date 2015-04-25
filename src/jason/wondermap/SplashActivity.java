package jason.wondermap;

import jason.wondermap.config.WMapConfig;
import jason.wondermap.manager.AccountUserManager;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import cn.bmob.im.BmobChat;

import com.baidu.mapapi.SDKInitializer;
import com.xiaomi.market.sdk.XiaomiUpdateAgent;

/**
 * 启动页 完成IM SDK初始化，检查更新好友信息到内存
 * 
 * @author liuzhenhui
 * 
 */
public class SplashActivity extends Activity {
	private static final int GO_HOME = 100;
	private static final int GO_LOGIN = 200;

	private BaiduReceiver mReceiver;// 注册广播接收器，用于监听网络以及验证key

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);
		// 是否开启debug模式--默认开启状态
		BmobChat.DEBUG_MODE = true;
		// BmobIM SDK初始化--只需要这一段代码即可完成初始化
		BmobChat.getInstance(this).init(WMapConfig.applicationId);
		// 注册地图 SDK 广播监听者
		IntentFilter iFilter = new IntentFilter();
		iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
		iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
		mReceiver = new BaiduReceiver();
		registerReceiver(mReceiver, iFilter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (AccountUserManager.getInstance().getCurrentUser() != null) {
			// 每次自动登陆的时候就需要更新下当前位置和好友的资料，因为好友的头像，昵称啥的是经常变动的
			AccountUserManager.getInstance().updateUserInfos();
			mHandler.sendEmptyMessageDelayed(GO_HOME, 2000);
		} else {// 前往登陆
			mHandler.sendEmptyMessageDelayed(GO_LOGIN, 2000);
		}
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Intent intent = null;
			switch (msg.what) {
			case GO_HOME:
				intent = new Intent(SplashActivity.this, MainActivity.class);
				break;
			case GO_LOGIN:
				intent = new Intent(SplashActivity.this, LoginActivity.class);
				break;
			}
			startActivity(intent);
			finish();
		}
	};

	@Override
	protected void onDestroy() {
		unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	/**
	 * 构造广播监听类，监听 SDK key 验证以及网络异常广播
	 */
	public class BaiduReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			String s = intent.getAction();
			if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
				Toast.makeText(SplashActivity.this,
						"key 验证出错! 请在 AndroidManifest.xml 文件中检查 key 设置",
						Toast.LENGTH_SHORT).show();
			} else if (s
					.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
				Toast.makeText(SplashActivity.this, "当前网络连接不稳定，请检查您的网络设置!",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

}
