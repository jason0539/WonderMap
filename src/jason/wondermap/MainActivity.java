package jason.wondermap;

import jason.wondermap.controler.WMapControler;
import jason.wondermap.manager.WAccountManager;
import jason.wondermap.utils.SharePreferenceUtil;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaAccount;
import com.baidu.mapapi.map.MapView;

public class MainActivity extends Activity {
	// 地图图层
	MapView mMapView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapView);
		WMapControler.getInstance().init(mMapView, this);
	}

	public void logout(View view) {// 退出逻辑需要处理
		WAccountManager.getInstance().startBaiduLogout();
	}

	@Override
	protected void onDestroy() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		WMapControler.getInstance().unInit();
		mMapView.onDestroy();
		mMapView = null;
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onPause() {
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
		super.onPause();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
