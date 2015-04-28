package jason.wondermap.utils;

import jason.wondermap.manager.WLocationManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.datatype.BmobGeoPoint;

import com.baidu.location.BDLocation;

public class BlogUtils {
	// 获取地点
	public static String getAddress(BDLocation location) {
		String pro = location.getProvince();
		String city = location.getCity();
		String district = location.getDistrict();
		StringBuffer buffer = new StringBuffer();
		if (pro != null && !pro.equals("")) {
			buffer.append(pro.substring(0, pro.length() - 1)).append("·");
		}
		if (city != null && !city.equals("")) {
			// buffer.append(city.substring(0, city.length() - 1)).append("-");
			buffer.append(city.substring(0, city.length() - 1));
		}
		// if (district != null && !district.equals("")) {
		// buffer.append(district.substring(0, district.length()-1));
		// }
		if (buffer.length() == 0) {
			return "未知区域";
		} else {
			return buffer.toString();
		}
	}

	// 获取距离
	public static String getDistance(BmobGeoPoint point) {
		BmobGeoPoint bmobGeoPoint = WLocationManager.getInstance()
				.getBmobGeoPoint();
		if (bmobGeoPoint.getLatitude() == 0 || bmobGeoPoint.getLongitude() == 0
				|| point.getLatitude() == 0 || point.getLongitude() == 0) {
			// 不管是我现在的地点还是对方发布足迹的地点，只要有一个位置不正确，就不计算距离
			return "";
		}
		double dis = point.distanceInKilometersTo(bmobGeoPoint);
		if (dis < 1) {
			int distance = (int) (dis * 1000);
			return "·" + distance + "m";
		} else {
			int distance = (int) dis;
			return "·" + distance + "km";
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
		SimpleDateFormat format = new SimpleDateFormat("MM月dd日  HH:mm");
		return format.format(new Date(l));
	}

	public static String getHourAndMin(long time) {
		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		return format.format(new Date(time));
	}
}
