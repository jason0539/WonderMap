package jason.wondermap.fragment;

import jason.wondermap.MainActivity;
import jason.wondermap.WonderMapApplication;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class BaseFragment extends Fragment {
	protected static MainActivity mActivity; // 依附的activity
	protected static Context mContext; // 上下文
	protected static WMFragmentManager wmFragmentManager; // fragment管理器
	protected boolean mViewCreated = false; // 视图生成标志，视图生成之后才能开始设置监听事件，在onInitView中

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝对外接口＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static void initBeforeAll(MainActivity activity) {
		mActivity = activity;
		mContext = WonderMapApplication.getInstance();
		wmFragmentManager = mActivity.getWMFragmentManager();
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// L.e(TAG, "onCreateView");
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

}
