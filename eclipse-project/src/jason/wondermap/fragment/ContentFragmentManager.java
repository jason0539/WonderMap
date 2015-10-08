package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.interfacer.IContentFragmentFactory;
import jason.wondermap.utils.L;

import java.util.ArrayList;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class ContentFragmentManager {
	public final static String KEY_BACK_BUNDLE = "back_bundle"; // 用于标识bundle是回退时传入的
	public final static String KEY_SHOW_BUNDLE = "show_bundle"; // 用于标识bunlde是显示时传入的
	public final static String KEY_FRAGMENT_TYPE = "key_fragment_type";// 用于在传入fragment参数bundle里保存type值
	private final static String TAG = ContentFragmentManager.class
			.getSimpleName();

	protected FragmentManager mFragmentManager; // fragment管理器
	protected ArrayList<FragmentInfo> mFragmentInfoStack; // fragment管理栈
	protected FragmentInfo mCurrentFragmentInfo; // 当前显示的fragment信息
	protected IContentFragmentFactory mContentFragmentFactory; // fragment工厂

	public ContentFragmentManager(FragmentActivity activity) {
		mFragmentManager = activity.getSupportFragmentManager();
		mFragmentInfoStack = new ArrayList<FragmentInfo>();
		mCurrentFragmentInfo = null;

	}

	/**
	 * 设置fragment工厂
	 * 
	 * @param contentFragmentFactory
	 *            fragment工厂
	 */
	public void setFragmentFactory(
			IContentFragmentFactory contentFragmentFactory) {
		mContentFragmentFactory = contentFragmentFactory;
	}

	/**
	 * 显示类型为type的fragment，并根据saveLastFragment把前一个显示的fragment入栈与否
	 * 
	 * @param type
	 *            fragment类型
	 * @param saveLastFragment
	 *            上一次显示的fragment是否入栈
	 */
	public void showFragment(int type, Bundle bundle) {
		ContentFragment fragment = null;
		if (mContentFragmentFactory != null)
			fragment = mContentFragmentFactory.createFragment(type);

		if (fragment != null) {
			/* 同一个fragment不可重复setArguments */
			/* 注意fragment可以不是新的fragment */
			Bundle parentBundle = fragment.getArguments();
			if (parentBundle == null) {
				parentBundle = new Bundle();
				parentBundle.putInt(KEY_FRAGMENT_TYPE, type);// 此时注入type值，系统自动创建时刻恢复
				fragment.setArguments(parentBundle);
			}

			if (bundle != null)// 注入要传递的参数
				parentBundle.putBundle(KEY_SHOW_BUNDLE, bundle);
		}

		if (mCurrentFragmentInfo != null)
			push(mCurrentFragmentInfo);
		replaceFragment(fragment, type, false);

		mCurrentFragmentInfo = new FragmentInfo(fragment, type);
		if (mCurrentFragmentInfo.mFragment != null)
			mCurrentFragmentInfo.mFragment.requestInitView();
	}

	/**
	 * carlife移植过来，获取栈中已有类型的fragment
	 * 
	 * @param type
	 * @return
	 */
	private ContentFragment getFragment(int type) {
		ContentFragment fragment = null;
		// Log.d("ouyang", "-------------getFragment1------type:" + type);
		int index = 0;
		for (FragmentInfo fragmentInfo : mFragmentInfoStack) {
			if (fragmentInfo.mType == type) {
				fragment = mFragmentInfoStack.remove(index).mFragment;
				Log.d("ouyang", "-------------getFragment-OK-isExist----");
				break;
			}
			index++;
		}

		if (null == fragment && mContentFragmentFactory != null) {
			fragment = mContentFragmentFactory.createFragment(type);
			Log.d("ouyang", "-------------getFragment-OK--create---");
		}
		fragment.setType(type);
		return fragment;
	}

	/**
	 * 回退一个fragment
	 */
	public void back(Bundle bundle) {
		FragmentInfo fragmentInfo = pop();
		if (fragmentInfo == null)
			return;

		if (fragmentInfo.mFragment != null) {
			/* 同一个fragment不可重复setArguments */
			Bundle parentBundle = fragmentInfo.mFragment.getArguments();
			if (parentBundle != null) {
				if (bundle != null)
					parentBundle.putBundle(KEY_BACK_BUNDLE, bundle);
				else
					parentBundle.remove(KEY_BACK_BUNDLE);
			}
		}

		replaceFragment(fragmentInfo.mFragment, fragmentInfo.mType, true);
		mCurrentFragmentInfo = fragmentInfo;

	}

	/**
	 * 获取当前显示的fragment
	 * 
	 * @return 返回fragment
	 */
	public ContentFragment getCurrentFragment() {
		if (mCurrentFragmentInfo == null)
			return null;

		return mCurrentFragmentInfo.mFragment;
	}

	/**
	 * 获取当前显示的fragment类型
	 * 
	 * @return 返回fragment类型
	 */
	public int getCurrentFragmentType() {
		if (mCurrentFragmentInfo == null)
			return 0;

		return mCurrentFragmentInfo.mType;
	}

	/**
	 * 获取fragment栈的大小
	 * 
	 * @return 返回fragment栈大小
	 */
	public int getFragmentStackSize() {
		return mFragmentInfoStack.size();
	}

	/**
	 * 获取栈中的第index个fragment
	 * 
	 * @param index
	 *            指定栈中的fragment下标
	 * @return 返回fragment
	 */
	public ContentFragment getFragmentInStack(int index) {
		if (index >= mFragmentInfoStack.size())
			return null;

		return mFragmentInfoStack.get(index).mFragment;
	}

	/**
	 * 获取栈中的第index个fragment类型
	 * 
	 * @param index
	 *            指定栈中的fragment下标
	 * @return 返回fragment类型
	 */
	public int getFragmentTypeInStack(int index) {
		if (index >= mFragmentInfoStack.size())
			return -1;

		return mFragmentInfoStack.get(index).mType;
	}

	/***
	 * 从Fragment栈中找到指定type的Fragment，返回其在栈中的Index
	 * 
	 * @param type
	 * @return
	 */
	public int findFragmentIndexInStack(int type) {
		for (int i = 0; i < mFragmentInfoStack.size(); i++) {
			if (mFragmentInfoStack.get(i).mType == type) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * 从栈中移除第index个fragment
	 * 
	 * @param index
	 *            指定栈中的fragment下标
	 * @return 返回fragment类型
	 */
	protected int removeFragmentFromStack(int index) {
		if (index >= mFragmentInfoStack.size())
			return -1;

		FragmentInfo fragmentInfo = mFragmentInfoStack.remove(index);
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		ft.remove(fragmentInfo.mFragment);
		ft.commitAllowingStateLoss();

		return fragmentInfo.mType;
	}

	/**
	 * 从页面栈中去掉所有指定类型的页面
	 * 
	 * @param fragmentType
	 */
	public void removeAllFragmentByType(int fragmentType) {
		int index = findFragmentIndexInStack(fragmentType);
		while (index >= 0) {
			removeFragmentFromStack(index);
			index = findFragmentIndexInStack(fragmentType);
		}

	}

	/**
	 * fragment入栈
	 * 
	 * @param fragment
	 *            需要入栈的fragment
	 */
	protected void push(FragmentInfo fragmentInfo) {
		if (fragmentInfo == null)
			return;

		mFragmentInfoStack.add(fragmentInfo);
		logFragmentStack();
	}

	/**
	 * fragment出栈
	 * 
	 * @return 返回出栈的fragment
	 */
	protected FragmentInfo pop() {
		int size = mFragmentInfoStack.size();
		if (size <= 0)
			return null;

		FragmentInfo fragmentInfo = mFragmentInfoStack.remove(size - 1);
		logFragmentStack();

		return fragmentInfo;
	}

	/**
	 * 替换fragment
	 * 
	 * @param fragment
	 *            新的fragment
	 */
	protected void replaceFragment(ContentFragment fragment, int type,
			boolean back) {
		// 允许空fragment的替换
		FragmentTransaction ft = mFragmentManager.beginTransaction();
		int lastFragmentType = 0;
		int nextFragmentType = type;
		long lastDuration = 0;
		ContentFragment currFragment = null;
		if (mCurrentFragmentInfo != null
				&& mCurrentFragmentInfo.mFragment != null) {
			lastFragmentType = mCurrentFragmentInfo.mType;
			lastDuration = mCurrentFragmentInfo.mFragment
					.getAnimationOutDuration(type, back);
			currFragment = mCurrentFragmentInfo.mFragment;
		}

		int transit = (int) lastDuration;
		if (back) {
			transit |= 0x00008000;
		}
		ft.setTransition(transit); // trick：利用transit代表旧页面动画时长
		ft.setCustomAnimations(lastFragmentType, nextFragmentType); // trick：利用nextAnim代表fragment类型
		if (back) {
			// old: onPause->onStop->onDestroyView(1)->(2)onDestroy->onDetach
			// new: (1)onCreateView->onActivityCreated->onStart->onResume(2)
			if (currFragment != null)
				ft.remove(currFragment);
			ft.attach(fragment);
		} else {
			// old: onPause->onStop->onDestroyView(1)
			// new:
			// (1)onAttach->onCreate->onCreateView->onActivityCreated->onStart->onResume
			if (currFragment != null)
				ft.detach(currFragment);
			ft.replace(R.id.frame_main_content, fragment);
		}
		ft.commitAllowingStateLoss();
	}

	/**
	 * 打印fragment栈日志信息（调试用）
	 */
	protected void logFragmentStack() {
		String fragmentStackStr = "fragment in stack: [";
		if (mContentFragmentFactory != null) {
			int size = mFragmentInfoStack.size();
			for (int i = 0; i < size; i++) {
				fragmentStackStr += mContentFragmentFactory
						.toString(mFragmentInfoStack.get(i).mType);
				if (i < mFragmentInfoStack.size() - 1)
					fragmentStackStr += ", ";
			}
		}
		fragmentStackStr += "]";

		L.e(TAG, fragmentStackStr);
	}

	/**
	 * fragment信息类 记录fragment对象，类型
	 * 
	 * @author caiminghui
	 * 
	 */
	protected class FragmentInfo {
		public ContentFragment mFragment; // fragment对象
		public int mType; // 类型

		public FragmentInfo(ContentFragment fragment, int type) {
			mFragment = fragment;
			mType = type;
		}
	}

}
