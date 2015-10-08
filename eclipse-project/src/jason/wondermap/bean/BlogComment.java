package jason.wondermap.bean;

import cn.bmob.v3.BmobObject;

public class BlogComment  extends BmobObject{
	
	public static final String TAG = "Comment";

	private User user;
	private String commentContent;
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
	public String getCommentContent() {
		return commentContent;
	}
	public void setCommentContent(String commentContent) {
		this.commentContent = commentContent;
	}
}
