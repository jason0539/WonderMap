package jason.wondermap;

import jason.wondermap.controler.WMapControler;
import jason.wondermap.manager.WAccountManager;
import jason.wondermap.manager.WStorageManager;
import jason.wondermap.utils.JasonLog;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.mapapi.map.MapView;

public class MainActivity extends Activity {
	// 地图图层
	MapView mMapView = null;
	Button logButton = null;
	TextView resultTextView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mMapView = (MapView) findViewById(R.id.bmapView);
		WMapControler.getInstance().init(mMapView, this);

		logButton = (Button) findViewById(R.id.bn_login);
		resultTextView = (TextView) findViewById(R.id.tv_result_show);
		WAccountManager.getInstance().init(this, handler);
		logButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				WAccountManager.getInstance().startBaidu();
			}
		});
	}

	 Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			resultTextView.setText(msg.getData().getString("result"));
		};
	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			android.content.Intent data) {
		WAccountManager.getInstance().onActivityResult(requestCode, resultCode,
				data);
	};

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
