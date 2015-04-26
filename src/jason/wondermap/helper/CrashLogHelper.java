package jason.wondermap.helper;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.crash.CrashLogFile;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

/**
 * 负责上传crash之后收集的日志
 * 
 * @author liuzhenhui
 * 
 */
public class CrashLogHelper {

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝外部接口＝＝＝＝＝＝＝＝＝＝＝＝
	List<BmobObject> logFileList = new ArrayList<BmobObject>();

	public void uploadLog() {
		String logPath = WMapConstants.CrashLogDir;
		File logPathFile = new File(logPath);
		if (!logPathFile.exists()) {
			// 文件夹不存在，则直接返回
			return;
		}
		// 存储
		if (logPathFile.isDirectory()) {
			L.d(WModel.CrashUpload, "文件夹存在");
			File[] files = logPathFile.listFiles();
			if (files.length <= 0) {
				return;// 空文件夹直接返回
			}
			final String[] logFilePaths = new String[files.length];
			// 获取所有log文件的路径，批量上传使用
			for (int i = 0; i < files.length; i++) {
				logFilePaths[i] = files[i].getAbsolutePath();
				L.d(WModel.CrashUpload, "文件名" + i + "是" + logFilePaths[i]);
			}

			// 批量上传
			Bmob.uploadBatch(BaseFragment.getMainActivity(), logFilePaths,
					new UploadBatchListener() {
						@Override
						public void onSuccess(List<BmobFile> files,
								List<String> urls) {
							// 1、files-上传完成后的BmobFile集合，是为了方便大家对其上传后的数据进行操作，例如你可以将该文件保存到表中
							// 2、urls-上传文件的服务器地址
							L.d(WModel.CrashUpload, "上传成功");
							CrashLogFile crashLogFile = new CrashLogFile(
									AccountUserManager.getInstance()
											.getCurrentUser(), files.get(files
											.size() - 1));
							logFileList.add(crashLogFile);
							deleFile(crashLogFile);
							if (files.size() == logFilePaths.length) {
								insertObject(logFileList);
							}
						}

						@Override
						public void onError(int statuscode, String errormsg) {
							L.d(WModel.CrashUpload, "上传文件出错" + errormsg);
						}

						@Override
						public void onProgress(int curIndex, int curPercent,
								int total, int totalPercent) {
							// 1、curIndex--表示当前第几个文件正在上传
							// 2、curPercent--表示当前上传文件的进度值（百分比）
							// 3、total--表示总的上传文件数
							// 4、totalPercent--表示总的上传进度（百分比）
							L.d(WModel.CrashUpload, "共" + total + "个文件，正在上传第"
									+ curIndex + "个");
						}
					});
		} else {
			// 防止恶意创建一个文件，名字与存储日志的文件夹同名，直接删除
			L.d(WModel.CrashUpload, "文件夹不存在");
			logPathFile.delete();
			WonderMapApplication.getInstance().getSpUtil().setCrashLog(false);
		}
	}

	private void deleFile(CrashLogFile crashLogFile) {
		File file = new File(WMapConstants.CrashLogDir
				+ crashLogFile.getFile().getFilename());
		if (file.exists()) {
			file.delete();
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝外部接口＝＝＝＝＝＝＝＝＝＝＝＝
	private void insertObject(List<BmobObject> logFileList2) {
		new BmobObject().insertBatch(WonderMapApplication.getInstance(),
				logFileList2, new SaveListener() {

					@Override
					public void onSuccess() {
						WonderMapApplication.getInstance().getSpUtil()
								.setCrashLog(false);
						L.d(WModel.CrashUpload, "---->批量更新成功");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						L.d(WModel.CrashUpload, "---->批量更新失败");

					}
				});
	}

}
