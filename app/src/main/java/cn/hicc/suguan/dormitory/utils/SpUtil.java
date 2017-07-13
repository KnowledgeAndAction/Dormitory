package cn.hicc.suguan.dormitory.utils;

import android.content.Context;
import android.content.SharedPreferences;

import cn.hicc.suguan.dormitory.MyApplication;


/**
 * Created by 陈帅 on 2017/07/12/025.
 * SharedPreferences工具
 */

public class SpUtil {
    private static SharedPreferences sSharedPreferences = MyApplication.getContext().
            getSharedPreferences(Constant.SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);

    public static void putString(String key, String value) {
        sSharedPreferences.edit().putString(key, value).apply();
    }

    public static void putInt(String key, int value) {
        sSharedPreferences.edit().putInt(key, value).apply();
    }

    public static void putBoolean(String key, boolean value) {
        sSharedPreferences.edit().putBoolean(key, value).apply();
    }

    /**
     * 根据key获取一个String，默认为空""
     */
    public static String getString(String key) {
        return sSharedPreferences.getString(key, "");
    }

    /**
     * 根据key获取一个int，默认为 -1
     */
    public static int getInt(String key) {
        return sSharedPreferences.getInt(key, -1);
    }

    /**
     * 根据key获取一个Boolean，默认为false
     */
    public static boolean getBoolean(String key) {
        return sSharedPreferences.getBoolean(key, false);
    }

    /**
     * 删除SharedPreferences中对应key的值
     * @param key 要删除的值的key
     */
    public static void remove(String key) {
        sSharedPreferences.edit().remove(key).apply();
    }
}
