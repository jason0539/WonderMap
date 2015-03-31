package jason.wondermap.interfacer;

public interface OnCreateRoleListener {
	public abstract void onSuccess();

	public abstract void onFail(int errCode, String errMsg);
}
