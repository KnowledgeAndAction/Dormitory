package cn.hicc.suguan.dormitory.utils;

import android.util.Log;

/**
 * Created by 陈帅 on 2017/07/12/025.
 * 日志工具
 */

public class Logs {
    private static String TAG = "宿管TAG：";
    private static int IS_OPEN = 1;

    public static void d(String msg) {
        if (IS_OPEN > 0) {
            Log.d(TAG, msg);
        }
    }

    public static void e(String msg) {
        if (IS_OPEN > 0) {
            Log.e(TAG, msg);
        }
    }

    public static void i(String msg) {
        if (IS_OPEN > 0) {
            Log.i(TAG, msg);
        }
    }

    public static void v(String msg) {
        if (IS_OPEN > 0) {
            Log.v(TAG, msg);
        }
    }

    public static void w(String msg) {
        if (IS_OPEN > 0) {
            Log.w(TAG, msg);
        }
    }
}
