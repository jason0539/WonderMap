package jason.wondermap.bean;

import cn.bmob.v3.BmobObject;

public class FeedBack extends BmobObject {
	// 反馈内容
	private String content;
	// 联系方式
	private String name;

	public FeedBack(String name, String content) {
		this.name = name;
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}