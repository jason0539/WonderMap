package jason.wondermap.receiver;

import jason.wondermap.MainActivity;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.ChatMessageManager;
import jason.wondermap.manager.MapUserManager;
import jason.wondermap.manager.PushMsgSendManager;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.CommonUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.UserInfo;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.config.BmobConstant;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.im.inteface.OnReceiveListener;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.listener.FindListener;

import com.bmob.utils.BmobLog;

/**
 * 推送消息接收器
 * MainBottomBar、RecentFragment和ChatFragment三个页面需要接收消息，ChatMessageManager管理消息
 * 
 * @author liuzhenhui
 * 
 */
public class MyMessageReceiver extends BroadcastReceiver {

	// 事件监听，三个监听者,有监听者则传给监听者处理，没有则给ChatMessageManager处理
	public static ArrayList<EventListener> ehList = new ArrayList<EventListener>();

	BmobChatUser currentUser;

	// 如果你想发送自定义格式的消息，请使用sendJsonMessage方法来发送Json格式的字符串，然后你按照格式自己解析并处理

	@Override
	public void onReceive(Context context, Intent intent) {
		String json = intent.getStringExtra("msg");
		L.d("收到的消息" + json);
		currentUser = AccountUserManager.getInstance().getCurrentUser();
		boolean isNetConnected = CommonUtils.isNetworkAvailable(context);
		if (isNetConnected) {
			parseMessage(context, json);
		} else {
			for (int i = 0; i < ehList.size(); i++)
				((EventListener) ehList.get(i)).onNetChange(isNetConnected);
		}
	}

	/**
	 * 推送 拦截处理地图显示的逻辑，hello－world
	 * 如果以后要考虑ios，则参考http://wenda.bmob.cn/?/question/204
	 */
	private void handleHelloMsg(JSONObject json) {
		L.d("收到hello消息，回复world消息");
		// if (!MapControler.getInstance().isVisible()) {
		// L.d("收地图不可见");
		//
		// return;
		// }
		MapUserManager.getInstance().addUserFromUserId(
				BmobJsonUtil.getString(json, UserInfo.USER_ID));// 更新地图
		// 应该加上参数，只发送给hello用户
		PushMsgSendManager.getInstance().sayWorld();
	}

	/**
	 * 推送 处理word消息
	 */
	private void handleWorldMsg(JSONObject json) {
		L.d("收到world消息");
		// if (!MapControler.getInstance().isVisible()) {
		// L.d("地图不可见");
		//
		// return;
		// }
		MapUserManager.getInstance().addUserFromUserId(
				BmobJsonUtil.getString(json, UserInfo.USER_ID));
	}

	/**
	 * 解析Json字符串
	 */
	private void parseMessage(final Context context, String json) {
		JSONObject jo;
		try {
			jo = new JSONObject(json);
			String tag = BmobJsonUtil.getString(jo, BmobConstant.PUSH_KEY_TAG);
			if (tag.equals(BmobConfig.TAG_OFFLINE)) {// 下线通知
				L.d("下线消息，在别处登录");
				if (currentUser != null) {
					// 下线直接交给消息管理处理
					ChatMessageManager.getInstance().onOffline();
				}
			} else if (tag.equals(UserInfo.HELLO)) {
				handleHelloMsg(jo);
			} else if (tag.equals(UserInfo.WORLD)) {
				handleWorldMsg(jo);
			} else {
				L.d("聊天的正常消息");
				String fromId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TARGETID);
				// 增加消息接收方的ObjectId--目的是解决多账户登陆同一设备时，无法接收到非当前登陆用户的消息。
				final String toId = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_KEY_TOID);
				String msgTime = BmobJsonUtil.getString(jo,
						BmobConstant.PUSH_READED_MSGTIME);
				if (fromId != null
						&& !BmobDB.create(context, toId).isBlackUser(fromId)) {// 该消息发送方不为黑名单用户
					if (TextUtils.isEmpty(tag)) {// 不携带tag标签--此可接收陌生人的消息
						BmobChatManager.getInstance(context).createReceiveMsg(
								json, new OnReceiveListener() {
									@Override
									public void onSuccess(BmobMsg msg) {
										if (ehList.size() > 0) {// 有监听的时候，传递下去
											for (int i = 0; i < ehList.size(); i++) {
												((EventListener) ehList.get(i))
														.onMessage(msg);
											}
										} else {// 没有监听交给消息管理处理
											if (currentUser != null
													&& currentUser
															.getObjectId()
															.equals(toId)) {// 当前登陆用户存在并且也等于接收方id
												ChatMessageManager
														.getInstance()
														.notifyMsg(msg);
											}
										}
									}

									@Override
									public void onFailure(int code, String arg1) {
										BmobLog.i("获取接收的消息失败：" + arg1);
									}
								});

					} else {// 带tag标签
						// 好友请求
						if (tag.equals(BmobConfig.TAG_ADD_CONTACT)) {
							// 保存好友请求道本地，并更新后台的未读字段
							BmobInvitation message = BmobChatManager
									.getInstance(context).saveReceiveInvite(
											json, toId);
							if (currentUser != null) {// 有登陆用户
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去
										for (EventListener handler : ehList)
											handler.onAddUser(message);
									} else {
										// 原来为显示好友请求页面，之后调整
										ChatMessageManager.getInstance()
												.showOtherNotify(
														context,
														message.getFromname(),
														toId,
														message.getFromname()
																+ "请求添加好友",
														MainActivity.class);
										// showOtherNotify(context,
										// message.getFromname(), toId,
										// message.getFromname()+"请求添加好友",
										// NewFriendActivity.class);
									}
								}
							}
						} else if (tag.equals(BmobConfig.TAG_ADD_AGREE)) {
							String username = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_KEY_TARGETUSERNAME);
							// 收到对方的同意请求之后，就得添加对方为好友--已默认添加同意方为好友，并保存到本地好友数据库
							BmobUserManager.getInstance(context)
									.addContactAfterAgree(username,
											new FindListener<BmobChatUser>() {

												@Override
												public void onError(int arg0,
														final String arg1) {

												}

												@Override
												public void onSuccess(
														List<BmobChatUser> arg0) {
													// method stub
													// 保存到内存中
													AccountUserManager
															.getInstance()
															.setContactList(
																	CollectionUtils
																			.list2map(BmobDB
																					.create(context)
																					.getContactList()));
												}
											});
							// 显示通知
							ChatMessageManager.getInstance().showOtherNotify(
									context, username, toId,
									username + "同意添加您为好友", MainActivity.class);
							// 创建一个临时验证会话--用于在会话界面形成初始会话
							BmobMsg.createAndSaveRecentAfterAgree(context, json);
						} else if (tag.equals(BmobConfig.TAG_READED)) {// 已读回执
							String conversionId = BmobJsonUtil.getString(jo,
									BmobConstant.PUSH_READED_CONVERSIONID);
							if (currentUser != null) {
								// 更改某条消息的状态
								BmobChatManager.getInstance(context)
										.updateMsgStatus(conversionId, msgTime);
								if (toId.equals(currentUser.getObjectId())) {
									if (ehList.size() > 0) {// 有监听的时候，传递下去--便于修改界面
										for (EventListener handler : ehList)
											handler.onReaded(conversionId,
													msgTime);
									}
								}
							}
						}
					}
				} else {// 在黑名单期间所有的消息都应该置为已读，不然等取消黑名单之后又可以查询的到
					BmobChatManager.getInstance(context).updateMsgReaded(true,
							fromId, msgTime);
					BmobLog.i("该消息发送方为黑名单用户");
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			// 这里截取到的有可能是web后台推送给客户端的消息，也有可能是开发者自定义发送的消息，需要开发者自行解析和处理
			BmobLog.i("parseMessage错误：" + e.getMessage());
		}
	}
}
