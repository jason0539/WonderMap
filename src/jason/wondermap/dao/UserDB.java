package jason.wondermap.dao;

import jason.wondermap.bean.User;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * 
 * 用户信息的增删改查
 */
public class UserDB {
	private UserDBHelper helper;

	/**
	 * 用户信息的增删改查
	 */
	public UserDB(Context context) {
		helper = new UserDBHelper(context);
	}

	public User selectInfo(String userId) {
		User u = new User();
		SQLiteDatabase db = helper.getReadableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId + "" });
		if (c.moveToFirst()) {
			u.setHeadIcon(c.getString(c.getColumnIndex("img")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		} else {
			return null;
		}
		return u;
	}

	public void addUser(List<User> list) {
		SQLiteDatabase db = helper.getWritableDatabase();
		for (User u : list) {
			db.execSQL(
					"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
					new Object[] { u.getUserId(), u.getNick(), u.getHeadIcon(),
							u.getChannelId(), u.getGroup() });
		}
		db.close();
	}

	public void addUser(User u) {
		if (selectInfo(u.getUserId()) != null) {
			update(u);
			return;
		}
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"insert into user (userId,nick,img,channelId,_group) values(?,?,?,?,?)",
				new Object[] { u.getUserId(), u.getNick(), u.getHeadIcon(),
						u.getChannelId(), u.getGroup() });
		db.close();

	}

	public User getUser(String userId) {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user where userId=?",
				new String[] { userId });
		User u = new User();
		if (c.moveToNext()) {
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		}
		return u;
	}

	public void updateUser(List<User> list) {
		if (list.size() > 0) {
			delete();
			addUser(list);
		}
	}

	/**
	 * 获取所有用户
	 * 
	 * @return
	 */
	public List<User> getUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<User> list = new LinkedList<User>();
		Cursor c = db.rawQuery("select * from user", null);
		while (c.moveToNext()) {
			User u = new User();
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
			list.add(u);
		}
		c.close();
		db.close();
		return list;
	}

	/**
	 * 获取所有用户的id
	 * 
	 * @return
	 */
	public List<String> getUserIds() {
		SQLiteDatabase db = helper.getWritableDatabase();
		List<String> list = new LinkedList<String>();
		Cursor c = db.rawQuery("select userId from user", null);
		while (c.moveToNext()) {
			String userId = c.getString(c.getColumnIndex("userId"));
			list.add(userId);
		}
		c.close();
		db.close();
		return list;
	}

	public void update(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL(
				"update user set nick=?,img=?,_group=? where userId=?",
				new Object[] { u.getNick(), u.getHeadIcon(), u.getGroup(),
						u.getUserId() });
		db.close();
	}

	public User getLastUser() {
		SQLiteDatabase db = helper.getWritableDatabase();
		Cursor c = db.rawQuery("select * from user", null);
		User u = new User();
		while (c.moveToLast()) {
			u.setUserId(c.getString(c.getColumnIndex("userId")));
			u.setNick(c.getString(c.getColumnIndex("nick")));
			u.setHeadIcon(c.getString(c.getColumnIndex("img")));
			u.setChannelId(c.getString(c.getColumnIndex("channelId")));
			u.setGroup(c.getInt(c.getColumnIndex("_group")));
		}
		c.close();
		db.close();
		return u;
	}

	public void delUser(User u) {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user where userId=?",
				new Object[] { u.getUserId() });
		db.close();
	}

	public void delete() {
		SQLiteDatabase db = helper.getWritableDatabase();
		db.execSQL("delete from user");
		db.close();
	}
}
