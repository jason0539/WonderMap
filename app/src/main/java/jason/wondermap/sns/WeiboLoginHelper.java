package jason.wondermap.sns;

import jason.wondermap.bean.User;
import jason.wondermap.config.WMapConfig;
import jason.wondermap.interfacer.LoginListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.HttpUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.TimeUtil;
import jason.wondermap.utils.WModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.OtherLoginListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * QQ登陆助手
 * 
 * @author liuzhenhui
 * 
 */
public class WeiboLoginHelper {
	private Context mContext;
	private LoginListener listener;
	private String access_token = "";
	private String uid = "";

	public WeiboLoginHelper(Context context) {
		mContext = context;
	}

	public void login(LoginListener weiboLoginListener) {
		listener = weiboLoginListener;
		BmobUser.weiboLogin(mContext, WMapConfig.weiboAppId,
				"http://huodianditu.bmob.cn", new OtherLoginListener() {

					@Override
					public void onSuccess(JSONObject userAuth) {
						L.d(WModel.ThirdPlateLogin,
								"微博登陆成功返回:" + userAuth.toString());
						// {
						// "weibo": {
						// "uid": "2696876973",
						// "access_token":
						// "2.00htoVwCV9DWcB02e14b7fa50vUwjg",
						// "expires_in": 1410461999162
						// }
						// }
						if (AccountUserManager.getInstance().getCurrentUser()
								.isInfoIsSet()) {
							// 信息确认过则无需获取用户信息,直接返回就行了
							listener.onSuccess();
							return;
						}
						String string = BmobJsonUtil.getString(userAuth,
								"weibo");
						L.d(WModel.ThirdPlateLogin, "解析到的weibo字符串是" + string);
						try {
							JSONObject jsonObject = new JSONObject(string);
							access_token = BmobJsonUtil.getString(jsonObject,
									"access_token");
							uid = BmobJsonUtil.getString(jsonObject, "uid");
						} catch (JSONException e) {
							e.printStackTrace();
							listener.onFail("微博登陆失败，登陆返回的json字符串解析错误");
							return;
						}
						getQQInfo();
					}

					@Override
					public void onFailure(int code, String msg) {
						listener.onFail("微博登陆失败：" + msg);
					}

					@Override
					public void onCancel() {
						listener.onFail("取消微博登陆");
					}
				});
	}

	public void getQQInfo() {
		// 根据http://open.weibo.com/wiki/2/users/show提供的API文档
		new Thread() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("access_token", access_token);// 此为QQ登陆成功之后返回access_token
				params.put("uid", uid);
				String result = HttpUtils.getRequest(
						"https://api.weibo.com/2/users/show.json", params);
				L.d(WModel.ThirdPlateLogin, result);
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
					// 出现异常，弹toast
					listener.onFail("获取微博用户信息出现问题");
				}
				if (jsonObject == null) {
					// 获取的信息异常，弹toast
					listener.onFail("没有获取到微博用户信息");
				} else {
					User u = AccountUserManager.getInstance().getCurrentUser();
					u.setUsername(getWeiboUsername(jsonObject));
					u.setSex(getWeiboSex(jsonObject));
					u.setAge(getWeiboAge(jsonObject));
					u.setAvatar(getWeiboFigureurl(jsonObject));
					u.setSignature(getWeiboSignature(jsonObject));
					u.update(mContext, new UpdateListener() {

						@Override
						public void onSuccess() {
							listener.onSuccess();
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							listener.onSuccess();
						}
					});
					L.d(WModel.ThirdPlateLogin, "解析到的access_token 是"
							+ access_token);
					L.d(WModel.ThirdPlateLogin, "解析到的uid 是" + uid);
				}
			}
		}.start();
	}

	protected String getWeiboSignature(JSONObject jsonObject) {
		String sig = BmobJsonUtil.getString(jsonObject, "description");
		if (sig == null || sig.equals("")) {
			sig = "这个家伙很懒，什么也没说";
		}
		return sig;
	}

	private String getWeiboFigureurl(JSONObject jsonObject) {
		String head50 = BmobJsonUtil.getString(jsonObject, "profile_image_url");
		return head50;
	}

	/**
	 * 微博没有年龄，先默认20
	 */
	private int getWeiboAge(JSONObject jsonObject) {
		// String age = BmobJsonUtil.getString(jsonObject, "year");
		// return TimeUtil.getAgeFromYear(age);
		return 20;
	}

	/**
	 * 获取昵称
	 */
	private String getWeiboUsername(JSONObject jsonObject) {
		String name = BmobJsonUtil.getString(jsonObject, "screen_name");
		if (name == null || name.equals("")) {
			Random random = new Random();
			name = random.nextDouble()
					+ AccountUserManager.getInstance().getCurrentUserName();
		}
		return name;
	}

	/**
	 * 获取性别，微博有未知性别，暂统一与qq，非女即男
	 */
	private boolean getWeiboSex(JSONObject jsonObject) {
		String sex = BmobJsonUtil.getString(jsonObject, "gender");
		if (("f").equals(sex)) {// 除了为女，其他所有情况都默认为男性
			return false;
		}
		return true;
	}
}
