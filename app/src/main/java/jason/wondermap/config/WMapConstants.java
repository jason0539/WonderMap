package jason.wondermap.config;

import android.os.Environment;

/**
 * 存储应用的一些常量
 * 
 * @author liuzhenhui
 * 
 */
public class WMapConstants {

	// 应用相关
	public static final String REAL_PACKAGE_NAME = "jason.wondermap";
	// ＝＝＝＝＝＝＝＝＝＝＝＝存储相关 start＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	/**
	 * sharedPreference存储名称
	 */
	public static final String PREFERENCE_NAME = "_wmap_sp";
	// 图片缓存
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

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝bmob移植 end＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝图片移植 start＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static final int NUMBERS_PER_PAGE = 15;// 每次请求返回评论条数
	public static final int GET_FAVOURITE = 3;
	// 点击足迹相关控件，如果没有登录，则先进入登陆页面，登陆成功后返回之前操作界面

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝图片移植 end＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝定位相关＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	public static final double LAT_DEFAILT_DOUBLE = 39.916439;
	public static final double LNG_DEFAILT_DOUBLE = 116.402724;
	public static final String LAT_DEFAILT_STRING = "39.916439";
	public static final String LNG_DEFAILT_STRING = "116.402724";
}
