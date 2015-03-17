package jason.wondermap;

import jason.wondermap.utils.L;
import jason.wondermap.utils.SharePreferenceUtil;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

public class AppStart extends Activity {

	private WonderMapApplication mApplication;
	private SharePreferenceUtil mPreferenceUtil;
	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.app_start);

		mApplication = (WonderMapApplication) getApplication();
		mPreferenceUtil = mApplication.getSpUtil();

		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				String userId = mPreferenceUtil.getUserId();
				if (mPreferenceUtil.hasLogin())// 是否登陆过，登陆过则进入，否则登陆
				{
					L.d("userId is " + userId+"进入");
					Intent intent = new Intent(AppStart.this,
							MainActivity.class);
					startActivity(intent);
					
				} else {
					L.d("userId is null,登陆");
					Intent intent = new Intent(AppStart.this,
							LoginActivity.class);
					startActivity(intent);
				}
				AppStart.this.finish();
			}
		}, 2000);

	}

}
