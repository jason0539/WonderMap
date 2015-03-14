package jason.wondermap.manager;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaAuthorization;
import com.baidu.frontia.api.FrontiaAuthorization.MediaType;
import com.baidu.frontia.api.FrontiaAuthorizationListener.AuthorizationListener;
import com.baidu.frontia.api.FrontiaAuthorizationListener.UserInfoListener;

public class WAccountManager {
	private FrontiaAuthorization mAuthorization;
	private final static String Scope_Basic = "basic";
	private final static String Scope_Netdisk = "netdisk";
	private Activity mainActivity;
	private Handler mHandler;

	public void startBaidu() {
		ArrayList<String> scope = new ArrayList<String>();
		scope.add(Scope_Basic);
		scope.add(Scope_Netdisk);
		mAuthorization.authorize(mainActivity,
				FrontiaAuthorization.MediaType.BAIDU.toString(), scope,
				new AuthorizationListener() {

					@Override
					public void onSuccess(FrontiaUser result) {
						Frontia.setCurrentAccount(result);
						updateMessage("social id: " + result.getId() + "\n"
								+ "token: " + result.getAccessToken() + "\n"
								+ "expired: " + result.getExpiresIn());
					}

					@Override
					public void onFailure(int errCode, String errMsg) {
						updateMessage("errCode:" + errCode + ", errMsg:"
								+ errMsg);
					}

					@Override
					public void onCancel() {
						updateMessage("cancel");
					}

				});
	}

	protected void startBaiduStatus() {
		boolean result = mAuthorization
				.isAuthorizationReady(FrontiaAuthorization.MediaType.BAIDU
						.toString());
		if (result) {
			updateMessage("已登陆百度账号");
		} else {
			updateMessage("未登录百度账号");
		}
	}

	protected void startBaiduUserInfo() {
		userinfo(MediaType.BAIDU.toString());
	}

	protected void startBaiduLogout() {
		boolean result = mAuthorization
				.clearAuthorizationInfo(FrontiaAuthorization.MediaType.BAIDU
						.toString());
		if (result) {
			Frontia.setCurrentAccount(null);
			updateMessage("百度退出成功");
		} else {
			updateMessage("百度退出失败");
		}
	}

	private void userinfo(String accessToken) {
		mAuthorization.getUserInfo(accessToken, new UserInfoListener() {

			@Override
			public void onSuccess(FrontiaUser.FrontiaUserDetail result) {
				updateMessage("username:" + result.getName() + "\n"
						+ "birthday:" + result.getBirthday() + "\n" + "city:"
						+ result.getCity() + "\n" + "province:"
						+ result.getProvince() + "\n" + "sex:"
						+ result.getSex() + "\n" + "pic url:"
						+ result.getHeadUrl() + "\n");
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				updateMessage("errCode:" + errCode + ", errMsg:" + errMsg);
			}

		});
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mAuthorization) {
			mAuthorization.onActivityResult(requestCode, resultCode, data);
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private void updateMessage(String msg) {
		Message message = new Message();
		Bundle data = new Bundle();
		data.putString("result", msg);
		message.setData(data);
		mHandler.sendMessage(message);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static WAccountManager instance = null;

	private WAccountManager() {

	}

	public void init(Activity activity, Handler handler) {
		mAuthorization = Frontia.getAuthorization();
		mainActivity = activity;
		mHandler = handler;
	}

	public static WAccountManager getInstance() {
		if (instance == null) {
			instance = new WAccountManager();
		}
		return instance;
	}
}
