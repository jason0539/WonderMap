package jason.wondermap.bean;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.controler.MapControler;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.interfacer.MapUserDownLoadHeadListener;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;

import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.model.LatLng;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

/**
 * 地图上展示的用户
 * 
 * @author liuzhenhui
 * 
 */
public class MapUser {
	private User user;
	private Marker mMarker;
	private Bitmap headBitmap;

	public MapUser(User u) {
		user = u;
	}

	/**
	 * 由于加载用户头像需要一定时间，采用监听器回调的方式
	 */
	public static void createMapuser(User u,
			final MapUserDownLoadHeadListener listener) {
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
						// markerView总是空指针，因为获取不到inflate，这里判断下如果为空则直接不添加
						if (BaseFragment.getInflater() != null) {
							listener.onSuccess(mapUser);
						}
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {

					}
				});
	}

	/**
	 * mapUser更新自己的user，头像，marker位置
	 */
	public void updateSelf() {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", user.getObjectId());
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						if (object.size() <= 0) {
							return;
						}
						String lastHead = user.getAvatar();
						String lastName = user.getUsername();
						double lastLat = user.getLat();
						double lastLng = user.getLng();
						// 新查询到的user
						user = object.get(0);
						// 名字或者头像发生变化，更新，否则只更新位置就可以
						if ((lastHead == null && user.getAvatar() != null)// 旧头像为空，新头像不空
								|| (lastHead != null
										&& user.getAvatar() != null && (!lastHead
											.equals(user.getAvatar())))// 新旧头像都不为空，并且不相同
								|| !lastName.equals(user.getUsername())) // 名字变化
						{
							L.d(WModel.UpdateFriend, lastName + "的头像或名字发生变化，更新");
							ImageLoader.getInstance().loadImage(
									user.getAvatar(),
									ImageLoadOptions.getOptions(),
									new ImageLoadingListener() {

										@Override
										public void onLoadingStarted(
												String imageUri, View view) {
										}

										@Override
										public void onLoadingFailed(
												String imageUri, View view,
												FailReason failReason) {

										}

										@Override
										public void onLoadingComplete(
												String imageUri, View c,
												Bitmap loadedImage) {
											headBitmap = loadedImage;
											if (headBitmap == null) {
												headBitmap = BitmapFactory
														.decodeResource(
																BaseFragment
																		.getMainActivity()
																		.getResources(),
																R.drawable.user_icon_default_main);
											}
											if (mMarker != null) {

												mMarker.remove();
											}
											// 头像加载成功之后，更新marker
											mMarker = MapControler
													.getInstance().addUser(
															MapUser.this);
										}

										@Override
										public void onLoadingCancelled(
												String imageUri, View view) {

										}
									});
						} else if (mMarker != null
								&& (lastLat != user.getLat() || lastLng != user
										.getLng())) {
							L.d(WModel.UpdateFriend, lastName + "的位置发生变化，只更新位置");
							// 名字头像没变化，则只更新位置
							mMarker.setPosition(new LatLng(user.getLat(), user
									.getLng()));
						} else {
							L.d(WModel.UpdateFriend, lastName + "的信息没变，不用更新");
						}

					}

					@Override
					public void onError(int code, String msg) {
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
