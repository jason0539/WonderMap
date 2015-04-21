package jason.wondermap.proxy;

import jason.wondermap.bean.User;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;

import java.util.List;

import android.content.Context;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.ResetPasswordListener;
import cn.bmob.v3.listener.SaveListener;

public class UserProxy {

	public static final String TAG = "UserProxy";

	private Context mContext;

	public UserProxy(Context context) {
		this.mContext = context;
	}

	public void register(final String email, final String password) {
		// 由于每个应用的注册所需的资料都不一样，故IM sdk未提供注册方法，用户可按照bmod SDK的注册方式进行注册。
		// 注册的时候需要注意两点：1、User表中绑定设备id和type，2、设备表中绑定username字段
		BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
		query.addWhereEqualTo(UserInfo.EMAIL, email);
		query.findObjects(mContext, new FindListener<BmobUser>() {
			@Override
			public void onSuccess(List<BmobUser> object) {
				if (object.size() <= 0) {
					final User user = new User();
					user.setEmail(email);
					user.setPassword(password);
					user.setUsername(email);
					user.setSex(true);
					user.setSignature("这个家伙很懒，什么也没说");
					user.setDeviceType("android");
					user.setInstallId(BmobInstallation
							.getInstallationId(mContext));// 用户与设备绑定
					user.signUp(mContext, new SaveListener() {

						@Override
						public void onSuccess() {
							if (signUpLister != null) {
								signUpLister.onSignUpSuccess(user);
							} else {
								L.i(TAG,
										"signup listener is null,you must set one!");
							}
						}

						@Override
						public void onFailure(int arg0, String msg) {
							if (signUpLister != null) {
								signUpLister.onSignUpFailure(msg);
							} else {
								L.i(TAG,
										"signup listener is null,you must set one!");
							}
						}
					});
				} else {
					signUpLister.onSignUpFailure("邮箱已存在");
				}
			}

			@Override
			public void onError(int code, String msg) {
				signUpLister.onSignUpFailure("注册失败，请重试");
			}
		});

	}

	public void login(String email, final String password) {
		BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
		query.addWhereEqualTo(UserInfo.EMAIL, email);
		query.findObjects(mContext, new FindListener<BmobUser>() {
			// 查询邮箱成功
			@Override
			public void onSuccess(List<BmobUser> object) {
				String userName = null;
				if (object.size() > 0 && object.get(0) != null
						&& !object.get(0).getUsername().equals("")) {
					userName = object.get(0).getUsername();
					final BmobUser user = new BmobUser();
					user.setUsername(userName);
					user.setPassword(password);
					user.login(mContext, new SaveListener() {
						// 登陆成功
						@Override
						public void onSuccess() {
							if (loginListener != null) {
								loginListener.onLoginSuccess();
							} else {
								L.i(TAG,
										"login listener is null,you must set one!");
							}
						}

						// 登陆失败
						@Override
						public void onFailure(int arg0, String msg) {
							if (loginListener != null) {
								loginListener.onLoginFailure(msg);
							} else {
								L.i(TAG,
										"login listener is null,you must set one!");
							}
						}
					});
				} else {
					if (loginListener != null) {
						loginListener.onLoginFailure("邮箱不存在，请重试");
					} else {
						L.i(TAG, "login listener is null,you must set one!");
					}
				}

			}

			// 查询邮箱失败
			@Override
			public void onError(int code, String msg) {
				if (loginListener != null) {
					loginListener.onLoginFailure(msg);
				} else {
					L.i(TAG, "login listener is null,you must set one!");
				}
			}
		});
	}

	public void resetPassword(final String email) {
		BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
		query.addWhereEqualTo(UserInfo.EMAIL, email);
		query.findObjects(mContext, new FindListener<BmobUser>() {
			@Override
			public void onSuccess(List<BmobUser> object) {
				if (object.size() > 0) {
					BmobUser.resetPassword(mContext, email,
							new ResetPasswordListener() {

								@Override
								public void onSuccess() {
									// TODO Auto-generated method stub
									if (resetPasswordListener != null) {
										resetPasswordListener.onResetSuccess();
									} else {
										L.i(TAG,
												"reset listener is null,you must set one!");
									}
								}

								@Override
								public void onFailure(int arg0, String msg) {
									// TODO Auto-generated method stub
									if (resetPasswordListener != null) {
										resetPasswordListener
												.onResetFailure(msg);
									} else {
										L.i(TAG,
												"reset listener is null,you must set one!");
									}
								}
							});
				} else {
					if (resetPasswordListener != null) {
						resetPasswordListener.onResetFailure("该邮箱还没有注册过");
					} else {
						L.i(TAG, "reset listener is null,you must set one!");
					}
				}
			}

			@Override
			public void onError(int code, String msg) {
				if (resetPasswordListener != null) {
					resetPasswordListener.onResetFailure(msg);
				} else {
					L.i(TAG, "reset listener is null,you must set one!");
				}
			}
		});
	}

	// 登陆监听
	public interface ILoginListener {
		void onLoginSuccess();

		void onLoginFailure(String msg);
	}

	private ILoginListener loginListener;

	public void setOnLoginListener(ILoginListener loginListener) {
		this.loginListener = loginListener;
	}

	// 重设密码监听
	public interface IResetPasswordListener {
		void onResetSuccess();
		void onResetFailure(String msg);
	}

	private IResetPasswordListener resetPasswordListener;

	public void setOnResetPasswordListener(
			IResetPasswordListener resetPasswordListener) {
		this.resetPasswordListener = resetPasswordListener;
	}

	// 注册监听
	public interface ISignUpListener {
		void onSignUpSuccess(User user);

		void onSignUpFailure(String msg);
	}

	private ISignUpListener signUpLister;

	public void setOnSignUpListener(ISignUpListener signUpLister) {
		this.signUpLister = signUpLister;
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
