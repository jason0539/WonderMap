package jason.wondermap.manager;

import android.util.Log;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaData;
import com.baidu.frontia.api.FrontiaStorage;
import com.baidu.frontia.api.FrontiaStorageListener.DataInsertListener;

public class WStorageManager {

	FrontiaStorage mCloudStorage = Frontia.getStorage();

	public void test() {
		FrontiaData newData = new FrontiaData();
		newData.put("animal", "panda");
		mCloudStorage.insertData(newData, new DataInsertListener() {
			@Override
			public void onSuccess() {
				Log.d("jason", "插入成功");
			}

			@Override
			public void onFailure(int errCode, String errMsg) {
				Log.d("jason", "错误为" + errCode + errMsg);
			}
		});
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝模式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static WStorageManager instance = null;

	private WStorageManager() {

	}

	public static WStorageManager getInstance() {
		if (instance == null) {
			instance = new WStorageManager();
		}
		return instance;
	}
}
