package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.bean.MapUser;
import jason.wondermap.fragment.BaseFragment;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MapMarkerView {
	private ImageView iv_marker_head;
	private TextView iv_marker_name;
	private LayoutInflater inflater;
	private View view;

	public MapMarkerView() {
		inflater = BaseFragment.getMainActivity().getLayoutInflater();
	}

	public View createView(final MapUser user) {
		view = inflater.inflate(R.layout.view_mapmarker, null);
		iv_marker_head = (ImageView) view
				.findViewById(R.id.view_mapmarker_head);
		iv_marker_name = (TextView) view.findViewById(R.id.view_mapmarker_name);
		iv_marker_name.setText(user.getName());
		Bitmap bitmap = user.getHeadBitmap();
		if (bitmap!=null&&!bitmap.isRecycled()) {
			iv_marker_head.setImageBitmap(bitmap);
		}
		return view;
	}
}
