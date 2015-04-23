package jason.wondermap.bean;

import jason.wondermap.interfacer.MapUserDownLoadHeadListener;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;
import android.graphics.Bitmap;
import android.view.View;

import com.baidu.mapapi.map.Marker;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class MapUser {
	private User user;
	private Marker mMarker;
	private Bitmap headBitmap;

	public MapUser(User u) {
		user = u;
	}
	public static void createMapuser(User u,final MapUserDownLoadHeadListener listener){
		final MapUser mapUser = new MapUser(u);
		ImageLoader.getInstance().loadImage(mapUser.getAvatar(),
				ImageLoadOptions.getOptions(), new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {

					}

					@Override
					public void onLoadingComplete(String imageUri, View c,
							Bitmap loadedImage) {
						mapUser.setHeadBitmap(loadedImage);
						listener.onSuccess(mapUser);
					}

					@Override
					public void onLoadingCancelled(String imageUri,
							View view) {

					}
				});
	}
	public String getObjectId() {
		return user.getObjectId();
	}

	public void setObjectId(String objectId) {
		user.setObjectId(objectId);
	}

	public String getName() {
		return user.getUsername();
	}

	public void setName(String name) {
		user.setUsername(name);
	}

	public String getAvatar() {
		return user.getAvatar();
	}

	public double getLat() {
		return user.getLat();
	}

	public void setLat(double lat) {
		user.setLat(lat);
	}

	public double getLng() {
		return user.getLng();
	}

	public void setLng(double lng) {
		user.setLng(lng);
	}

	public Marker getMarker() {
		return mMarker;
	}

	public void setMarker(Marker mMarker) {
		this.mMarker = mMarker;
	}

	public Bitmap getHeadBitmap() {
		return headBitmap;
	}

	public void setHeadBitmap(Bitmap headBitmap) {
		this.headBitmap = headBitmap;
	}

}
