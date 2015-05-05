package jason.wondermap.helper;

import jason.wondermap.interfacer.GetOnlineUserListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import B.i;
import android.content.Context;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.listener.CountListener;
import cn.bmob.v3.listener.FindListener;

public class OnlineUserHelper {
	private static final int MIN_USER = 20;
	private static final int MINUTE = 60 * 1000;// 分钟
	private static final int HOUR = 60 * MINUTE;// 小时
	private Context mContext;

	public OnlineUserHelper(Context context) {
		mContext = context;
	}

	/**
	 * 获取在线用户，默认1小时内更新过的为在线，如果1小时内的人数少于20人，则显示最近更新过的20人，忽略时间
	 */
	public void getOnlineList(final GetOnlineUserListener getOnlineUserListener) {
		BmobQuery<BmobChatUser> query = new BmobQuery<BmobChatUser>();
		BmobDate date = new BmobDate(
				new Date(System.currentTimeMillis() - HOUR));
		query.addWhereGreaterThan("updatedAt", date);
		query.count(mContext, BmobChatUser.class, new CountListener() {
			@Override
			public void onSuccess(int count) {
				L.d(WModel.UpdateFriend, "查询到的用户数量为" + count);
				if (count > MIN_USER) {// 最近1小时用户
					L.d(WModel.UpdateFriend, "数量大于20，查询1小时内所有人数");
					BmobQuery<BmobChatUser> query = new BmobQuery<BmobChatUser>();
					BmobDate date = new BmobDate(new Date(System
							.currentTimeMillis() - HOUR));
					query.addWhereGreaterThanOrEqualTo("updatedAt", date);
					query.addWhereNotEqualTo("objectId", AccountUserManager.getInstance().getCurrentUserid());     //名字不等于Barbie
					query.findObjects(mContext,
							new FindListener<BmobChatUser>() {

								@Override
								public void onSuccess(List<BmobChatUser> arg0) {
									L.d(WModel.UpdateFriend,
											"查询到的用户数量为" + arg0.size());
									for (BmobChatUser bmobChatUser : arg0) {
										L.d(WModel.UpdateFriend, "用户名"
												+ bmobChatUser.getUsername());
									}
									getOnlineUserListener.onSuccess(arg0);
								}

								@Override
								public void onError(int arg0, String arg1) {

								}
							});
				} else {// 最近更新的20个用户
					L.d(WModel.UpdateFriend, "数量小于20，查询最近更新的20个人");
					BmobQuery<BmobChatUser> query = new BmobQuery<BmobChatUser>();
					query.order("-updatedAt");
					BmobDate date = new BmobDate(new Date(System
							.currentTimeMillis()));
					query.addWhereLessThanOrEqualTo("updatedAt", date);
					query.addWhereNotEqualTo("objectId", AccountUserManager.getInstance().getCurrentUserid());     //名字不等于Barbie
					query.setLimit(MIN_USER);
					query.findObjects(mContext,
							new FindListener<BmobChatUser>() {

								@Override
								public void onSuccess(List<BmobChatUser> arg0) {
									L.d(WModel.UpdateFriend,
											"查询到的用户数量为" + arg0.size());
									for (BmobChatUser bmobChatUser : arg0) {
										L.d(WModel.UpdateFriend, "用户名"
												+ bmobChatUser.getUsername());
									}
									getOnlineUserListener.onSuccess(arg0);
								}

								@Override
								public void onError(int arg0, String arg1) {

								}
							});
				}
			}

			@Override
			public void onFailure(int code, String msg) {
			}
		});

	}
}
