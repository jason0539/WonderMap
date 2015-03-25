package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;
import jason.wondermap.utils.WModel;

import com.baidu.frontia.FrontiaRole;

public class FriendAndGroupManager {
	private FrontiaRole mRole;
	private SharePreferenceUtil spUtil;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 以绑定的社交账户socialId为id创建自己的账户，以Role为账户，role下的user为好友
	 */
	public void createRole() {
		onCreateRole();
	}

	/**
	 * 查找一个用户
	 */
	public void findUser() {

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
	private void onCreateRole() {
		final String socialId = spUtil.getSocialId();

		mRole = new FrontiaRole(socialId);

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
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				// mResultTextView.setText("errCode:" + errCode
				// + ", errMsg:" + errMsg);
				mRole = new FrontiaRole(socialId);
				mRole.create(new FrontiaRole.CommonOperationListener() {

					@Override
					public void onSuccess() {
						L.d(WModel.CreateRole, "角色创建成功");
					}

					@Override
					public void onFailure(int errCode, String errMsg) {
						L.d(WModel.CreateRole, "角色创建失败：" + errCode + ":"
								+ errMsg);
					}
				});
			}
		});
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static FriendAndGroupManager instance = null;

	private FriendAndGroupManager() {
		spUtil = WonderMapApplication.getInstance().getSpUtil();
	}

	public static FriendAndGroupManager getInstance() {
		if (instance == null) {
			instance = new FriendAndGroupManager();
		}
		return instance;
	}
}
