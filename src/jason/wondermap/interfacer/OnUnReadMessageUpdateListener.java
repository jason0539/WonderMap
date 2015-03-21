package jason.wondermap.interfacer;

/**
 * 提供未读消息更新的回调，比如来了一个新消息或者用户点击查看某个用户的消息
 * 
 * @author zhy
 * 
 */
public interface OnUnReadMessageUpdateListener {
	void unReadMessageUpdate(int count);
}
