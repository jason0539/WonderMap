package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.interfacer.OnAddFriendListener;
import jason.wondermap.interfacer.OnCreateRoleListener;
import jason.wondermap.interfacer.OnDeleFriendListener;
import jason.wondermap.interfacer.OnFindUserListener;
import jason.wondermap.interfacer.OnGetAllFriendListener;
import jason.wondermap.utils.AccountInfo;
import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.WModel;

import java.util.List;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaAccount;
import com.baidu.frontia.FrontiaData;
import com.baidu.frontia.FrontiaQuery;
import com.baidu.frontia.FrontiaRole;
import com.baidu.frontia.FrontiaRole.DescribeRoleListener;
import com.baidu.frontia.FrontiaUser;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener;
import com.baidu.frontia.api.FrontiaStorageListener.DataInfoListener;

public class FriendAndGroupManager {
	private FrontiaRole mRole;
	private FrontiaStorage mCloudStorage;
	private SharePreferenceUtil spUtil;
	private String mSocialId;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 以绑定的社交账户socialId为id创建自己的账户，以Role为账户，role下的user为好友
	 */
	public void createRole(OnCreateRoleListener roleListener) {
		onCreateMyRole(roleListener);
	}

	/**
	 * 查找一个用户,返回true存在，false不存在
	 */
	public void findUser(String socialId,
			OnFindUserListener findUserResultListener) {
		onFindUser(socialId, findUserResultListener);
	}

	/**
	 * 添加一个好友
	 */
	public void addFriend() {

	}

	/**
	 * 删除一个好友
	 */
	public void deleteFriend() {

	}

	/**
	 * 获取所有好友列表
	 */
	public void getAllFriend() {

	}

	/**
	 * 更新一个好友的信息
	 */
	public void updateFriend() {

	}

	/**
	 * 查找某个好友
	 */
	public void findFriend() {

	}

	/**
	 * 查找某个群组
	 */
	public void findGroup() {

	}

	/**
	 * 加入某个群组
	 */
	public void joinGroup() {

	}

	/**
	 * 退出某个群组
	 */
	public void exitGroup() {

	}

	/**
	 * 创建某个群组
	 */
	public void createGroup() {

	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝内部实现代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 创建自己的账户
	 * 
	 * @param onRoleCreateListener
	 */
	private void onCreateMyRole(final OnCreateRoleListener onRoleCreateListener) {

		mRole = new FrontiaRole(mSocialId);

		mRole.describe(new FrontiaRole.DescribeRoleListener() {
			@Override
			public void onSuccess(FrontiaRole role) {
				StringBuilder buf = new StringBuilder();
				buf.append(role.getId()).append("\n");
				// List<FrontiaAccount> accounts = role.getMembers();
				// for (FrontiaAccount account : accounts) {
				// buf.append("    ").append(account.getId()).append("\n");
				// }
				L.d(WModel.CreateRole, "角色已经存在");
				onRoleCreateListener.onSuccess();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				L.d(WModel.CreateRole, "查找失败" + errCode + ":" + errMsg
						+ "开始创建账号");
				mRole = new FrontiaRole(mSocialId);
				mRole.create(new FrontiaRole.CommonOperationListener() {

					@Override
					public void onSuccess() {
						L.d(WModel.CreateRole, "角色创建成功");
						// 储存个人用户信息，用于之后查找用户时加载用户信息
						FrontiaData data = new FrontiaData();
						data.put(AccountInfo.SOCIAL_ID, mSocialId);
						data.put(AccountInfo.USER_ID, spUtil.getUserId());
						data.put(AccountInfo.USER_NICK, spUtil.getUserNick());
						data.put(AccountInfo.USER_SEX, spUtil.getUserSex());
						data.put(AccountInfo.USER_HEADPIC_URL,
								spUtil.getUserHeadPicUrl());
						data.put(AccountInfo.USER_BIRTHDAY,
								spUtil.getUserBirthdayl());
						data.put(AccountInfo.USER_CITY, spUtil.getUserCity());
						data.put(AccountInfo.USER_PROVINCE,
								spUtil.getUserProvince());
						mCloudStorage
								.insertData(
										data,
										new FrontiaStorageListener.DataInsertListener() {

											@Override
											public void onSuccess() {
												// 信息存储成功
												onRoleCreateListener
														.onSuccess();
											}

											@Override
											public void onFailure(int errCode,
													String errMsg) {
												// 信息存储失败
												onRoleCreateListener.onFail(
														errCode, errMsg);
											}

										});

					}

					@Override
					public void onFailure(int errCode, String errMsg) {
						L.d(WModel.CreateRole, "角色创建失败：" + errCode + ":"
								+ errMsg);
						onRoleCreateListener.onFail(errCode, errMsg);
					}
				});
			}
		});
	}

	/**
	 * 查找一个用户
	 * 
	 * @param socialId
	 *            目标用户的id
	 * @param findUserResultListener
	 */
	private void onFindUser(final String socialId,
			final OnFindUserListener findUserResultListener) {
		FrontiaRole frontiaRole = new FrontiaRole(socialId);
		frontiaRole.describe(new DescribeRoleListener() {

			@Override
			public void onSuccess(FrontiaRole role) {
				FrontiaQuery query = new FrontiaQuery();
				query.equals(AccountInfo.SOCIAL_ID, socialId);// 用户存在，则查找该用户信息

				mCloudStorage.findData(query, new DataInfoListener() {

					@Override
					public void onSuccess(List<FrontiaData> dataList) {
						// 用户信息查找成功
						for (int i = 0; i < dataList.size(); i++) {
							// dataList.get(i).get(acc)
							// 直接返回user
						}
					}

					@Override
					public void onFailure(int errCode, String errMsg) {
						// 用户信息查找失败
						findUserResultListener.onFail(errCode, errMsg);
					}
				});
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				findUserResultListener.onFail(errCode, errMsg);
			}
		});
	}

	/**
	 * 添加一个好友
	 * 
	 * @param socialId
	 *            目标用户id
	 * @param addFriendListener
	 */
	private void onAddFriend(String socialId,
			final OnAddFriendListener addFriendListener) {
		if (mRole == null) {
			mRole = new FrontiaRole(mSocialId);// 我的role，用来存储好友
		}

		mRole.addMember(new FrontiaUser(socialId));
		mRole.update(new FrontiaRole.CommonOperationListener() {
			@Override
			public void onSuccess() {
				addFriendListener.onSuccess();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				addFriendListener.onFail(errCode, errMsg);
			}
		});
	}

	/**
	 * 删除一个好友
	 * 
	 * @param socialId
	 *            目标用户id
	 */
	private void onDeleteFriend(String socialId,
			final OnDeleFriendListener deleFriendListener) {
		if (mRole == null) {
			mRole = new FrontiaRole(mSocialId);
		}

		mRole.addMember(new FrontiaUser(socialId));
		mRole.update(new FrontiaRole.CommonOperationListener() {
			@Override
			public void onSuccess() {
				deleFriendListener.onSuccess();
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				deleFriendListener.onFail(errCode, errMsg);
			}
		});
	}

	/**
	 * 获取所有好友列表
	 */
	private void onGetAllFriend(final OnGetAllFriendListener allFriendListener) {
		if (null == mRole) {
			mRole = new FrontiaRole(mSocialId);
		}

		mRole.describe(new FrontiaRole.DescribeRoleListener() {
			@Override
			public void onSuccess(FrontiaRole role) {

				List<FrontiaAccount> accounts = role.getMembers();
				for (FrontiaAccount account : accounts) {
					allFriendListener.onSuccess();
				}
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				allFriendListener.onFail();
			}
		});

	}

	/**
	 * 更新一个好友的信息
	 */
	private void onUpdateFriend() {

	}

	/**
	 * 查找某个好友
	 */
	private void onFindFriend() {

	}

	/**
	 * 查找某个群组
	 */
	private void onFindGroup() {

	}

	/**
	 * 加入某个群组
	 */
	private void onJoinGroup() {

	}

	/**
	 * 退出某个群组
	 */
	private void onExitGroup() {

	}

	/**
	 * 创建某个群组
	 */
	private void onCreateGroup() {

	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static FriendAndGroupManager instance = null;

	private FriendAndGroupManager() {
		spUtil = WonderMapApplication.getInstance().getSpUtil();
		mSocialId = spUtil.getSocialId();
		mCloudStorage = Frontia.getStorage();
	}

	public static FriendAndGroupManager getInstance() {
		if (instance == null) {
			instance = new FriendAndGroupManager();
		}
		return instance;
	}
}
