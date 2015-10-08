package jason.wondermap.fragment;

import jason.wondermap.MainActivity;
import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.view.HeaderLayout;
import jason.wondermap.view.HeaderLayout.HeaderStyle;
import jason.wondermap.view.HeaderLayout.onLeftImageButtonClickListener;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;
import jason.wondermap.view.dialog.DialogTips;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import cn.bmob.im.util.BmobLog;

public class BaseFragment extends Fragment {
	protected static MainActivity mActivity; // 依附的activity
	protected static Context mContext; // 上下文
	protected static WMFragmentManager wmFragmentManager; // fragment管理器
	protected boolean mViewCreated = false; // 视图生成标志，视图生成之后才能开始设置监听事件，在onInitView中
	protected ViewGroup mContainer;
	protected static LayoutInflater mInflater;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static void initBeforeAll(MainActivity activity,
			WMFragmentManager fragmentManager) {
		mActivity = activity;
		mContext = WonderMapApplication.getInstance();
		wmFragmentManager = fragmentManager;
		mInflater = mActivity.getLayoutInflater();
	}

	public static LayoutInflater getInflater() {
		return mInflater;
	}

	public boolean canProcessUI() {
		return isAdded();
	}

	public static WMFragmentManager getWMFragmentManager() {
		return wmFragmentManager;
	}

	public static MainActivity getMainActivity() {
		return mActivity;
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return false;
	}

	protected int fragmentType;

	public int getType() {
		return fragmentType;
	}

	public void setType(int fragmentType) {
		this.fragmentType = fragmentType;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private final static String TAG = BaseFragment.class.getSimpleName();

	// 以下方法按照生命周期排序
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// L.e(TAG, "onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// L.e(TAG, "onCreate");
		setHasOptionsMenu(true); // 允许fragment修改menu
		// TODO 自动登陆状态下检测是否在其他设备登陆
		// AccountUserManager.getInstance().checkLogin();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// L.e(TAG, "onCreateView");
		mContainer = container;
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// L.e(TAG, "onActivityCreated");
	}

	@Override
	public void onStart() {
		super.onStart();
		// LogUtil.e(TAG, "onStart");
	}

	@Override
	public void onResume() {
		super.onResume();

		// LogUtil.e(TAG, "onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		// LogUtil.e(TAG, "onPause");
	}

	@Override
	public void onStop() {
		super.onStop();
		// LogUtil.e(TAG, "onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		hideSoftInputView();
		// LogUtil.e(TAG, "onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// LogUtil.e(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		// LogUtil.e(TAG, "onDetach");
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝移植过来的标题－－－－－－－－－－－－－－－－－－－－－－
	Toast mToast;

	/**
	 * 打Log
	 */
	public void ShowLog(String msg) {
		BmobLog.i(msg);
	}

	public void ShowToast(String text) {
		if (text == null || text.equals("")) {
			return;
		}
		if (null == this || !this.isAdded()) {
			return;
		}
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public void ShowToast(int text) {
		if (null == this || !this.isAdded()) {
			return;
		}
		if (mToast == null) {
			mToast = Toast.makeText(getActivity(), text, Toast.LENGTH_LONG);
		} else {
			mToast.setText(text);
		}
		mToast.show();
	}

	public View findViewById(int paramInt) {
		return getView().findViewById(paramInt);
	}

	/**
	 * 公用的Header布局
	 */
	public HeaderLayout mHeaderLayout;

	/**
	 * 只有title initTopBarLayoutByTitle
	 */
	public void initTopBarForOnlyTitle(ViewGroup rootGroup, String titleName) {
		mHeaderLayout = (HeaderLayout) rootGroup
				.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.DEFAULT_TITLE);
		mHeaderLayout.setDefaultTitle(titleName);
	}

	/**
	 * 初始化标题栏-带左右按钮
	 */
	public void initTopBarForBoth(ViewGroup rootView, String titleName,
			int rightDrawableId, onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) rootView
				.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	public void initTopBarForBoth(ViewGroup rootView, String titleName,
			int rightDrawableId, String text,
			onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) rootView
				.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_DOUBLE_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
		mHeaderLayout.setTitleAndRightButton(titleName, rightDrawableId, text,
				listener);
	}

	/**
	 * 只有左边按钮和Title initTopBarLayout
	 * 
	 * @throws
	 */
	public void initTopBarForLeft(ViewGroup rootView, String titleName) {
		mHeaderLayout = (HeaderLayout) rootView
				.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_LIFT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndLeftImageButton(titleName,
				R.drawable.base_action_bar_back_bg_selector,
				new OnLeftButtonClickListener());
	}

	/**
	 * 右边+title initTopBarForRight
	 * 
	 * @return void
	 * @throws
	 */
	public void initTopBarForRight(ViewGroup rootView, String titleName,
			int rightDrawableId, onRightImageButtonClickListener listener) {
		mHeaderLayout = (HeaderLayout) rootView
				.findViewById(R.id.common_actionbar);
		mHeaderLayout.init(HeaderStyle.TITLE_RIGHT_IMAGEBUTTON);
		mHeaderLayout.setTitleAndRightImageButton(titleName, rightDrawableId,
				listener);
	}

	// 左边按钮的点击事件
	public class OnLeftButtonClickListener implements
			onLeftImageButtonClickListener {

		@Override
		public void onClick() {
			wmFragmentManager.back(null);
		}
	}

	public void hideSoftInputView() {
		InputMethodManager manager = ((InputMethodManager) mContext
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
			if (getActivity().getCurrentFocus() != null)
				manager.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

}
