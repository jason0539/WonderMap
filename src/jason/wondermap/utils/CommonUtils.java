package jason.wondermap.utils;

import jason.wondermap.WonderMapApplication;
import jason.wondermap.bean.MapUser;
import jason.wondermap.bean.User;
import jason.wondermap.fragment.BaseFragment;
import jason.wondermap.interfacer.MapUserTransferListener;
import jason.wondermap.manager.CrashLogManager;

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
		// String name = BmobJsonUtil.getString(msg, UserInfo.USER_NAME);
		String id = BmobJsonUtil.getString(msg, UserInfo.USER_ID);
		// double lat = Double.valueOf(BmobJsonUtil.getString(msg,
		// UserInfo.LATITUDE));
		// double lng = Double.valueOf(BmobJsonUtil.getString(msg,
		// UserInfo.LONGTITUDE));
		// MapUser user = new MapUser(id, name, lat, lng);
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(WonderMapApplication.getInstance(),
				new FindListener<User>() {
					@Override
					public void onSuccess(List<User> object) {
						final MapUser user = new MapUser(object.get(0));
						listener.onSuccess(user);
					}

					@Override
					public void onError(int code, String msg) {
					}
				});
	}

	public static void checkCrashLog() {
		String logName = WonderMapApplication.getInstance().getSpUtil()
				.hasCrashLog();
		if ("".equals(logName) || logName == null) {
			L.d(WModel.CrashUpload, "没有crash");
		} else {
			CrashLogManager.getInstance().uploadLog(logName);
			L.d(WModel.CrashUpload, "crash 文件");
		}
	}

}
