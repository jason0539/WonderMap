package jason.wondermap.manager;

public class FootblogManager {

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝格式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static FootblogManager instance;

	private FootblogManager() {

	}

	public FootblogManager getInstance() {
		if (instance == null) {
			instance = new FootblogManager();
		}
		return instance;
	}
}
