package jason.wondermap.fragment;

import jason.wondermap.R;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LoginiFragment extends ContentFragment{
	ViewGroup viewGroup;
	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		viewGroup = (ViewGroup) inflater.inflate(R.layout.activity_splash, mContainer, false);
		return viewGroup;
	}

	@Override
	protected void onInitView() {
		// TODO Auto-generated method stub
		
	}

}
