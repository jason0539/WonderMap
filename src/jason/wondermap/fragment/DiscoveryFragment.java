package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.view.MainBottomBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class DiscoveryFragment extends ContentFragment {

	private final static String TAG = DiscoveryFragment.class.getSimpleName();

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_discover, null);
		return viewGroup;
	}


	@Override
	protected void onInitView() {
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void onPause() {
		super.onPause();
	};

	@Override
	public void onDestroyView() {
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		super.onDestroyView();
	}

}
