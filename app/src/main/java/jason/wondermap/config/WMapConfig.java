package jason.wondermap.config;

/**
 * 存储应用的属性配置，appid等
 * 
 * @author liuzhenhui
 * 
 */
public class WMapConfig {
	// 这是Bmob的ApplicationId,用于初始化操作
	////	public static String applicationId = "97d9b9786bc2ce1cddf8dddfe81a5d29";
	public static String applicationId = "62895bc89db57fd7bb5afb7ee6ca4962";
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝qq==========================
	public static String qqAppId = "100394363";// qq sdk申请的应用id
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝weibo==========================
	public static String weiboAppId = "999019798";// weibo sdk申请的应用id
	public static final String REDIRECT_URL = "http://huodianditu.bmob.cn";// 应用的回调页
	public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog," + "invitation_write";
	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝xiaomi==========================
	public static String MiAppId = "2882303761517328500";
	public static String MiAppKey = "5881732890500";
	public static String CHANNEL_MI = "Mi";
	public static String CHANNEL_BMOB = "Bmob";
}
