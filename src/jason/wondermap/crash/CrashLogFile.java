package jason.wondermap.crash;

import jason.wondermap.bean.User;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class CrashLogFile extends BmobObject {
	private User author;// 反馈者
	private BmobFile file;// 文件

	public CrashLogFile() {
	}

	public CrashLogFile(User author, BmobFile file) {
		this.author = author;
		this.file = file;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public BmobFile getFile() {
		return file;
	}

	public void setFile(BmobFile file) {
		this.file = file;
	}
}
