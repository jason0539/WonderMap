package jason.wondermap.proxy;

import jason.wondermap.bean.User;
import jason.wondermap.utils.L;
import android.content.Context;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordListener;
import cn.bmob.v3.listener.SaveListener;

public class UserProxy {

	public static final String TAG = "UserProxy";

	private Context mContext;

	public UserProxy(Context context) {
		this.mContext = context;
	}

	public void register(String userName, String password, String email) {
		// 由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		// 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		final User user = new User();
		user.setUsername(userName);
		user.setPassword(password);
		user.setEmail(email);
		user.setSex(true);
		user.setSignature("这个家伙很懒，什么也没说。");
		user.setDeviceType("android");
		user.setInstallId(BmobInstallation.getInstallationId(mContext));// 用户与设备绑定
		user.signUp(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if (signUpLister != null) {
					signUpLister.onSignUpSuccess(user);
				} else {
					L.i(TAG, "signup listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if (signUpLister != null) {
					signUpLister.onSignUpFailure(msg);
				} else {
					L.i(TAG, "signup listener is null,you must set one!");
				}
			}
		});
	}

	public interface ISignUpListener {
		void onSignUpSuccess(User user);

		void onSignUpFailure(String msg);
	}

	private ISignUpListener signUpLister;

	public void setOnSignUpListener(ISignUpListener signUpLister) {
		this.signUpLister = signUpLister;
	}

	public User getCurrentUser() {
		User user = BmobUser.getCurrentUser(mContext, User.class);
		if (user != null) {
			L.i(TAG, "本地用户信息" + user.getObjectId() + "-" + user.getUsername()
					+ "-" + user.getSessionToken() + "-" + user.getCreatedAt()
					+ "-" + user.getUpdatedAt() + "-" + user.getSignature()
					+ "-" + user.getSex());
			return user;
		} else {
			L.i(TAG, "本地用户为null,请登录。");
		}
		return null;
	}

	public void login(String userName, String password) {
		final BmobUser user = new BmobUser();
		user.setUsername(userName);
		user.setPassword(password);
		user.login(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if (loginListener != null) {
					loginListener.onLoginSuccess();
				} else {
					L.i(TAG, "login listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if (loginListener != null) {
					loginListener.onLoginFailure(msg);
				} else {
					L.i(TAG, "login listener is null,you must set one!");
				}
			}
		});
	}

	public interface ILoginListener {
		void onLoginSuccess();

		void onLoginFailure(String msg);
	}

	private ILoginListener loginListener;

	public void setOnLoginListener(ILoginListener loginListener) {
		this.loginListener = loginListener;
	}

	public void logout() {
		BmobUser.logOut(mContext);
		L.i(TAG, "logout result:" + (null == getCurrentUser()));
	}

	public void resetPassword(String email) {
		BmobUser.resetPassword(mContext, email, new ResetPasswordListener() {

			@Override
			public void onSuccess() {
				// TODO Auto-generated method stub
				if (resetPasswordListener != null) {
					resetPasswordListener.onResetSuccess();
				} else {
					L.i(TAG, "reset listener is null,you must set one!");
				}
			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				if (resetPasswordListener != null) {
					resetPasswordListener.onResetFailure(msg);
				} else {
					L.i(TAG, "reset listener is null,you must set one!");
				}
			}
		});
	}

	public interface IResetPasswordListener {
		void onResetSuccess();

		void onResetFailure(String msg);
	}

	private IResetPasswordListener resetPasswordListener;

	public void setOnResetPasswordListener(
			IResetPasswordListener resetPasswordListener) {
		this.resetPasswordListener = resetPasswordListener;
	}

	// public void update(String... args) {
	// User user = getCurrentUser();
	// user.setUsername(args[0]);
	// user.setEmail(args[1]);
	// user.setPassword(args[2]);
	// user.setSex(args[3]);
	// user.setSignature(args[4]);
	// // ...
	// user.update(mContext, new UpdateListener() {
	//
	// @Override
	// public void onSuccess() {
	// // TODO Auto-generated method stub
	// if (updateListener != null) {
	// updateListener.onUpdateSuccess();
	// } else {
	// L.i(TAG, "update listener is null,you must set one!");
	// }
	// }
	//
	// @Override
	// public void onFailure(int arg0, String msg) {
	// // TODO Auto-generated method stub
	// if (updateListener != null) {
	// updateListener.onUpdateFailure(msg);
	// } else {
	// L.i(TAG, "update listener is null,you must set one!");
	// }
	// }
	// });
	// }
	//
	// public interface IUpdateListener {
	// void onUpdateSuccess();
	//
	// void onUpdateFailure(String msg);
	// }
	//
	// private IUpdateListener updateListener;
	// public void setOnUpdateListener(IUpdateListener updateListener) {
	// this.updateListener = updateListener;
	// }
}
