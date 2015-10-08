package jason.wondermap.manager;

import java.io.File;

import android.content.Context;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import jason.wondermap.bean.Blog;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.utils.L;
import jason.wondermap.utils.T;
import jason.wondermap.utils.WModel;

public class FootblogManager {
	private Blog currentBlog = null;

	public Blog getCurrentBlog() {
		return currentBlog;
	}

	public void setCurrentBlog(Blog blog) {
		this.currentBlog = blog;
	}

	/*
	 * 发表带图片
	 */
	public void publish(final String commitContent, String targeturl) {

		final BmobFile figureFile = new BmobFile(new File(targeturl));
		figureFile.upload(mContext, new UploadFileListener() {

			@Override
			public void onSuccess() {
				L.i("上传文件成功。" + figureFile.getFileUrl(mContext));
				publishWithoutFigure(commitContent, figureFile);
			}

			@Override
			public void onProgress(Integer arg0) {
				L.d(WModel.PublishBlog, "progress --->" + arg0);
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.d(WModel.PublishBlog, "progress --->" + arg0);
				L.i("上传文件失败。" + arg1);
			}
		});
	}

	public void publishWithoutFigure(final String commitContent,
			final BmobFile figureFile) {
		User user = AccountUserManager.getInstance().getCurrentUser();
		final Blog blog = new Blog();
		blog.setAuthor(user);
		blog.setContent(commitContent);
		if (figureFile != null) {
			blog.setContentfigureurl(figureFile);
		}
		blog.setLocation(WLocationManager.getInstance().getBmobGeoPoint());
		blog.setBdLocation(WLocationManager.getInstance().getBDLocation());
		blog.setLove(0);
		blog.setHate(0);
		blog.setShare(0);
		blog.setComment(0);
		blog.setPass(true);
		blog.save(mContext, new SaveListener() {

			@Override
			public void onSuccess() {
				ShowToast("发表成功");
				// TODO 发表成功自动返回//应该带参数，成功则刷新
				// setResult(RESULT_OK);
				// finish();
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				ShowToast("发表失败！" + arg1);
			}

		});
	}

	private void ShowToast(String string) {
		T.showShort(mContext, string);
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝格式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static FootblogManager instance;
	private Context mContext;

	private FootblogManager() {
		mContext = BaseFragment.getMainActivity();
	}

	public static FootblogManager getInstance() {
		if (instance == null) {
			instance = new FootblogManager();
		}
		return instance;
	}
}
