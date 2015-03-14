package jason.wondermap;

import jason.wondermap.utils.JasonLog;

import com.baidu.frontia.Frontia;
import com.baidu.frontia.FrontiaApplication;
import com.baidu.mapapi.SDKInitializer;

public class WonderMapApplication extends FrontiaApplication {
	@Override
	public void onCreate() {
		super.onCreate();
		// 在使用SDK各组件之前初始化context信息，传入ApplicationContext
		SDKInitializer.initialize(this);
		boolean isInit = Frontia.init(this, "784FwY2gl6Wh4tIyWg639AGl");
		if (!isInit) {
			JasonLog.log("Frontia初始化失败");
		} else {
			JasonLog.log("Frontia初始化成功");
		}
	}
}
