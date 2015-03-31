package jason.wondermap.interfacer;

public interface OnAddFriendListener {
	public abstract void onSuccess();
	public abstract void onFail(int errCode, String errMsg);
}
