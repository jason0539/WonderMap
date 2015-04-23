package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.bean.MapUser;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

public class MapMarkerView {
	private ImageView iv_marker_head;
	private TextView iv_marker_name;

	public MapMarkerView() {
	}

	public View createView(MapUser user) {
		LayoutInflater inflater = BaseFragment.getMainActivity()
				.getLayoutInflater();
		View view = inflater.inflate(R.layout.view_mapmarker, null);
		iv_marker_head = (ImageView) view
				.findViewById(R.id.view_mapmarker_head);
		iv_marker_name = (TextView) view.findViewById(R.id.view_mapmarker_name);
		iv_marker_name.setText(user.getName());
		L.d(WModel.mapMarkerLoad, "头像url" + user.getAvatar());
		String url = user.getAvatar();
		if (url != null && !url.equals("")) {
			ImageLoader.getInstance().displayImage(user.getAvatar(),
					iv_marker_head, ImageLoadOptions.getOptions());
		}
		return view;
	}
}
