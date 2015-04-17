package jason.wondermap.manager;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.crash.CrashLogFile;
import jason.wondermap.utils.L;
import jason.wondermap.utils.WModel;

import java.io.File;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;

/**
 * 负责上传crash之后收集的日志
 * 
 * @author liuzhenhui
 * 
 */
public class CrashLogManager {
	private File file;

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝外部接口＝＝＝＝＝＝＝＝＝＝＝＝

	public void uploadLog(final String logString) {
		file = new File(WMapConstants.CrashLogDir + logString);
		if (file.exists()) {
			L.d(WModel.CrashUpload, "文件存在");
			final BmobFile bmobFile = new BmobFile(file);
			bmobFile.uploadblock(WonderMapApplication.getInstance(),
					new UploadFileListener() {

						@Override
						public void onSuccess() {
							L.d(WModel.CrashUpload, "上传成功");
							CrashLogFile crashLogFile = new CrashLogFile(
									AccountUserManager.getInstance()
											.getCurrentUserName(), bmobFile);
							insertObject(crashLogFile);
							WonderMapApplication.getInstance().getSpUtil()
									.setCrashLog("");
						}

						@Override
						public void onFailure(int arg0, String arg1) {
							L.d(WModel.CrashUpload, "上传失败");

						}
					});
		} else {
			L.d(WModel.CrashUpload, "文件不存在");
		}
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝外部接口＝＝＝＝＝＝＝＝＝＝＝＝
	private void insertObject(final BmobObject obj) {
		obj.save(WonderMapApplication.getInstance(), new SaveListener() {

			@Override
			public void onSuccess() {
				file.delete();
				L.d(WModel.CrashUpload, "创建数据成功：" + obj.getObjectId());
			}

			@Override
			public void onFailure(int arg0, String arg1) {
				L.d(WModel.CrashUpload, "创建数据失败：" + arg0 + ",msg = " + arg1);
			}
		});
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static CrashLogManager instance;

	private CrashLogManager() {
	}

	public static CrashLogManager getInstance() {
		if (instance == null) {
			instance = new CrashLogManager();
		}
		return instance;
	}
}
