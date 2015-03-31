package jason.wondermap.utils;

public class AccountInfo {
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝账户信息＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	// 绑定推送后返回的各种id
	public static final String APP_ID = "app_id";// 推送服务识别应用
	public static final String USER_ID = "user_id";// 每个绑定的百度账号唯一id
	public static final String CHANNEL_ID = "channel_id";// 每个设备唯一，相同账号的不同设备也唯一
	// 绑定账户后返回的id和token
	public static final String SOCIAL_ID = "social_id";// 第三方登陆返回的社交账号id
	public static final String ACCESS_TOKEN = "access_token";
	public static final String EXPIRES_IN = "expires_in";
	// 查询账户后返回的个人信息
	public static final String USER_SEARCH_ID = "user_search_id";//用来搜索查找
	public static final String USER_NICK = "user_nick";// 用户昵称
	public static final String USER_BIRTHDAY = "user_birthday";// 用户生日
	public static final String USER_CITY = "user_city";// 用户城市
	public static final String USER_PROVINCE = "user_province";// 用户省份
	public static final String USER_SEX = "user_sex";// 用户性别
	public static final String USER_HEADPIC_URL = "user_headpic_url";// 用户头像url
}
