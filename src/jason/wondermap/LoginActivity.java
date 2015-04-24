package jason.wondermap;

import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.interfacer.QQLoginListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.proxy.UserProxy;
import jason.wondermap.proxy.UserProxy.ILoginListener;
import jason.wondermap.proxy.UserProxy.IResetPasswordListener;
import jason.wondermap.proxy.UserProxy.ISignUpListener;
import jason.wondermap.sns.TencentLoginHelper;
import jason.wondermap.utils.StringUtils;
import jason.wondermap.utils.T;
import jason.wondermap.view.DeletableEditText;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;

public class LoginActivity extends FragmentActivity implements OnClickListener,
		ILoginListener, ISignUpListener, IResetPasswordListener {
	public static final int QQLoginSuccess = 11;

	private TextView loginTitle;
	private TextView registerTitle;
	private TextView resetPassword;
	private TextView qqLogin;
	private TextView wbLogin;

	private DeletableEditText userPasswordInput;
	private DeletableEditText userEmailInput;
	private DeletableEditText userPasswordRepeat;

	private Button registerButton;
	private SmoothProgressBar progressbar;
	private UserProxy userProxy;
	private Context mContext;

	private enum UserOperation {
		LOGIN, REGISTER, RESET_PASSWORD
	}

	UserOperation operation = UserOperation.LOGIN;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.activity_register);
		initView();
	}

	@Override
	public void onSignUpSuccess(User bu) {
		AccountUserManager.getInstance().updateUserInfos();
		dimissProgressbar();
		T.showShort(mContext, "注册成功");
		// 前往设置资料页
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.putExtra(BundleTake.NeedToEditInfo, true);
		startActivity(intent);
		finish();
	}

	@Override
	public void onSignUpFailure(String msg) {
		dimissProgressbar();
		T.showShort(mContext, "邮箱已存在");
	}

	@Override
	public void onLoginSuccess() {
		// 更新用户的地理位置以及好友的资料
		AccountUserManager.getInstance().updateUserInfos();
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onLoginFailure(String msg) {
		dimissProgressbar();
		Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onResetSuccess() {
		dimissProgressbar();
		T.showShort(mContext, "请到邮箱修改密码后再登录。");
		operation = UserOperation.LOGIN;
		updateLayout(operation);
	}

	@Override
	public void onResetFailure(String msg) {
		dimissProgressbar();
		T.showShort(mContext, "重置密码失败。请确认网络连接后再重试。");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.register:
			if (operation == UserOperation.LOGIN) {
				if (TextUtils.isEmpty(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "请输入邮箱地址", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!StringUtils.isValidEmail(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "邮箱格式不正确", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (TextUtils.isEmpty(userPasswordInput.getText())) {
					userPasswordInput.setShakeAnimation();
					Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				userProxy.setOnLoginListener(this);
				progressbar.setVisibility(View.VISIBLE);
				userProxy.login(userEmailInput.getText().toString().trim(),
						userPasswordInput.getText().toString().trim());

			} else if (operation == UserOperation.REGISTER) {
				if (TextUtils.isEmpty(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "请输入邮箱地址", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!StringUtils.isValidEmail(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "邮箱格式不正确", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (TextUtils.isEmpty(userPasswordInput.getText())) {
					userPasswordInput.setShakeAnimation();
					Toast.makeText(mContext, "请输入密码", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (TextUtils.isEmpty(userPasswordRepeat.getText())) {
					userPasswordRepeat.setShakeAnimation();
					Toast.makeText(mContext, "请确认密码", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				String password = userPasswordInput.getText().toString().trim();
				String repeatPassword = userPasswordRepeat.getText().toString()
						.trim();
				if (!password.equals(repeatPassword)) {
					userPasswordRepeat.setShakeAnimation();
					userPasswordInput.setShakeAnimation();
					Toast.makeText(mContext, "两次输入的密码不一致", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				userProxy.setOnSignUpListener(this);
				progressbar.setVisibility(View.VISIBLE);
				userProxy.register(userEmailInput.getText().toString().trim(),
						userPasswordInput.getText().toString().trim());
			} else {
				if (TextUtils.isEmpty(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "请输入邮箱地址", Toast.LENGTH_SHORT)
							.show();
					return;
				}
				if (!StringUtils.isValidEmail(userEmailInput.getText())) {
					userEmailInput.setShakeAnimation();
					Toast.makeText(mContext, "邮箱格式不正确", Toast.LENGTH_SHORT)
							.show();
					return;
				}

				userProxy.setOnResetPasswordListener(this);
				progressbar.setVisibility(View.VISIBLE);
				userProxy.resetPassword(userEmailInput.getText().toString()
						.trim());
			}
			break;
		case R.id.login_menu:
			operation = UserOperation.LOGIN;
			updateLayout(operation);
			break;
		case R.id.register_menu:
			operation = UserOperation.REGISTER;
			updateLayout(operation);
			break;
		case R.id.reset_password_menu:
			operation = UserOperation.RESET_PASSWORD;
			updateLayout(operation);
			break;
		case R.id.tv_qq:
			// qq登陆
			loginByQQ();
			break;
		case R.id.tv_weibo:
			toast("敬请期待");
			break;
		default:
			break;
		}
	}

	private void loginByQQ() {
		progressbar.setVisibility(View.VISIBLE);
		TencentLoginHelper helper = new TencentLoginHelper(LoginActivity.this);
		helper.login(qqLoginListener);
	}

	QQLoginListener qqLoginListener = new QQLoginListener() {

		@Override
		public void onSuccess() {
			handler.sendEmptyMessageDelayed(QQLoginSuccess, 500);
		}

		@Override
		public void onFail(String errString) {
			dimissProgressbar();
			toast(errString);
		}
	};

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case QQLoginSuccess:
				onLoginSuccess();
				break;

			default:
				break;
			}
		};
	};

	private void updateLayout(UserOperation op) {
		if (op == UserOperation.LOGIN) {
			loginTitle.setTextColor(Color.parseColor("#D95555"));
			loginTitle.setBackgroundResource(R.drawable.bg_login_tab);
			loginTitle.setPadding(16, 16, 16, 16);
			loginTitle.setGravity(Gravity.CENTER);

			registerTitle.setTextColor(Color.parseColor("#888888"));
			registerTitle.setBackgroundDrawable(null);
			registerTitle.setPadding(16, 16, 16, 16);
			registerTitle.setGravity(Gravity.CENTER);

			resetPassword.setTextColor(Color.parseColor("#888888"));
			resetPassword.setBackgroundDrawable(null);
			resetPassword.setPadding(16, 16, 16, 16);
			resetPassword.setGravity(Gravity.CENTER);

			userPasswordInput.setVisibility(View.VISIBLE);
			userPasswordRepeat.setVisibility(View.GONE);
			registerButton.setText("登录");
		} else if (op == UserOperation.REGISTER) {
			loginTitle.setTextColor(Color.parseColor("#888888"));
			loginTitle.setBackgroundDrawable(null);
			loginTitle.setPadding(16, 16, 16, 16);
			loginTitle.setGravity(Gravity.CENTER);

			registerTitle.setTextColor(Color.parseColor("#D95555"));
			registerTitle.setBackgroundResource(R.drawable.bg_login_tab);
			registerTitle.setPadding(16, 16, 16, 16);
			registerTitle.setGravity(Gravity.CENTER);

			resetPassword.setTextColor(Color.parseColor("#888888"));
			resetPassword.setBackgroundDrawable(null);
			resetPassword.setPadding(16, 16, 16, 16);
			resetPassword.setGravity(Gravity.CENTER);

			userPasswordInput.setVisibility(View.VISIBLE);
			userPasswordRepeat.setVisibility(View.VISIBLE);
			userEmailInput.setVisibility(View.VISIBLE);
			registerButton.setText("注册");
		} else {
			loginTitle.setTextColor(Color.parseColor("#888888"));
			loginTitle.setBackgroundDrawable(null);
			loginTitle.setPadding(16, 16, 16, 16);
			loginTitle.setGravity(Gravity.CENTER);

			registerTitle.setTextColor(Color.parseColor("#888888"));
			registerTitle.setBackgroundDrawable(null);
			registerTitle.setPadding(16, 16, 16, 16);
			registerTitle.setGravity(Gravity.CENTER);

			resetPassword.setTextColor(Color.parseColor("#D95555"));
			resetPassword.setBackgroundResource(R.drawable.bg_login_tab);
			resetPassword.setPadding(16, 16, 16, 16);
			resetPassword.setGravity(Gravity.CENTER);

			userPasswordInput.setVisibility(View.GONE);
			userPasswordRepeat.setVisibility(View.GONE);
			userEmailInput.setVisibility(View.VISIBLE);
			registerButton.setText("找回密码");
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private void initView() {
		loginTitle = (TextView) findViewById(R.id.login_menu);
		registerTitle = (TextView) findViewById(R.id.register_menu);
		resetPassword = (TextView) findViewById(R.id.reset_password_menu);
		qqLogin = (TextView) findViewById(R.id.tv_qq);
		wbLogin = (TextView) findViewById(R.id.tv_weibo);
		userPasswordInput = (DeletableEditText) findViewById(R.id.user_password_input);
		userPasswordRepeat = (DeletableEditText) findViewById(R.id.user_password_input_repeat);
		userEmailInput = (DeletableEditText) findViewById(R.id.user_email_input);
		registerButton = (Button) findViewById(R.id.register);
		progressbar = (SmoothProgressBar) findViewById(R.id.sm_progressbar);
		updateLayout(operation);
		userProxy = new UserProxy(mContext);
		loginTitle.setOnClickListener(this);
		registerTitle.setOnClickListener(this);
		resetPassword.setOnClickListener(this);
		registerButton.setOnClickListener(this);
		qqLogin.setOnClickListener(this);
		wbLogin.setOnClickListener(this);
	}

	private void dimissProgressbar() {
		if (progressbar != null && progressbar.isShown()) {
			progressbar.setVisibility(View.GONE);
		}
	}

	private void toast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}
}
