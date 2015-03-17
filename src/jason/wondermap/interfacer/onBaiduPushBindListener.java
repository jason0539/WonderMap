package jason.wondermap.interfacer;

public interface onBaiduPushBindListener {
	/**
	 * @param userId
	 *            绑定云推送成功后返回的用户id
	 * @param errorCode
	 *            绑定成功或失败的返回值
	 */
	public abstract void onBind(String userId, int errorCode);
}
