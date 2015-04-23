package jason.wondermap.utils;

import jason.wondermap.manager.WLocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.widget.TextView;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class BlogUtils {
	// 获取地点
	public static String getAddress(BmobGeoPoint point, TextView tvAddress) {
		return "";
	}

	// 获取距离
	public static String getDistance(BmobGeoPoint point) {
		double dis = point.distanceInKilometersTo(WLocationManager
				.getInstance().getBmobGeoPoint());
		if (dis < 1) {
			int distance = (int) (dis * 1000);
			return distance + "米";
		} else {
			int distance = (int) dis;
			return distance + "千米";
		}
	}

	// 获取时间
	public static String getTime(String time) {
		Date date = TimeUtil.stringToDate(time, "yyyy-MM-dd HH:mm:ss");
		long timestap = TimeUtil.dateToLong(date);
		return getBlogTime(timestap);
	}

	public static String getBlogTime(long timesamp) {
		String result = "";
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		Date today = new Date(System.currentTimeMillis());
		Date otherDay = new Date(timesamp);
		int temp = Integer.parseInt(sdf.format(today))
				- Integer.parseInt(sdf.format(otherDay));

		switch (temp) {
		case 0:
			result = "今天 " + getHourAndMin(timesamp);
			break;
		case 1:
			result = "昨天 " + getHourAndMin(timesamp);
			break;
		case 2:
			result = "前天 " + getHourAndMin(timesamp);
			break;

		default:
			result = getAgoTime(timesamp);
			break;
		}

		return result;
	}

	public static String getAgoTime(long l) {
		SimpleDateFormat format = new SimpleDateFormat(
				TimeUtil.FORMAT_MONTH_DAY_TIME);
		return format.format(new Date(l));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}
}
