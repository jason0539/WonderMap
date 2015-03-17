package jason.wondermap.dao;

import jason.wondermap.bean.ChatMessage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * 消息数据的增删改查
 * 
 */
public class MessageDB {
	/**
	 * 常量
	 */
	private static final String COL_MESSAGE = "message";
	// 1：from ; 0:to
	private static final String COL_IS_COMING = "is_coming";
	private static final String COL_USER_ID = "user_id";
	private static final String COL_ICON = "icon";
	private static final String COL_NICKNAME = "nickname";
	// 1:readed ; 0 unreaded ;
	private static final String COL_READED = "readed";
	private static final String COL_DATE = "date";

	private static final String DB_NAME = "message.db";
	private SQLiteDatabase mDb;

	/**
	 * 消息数据的增删改查
	 * 
	 */
	public MessageDB(Context context) {
		mDb = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
	}

	/**
	 * 为每个用户根据其userId创建一张消息表
	 * 
	 * @param userId
	 * @param chatMessage
	 */
	public void add(String userId, ChatMessage chatMessage) {
		createTable(userId);

		int isComing = chatMessage.isComing() ? 1 : 0;
		int readed = chatMessage.isReaded() ? 1 : 0;
		mDb.execSQL(
				"insert into _" + userId + " (" + COL_USER_ID + "," + COL_ICON
						+ "," + COL_IS_COMING + "," + COL_MESSAGE + ","
						+ COL_NICKNAME + "," + COL_READED + "," + COL_DATE
						+ ") values(?,?,?,?,?,?,?)",
				new Object[] { chatMessage.getUserId(), chatMessage.getIcon(),
						isComing, chatMessage.getMessage(),
						chatMessage.getNickname(), readed,
						chatMessage.getDateStr() });
	}

	public List<ChatMessage> find(String userId, int currentPage, int pageSize) {
		List<ChatMessage> chatMessages = new ArrayList<ChatMessage>();
		createTable(userId);
		int start = (currentPage - 1) * pageSize;
		int end = start + pageSize;
		// 取最后的10条
		String sql = "select * from _" + userId + " order by _id  desc limit  "
				+ start + " , " + end;
		Cursor c = mDb.rawQuery(sql, null);
		ChatMessage chatMessage = null;
		while (c.moveToNext()) {
			String dateStr = c.getString(c.getColumnIndex(COL_DATE));
			String icon = c.getString(c.getColumnIndex(COL_ICON));
			int isComingVal = c.getInt(c.getColumnIndex(COL_IS_COMING));
			String message = c.getString(c.getColumnIndex(COL_MESSAGE));
			String nickname = c.getString(c.getColumnIndex(COL_NICKNAME));
			int readedVal = c.getInt(c.getColumnIndex(COL_READED));
			String _userId = c.getString(c.getColumnIndex(COL_USER_ID));

			chatMessage = new ChatMessage(message, isComingVal == 1, _userId,
					icon, nickname, readedVal == 1, dateStr);

			chatMessages.add(chatMessage);
		}
		Collections.reverse(chatMessages);
		return chatMessages;
	}

	public Map<String, Integer> getUserUnReadMsgs(List<String> userIds) {

		Map<String, Integer> userUnReadMsgs = new HashMap<String, Integer>();
		for (String userId : userIds) {
			int count = getUnreadedMsgsCountByUserId(userId);
			userUnReadMsgs.put(userId, count);
		}

		return userUnReadMsgs;
	}

	/**根据userId获取用户未读消息
	 * @param userId
	 * @return
	 */
	private int getUnreadedMsgsCountByUserId(String userId) {
		createTable(userId);
		String sql = "select count(*) as count from _" + userId + " where "
				+ COL_IS_COMING + " = 1 and " + COL_READED + " = 0";
		Cursor c = mDb.rawQuery(sql, null);
		int count = 0;
		if (c.moveToNext())
			count = c.getInt(c.getColumnIndex("count"));
		Log.e("getUnreadedMsgsCountByUserId", userId + " , " + count);
		c.close();
		return count;
	}

	/**
	 * 更新已读标志位
	 */
	public void updateReaded(String userId) {
		createTable(userId);
		mDb.execSQL("update  _" + userId + " set " + COL_READED + " = 1 where "
				+ COL_READED + " = 0 ", new Object[] {});
	}

	private void createTable(String userId) {
		mDb.execSQL("CREATE table IF NOT EXISTS _" + userId
				+ " (_id INTEGER PRIMARY KEY AUTOINCREMENT, " //
				+ COL_USER_ID + " TEXT, " //
				+ COL_ICON + " TEXT, "//
				+ COL_IS_COMING + " integer ,"//
				+ COL_MESSAGE + " text , " //
				+ COL_NICKNAME + " text , " //
				+ COL_DATE + " text , "//
				+ COL_READED + " integer); ");//
	}

	public void close() {
		if (mDb != null && mDb.isOpen())
			mDb.close();
	}

}
