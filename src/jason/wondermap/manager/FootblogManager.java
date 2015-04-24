package jason.wondermap.manager;

import jason.wondermap.bean.Blog;

public class FootblogManager {
	private Blog currentBlog = null;

	public Blog getCurrentBlog() {
		return currentBlog;
	}

	public void setCurrentBlog(Blog blog) {
		this.currentBlog = blog;
	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝格式化代码＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private static FootblogManager instance;

	private FootblogManager() {

	}

	public static FootblogManager getInstance() {
		if (instance == null) {
			instance = new FootblogManager();
		}
		return instance;
	}
}
