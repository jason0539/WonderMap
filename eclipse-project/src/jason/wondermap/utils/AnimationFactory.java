package jason.wondermap.utils;

import jason.wondermap.R;
import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class AnimationFactory {
	/* 空动画 */
	public final static int ANIM_EMPTY = 0x0000;
	/* 系统动画 */
	public final static int ANIM_FADE_IN = 0x0001;
	public final static int ANIM_FADE_OUT = 0x0002;
	public final static int ANIM_SLIDE_IN_LEFT = 0x0003;
	public final static int ANIM_SLIDE_OUT_LEFT = 0x0004;
	public final static int ANIM_SLIDE_IN_RIGHT = 0x0005;
	public final static int ANIM_SLIDE_OUT_RIGHT = 0x0006;
	/* 自定义动画 */
	public final static int ANIM_POP_IN = 0x0011;
	public final static int ANIM_POP_OUT = 0x0012;
	public final static int ANIM_UP_IN = 0x0013;
	public final static int ANIM_UP_OUT = 0x0014;
	public final static int ANIM_DOWN_IN = 0x0015;
	public final static int ANIM_DOWN_OUT = 0x0016;
	public final static int ANIM_LEFT_IN = 0x0017;
	public final static int ANIM_LEFT_OUT = 0x0018;
	public final static int ANIM_RIGHT_IN = 0x0019;
	public final static int ANIM_RIGHT_OUT = 0x001a;
	public final static int ANIM_EXPAND_DOWN = 0x001b;
	public final static int ANIM_ECLIPSE_UP = 0x001c;

	/* 特殊页面动画 */
	public final static int ANIM_ENTER_HOME_FROM_LAUNCH = 0x0101;

	/**
	 * 根据类型获取动画
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            动画类型
	 * @return 动画
	 */
	public static Animation getAnimation(Context context, int type) {
		if (context == null) {
			return null;
		}

		Animation anim = null;
		switch (type) {
		case ANIM_FADE_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.fade_in);
			break;
		case ANIM_FADE_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.fade_out);
			break;
		case ANIM_SLIDE_IN_LEFT:
			anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_left);
			break;
		case ANIM_SLIDE_OUT_LEFT:
			anim = AnimationUtils.loadAnimation(context, R.anim.slide_out_left);
			break;
		case ANIM_SLIDE_IN_RIGHT:
			anim = AnimationUtils.loadAnimation(context, R.anim.slide_in_right);
		case ANIM_SLIDE_OUT_RIGHT:
			anim = AnimationUtils
					.loadAnimation(context, R.anim.slide_out_right);
			break;
		case ANIM_POP_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.pop_in);
			break;
		case ANIM_POP_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.pop_out);
			break;
		case ANIM_UP_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.up_in);
			break;
		case ANIM_UP_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.up_out);
			break;
		case ANIM_DOWN_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.down_in);
			break;
		case ANIM_DOWN_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.down_out);
			break;
		case ANIM_LEFT_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.left_in);
			break;
		case ANIM_LEFT_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.left_out);
			break;
		case ANIM_RIGHT_IN:
			anim = AnimationUtils.loadAnimation(context, R.anim.right_in);
			break;
		case ANIM_RIGHT_OUT:
			anim = AnimationUtils.loadAnimation(context, R.anim.right_out);
			break;
		case ANIM_ENTER_HOME_FROM_LAUNCH:
			anim = AnimationUtils.loadAnimation(context,
					R.anim.enter_home_from_launch);
			break;
		case ANIM_EXPAND_DOWN:
			anim = AnimationUtils.loadAnimation(context, R.anim.expand_down);
			break;
		case ANIM_ECLIPSE_UP:
			anim = AnimationUtils.loadAnimation(context, R.anim.eclipse_up);
			break;
		default:
			anim = new Animation() {
			};
			break;
		}

		anim.setFillAfter(true);
		return anim;
	}

	/**
	 * 根据类型获取动画，并设置起始时间和持续时长
	 * 
	 * @param context
	 *            上下文
	 * @param type
	 *            动画类型
	 * @param startOffset
	 *            起始时间，负数为无效值
	 * @param duration
	 *            持续时长，负数为无效值
	 * @return 动画
	 */
	public static Animation getAnimation(Context context, int type,
			long startOffset, long duration) {
		Animation anim = getAnimation(context, type);

		if (anim != null && startOffset >= 0)
			anim.setStartOffset(startOffset);

		if (anim != null && duration >= 0)
			anim.setDuration(duration);

		return anim;
	}
}
