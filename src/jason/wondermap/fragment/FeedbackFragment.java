package jason.wondermap.fragment;

import com.xiaomi.mistatistic.sdk.MiStatInterface;

import cn.bmob.v3.listener.SaveListener;
import jason.wondermap.R;
import jason.wondermap.helper.FeedbackHelper;
import jason.wondermap.utils.T;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class FeedbackFragment extends ContentFragment {
	private ViewGroup mRootView;
	private EditText mEditFeedbackContent;
	private Button mBtnFeedbackCommit;
	private FeedbackHelper feedbackHelper;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		mRootView = (ViewGroup) inflater.inflate(R.layout.fragment_feedback,
				mContainer, false);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		mEditFeedbackContent = (EditText) mRootView
				.findViewById(R.id.et_feedback_content);
		mBtnFeedbackCommit = (Button) mRootView
				.findViewById(R.id.btn_feedback_commit);
		mBtnFeedbackCommit.setOnClickListener(commitClickListener);
		initTopBarForLeft(mRootView, "意见反馈");
		feedbackHelper = new FeedbackHelper();
	}

	OnClickListener commitClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			String contentString = mEditFeedbackContent.getText().toString();
			if (contentString.equals("") || contentString == null) {
				T.showShort(mContext, "不能为空哟");
			} else {
				feedbackHelper.saveFeedbackMsg(contentString,
						new SaveListener() {

							@Override
							public void onSuccess() {
								T.showShort(mContext, "已经收到您的反馈");
								hideSoftInputView();
								wmFragmentManager.back(null);
							}

							@Override
							public void onFailure(int arg0, String arg1) {
								T.showShort(mContext, "提交失败了，抱歉");
							}
						});
			}
		}
	};
	@Override
	public void onResume() {
		super.onResume();
		MiStatInterface.recordPageStart(getActivity(), "反馈页");
	};
	@Override
	public void onPause() {
		super.onPause();
		MiStatInterface.recordPageEnd();
	}
}
