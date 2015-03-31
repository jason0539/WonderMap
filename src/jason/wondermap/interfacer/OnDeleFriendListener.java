package jason.wondermap.interfacer;

public interface OnDeleFriendListener {
	public abstract void onSuccess();

	public abstract void onFail(int errCode, String errMsg);
}
