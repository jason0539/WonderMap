package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.view.MainBottomBar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends ContentFragment {

	private final static String TAG = MessageFragment.class.getSimpleName();
	// bottomBar
	private TextView textView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		ViewGroup viewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_message, null);
		textView = (TextView) viewGroup.findViewById(R.id.tv_msg_test);
		return viewGroup;
	}


	@Override
	protected void onInitView() {
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BaseFragment.getWMFragmentManager().showFragment(
						WMFragmentManager.TYPE_CHAT);
			}
		});
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
