package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.sns.TencentShare;
import jason.wondermap.sns.TencentShareEntity;
import jason.wondermap.utils.T;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
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
	private ViewGroup layout_recommend;
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
		layout_recommend = (ViewGroup) mRootViewGroup
				.findViewById(R.id.layout_recommend);
		layout_weiboGroup.setOnClickListener(this);
		layout_meGroup.setOnClickListener(this);
		layout_recommend.setOnClickListener(this);
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
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.weibo.com/u/2553717707"));
			startActivity(intent);
			break;
		case R.id.layout_recommend:
			T.showShort(mContext, "推荐给好友一起玩");
			final TencentShare tencentShare = new TencentShare(
					BaseFragment.getMainActivity(), getQQShareEntity());
			tencentShare.shareToQQ();
			break;
		default:
			break;
		}
	}

	private TencentShareEntity getQQShareEntity() {
		String title = "活点地图，随时随地看见TA";
		String comment = "快来加入活点地图,看看TA在哪里";
		String img = null;
		// img =
		// "http://file.bmob.cn/M01/B2/14/oYYBAFVBexuALTLNAAC3_XgW_sY104.png";
		img = "http://file.bmob.cn/M00/69/6C/oYYBAFU5-R6AHUciAADjtQ_g_-8687.jpg";
		String summary = "在活点地图里我叫“"
				+ AccountUserManager.getInstance().getCurrentUserName()
				+ "”，进来就能看见我了";
		String targetUrl = "http://huodianditu.bmob.cn";
		TencentShareEntity entity = new TencentShareEntity(title, img,
				targetUrl, summary, comment);
		return entity;
	}
}
