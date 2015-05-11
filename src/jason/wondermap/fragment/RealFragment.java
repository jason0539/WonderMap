package jason.wondermap.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RealFragment extends Fragment{
	public static String TAG;
	protected Context mContext;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		TAG = this.getClass().getSimpleName();
		mContext = getActivity();
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}
}
