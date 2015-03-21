package jason.wondermap;

import jason.wondermap.interfacer.onBaiduPushBindListener;
import jason.wondermap.manager.WAccountManager;
import jason.wondermap.receiver.BDPushMessageReceiver;
import jason.wondermap.utils.L;
import jason.wondermap.utils.NetUtil;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.StaticConstant;
import jason.wondermap.utils.T;
import jason.wondermap.view.LoadingDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.TextView;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class LoginActivity extends FragmentActivity implements
		onBaiduPushBindListener {
	// UI相关
	private TextView resultTextView;
	private LoadingDialog mLoadingDialog;
	// 工具相关
	private SharePreferenceUtil mSpUtil;
//	private UserDB mUserDB;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			handleMsg(msg);
		};
	};

	private void handleMsg(Message msg) {
		switch (msg.what) {
		case StaticConstant.AccountBindSuccess:
			L.i("账户绑定成功");
			break;
		case StaticConstant.AccountBindFail:
			L.i("账户绑定失败");
			break;
		case StaticConstant.AccountBindCancle:
			L.i("账户绑定操作取消");
			break;
		case StaticConstant.UserInfoGetSuccess:
			L.i("获取用户信息成功");
			bindBaiduPush();
			break;
		case StaticConstant.UserInfoGetFail:
			L.i("获取用户信息失败");
			break;
		default:
			break;
		}
		resultTextView.setText(resultTextView.getText() + "\n"
				+ msg.getData().getString("result"));
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// 绑定推送后回调，绑定基本很快就能成功，推送限制了速度，总是超时是因为登陆后的推送
	@Override
	public void onBind(String userId, int errorCode) {
		if (!WAccountManager.getInstance().startBaiduStatus()) {
			return;
		}
		if (errorCode == 0) {// 绑定成功，个人信息从数据库读取，构建自己的用户，添加到用户数据库
			if (mLoadingDialog != null && mLoadingDialog.isVisible())
				mLoadingDialog.dismiss();
			L.d("成功绑定推送");
//			// 如果绑定账号成功，由于第一次运行，给同一tag的人推送一条新人消息
//			User u = new User(mSpUtil.getUserId(), mSpUtil.getChannelId(),
//					mSpUtil.getUserNick(), mSpUtil.getUserHeadPicUrl(), 0);
//			mUserDB.addUser(u);// 把自己添加到数据库,区分用户和好友的逻辑在这里不存储自己
			mSpUtil.login();// 绑定推送后即可视为登陆成功
			finish();
			// 绑定推送成功则进入主页，等定位成功发送hello消息
			Intent intent = new Intent(LoginActivity.this, MainActivity.class);
			startActivity(intent);
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 绑定社交账号
	 */
	public void bindSocialAccount(View view) {
		if (!NetUtil.isNetConnected(this)) {
			T.showLong(this, R.string.net_error_tip);
			return;
		}
		WAccountManager.getInstance().startBaidu();
	}

	/**
	 * 绑定百度推送
	 */
	private void bindBaiduPush() {
		mLoadingDialog.show(getSupportFragmentManager(), "LOADING_DIALOG");
		mLoadingDialog.setCancelable(false);
		PushManager
				.startWork(getApplicationContext(),
						PushConstants.LOGIN_TYPE_ACCESS_TOKEN,
						mSpUtil.getAccessToken());
		// 无baidu帐号登录,以apiKey随机获取一个id，改为账户体系参考http://developer.baidu.com/wiki/index.php?title=docs/cplat/push/scene
		//
		// PushManager.startWork(getApplicationContext(),
		// PushConstants.LOGIN_TYPE_API_KEY, WonderMapApplication.API_KEY);//
		// 无baidu帐号登录,以apiKey随机获取一个id，之后改为账户体系参考http://developer.baidu.com/wiki/index.php?title=docs/cplat/push/scene
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		init();
	}

	public void init() {
		// UI相关
		resultTextView = (TextView) findViewById(R.id.tv_result_show);
		mLoadingDialog = new LoadingDialog();
		// 工具初始化
		mSpUtil = WonderMapApplication.getInstance().getSpUtil();
//		mUserDB = WonderMapApplication.getInstance().getUserDB();
		// 账户绑定监听器
		WAccountManager.getInstance().init(this, mHandler);
		// 推送绑定监听器
		BDPushMessageReceiver.bindListeners.add(this);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		WAccountManager.getInstance().onActivityResult(requestCode, resultCode,
				data);
	};

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 注销推送绑定结果监听
		BDPushMessageReceiver.bindListeners.remove(this);
	}

}
