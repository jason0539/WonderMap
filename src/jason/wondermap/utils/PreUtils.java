package jason.wondermap.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreUtils
{

	private static final String BIND__FLAG = "bind_flag";

	public static boolean isBind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		return sp.getBoolean(BIND__FLAG, false);
	}

	public static void bind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND__FLAG, true).commit();
	}

	public static void unbind(Context context)
	{
		SharedPreferences sp = context.getSharedPreferences("pre_tuisong",
				Context.MODE_PRIVATE);
		sp.edit().putBoolean(BIND__FLAG, false).commit();
	}

}
