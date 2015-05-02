package jason.wondermap.fragment;

import jason.wondermap.R;
import android.app.AlertDialog;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends ContentFragment implements OnClickListener {
	ViewGroup mRootViewGroup;
	private TextView tv_about_version;
	private ViewGroup layout_meGroup;
	private ViewGroup layout_weiboGroup;
	private AlertDialog albumDialog = null;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_about,
				mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForLeft(mRootViewGroup, "关于");
		tv_about_version = (TextView) mRootViewGroup
				.findViewById(R.id.tv_about_version);
		tv_about_version.setText("V " + getVersion());
		layout_meGroup = (ViewGroup) mRootViewGroup
				.findViewById(R.id.layout_me);
		layout_weiboGroup = (ViewGroup) mRootViewGroup
				.findViewById(R.id.layout_weibo);
		layout_weiboGroup.setOnClickListener(this);
		layout_meGroup.setOnClickListener(this);
	}

	public String getVersion() {
		try {
			PackageManager manager = mContext.getPackageManager();
			PackageInfo info = manager.getPackageInfo(
					mContext.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "1.0.0";
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_me:
			albumDialog = new AlertDialog.Builder(getActivity()).create();
			albumDialog.setCanceledOnTouchOutside(true);
			View view = LayoutInflater.from(mContext).inflate(
					R.layout.layout_dialog_about_me, null);
			albumDialog.show();
			albumDialog.setContentView(view);
			albumDialog.getWindow().setGravity(Gravity.CENTER);
			break;
		case R.id.layout_weibo:

			break;

		default:
			break;
		}
	}
}
