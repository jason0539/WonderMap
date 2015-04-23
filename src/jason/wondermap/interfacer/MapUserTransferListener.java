package jason.wondermap.interfacer;

import android.view.View;
import jason.wondermap.bean.MapUser;

public interface MapUserTransferListener {
	/**
	 * 转换成功，返回Mapuser
	 * @param view 
	 */
	public void onSuccess(MapUser user);
}
