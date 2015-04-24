package jason.wondermap.sns;

import jason.wondermap.bean.User;
import jason.wondermap.config.WMapConfig;
import jason.wondermap.interfacer.QQLoginListener;
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
public class TencentLoginHelper {
	private Context mContext;
	private QQLoginListener listener;
	private String access_token = "";
	private String openid = "";

	public TencentLoginHelper(Context context) {
		mContext = context;
	}

	public void login(QQLoginListener qqLoginListener) {
		listener = qqLoginListener;
		BmobUser.qqLogin(mContext, WMapConfig.qqAppId,
				new OtherLoginListener() {

					@Override
					public void onSuccess(JSONObject userAuth) {
						L.d(WModel.ThirdPlateLogin,
								"QQ登陆成功返回:" + userAuth.toString());
						// 下面则是返回的json字符
						// {
						// "qq": {
						// "openid": "B4F5ABAD717CCC93ABF3BF28D4BCB03A",
						// "access_token": "05636ED97BAB7F173CB237BA143AF7C9",
						// "expires_in": 7776000
						// }
						// }
						// 如果你想在登陆成功之后关联当前用户
						if (AccountUserManager.getInstance().getCurrentUser()
								.isInfoIsSet()) {
							// 信息确认过则无需获取用户信息,直接返回就行了
							listener.onSuccess();
							return;
						}
						String string = BmobJsonUtil.getString(userAuth, "qq");
						L.d(WModel.ThirdPlateLogin, "解析到的qq字符串是" + string);
						try {
							JSONObject jsonObject = new JSONObject(string);
							access_token = BmobJsonUtil.getString(jsonObject,
									"access_token");
							openid = BmobJsonUtil.getString(jsonObject,
									"openid");
						} catch (JSONException e) {
							e.printStackTrace();
							listener.onFail("QQ登陆失败，登陆返回的json字符串解析错误");
							return;
						}
						getQQInfo();
					}

					@Override
					public void onFailure(int code, String msg) {
						listener.onFail("QQ登陆失败：" + msg);
					}

					@Override
					public void onCancel() {
						listener.onFail("取消QQ登陆");
					}
				});
	}

	public void getQQInfo() {
		// 若更换为自己的APPID后，仍然获取不到自己的用户信息，则需要
		// 根据http://wiki.connect.qq.com/get_user_info提供的API文档，想要获取QQ用户的信息，则需要自己调用接口，传入对应的参数
		new Thread() {
			@Override
			public void run() {
				Map<String, String> params = new HashMap<String, String>();
				params.put("access_token", access_token);// 此为QQ登陆成功之后返回access_token
				params.put("openid", openid);
				params.put("oauth_consumer_key", WMapConfig.qqAppId);// oauth_consumer_key为申请QQ登录成功后，分配给应用的appid
				params.put("format", "json");// 格式--非必填项
				String result = HttpUtils.getRequest(
						"https://graph.qq.com/user/get_user_info", params);
				L.d(WModel.ThirdPlateLogin, result);
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(result);
				} catch (JSONException e) {
					e.printStackTrace();
					// 出现异常，弹toast
					listener.onFail("获取QQ用户信息出现问题");
				}
				if (jsonObject == null) {
					// 获取的信息异常，弹toast
					listener.onFail("没有获取到QQ用户信息");
				} else {
					User u = AccountUserManager.getInstance().getCurrentUser();
					u.setUsername(getQQUsername(jsonObject));
					u.setSex(getQQSex(jsonObject));
					u.setAge(getQQAge(jsonObject));
					u.setAvatar(getQQFigureurl(jsonObject));
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
					L.d(WModel.ThirdPlateLogin, "解析到的openid 是" + openid);
				}
			}
		}.start();
	}

	private String getQQFigureurl(JSONObject jsonObject) {
		// 头像100x100的不一定会有，但40x40的一定有，先设定100的，如果100的为空，再用40的
		String head40 = BmobJsonUtil.getString(jsonObject, "figureurl_qq_1");
		String head100 = BmobJsonUtil.getString(jsonObject, "figureurl_qq_2");
		String headString = head100;
		if (head100 == null || head100.equals("")) {
			headString = head40;
		}
		return headString;
	}

	private int getQQAge(JSONObject jsonObject) {
		String age = BmobJsonUtil.getString(jsonObject, "year");
		return TimeUtil.getAgeFromYear(age);
	}

	private String getQQUsername(JSONObject jsonObject) {
		String name = BmobJsonUtil.getString(jsonObject, "nickname");
		if (name == null || name.equals("")) {
			Random random = new Random();
			name = random.nextDouble()
					+ AccountUserManager.getInstance().getCurrentUserName();
		}
		return name;
	}

	/**
	 * @param jsonObject
	 * @return
	 */
	private boolean getQQSex(JSONObject jsonObject) {
		String sex = BmobJsonUtil.getString(jsonObject, "gender");
		if (("女").equals(sex)) {// 除了为女，其他所有情况都默认为男性
			return false;
		}
		return true;
	}
}
