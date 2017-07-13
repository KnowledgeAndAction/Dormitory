package cn.hicc.suguan.dormitory.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 陈帅 on 2017/07/12/025.
 * 利用OkHttp发送网络请求工具
 */

public class HttpUtil {
    public static void sendHttpRequest(String url, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(callback);
    }
}
