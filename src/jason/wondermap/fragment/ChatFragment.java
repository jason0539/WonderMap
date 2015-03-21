package jason.wondermap.fragment;

import jason.wondermap.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ChatFragment extends ContentFragment {

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		ViewGroup group = (ViewGroup) inflater.inflate(R.layout.fragment_chat,
				null);
		return group;
	}

	@Override
	protected void onInitView() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onBackPressed() {
		wmFragmentManager.back(null);
		return true;
	}
}
