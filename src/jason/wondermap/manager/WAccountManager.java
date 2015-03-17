package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.StaticConstant;

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
	/**
	 * 绑定账号后获取到的信息存储帮助
	 */
	private SharePreferenceUtil spUtil;

	/**
	 * 登陆百度账号，调用前需要调用init方法用来接收登陆消息
	 */
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
						// 存储绑定社交账号后的id和token
						spUtil.setSocialId(result.getId());
						spUtil.setAccessToken(result.getAccessToken());
						spUtil.setExpiresIn(result.getExpiresIn());
						updateMessage(
								StaticConstant.AccountBindSuccess,
								"social id: " + result.getId() + "\n"
										+ "token: " + result.getAccessToken()
										+ "\n" + "expired: "
										+ result.getExpiresIn());
						startBaiduUserInfo();
					}

					@Override
					public void onFailure(int errCode, String errMsg) {
						updateMessage(StaticConstant.AccountBindFail,
								"errCode:" + errCode + ", errMsg:" + errMsg);
					}

					@Override
					public void onCancel() {
						updateMessage(StaticConstant.AccountBindCancle,
								"cancel");
					}

				});
	}

	protected void startBaiduUserInfo() {
		userinfo(MediaType.BAIDU.toString());
	}

	private void userinfo(String accessToken) {
		mAuthorization.getUserInfo(accessToken, new UserInfoListener() {

			@Override
			public void onSuccess(FrontiaUser.FrontiaUserDetail userDetail) {
				spUtil.setUserNick(userDetail.getName());
				spUtil.setUserSex(userDetail.getSex().intValue());
				spUtil.setUserBirthday(userDetail.getBirthday());
				spUtil.setUserProvince(userDetail.getProvince());
				spUtil.setUserCity(userDetail.getCity());
				spUtil.setUserHeadPicUrl(userDetail.getHeadUrl());
				updateMessage(StaticConstant.UserInfoGetSuccess,
						"username:" + userDetail.getName() + "\n" + "birthday:"
								+ userDetail.getBirthday() + "\n" + "city:"
								+ userDetail.getCity() + "\n" + "province:"
								+ userDetail.getProvince() + "\n" + "sex:"
								+ userDetail.getSex() + "\n" + "pic url:"
								+ userDetail.getHeadUrl() + "\n");
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				updateMessage(StaticConstant.UserInfoGetFail, "errCode:"
						+ errCode + ", errMsg:" + errMsg);
			}

		});
	}

	/**
	 * 微博登陆等的回调，在activity的onActivityResult中调用，暂时用不到
	 * 
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 */
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (null != mAuthorization) {
			mAuthorization.onActivityResult(requestCode, resultCode, data);
		}
	}

	/**
	 * 查询是否登陆百度账号，true则登陆
	 */
	public boolean startBaiduStatus() {
		return mAuthorization
				.isAuthorizationReady(FrontiaAuthorization.MediaType.BAIDU
						.toString());
	}

	/**
	 * 退出登陆
	 * 
	 * @return true则退出成功，否则失败
	 */
	public void startBaiduLogout() {
		mAuthorization.clearAllAuthorizationInfos();
		Frontia.setCurrentAccount(null);
		spUtil.logout();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private void updateMessage(int result, String msg) {
		Message message = new Message();
		message.what = result;
		Bundle data = new Bundle();
		data.putString("result", msg);
		message.setData(data);
		mHandler.sendMessage(message);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static WAccountManager instance = null;

	private WAccountManager() {
		mAuthorization = Frontia.getAuthorization();
		spUtil = WonderMapApplication.getInstance().getSpUtil();
	}

	/**
	 * 在LoginActivity中需要先调用本方法再调用
	 * 
	 * @param activity
	 * @param handler
	 */
	public void init(Activity activity, Handler handler) {
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
