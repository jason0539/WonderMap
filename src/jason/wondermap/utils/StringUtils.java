package jason.wondermap.utils;

import android.text.TextUtils;

public class StringUtils {
	/**
	 * 检验邮箱格式是否正确
	 * 
	 * @param target
	 * @return
	 */
	public final static boolean isValidEmail(CharSequence target) {
		if (target == null) {
			return false;
		} else {
			return android.util.Patterns.EMAIL_ADDRESS.matcher(target)
					.matches();
		}
	}

	/**
	 * 验证手机格式
	 */
	public final static boolean isPhoneNumber(String mobiles) {
		if (mobiles == null || mobiles.equals("")) {
			return false;
		}
		String phone = getPhoneNumberFromString(mobiles);
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][3578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(phone))
			return false;
		else
			return phone.matches(telRegex);
	}

	/**
	 * 从输入字符串获取电话号码
	 * 
	 * @param string
	 * @return
	 */
	public final static String getPhoneNumberFromString(String string) {
		if (string == null || string.equals("")) {
			return "";
		}
		String phone = removeALlBlankSigal(string);
		if (phone.startsWith("+86")) {
			phone = phone.substring(phone.indexOf("6") + 1);
		}
		return phone;
	}

	public final static String removeALlBlankSigal(String string) {
		String s = string.replace(" ", "");
		if (s.contains("-")) {
			return s.replaceAll("-", "");
		} else {
			return s;
		}

	}
}