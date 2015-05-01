package jason.wondermap.view;

import jason.wondermap.R;
import jason.wondermap.bean.MapUser;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.app.Activity;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MapMarkerView {
	private static ImageView iv_marker_head;
	private static TextView iv_marker_name;
	private static View view;
	private static LayoutInflater mInflater;

	public static void initInflate(Activity activity) {
		mInflater = activity.getLayoutInflater();
	}

	public static View createView(final MapUser user) {
		if (user == null) {
			L.d(WModel.CrashUpload, "user 是空指针");
		}
		view = mInflater.inflate(R.layout.view_mapmarker, null);
		iv_marker_head = (ImageView) view
				.findViewById(R.id.view_mapmarker_head);
		iv_marker_name = (TextView) view.findViewById(R.id.view_mapmarker_name);
		iv_marker_name.setText(user.getName());
		Bitmap bitmap = user.getHeadBitmap();
		if (bitmap != null && !bitmap.isRecycled()) {
			iv_marker_head.setImageBitmap(bitmap);
		}
		return view;
	}
}
