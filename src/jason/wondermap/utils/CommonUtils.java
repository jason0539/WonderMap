package jason.wondermap.utils;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.helper.CrashLogHelper;
import jason.wondermap.interfacer.MapUserDownLoadHeadListener;
import jason.wondermap.interfacer.MapUserTransferListener;

import java.util.List;

import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import cn.bmob.im.util.BmobJsonUtil;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class CommonUtils {

	/** 检查是否有网络 */
	public static boolean isNetworkAvailable(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			return info.isAvailable();
		}
		return false;
	}

	/** 检查是否是WIFI */
	public static boolean isWifi(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_WIFI)
				return true;
		}
		return false;
	}

	/** 检查是否是移动网络 */
	public static boolean isMobile(Context context) {
		NetworkInfo info = getNetworkInfo(context);
		if (info != null) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE)
				return true;
		}
		return false;
	}

	private static NetworkInfo getNetworkInfo(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		return cm.getActiveNetworkInfo();
	}

	/** 检查SD卡是否存在 */
	public static boolean checkSdCard() {
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			return true;
		else
			return false;
	}

	public static int getScreenWidth() {
		DisplayMetrics metric = new DisplayMetrics();
		BaseFragment.getMainActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(metric);
		return metric.widthPixels;
	}

	/**
	 * hello消息转换成MapUser
	 * 
	 * @param msg
	 */
	public static void HelloMsgToUser(JSONObject msg,
			final MapUserTransferListener listener) {
		String id = BmobJsonUtil.getString(msg, UserInfo.USER_ID);
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						if (object.size() <= 0) {
							return;
						}
						MapUser.createMapuser(object.get(0),
								new MapUserDownLoadHeadListener() {

									@Override
									public void onSuccess(MapUser uMapUser) {
										listener.onSuccess(uMapUser);
									}
								});
					}

					@Override
					public void onError(int code, String msg) {
					}
				});
	}

	/**
	 * 检查是否有crash 日志信息需要上传，如果有且当前为wifi环境则上传
	 * 
	 * @param context
	 */
	public static void checkCrashLog(final Context context) {
		new Thread() {
			@Override
			public void run() {
				super.run();
				if (!isWifi(context)) {// 仅wifi环境下上传
					return;
				}
				if (WonderMapApplication.getInstance().getSpUtil()
						.hasCrashLog()) {
					CrashLogHelper crashLogManager = new CrashLogHelper();
					crashLogManager.uploadLog();
					L.d(WModel.CrashUpload, "存在crash 文件");
				} else {
					L.d(WModel.CrashUpload, "没有crash");
				}
			}
		}.start();
	}

}
