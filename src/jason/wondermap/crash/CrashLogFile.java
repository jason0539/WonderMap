package jason.wondermap.crash;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;

public class CrashLogFile extends BmobObject {
	private String name;// 反馈者
	private BmobFile file;// 文件

	public CrashLogFile() {
	}

	public CrashLogFile(String name, BmobFile file) {
		this.name = name;
		this.file = file;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BmobFile getFile() {
		return file;
	}

	public void setFile(BmobFile file) {
		this.file = file;
	}
}
