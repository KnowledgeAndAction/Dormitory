package cn.hicc.suguan.dormitory.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.text.SimpleDateFormat;
import java.util.Date;

import cn.hicc.suguan.dormitory.MyApplication;

public class Utils {
	public static String GetTime() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date(System.currentTimeMillis());// 获取当前时间
		String time = formatter.format(date);
		return time;
	}
	public static String GetShortDate() {
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy年MM月");
		String date = sDateFormat.format(new Date());
		return date;
	}

	//版本名
	public static String getVersionName() {
		return getPackageInfo(MyApplication.getContext()).versionName;
	}

	//版本号
	public static int getVersionCode() {
		return getPackageInfo(MyApplication.getContext()).versionCode;
	}

	private static PackageInfo getPackageInfo(Context context) {
		PackageInfo pi = null;
		try {
			PackageManager pm = context.getPackageManager();
			pi = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_CONFIGURATIONS);
			return pi;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return pi;
	}
}
