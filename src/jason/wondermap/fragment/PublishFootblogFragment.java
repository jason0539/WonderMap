package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.User;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.FootblogManager;
import jason.wondermap.manager.WLocationManager;
import jason.wondermap.utils.CacheUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.StringUtils;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.HeaderLayout.onRightImageButtonClickListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * @author liuzhenhui
 * 
 */
public class PublishFootblogFragment extends ContentFragment implements
		OnClickListener {
	private static final int REQUEST_CODE_ALBUM = 1;
	private static final int REQUEST_CODE_CAMERA = 2;
	private ViewGroup mRootViewGroup;
	EditText content;

	ImageView openLayout;
	ImageView takeLayout;
	ImageView picView;
	String dateTime;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootViewGroup = (ViewGroup) inflater.inflate(
				R.layout.fragment_newfootblog, mContainer, false);
		return mRootViewGroup;
	}

	@Override
	protected void onInitView() {
		initTopBarForBoth(mRootViewGroup, "发布足迹",
				R.drawable.btn_footblog_publish_selector, newFootblogSend);
		content = (EditText) mRootViewGroup.findViewById(R.id.edit_content);

		openLayout = (ImageView) mRootViewGroup.findViewById(R.id.open_pic);
		takeLayout = (ImageView) mRootViewGroup.findViewById(R.id.take_pic);

		picView = (ImageView) mRootViewGroup
				.findViewById(R.id.iv_publish_footblog_view);
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
						| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

		openLayout.setOnClickListener(this);
		takeLayout.setOnClickListener(this);
	}

	onRightImageButtonClickListener newFootblogSend = new onRightImageButtonClickListener() {

		@Override
		public void onClick() {
			String commitContent = content.getText().toString().trim();
			//足迹可以还有图片，文字图片有一个即可
			if (TextUtils.isEmpty(commitContent)&&StringUtils.isStringNull(targeturl)) {
				ShowToast("内容不能为空");
				return;
			}
			if (targeturl == null) {
				FootblogManager.getInstance().publishWithoutFigure(
						commitContent, null);
			} else {
				FootblogManager.getInstance().publish(commitContent, targeturl);
			}
			wmFragmentManager.back(null);
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.open_pic:
			Date date1 = new Date(System.currentTimeMillis());
			dateTime = date1.getTime() + "";
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
			intent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI,
					"image/*");
			startActivityForResult(intent, REQUEST_CODE_ALBUM);
			break;
		case R.id.take_pic:
			Date date = new Date(System.currentTimeMillis());
			dateTime = date.getTime() + "";
			File f = new File(CacheUtils.getCacheDirectory(mContext, true,
					"pic") + dateTime + ".jpg");
			L.d(WModel.PublishBlog,
					"camera file path is " + f.getAbsolutePath());
			if (f.exists()) {
				f.delete();
			}
			try {
				f.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			Uri uri = Uri.fromFile(f);
			Log.e("uri", uri + "");

			Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			camera.putExtra(MediaStore.EXTRA_OUTPUT, uri);
			startActivityForResult(camera, REQUEST_CODE_CAMERA);
			break;
		default:
			break;
		}
	}

	String targeturl = null;

	@SuppressLint("NewApi")
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == Activity.RESULT_OK) {
			switch (requestCode) {
			case REQUEST_CODE_ALBUM:
				String fileName = null;
				if (data != null) {
					Uri originalUri = data.getData();
					ContentResolver cr = mContext.getContentResolver();
					Cursor cursor = cr.query(originalUri, null, null, null,
							null);
					if (cursor.moveToFirst()) {
						do {
							fileName = cursor.getString(cursor
									.getColumnIndex("_data"));
							L.d(WModel.PublishBlog, "get album:" + fileName);
						} while (cursor.moveToNext());
					}
					Bitmap bitmap = compressImageFromFile(fileName);
					String suffix = fileName.substring(fileName
							.lastIndexOf("."));
					targeturl = saveToSdCard(bitmap, suffix);
					L.d(WModel.PublishBlog, "targeturl:" + targeturl);
					picView.setImageBitmap(bitmap);
					picView.setVisibility(View.VISIBLE);
				}
				break;
			case REQUEST_CODE_CAMERA:
				String files = CacheUtils.getCacheDirectory(mContext, true,
						"pic") + dateTime + ".jpg";
				File file = new File(files);
				if (file.exists()) {
					Bitmap bitmap = compressImageFromFile(files);
					targeturl = saveToSdCard(bitmap, ".jpg");
					picView.setImageBitmap(bitmap);
					picView.setVisibility(View.VISIBLE);
				} else {

				}
				break;
			default:
				break;
			}
		}
	}

	private Bitmap compressImageFromFile(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		newOpts.inJustDecodeBounds = true;// 只读边,不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		float hh = 800f;//
		float ww = 480f;//
		int be = 1;
		if (w > h && w > ww) {
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0)
			be = 1;
		newOpts.inSampleSize = be;// 设置采样率

		newOpts.inPreferredConfig = Config.ARGB_8888;// 该模式是默认的,可不设
		newOpts.inPurgeable = true;// 同时设置才会有效
		newOpts.inInputShareable = true;// 。当系统内存不够时候图片自动被回收

		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		// return compressBmpFromBmp(bitmap);//原来的方法调用了这个方法企图进行二次压缩
		// 其实是无效的,大家尽管尝试
		return bitmap;
	}

	/**
	 * my
	 * 
	 * @param bitmap
	 * @param suffix
	 *            防止上传图片失败增加的文件后缀
	 * @return
	 */
	public String saveToSdCard(Bitmap bitmap, String suffix) {
		String files = CacheUtils.getCacheDirectory(mContext, true, "pic")
				+ dateTime + "_11" + suffix;
		File file = new File(files);
		try {
			FileOutputStream out = new FileOutputStream(file);
			if (bitmap.compress(Bitmap.CompressFormat.JPEG, 50, out)) {
				out.flush();
				out.close();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}
}
