package cn.hicc.suguan.dormitory.utils;

import android.widget.Toast;

import cn.hicc.suguan.dormitory.MyApplication;


/**
 * Created by 陈帅 on 2017/07/12/025.
 * 吐司工具
 */

public class ToastUtil {

    public static void showShort(int msgId) {
        Toast.makeText(MyApplication.getContext(), msgId, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(int msgId) {
        Toast.makeText(MyApplication.getContext(), msgId, Toast.LENGTH_LONG).show();
    }

    public static void showShort(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void showLong(String msg) {
        Toast.makeText(MyApplication.getContext(), msg, Toast.LENGTH_LONG).show();
    }
}
