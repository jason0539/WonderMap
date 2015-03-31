package jason.wondermap.interfacer;

public interface OnFindUserListener {
	/**
	 * 返回用户的socialid和userid（推送需要）
	 * 
	 * @param socialId
	 * @param userId
	 */
	public abstract void onSucess(String socialId, String userId);

	public abstract void onFail(int errorCode,String errMsg);
}
