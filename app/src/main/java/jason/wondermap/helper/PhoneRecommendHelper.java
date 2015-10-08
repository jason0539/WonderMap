package jason.wondermap.helper;

import jason.wondermap.bean.User;
import jason.wondermap.interfacer.PhoneNumberRecommendListener;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.L;
import jason.wondermap.utils.StringUtils;
import jason.wondermap.utils.T;
import jason.wondermap.utils.WModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class PhoneRecommendHelper {
	private Context mContext;
	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 联系人列表 **/
	private HashMap<String, String> phoneNum = new HashMap<String, String>();
	private HashMap<String, User> recommedFriends = new HashMap<String, User>();
	PhoneNumberRecommendListener listener;
	int total = 0;
	int count = 0;

	public PhoneRecommendHelper(Context context) {
		mContext = context;
	}

	public void fetchRecommendFriends(PhoneNumberRecommendListener dListener) {
		listener = dListener;
		new Thread(new Runnable() {
			public void run() {
				getPhoneContacts();
				L.d(WModel.PhoneNumber, "下面是手机卡");
				getSIMContacts();
				L.d(WModel.PhoneNumber,
						"共添加" + count + "次，实际联系人" + phoneNum.size() + "个");
				total = phoneNum.size();
				getRecommed();
			}
		}).start();
	}

	private void getRecommed() {
		for (Map.Entry<String, String> entry : phoneNum.entrySet()) {
			BmobQuery<User> query = new BmobQuery<User>();
			// query.setCachePolicy(CachePolicy.NETWORK_ONLY);
			query.addWhereEqualTo("phone", entry.getKey());
			// query.addWhereContainedIn("phone", mContactsNumber);
			query.findObjects(mContext, new FindListener<User>() {

				@Override
				public void onSuccess(List<User> list) {
					count++;
					for (int i = 0; i < list.size(); i++) {
						L.d(WModel.PhoneNumber, list.get(i).getUsername()
								+ "是你的" + list.get(i).getPhone() + "联系人，名字叫"
								+ phoneNum.get(list.get(i).getPhone()));
					}
					if (CollectionUtils.isNotNull(list)) {
						if (list.size() == 1) {
							if (AccountUserManager.getInstance()
									.getContactList()
									.containsKey(list.get(0).getObjectId())
									|| list.get(0)
											.getObjectId()
											.equals(AccountUserManager
													.getInstance()
													.getCurrentUserid())) {
								// 已经是好友，或者查到的是自己，则跳过
								return;
							}
							recommedFriends.put(list.get(0).getObjectId(),
									list.get(0));
						} else {
							for (User user : list) {
								if (AccountUserManager.getInstance()
										.getContactList()
										.containsKey(user.getObjectId())
										|| user.getObjectId().equals(
												AccountUserManager
														.getInstance()
														.getCurrentUserid())) {
									continue;
								}
								recommedFriends.put(user.getObjectId(), user);
							}
						}
					} else {
						// L.d(WModel.PhoneNumber, "");
					}
					listener.onProgress(total, count);
					if (count == total) {
						listener.onSuccess(recommedFriends);
					}
				}

				@Override
				public void onError(int arg0, String arg1) {
					L.d(WModel.PhoneNumber, arg0 + arg1);
				}
			});
		}
	}

	public HashMap<String, String> getPhoneNumList() {
		return phoneNum;
	}

	public HashMap<String, User> getRecommedList() {
		return recommedFriends;
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = mContext.getContentResolver();

		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				if (StringUtils.isPhoneNumber(phoneNumber)) {
					L.d(WModel.PhoneNumber,
							contactName
									+ "的手机号是"
									+ StringUtils
											.getPhoneNumberFromString(phoneNumber));
				}
				phoneNum.put(phoneNumber, contactName);
			}

			phoneCursor.close();
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = mContext.getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);

		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				if (StringUtils.isPhoneNumber(phoneNumber)) {
					L.d(WModel.PhoneNumber,
							contactName
									+ "的手机号是"
									+ StringUtils
											.getPhoneNumberFromString(phoneNumber));
				}

				phoneNum.put(phoneNumber, contactName);
			}
			phoneCursor.close();
		}
	}
}
