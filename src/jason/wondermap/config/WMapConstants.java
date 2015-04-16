package jason.wondermap.config;

import android.os.Environment;

/**
 * 存储应用的一些常量
 * 
 * @author liuzhenhui
 * 
 */
public class WMapConstants {
	// ＝＝＝＝＝＝＝＝＝＝＝＝存储相关 start＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * sharedPreference存储名称
	 */
	public static final String PREFERENCE_NAME = "_wmap_sp";
	public static final String CACHE_DIR = "WMap/Cache";

	// ＝＝＝＝＝＝＝＝＝＝＝＝SharedPreference存储相关 end＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝bmob移植 start＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static final String APP_ROOT_PATH = Environment
			.getExternalStorageDirectory() + "/WonderMap/";
	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = APP_ROOT_PATH + "sendImage/";

	/**
	 * 我的头像保存目录
	 */
	public static String MyAvatarDir = APP_ROOT_PATH + "myAvatar/";
	/**
	 * 发生crash之后，log存放位置
	 */
	public static String CrashLogDir = APP_ROOT_PATH + "crashLog/";
	/**
	 * 拍照回调
	 */
	public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;// 拍照修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;// 本地相册修改头像
	public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;// 系统裁剪头像

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;// 拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;// 本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;// 位置
	public static final String EXTRA_STRING = "extra_string";

	public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";// 注册成功之后登陆页面退出
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝bmob移植 end＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
}
