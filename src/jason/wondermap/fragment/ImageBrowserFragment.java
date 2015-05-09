package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.StringUtils;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.CustomViewPager;

import java.util.ArrayList;

import uk.co.senab.photoview.PhotoView;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.xiaomi.mistatistic.sdk.MiStatInterface;

public class ImageBrowserFragment extends ContentFragment implements
		OnPageChangeListener {
	private CustomViewPager mSvpPager;
	private ImageBrowserAdapter mAdapter;
	LinearLayout layout_image;
	private int mPosition;
	ViewGroup mRootView;

	private ArrayList<String> mPhotos;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_showpicture,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		mPhotos = mShowBundle.getStringArrayList(UserInfo.PHOTOS);
		if (mPhotos == null) {
			mPhotos = new ArrayList<String>();
		}
		mPosition = mShowBundle.getInt(UserInfo.POSITION);
		mSvpPager = (CustomViewPager) mRootView.findViewById(R.id.pagerview);
		mAdapter = new ImageBrowserAdapter(mContext);
		mSvpPager.setAdapter(mAdapter);
		mSvpPager.setCurrentItem(mPosition, false);
		mSvpPager.setOnPageChangeListener(this);
		for (int i = 0; i < mPhotos.size(); i++) {
			L.d(WModel.ImageShow, mPhotos.get(i));
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {

	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {

	}

	@Override
	public void onPageSelected(int arg0) {
		mPosition = arg0;
	}

	private class ImageBrowserAdapter extends PagerAdapter {

		private LayoutInflater inflater;

		public ImageBrowserAdapter(Context context) {
			this.inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return mPhotos.size();
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {

			View imageLayout = inflater.inflate(R.layout.item_show_picture,
					container, false);
			final PhotoView photoView = (PhotoView) imageLayout
					.findViewById(R.id.photoview);
			final ProgressBar progress = (ProgressBar) imageLayout
					.findViewById(R.id.progress);

			final String imgUrl = mPhotos.get(position);
			if (TextUtils.isEmpty(imgUrl)) {
				photoView.setImageResource(R.drawable.user_icon_default_main);
			} else {
				ImageLoader.getInstance().displayImage(imgUrl, photoView,
						ImageLoadOptions.getOptions(),
						new SimpleImageLoadingListener() {

							@Override
							public void onLoadingStarted(String imageUri,
									View view) {
								progress.setVisibility(View.VISIBLE);
							}

							@Override
							public void onLoadingFailed(String imageUri,
									View view, FailReason failReason) {
								progress.setVisibility(View.GONE);

							}

							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								progress.setVisibility(View.GONE);
							}

							@Override
							public void onLoadingCancelled(String imageUri,
									View view) {
								progress.setVisibility(View.GONE);
							}
						});
			}

			container.addView(imageLayout, 0);
			return imageLayout;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

	}
	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "图片详情页");
	}
	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
	}
}
