package jason.wondermap.fragment;

import jason.wondermap.utils.AnimationFactory;
import jason.wondermap.utils.L;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.baidu.location.l;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

/**
 * 为所有子类fragment获取bundle参数，方便子类直接使用，
 * 
 * @author liuzhenhui
 * 
 */
public abstract class ContentFragment extends BaseFragment {
	/** 当前fragment 类型 */
	private int mType = -1;
	protected Bundle mShowBundle; // 展示参数
	protected Bundle mBackBundle; // 回退参数
	protected View mContentView; // 内容视图
	protected boolean mNeedInitView = false; // 初始化视图标志
	protected boolean mIsDisplayed=false;
	public ContentFragment(){
		mIsDisplayed = false;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝生命周期UI相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// fragment生命周期：onAttach-->onCreate-->onCreateView-->onActivityCreated-->onStart-->onResume-->onPause-->onStop-->onDestroyView-->onDestroy-->onDetach
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ContentFragmentManager.KEY_FRAGMENT_TYPE)) {// 类型参数
				mType = bundle.getInt(ContentFragmentManager.KEY_FRAGMENT_TYPE);
				L.d("type is " + mType);
			}
			if (bundle.containsKey(ContentFragmentManager.KEY_SHOW_BUNDLE)) {// 显示参数
				mShowBundle = bundle
						.getBundle(ContentFragmentManager.KEY_SHOW_BUNDLE);
			}
		}
	}

	/**
	 * 子类没有必要再重写该方法，视图创建通过onCreateView，监听事件通过onInitView
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);

		Bundle bundle = getArguments();
		if (bundle != null) {
			if (bundle.containsKey(ContentFragmentManager.KEY_BACK_BUNDLE))// 回退参数
				mBackBundle = bundle
						.getBundle(ContentFragmentManager.KEY_BACK_BUNDLE);
		}

		if (mContentView != null) {
			ViewGroup parent = (ViewGroup) mContentView.getParent();
			if (parent != null)
				parent.removeView(mContentView);
			mViewCreated = true;
		} else {
			mContentView = onCreateContentView(inflater);
			mViewCreated = true;
		}
		if (mContentView != null) {
			mContentView.setClickable(true);
		}
		onInit();
		return mContentView;
	}

	/**
	 * 初始化函数
	 */
	protected void onInit() {
		/* 初始化视图，可以包含地图 */
		if (mNeedInitView) {
			onInitView();
			mNeedInitView = false;
		}
	}

	/**
	 * 要求初始化视图，若视图已生成，则立刻初始化，否则延迟到onActivityCreated中初始化(
	 * 在onCreateView的最后调用onInitView)
	 */
	public void requestInitView() {
		if (mViewCreated)
			onInitView();
		else
			mNeedInitView = true;

	}

	/**
	 * 生成内容fragment视图，由onCreateView在mContentView为null时调用
	 * 
	 * @param inflater
	 *            布局展开对象
	 * @return 返回内容fragment视图
	 */
	protected abstract View onCreateContentView(LayoutInflater inflater);

	/**
	 * 初始化视图图,onCreateContentView只是把视图显示出来，这里才初始化视图中的各种事件（点击监听等）
	 */
	protected abstract void onInitView();

	@Override
	public void onDestroyView() {
		mIsDisplayed = false;
		mViewCreated = false;
		mNeedInitView = false;

		Animation anim = createEmptyAnimation(100);
		anim.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				L.e(TAG, "onDestroyView onAnimationStart: " + animation);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				/* 注意：这里只触发进入新页面的动画结束，不触发退出旧页面的动画结束！ */
				L.e(TAG, "onDestroyView onAnimationEnd: " + animation);
			}
		});

		super.onDestroyView();
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝子类重写自定义的方法＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝

	/**
	 * 响应返回键点击
	 * 
	 * @return 是否处理
	 */
	public boolean onBackPressed() {
		return false;
	}

	/**
	 * 响应触摸事件
	 * 
	 * @param event
	 *            触摸事件
	 * @return 是否处理
	 */
	public boolean onTouchEvent(MotionEvent event) {
		return false;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝fragment切换的动画相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * 获取退出动画时长
	 * 
	 * @param fragmentType
	 *            fragment类型
	 * @return 动画时长
	 */
	public long getAnimationOutDuration(int fragmentType, boolean isBack) {
		if (!isAdded()) { // 防御连续多次replaceFragment造成的crash
			return 0;
		}

		Map<View, Animation> animMap = animationOut(fragmentType, isBack);
		if (animMap == null) {
			return 0;
		}

		return getAnimationTotalDuration(animMap.values());
	}

	/**
	 * 获取动画的总共时间
	 * 
	 * @param animList
	 *            动画列表
	 * @return 返回动画的总共时间
	 */
	protected long getAnimationTotalDuration(Collection<Animation> animList) {

		long duration = 0;
		if (animList != null) {
			for (Animation anim : animList) {
				duration = Math.max(anim.getStartOffset() + anim.getDuration(),
						duration);
			}
		}

		return duration;
	}

	/**
	 * 构造一个空动画，使其长度等于time
	 * 
	 * @param duration
	 *            动画时长
	 * @return 返回空动画
	 */
	protected Animation createEmptyAnimation(long duration) {
		Animation animation = new Animation() {
		};
		animation.setDuration(duration);

		return animation;
	}

	/**
	 * 开始animMap中的所有动画
	 * 
	 * @param animMap
	 *            动画映射表
	 */
	protected void startAnimation(Map<View, Animation> animMap) {
		if (animMap == null)
			return;

		Set<View> viewList = animMap.keySet();
		Animation anim = null;
		for (View view : viewList) {
			anim = animMap.get(view);
			if (anim != null && view != null)
				view.startAnimation(anim);
		}
	}

	/**
	 * 进入页面的控件动画安排
	 * 
	 * @param lastDuration
	 *            旧页面动画时长
	 * @param fragmentType
	 *            fragment类型
	 * @return 返回控件动画映射
	 */
	protected Map<View, Animation> animationIn(long lastDuration,
			int fragmentType, boolean isBack) {
		Map<View, Animation> animMap = new HashMap<View, Animation>();
		if (wmFragmentManager.isMapContent(fragmentType)) {
			animMap.put(mContentView, AnimationFactory.getAnimation(mContext,
					AnimationFactory.ANIM_POP_IN, lastDuration, 300));
		} else {
			if (isBack)
				animMap.put(mContentView, AnimationFactory.getAnimation(
						mContext, AnimationFactory.ANIM_LEFT_IN, -1, 300));
			else
				animMap.put(mContentView, AnimationFactory.getAnimation(
						mContext, AnimationFactory.ANIM_RIGHT_IN, -1, 300));

		}

		return animMap;
	}

	/**
	 * 退出页面的控件动画安排
	 * 
	 * @param fragmentType
	 *            fragment类型
	 * @return 返回控件动画映射
	 */
	protected Map<View, Animation> animationOut(int fragmentType, boolean isBack) {
		Map<View, Animation> animMap = new HashMap<View, Animation>();
		if (wmFragmentManager.isMapContent(fragmentType)) {
			animMap.put(mContentView, AnimationFactory.getAnimation(mContext,
					AnimationFactory.ANIM_POP_OUT, -1, 300));
		} else {
			if (isBack)
				animMap.put(mContentView, AnimationFactory.getAnimation(
						mContext, AnimationFactory.ANIM_RIGHT_OUT, -1, 300));
			else
				animMap.put(mContentView, AnimationFactory.getAnimation(
						mContext, AnimationFactory.ANIM_LEFT_OUT, -1, 300));
		}
		return animMap;
	}

	@Override
	public Animation onCreateAnimation(int transit, final boolean enter,
			int nextAnim) {

		/* 解决内存被回收后进入APP会crash的问题 */
		if (wmFragmentManager == null) {
			mActivity.exitApp();
			return null;
		}

		L.e(TAG, "onCreateAnimation: " + transit + "; " + enter + "; "
				+ nextAnim);
		Map<View, Animation> animMap = null;
		Animation anim = null;

		final int fragmentType = nextAnim; // trick：利用nextAnim代表fragment类型
		boolean isBack = ((transit & 0x00008000) == 0x00008000);

		if (enter) {
			long lastDuration = isBack ? transit & 0x00007fff : transit; // trick：利用transit代表旧页面动画时长
			animMap = animationIn(lastDuration, fragmentType, isBack);
			long duration = lastDuration;
			if (animMap != null && animMap.size() > 0)
				duration = Math.max(
						getAnimationTotalDuration(animMap.values()), duration);
			anim = createEmptyAnimation(duration);
			anim.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					L.e(TAG, "onAnimationStart: " + animation);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {

				}

				@Override
				public void onAnimationEnd(Animation animation) {
					/* 注意：这里只触发进入新页面的动画结束，不触发退出旧页面的动画结束！ */
					L.e(TAG, "onAnimationEnd: " + animation);
					mActivity.forbidTouch(false);// 若进入动画先于退出动画，且进入动画为0时长动画，则可能引起禁止点击不消失的bug
					afterAnimationIn(fragmentType);
				}
			});
		} else {
			beforeAnimationOut(fragmentType);
			animMap = animationOut(fragmentType, isBack);
			long duration = 0;
			if (animMap != null && animMap.size() > 0)
				duration = Math.max(
						getAnimationTotalDuration(animMap.values()), duration);
			anim = createEmptyAnimation(duration);
		}

		mActivity.forbidTouch(true); // 若进入动画先于退出动画，且进入动画为0时长动画，则可能引起禁止点击不消失的bug
		startAnimation(animMap);

		return anim;
	}

	/**
	 * 退出动画开始前
	 * 
	 * @param fragmentType
	 *            即将进入的fragment类型
	 */
	protected void beforeAnimationOut(int fragmentType) {
	}

	/**
	 * 进入动画结束后
	 * 
	 * @param fragmentType
	 *            上一个fragment类型
	 */
	protected void afterAnimationIn(int fragmentType) {
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private final static String TAG = ContentFragment.class.getSimpleName();

	/** 获取当前fragment类型 */
	public int getType() {
		return mType;
	}

	@Override
	public void onResume() {
		mIsDisplayed =true;
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

}
