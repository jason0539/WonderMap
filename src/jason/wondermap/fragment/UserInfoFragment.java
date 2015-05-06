package jason.wondermap.fragment;

import jason.wondermap.R;
import jason.wondermap.bean.User;
import jason.wondermap.config.BundleTake;
import jason.wondermap.config.WMapConstants;
import jason.wondermap.manager.AccountUserManager;
import jason.wondermap.manager.PushMsgSendManager;
import jason.wondermap.utils.CollectionUtils;
import jason.wondermap.utils.ImageLoadOptions;
import jason.wondermap.utils.L;
import jason.wondermap.utils.PhotoUtil;
import jason.wondermap.utils.UserInfo;
import jason.wondermap.utils.WModel;
import jason.wondermap.view.dialog.DialogTips;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.bmob.im.BmobChatManager;
import cn.bmob.im.BmobUserManager;
import cn.bmob.im.bean.BmobChatUser;
import cn.bmob.im.config.BmobConfig;
import cn.bmob.im.db.BmobDB;
import cn.bmob.im.util.BmobLog;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 资料页面
 * 
 * @author liuzhenhui 目前策略是，自己只显示“查看足迹” 好友显示“查看足迹，发起会话，黑名单”
 *         陌生人显示“查看足迹，加为好友，发起会话” 因为陌生人添加黑名单有问题，这里先设定陌生人不能加入黑名单
 * 
 */
public class UserInfoFragment extends ContentFragment implements
		OnClickListener {
	private TextView tv_set_name, tv_set_age, tv_user_signs, tv_editinfo_tips,
			tv_user_phone;
	private CheckBox tv_set_gender;
	private ImageView iv_set_avator;

	private Button btn_chat, btn_black, btn_add_friend, btn_browse_footblog,
			btn_confirm_info;
	private RelativeLayout layout_head, layout_age, layout_gender,
			layout_signs, layout_black_tips, layout_name, layout_phoneNumber;
	private BmobUserManager userManager;
	boolean isMyself, isNeedToEdit, isFriends, isStranger;
	private String userid = "";
	private String username = "";
	private User user;
	private ViewGroup mRootView;

	@Override
	protected View onCreateContentView(LayoutInflater inflater) {
		userManager = AccountUserManager.getInstance().getUserManager();
		mRootView = (ViewGroup) inflater.inflate(R.layout.activity_set_info,
				mContainer, false);
		userid = mShowBundle.getString(UserInfo.USER_ID);
		return mRootView;
	}

	@Override
	protected void onInitView() {
		findviews();
		if (userid.equals(AccountUserManager.getInstance().getCurrentUserid())) {
			isMyself = true;
			if (mShowBundle.containsKey(BundleTake.NeedToEditInfo)) {
				isNeedToEdit = true;
				viewForConfirmInfo();
			} else {
				viewForMyself();
			}
			initMyData();
		} else {// 来自他人则根据策略显示
			if (AccountUserManager.getInstance().getContactList()
					.containsKey(userid)) {// 是好友，不显示加为好友
				isFriends = true;
				viewForFriends();
			} else {
				isStranger = true;
				viewForStrangers();
			}
			initOtherData(userid);
		}
	}

	private void initOtherData(String id) {
		BmobQuery<User> query = new BmobQuery<User>();
		query.addWhereEqualTo("objectId", id);
		query.findObjects(mContext, new FindListener<User>() {
			@Override
			public void onSuccess(List<User> object) {
				if (object != null && object.size() > 0) {
					user = object.get(0);
					username = user.getUsername();
					updateUser(user);
				} else {
					ShowLog("该用户不存在");
				}
			}

			@Override
			public void onError(int code, String msg) {
				// TODO Auto-generated method stub
				ShowLog("获取用户信息失败" + msg);
			}
		});
	}

	private void initMyData() {
		user = AccountUserManager.getInstance().getCurrentUser();
		username = user.getUsername();
		updateUser(user);
	}

	private void updateUser(User user) {
		// 更改
		refreshAvatar(user.getAvatar());
		tv_set_name.setText(user.getUsername());
		tv_set_age.setText(user.getAge() + "岁");
		String phone = user.getPhone();
		if (phone != null && !phone.equals("")) {
			tv_user_phone.setText(phone);
		}
		String sign = user.getSignature();
		if (sign != null && !sign.equals("")) {
			tv_user_signs.setText(sign);
		}
		tv_set_gender.setChecked(user.getSex());
		// 检测是否为黑名单用户,之后把陌生人这个去掉，只要不是自己，都能加入黑名单
		if (!isMyself && !isStranger) {
			if (BmobDB.create(mContext).isBlackUser(user.getUsername())) {
				btn_black.setVisibility(View.GONE);
				layout_black_tips.setVisibility(View.VISIBLE);
			} else {
				btn_black.setVisibility(View.VISIBLE);
				layout_black_tips.setVisibility(View.GONE);
			}
		}
	}

	/**
	 * 更新头像 refreshAvatar
	 */
	private void refreshAvatar(String avatar) {
		if (avatar != null && !avatar.equals("")) {
			ImageLoader.getInstance().displayImage(avatar, iv_set_avator,
					ImageLoadOptions.getOptions());
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (isMyself) {
			initMyData();// 修改完之后接着刷新
		}
	}

	@Override
	public void onClick(View v) {
		if (user == null) {
			ShowToast("正在加载信息，请稍等");
			return;
		}
		switch (v.getId()) {
		case R.id.btn_chat:// 发起聊天
			Bundle bundle = new Bundle();
			bundle.putString(UserInfo.AVATAR, user.getAvatar());
			bundle.putString(UserInfo.USER_NAME, user.getUsername());
			bundle.putString(UserInfo.USER_ID, user.getObjectId());
			wmFragmentManager.showFragment(WMFragmentManager.TYPE_CHAT, bundle);
			break;
		case R.id.layout_head:
			if (isMyself) {
				showAvatarPop();
			} else {
				ArrayList<String> photos = new ArrayList<String>();
				//这里头像可能为空
				photos.add(user.getAvatar());
				Bundle bundle1 = new Bundle();
				bundle1.putStringArrayList(UserInfo.PHOTOS, photos);
				bundle1.putInt(UserInfo.POSITION, 0);
				BaseFragment.getWMFragmentManager().showFragment(
						WMFragmentManager.TYPE_IMAGE_BROWSER, bundle1);
			}
			break;
		case R.id.layout_age:
			Bundle nickBundle = new Bundle();
			nickBundle.putString(BundleTake.InfoToEdit, UserInfo.AGE);
			wmFragmentManager.showFragment(
					WMFragmentManager.TYPE_UPDATE_USERINFO, nickBundle);
			// addBlog();
			break;
		case R.id.user_sign:
			Bundle sign = new Bundle();
			sign.putString(BundleTake.InfoToEdit, UserInfo.SIGN);
			wmFragmentManager.showFragment(
					WMFragmentManager.TYPE_UPDATE_USERINFO, sign);
			break;
		case R.id.layout_phone_number:
			Bundle phone = new Bundle();
			phone.putString(BundleTake.InfoToEdit, UserInfo.USER_PHONENUMBER);
			wmFragmentManager.showFragment(
					WMFragmentManager.TYPE_UPDATE_USERINFO, phone);
			break;
		case R.id.btn_browse_footblog:
			Bundle bundle2 = new Bundle();
			bundle2.putSerializable(BundleTake.FootblogOfUser, user);
			wmFragmentManager.showFragment(
					WMFragmentManager.TYPE_PERSONAL_FOOTBLOG, bundle2);
			break;
		case R.id.layout_gender:// 性别
			if (tv_set_gender.isChecked()) {
				tv_set_gender.setChecked(false);
				updateSex(false);
			} else {
				tv_set_gender.setChecked(true);
				updateSex(true);
			}
			break;
		case R.id.btn_back:// 黑名单
			L.d("点击黑名单");
			showBlackDialog(user.getUsername());
			break;
		case R.id.btn_add_friend:// 添加好友
			addFriend();
			break;
		case R.id.btn_confirm_info:
			confirmInfo();
			break;
		case R.id.layout_name:
			Bundle name = new Bundle();
			name.putString(BundleTake.InfoToEdit, UserInfo.USER_NAME);
			wmFragmentManager.showFragment(
					WMFragmentManager.TYPE_UPDATE_USERINFO, name);
			break;
		default:
			break;
		}
		L.d(WModel.clickUseless, "click");
	}

	/**
	 * 确认信息
	 */
	private void confirmInfo() {
		AccountUserManager.getInstance().confirmCurrentUserInfo(
				new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("信息已确认,进入地图");
						AccountUserManager
								.getInstance()
								.getUserManager()
								.bindInstallationForRegister(
										AccountUserManager.getInstance()
												.getCurrentUserName());
						// // 确认信息后马上sayhello，更新信息给对方
						// PushMsgSendManager.getInstance().sayHello();
						wmFragmentManager.back(null);
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("确认失败，请重试");
					}
				});
		BmobUserManager.getInstance(mContext).addContactAfterAgree("活点地图",
				new FindListener<BmobChatUser>() {

					@Override
					public void onError(int arg0, final String arg1) {

					}

					@Override
					public void onSuccess(List<BmobChatUser> arg0) {
						// method stub
						// 保存到内存中
						AccountUserManager.getInstance().setContactList(
								CollectionUtils.list2map(BmobDB
										.create(mContext).getContactList()));
					}
				});
	}

	/**
	 * 修改资料 updateInfo
	 */
	private void updateSex(boolean which) {
		AccountUserManager.getInstance().updateCurrentUserSex(which,
				new UpdateListener() {
					@Override
					public void onSuccess() {
						ShowToast("修改成功");
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("修改失败" + arg1);
					}
				});
	}

	/**
	 * 添加好友请求
	 */
	private void addFriend() {
		if (mContext == null || user == null) {
			L.d("UserInfoFragment 中的addFriend 方法出现空指针");
			ShowToast("请求失败，请重试");
			return;
		}
		final ProgressDialog progress = new ProgressDialog(getActivity());
		progress.setMessage("正在添加...");
		progress.setCanceledOnTouchOutside(false);
		progress.show();
		// 发送tag请求
		BmobChatManager.getInstance(mContext).sendTagMessage(
				BmobConfig.TAG_ADD_CONTACT, user.getObjectId(),
				new PushListener() {

					@Override
					public void onSuccess() {
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
					}

					@Override
					public void onFailure(int arg0, final String arg1) {
						// TODO Auto-generated method stub
						progress.dismiss();
						ShowToast("发送请求成功，等待对方验证！");
						ShowLog("发送请求失败:" + arg1);
					}
				});
	}

	/**
	 * 显示黑名单提示框
	 */
	private void showBlackDialog(final String username) {
		DialogTips dialog = new DialogTips(getActivity(), "加入黑名单",
				"加入黑名单，你将不再收到对方的消息，确定要继续吗？", "确定", true, true);
		dialog.SetOnSuccessListener(new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialogInterface, int userId) {
				userManager.addBlack(username, new UpdateListener() {

					@Override
					public void onSuccess() {
						ShowToast("黑名单添加成功!");
						btn_black.setVisibility(View.GONE);
						layout_black_tips.setVisibility(View.VISIBLE);
						// 重新设置下内存中保存的好友列表
						AccountUserManager.getInstance().setContactList(
								CollectionUtils.list2map(BmobDB
										.create(mContext).getContactList()));
					}

					@Override
					public void onFailure(int arg0, String arg1) {
						ShowToast("黑名单添加失败:" + arg1);
					}
				});
			}
		});
		// 显示确认对话框
		dialog.show();
		dialog = null;
	}

	RelativeLayout layout_choose;
	RelativeLayout layout_photo;
	PopupWindow avatorPop;

	public String filePath = "";
	AlertDialog albumDialog;
	String dateTime;

	private void showAvatarPop() {
		albumDialog = new AlertDialog.Builder(getActivity()).create();
		albumDialog.setCanceledOnTouchOutside(true);
		View v = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_usericon, null);
		albumDialog.show();
		albumDialog.setContentView(v);
		albumDialog.getWindow().setGravity(Gravity.CENTER);

		TextView albumPic = (TextView) v.findViewById(R.id.album_pic);
		TextView cameraPic = (TextView) v.findViewById(R.id.camera_pic);
		albumPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				albumDialog.dismiss();
				Intent intent = new Intent(Intent.ACTION_PICK, null);
				intent.setDataAndType(
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
				startActivityForResult(intent,
						WMapConstants.REQUESTCODE_UPLOADAVATAR_LOCATION);
			}
		});
		cameraPic.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				albumDialog.dismiss();
				File dir = new File(WMapConstants.MyAvatarDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				// 原图
				File file = new File(dir, new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()));
				filePath = file.getAbsolutePath();// 获取相片的保存路径
				Uri imageUri = Uri.fromFile(file);

				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
				startActivityForResult(intent,
						WMapConstants.REQUESTCODE_UPLOADAVATAR_CAMERA);
			}
		});
	}

	/**
	 * @Title: startImageAction
	 */
	private void startImageAction(Uri uri, int outputX, int outputY,
			int requestCode, boolean isCrop) {
		Intent intent = null;
		if (isCrop) {
			intent = new Intent("com.android.camera.action.CROP");
		} else {
			intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		}
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("return-data", true);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, requestCode);
	}

	Bitmap newBitmap;
	boolean isFromCamera = false;// 区分拍照旋转
	int degree = 0;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case WMapConstants.REQUESTCODE_UPLOADAVATAR_CAMERA:// 拍照修改头像
			if (resultCode == Activity.RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = true;
				File file = new File(filePath);
				degree = PhotoUtil.readPictureDegree(file.getAbsolutePath());
				Log.i("life", "拍照后的角度：" + degree);
				startImageAction(Uri.fromFile(file), 200, 200,
						WMapConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			}
			break;
		case WMapConstants.REQUESTCODE_UPLOADAVATAR_LOCATION:// 本地修改头像
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			Uri uri = null;
			if (data == null) {
				return;
			}
			if (resultCode == Activity.RESULT_OK) {
				if (!Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					ShowToast("SD不可用");
					return;
				}
				isFromCamera = false;
				uri = data.getData();
				startImageAction(uri, 200, 200,
						WMapConstants.REQUESTCODE_UPLOADAVATAR_CROP, true);
			} else {
				ShowToast("照片获取失败");
			}

			break;
		case WMapConstants.REQUESTCODE_UPLOADAVATAR_CROP:// 裁剪头像返回
			// TODO sent to crop
			if (avatorPop != null) {
				avatorPop.dismiss();
			}
			if (data == null) {
				// Toast.makeText(this, "取消选择", Toast.LENGTH_SHORT).show();
				return;
			} else {
				saveCropAvator(data);
			}
			// 初始化文件路径
			filePath = "";
			// 上传头像
			uploadAvatar();
			break;
		default:
			break;

		}
	}

	private void uploadAvatar() {
		BmobLog.i("头像地址：" + path);
		final BmobFile bmobFile = new BmobFile(new File(path));
		bmobFile.upload(mContext, new UploadFileListener() {

			@Override
			public void onSuccess() {
				String url = bmobFile.getFileUrl(mContext);
				// 更新BmobUser对象
				updateUserAvatar(url);
			}

			@Override
			public void onProgress(Integer arg0) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onFailure(int arg0, String msg) {
				// TODO Auto-generated method stub
				ShowToast("头像上传失败：" + msg);
			}
		});
	}

	private void updateUserAvatar(final String url) {
		AccountUserManager.getInstance().updateCurrentUserAvatar(url,
				new UpdateListener() {
					@Override
					public void onSuccess() {
						// PushMsgSendManager.getInstance().sayHello();
						ShowToast("头像更新成功！");
						refreshAvatar(url);
					}

					@Override
					public void onFailure(int code, String msg) {
						ShowToast("头像更新失败：" + msg);
					}
				});
	}

	String path;

	/**
	 * 保存裁剪的头像
	 * 
	 * @param data
	 */
	private void saveCropAvator(Intent data) {
		if (data == null) {
			ShowToast("换张头像吧，这张好像有问题");
			return;
		}
		Bundle extras = data.getExtras();
		if (extras != null) {
			Bitmap bitmap = extras.getParcelable("data");
			Log.i("life", "avatar - bitmap = " + bitmap);
			if (bitmap != null) {
				bitmap = PhotoUtil.toRoundCorner(bitmap, 10);
				if (isFromCamera && degree != 0) {
					bitmap = PhotoUtil.rotaingImageView(degree, bitmap);
				}
				iv_set_avator.setImageBitmap(bitmap);
				// 保存图片
				String filename = new SimpleDateFormat("yyMMddHHmmss")
						.format(new Date()) + ".png";
				path = WMapConstants.MyAvatarDir + filename;
				PhotoUtil.saveBitmap(WMapConstants.MyAvatarDir, filename,
						bitmap, true);
				// 上传头像
				if (bitmap != null && bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
		}
	}

	@Override
	public boolean onBackPressed() {
		if (isNeedToEdit) {
			return true;
		}
		return super.onBackPressed();
	}

	private void fetchMyPhoneNumber() {
		// TODO Auto-generated method stub

	}

	// ＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝UI显示策略＝＝＝＝＝＝＝＝＝＝＝＝＝＝＝
	private void viewForConfirmInfo() {
		fetchMyPhoneNumber();
		initTopBarForOnlyTitle(mRootView, "编辑资料");// 标题，没有返回键
		btn_confirm_info.setVisibility(View.VISIBLE);// 显示确认资料键
		tv_editinfo_tips.setVisibility(View.VISIBLE);// 显示确认资料键
		layout_name.setEnabled(true);
		layout_age.setEnabled(true);
		// layout_head.setEnabled(true);
		layout_gender.setEnabled(true);
		layout_signs.setEnabled(true);
		layout_phoneNumber.setEnabled(true);
		layout_phoneNumber.setVisibility(View.VISIBLE);
	}

	private void viewForMyself() {
		initTopBarForLeft(mRootView, "我的资料");
		btn_browse_footblog.setVisibility(View.VISIBLE);
		layout_name.setEnabled(true);
		layout_age.setEnabled(true);
		// layout_head.setEnabled(true);
		layout_gender.setEnabled(true);
		layout_signs.setEnabled(true);
		layout_phoneNumber.setVisibility(View.VISIBLE);
		layout_phoneNumber.setEnabled(true);
	}

	private void viewForFriends() {
		initTopBarForLeft(mRootView, "详细资料");
		btn_chat.setVisibility(View.VISIBLE);
		btn_black.setVisibility(View.VISIBLE);
		btn_browse_footblog.setVisibility(View.VISIBLE);
	}

	private void viewForStrangers() {
		initTopBarForLeft(mRootView, "详细资料");
		btn_chat.setVisibility(View.VISIBLE);
		btn_browse_footblog.setVisibility(View.VISIBLE);
		btn_add_friend.setVisibility(View.VISIBLE);
	}

	private void findviews() {
		iv_set_avator = (ImageView) mRootView.findViewById(R.id.iv_set_avator);
		tv_set_gender = (CheckBox) mRootView.findViewById(R.id.tv_set_gender);
		tv_set_name = (TextView) mRootView.findViewById(R.id.tv_set_name);
		tv_set_age = (TextView) mRootView.findViewById(R.id.tv_set_age);
		tv_user_signs = (TextView) mRootView.findViewById(R.id.user_sign_text);
		tv_user_phone = (TextView) mRootView
				.findViewById(R.id.tv_set_phone_number);
		layout_head = (RelativeLayout) mRootView.findViewById(R.id.layout_head);
		layout_age = (RelativeLayout) mRootView.findViewById(R.id.layout_age);
		layout_gender = (RelativeLayout) mRootView
				.findViewById(R.id.layout_gender);
		layout_signs = (RelativeLayout) mRootView.findViewById(R.id.user_sign);
		layout_phoneNumber = (RelativeLayout) mRootView
				.findViewById(R.id.layout_phone_number);
		layout_name = (RelativeLayout) mRootView.findViewById(R.id.layout_name);
		// 黑名单提示语
		layout_black_tips = (RelativeLayout) mRootView
				.findViewById(R.id.layout_black_tips);
		btn_chat = (Button) mRootView.findViewById(R.id.btn_chat);
		btn_black = (Button) mRootView.findViewById(R.id.btn_back);
		btn_add_friend = (Button) mRootView.findViewById(R.id.btn_add_friend);
		btn_confirm_info = (Button) mRootView
				.findViewById(R.id.btn_confirm_info);
		tv_editinfo_tips = (TextView) mRootView
				.findViewById(R.id.tv_editinfo_tips);
		btn_browse_footblog = (Button) mRootView
				.findViewById(R.id.btn_browse_footblog);
		// 监听器
		layout_head.setOnClickListener(this);
		layout_name.setOnClickListener(this);
		layout_gender.setOnClickListener(this);
		layout_age.setOnClickListener(this);
		layout_signs.setOnClickListener(this);
		layout_phoneNumber.setOnClickListener(this);
		btn_browse_footblog.setOnClickListener(this);
		btn_confirm_info.setOnClickListener(this);
		btn_add_friend.setOnClickListener(this);
		btn_black.setOnClickListener(this);
		btn_chat.setOnClickListener(this);
		// 不可用
		layout_age.setEnabled(false);
		// layout_head.setEnabled(false);//头像头可以点击，自己的点击设置，别人的放大查看
		layout_gender.setEnabled(false);
		layout_age.setEnabled(false);
		layout_name.setEnabled(false);
		layout_signs.setEnabled(false);
		layout_phoneNumber.setEnabled(false);
	}

}
