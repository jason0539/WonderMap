package jason.wondermap.bean;

import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

public class User extends BmobChatUser {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 发布的博客列表
	 */
	private BmobRelation blogs;
	private BmobRelation favorite;//收藏
	/**
	 * 显示数据拼音的首字母
	 */
	private String sortLetters;

	/**
	 * 性别-true-男
	 */
	private boolean sex;

	private Blog blog;

	/**
	 * 地理坐标
	 */
	private BmobGeoPoint location;//

	private Integer hight;

	public BmobRelation getFavorite() {
		return favorite;
	}

	public void setFavorite(BmobRelation favorite) {
		this.favorite = favorite;
	}

	public Blog getBlog() {
		return blog;
	}

	public void setBlog(Blog blog) {
		this.blog = blog;
	}

	public Integer getHight() {
		return hight;
	}

	public void setHight(Integer hight) {
		this.hight = hight;
	}

	public BmobRelation getBlogs() {
		return blogs;
	}

	public void setBlogs(BmobRelation blogs) {
		this.blogs = blogs;
	}

	public BmobGeoPoint getLocation() {
		return location;
	}

	public void setLocation(BmobGeoPoint location) {
		this.location = location;
	}

	public boolean getSex() {
		return sex;
	}

	/**
	 * true 为男
	 * 
	 * @param sex
	 */
	public void setSex(boolean sex) {
		this.sex = sex;
	}

	public String getSortLetters() {
		return sortLetters;
	}

	public void setSortLetters(String sortLetters) {
		this.sortLetters = sortLetters;
	}

}
