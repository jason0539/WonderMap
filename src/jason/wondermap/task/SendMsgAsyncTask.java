package jason.wondermap.task;

import jason.wondermap.R;
import jason.wondermap.WonderMapApplication;
import jason.wondermap.interfacer.OnHelloMessageSendSuccessListener;
import jason.wondermap.server.BaiduPush;
import jason.wondermap.utils.L;
import jason.wondermap.utils.NetUtil;
import jason.wondermap.utils.T;

import java.util.Timer;
import java.util.TimerTask;

import android.os.AsyncTask;
import android.text.TextUtils;

/**
 * 异步消息发送任务
 */
public class SendMsgAsyncTask {
	private BaiduPush mBaiduPush;
	private String mMessage;
	private MyAsyncTask mTask;
	private Timer failReSendTimer;
	private String mUserId;
	private OnHelloMessageSendSuccessListener mListener;

	public void setOnHelloMessageSendScuessListener(
			OnHelloMessageSendSuccessListener listener) {
		this.mListener = listener;
	}

	/**
	 * 构造一个消息发送任务，失败自动重发，直到成功
	 * 
	 * @param jsonMsg
	 *            消息内容
	 * @param useId
	 *            用户id
	 */
	public SendMsgAsyncTask(String jsonMsg, String useId) {
		// TODO Auto-generated constructor stub
		mBaiduPush = WonderMapApplication.getInstance().getBaiduPush();
		mMessage = jsonMsg;
		mUserId = useId;
		failReSendTimer = new Timer();
	}

	// 发送
	public void send() {
		if (NetUtil.isNetConnected(WonderMapApplication.getInstance())) {// 如果网络可用
			mTask = new MyAsyncTask();
			mTask.execute();
		} else {
			// 网络不可用，UI提醒
			T.showLong(WonderMapApplication.getInstance(),
					R.string.net_error_tip);
		}
	}

	// 停止
	public void stop() {
		if (mTask != null)
			mTask.cancel(true);
	}

	class MyAsyncTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... message) {
			String result = "";
			if (TextUtils.isEmpty(mUserId))
				result = mBaiduPush.PushMessage(mMessage);
			else
				result = mBaiduPush.PushMessage(mMessage, mUserId);
			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			L.i("send msg result:" + result);
			if (result.contains(BaiduPush.SEND_MSG_ERROR)) {// 如果消息发送失败，则100ms后重发
				
				failReSendTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						L.i("resend msg...");
						send();// 重发
					}
				}, 100);
			} else {
				if (mListener != null)
					mListener.sendScuess();
			}
		}
	}
}
