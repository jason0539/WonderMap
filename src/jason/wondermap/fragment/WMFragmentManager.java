package jason.wondermap.fragment;

import jason.wondermap.interfacer.IContentFragmentFactory;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class WMFragmentManager extends ContentFragmentManager implements
		IContentFragmentFactory {

	/** fragment类型常量 **/
	/* 无效值 */
	public final static int TYPE_NONE = 0x0000;
	/* 1x: 地图相关页面 */
	public final static int TYPE_MAP_HOME = 0x0011;
	/* 2x: 其他页面 */
	public final static int TYPE_RECENT = 0x0021;// 最近聊天
	public final static int TYPE_CONTACT = 0x0022;// 好友页面
	public final static int TYPE_DISCOVER = 0x0023;// 发现页面
	public final static int TYPE_CHAT = 0x0024;// 聊天界面
	public final static int TYPE_MINE = 0x0025;//
	public final static int TYPE_USERINFO = 0x0026;// 个人信息页面
	public final static int TYPE_UPDATE_USERINFO = 0x0027;// 修改个人信息页面
	public final static int TYPE_ADD_FRIEND = 0x0028;// 添加好友
	public final static int TYPE_NEW_FRIEND = 0x0029;// 好友请求
	public final static int TYPE_NEAR_PEOPLE = 0x0030;// 附近的人
	public final static int TYPE_BLACK_LIST = 0x0031;// 黑名单
	public final static int TYPE_LOCATION_MAP = 0x0032;// 地图查看页面
	public final static int TYPE_IMAGE_BROWSER = 0x0033;// 图片查看页面
	public final static int TYPE_FEEDBACK = 0x0034;// 图片查看页面
	public final static int TYPE_NEW_FOOTBLOG = 0x0035;// 发布足迹页面

	/** 前一个fragment 类型 ，即从哪个fragment跳转过来的 */
	private int mPreviousFragmentType = TYPE_NONE;

	private final static String TAG = WMFragmentManager.class.getSimpleName();

	public WMFragmentManager(FragmentActivity activity) {
		super(activity);
		setFragmentFactory(this);
	}

	@Override
	public ContentFragment createFragment(int type) {
		ContentFragment fragment = null;
		switch (type) {
		case TYPE_MAP_HOME:
			fragment = new MapHomeFragment();
			break;
		case TYPE_RECENT:
			fragment = new RecentFragment();
			break;
		case TYPE_CONTACT:
			fragment = new ContactFragment();
			break;
		case TYPE_DISCOVER:
			fragment = new DiscoveryFragment();
			break;
		case TYPE_CHAT:
			fragment = new ChatFragment();
			break;
		case TYPE_MINE:
			fragment = new MineFragment();
			break;
		case TYPE_USERINFO:
			fragment = new UserInfoFragment();
			break;
		case TYPE_UPDATE_USERINFO:
			fragment = new UpdateInfoFragment();
			break;
		case TYPE_ADD_FRIEND:
			fragment = new AddFriendFragment();
			break;
		case TYPE_NEW_FRIEND:
			fragment = new NewFriendFragment();
			break;
		case TYPE_NEAR_PEOPLE:
			fragment = new NearPeopleFragment();
			break;
		case TYPE_BLACK_LIST:
			fragment = new BlackListFragment();
			break;
		case TYPE_LOCATION_MAP:
			fragment = new LocationFragment();
			break;
		case TYPE_IMAGE_BROWSER:
			fragment = new ImageBrowserFragment();
			break;
		case TYPE_FEEDBACK:
			fragment = new FeedbackFragment();
			break;
		case TYPE_NEW_FOOTBLOG:
			fragment = new PublishFootblogFragment();
			break;
		}
		return fragment;
	}

	@Override
	public String toString(int type) {
		String str = null;
		switch (type) {
		/* 1x: 地图相关页面 */
		case TYPE_MAP_HOME:
			str = "TYPE_BROWSE_MAP";
			break;

		/* 无效值 */
		default:
			str = "TYPE_NONE";
			break;
		}

		return str;
	}

	public int getCurrentFragmentType() {
		if (null != mCurrentFragmentInfo
				&& null != mCurrentFragmentInfo.mFragment) {
			return mCurrentFragmentInfo.mFragment.getType();
		}
		return TYPE_NONE;
	}

	/** 获取前一个fragment类型，即从哪个fragment跳转过来的 */
	public int getPreviousFragmentType() {
		return mPreviousFragmentType;
	}

	/**
	 * 回退到type类型的fragment
	 * 
	 * @param fragmentType
	 *            fragment类型
	 */
	public void backTo(int type, Bundle bundle) {
		while (mFragmentInfoStack.size() > 0) {
			if (mFragmentInfoStack.get(mFragmentInfoStack.size() - 1).mType == type) {
				back(bundle);
				break;
			} else {
				removeFragmentFromStack(mFragmentInfoStack.size() - 1);
			}
		}
	}

	/**
	 * 清理HOME页之后压栈的所有fragment，仅用于导航过程页(真实导航)启动前调用。
	 * 
	 * @author hechaoyang
	 */
	public void removeFragmentTo(int type) {
		int index = getIndexFromLast(type);
		if (index >= 0) {
			int stackSize = mFragmentInfoStack.size();
			for (int i = stackSize - 1; i > index; i--) {
				removeFragmentFromStack(i);
			}
		}
	}

	public void showFragment(int type) {
		showFragment(type, null);
	}

	@Override
	public void showFragment(int type, Bundle bundle) {
		super.showFragment(type, bundle);

		// if (type == TYPE_STREETSCAPE) { // 周边检索页面截流
		// int index = getIndexFromLast(type);
		// if (index >= 0) {
		// int stackSize = mFragmentInfoStack.size();
		// for (int i = stackSize - 1; i >= index; i--) {
		// removeFragmentFromStack(i);
		// }
		// }
		// } else if (type == TYPE_ROUTE_GUIDE) {
		// /*
		// * 诱导过程页截流： 场景A：HOME -> RoutePlan(A-B) -> RouteDetail(A-B) ->
		// * RouteGuide(A-B) -> RoutePlan(A-C) -> RouteDetail(A-C) ->
		// * RouteGuide(A-C) 在上述跳转路径下，最后从RouteGuide(A-C)返回应该回到RouteDetail(A-C)
		// * 需要把历史栈中的RoutePlan(A-B),RouteDetail(A-B), RouteGuide(A-B)清理掉;
		// *
		// * 场景B：HOME -> QuickPlan(A-B) -> RouteDetail(A-B) -> RouteGuide(A-B)
		// * -> RoutePlan(A-C) -> RouteDetail(A-C) -> RouteGuide(A-C)
		// * 在上述跳转路径下，最后从RouteGuide(A-C)返回应该回到RouteDetail(A-C)
		// * 需要把历史栈中的QuickPlan(A-B),RouteDetail(A-B), RouteGuide(A-B)清理掉;
		// *
		// * 场景C：HOME -> QuickPlan(A-B) -> NameSearch(A-B) ->
		// * SearchResult(A-B) -> RouteDetail(A-B) -> RouteGuide(A-B) ->
		// * RoutePlan(A-C) -> RouteDetail(A-C) -> RouteGuide(A-C)
		// * 在上述跳转路径下，最后从RouteGuide(A-C)返回应该回到RouteDetail(A-C)
		// * 需要把历史栈中的QuickPlan(A-B) -> NameSearch(A-B) -> SearchResult(A-B) ->
		// * RouteDetail(A-B)-> RouteGuide(A-B)清理掉;
		// *
		// * 其他类似场景还包括POI详情页进入到RouteDetail.
		// *
		// * 综上，发起导航后，发现之前栈中存在RouteGuide，统一将HOME与RouteGuide(A-B)[包含RouteGuide(A
		// * -B)]之间的fragment清理掉。
		// *
		// * @author hechaoyang
		// */
		// int index_home = getIndexFromLast(TYPE_HOME);
		// int index_routeguide = getIndexFromLast(TYPE_ROUTE_GUIDE);
		// if (index_home >= 0 && index_routeguide >= 0
		// && index_routeguide > index_home) {
		//
		// for (int i = index_routeguide; i > index_home; i--) {
		// removeFragmentFromStack(i);
		// }
		//
		// }
		// }
	}

	/**
	 * 判断type类型的fragment是否MapContentFragment
	 * 
	 * @param type
	 *            fragment类型
	 * @return 是否MapContentFragment
	 */
	public boolean isMapContent(int type) {
		boolean isMapContentFragment = false;
		switch (type) {
		case TYPE_MAP_HOME:
			
			isMapContentFragment = true;
			break;
		case TYPE_CHAT:

		default:
			break;
		}

		return isMapContentFragment;
	}

	/**
	 * 从后往前，获取第一个类型是type的fragment在栈中的下标
	 * 
	 * @param type
	 *            fragment类型
	 * @return 下标
	 */
	private int getIndexFromLast(int type) {
		int i = mFragmentInfoStack.size() - 1;
		for (; i >= 0; i--) {
			if (mFragmentInfoStack.get(i).mType == type) {
				break;
			}
		}

		return i;
	}
}
