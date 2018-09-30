package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import okhttp3.Call;

/**
 * Created by 陈帅 on 2018/6/3/003.
 * 主页基类  抽出检测更新和双击退出功能
 */

public class MainBaseActivity extends AppCompatActivity {

    protected ProgressDialog progressDialog;
    protected boolean isExit = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 检测更新
        checkVersionCode();
    }

    // 检测更新
    protected void checkVersionCode() {
        OkHttpUtils
                .get()
                .url(URL.CHECK_UPDATA)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.d("检查更新失败，" + e.toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.i("获取最新app信息成功，" + response);
                        // 解析json
                        getAppInfoJson(response);
                    }
                });
    }

    // 解析json
    protected void getAppInfoJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean falg = jsonObject.getBoolean("flag");
            if (falg) {
                JSONObject data = jsonObject.getJSONObject("data");
                double v = Double.valueOf(data.getString("appVersion"));
                int version = (int) v;
                // 如果服务器的版本号大于本地的  就更新
                if(version > Utils.getVersionCode()){
                    // 获取下载地址
                    String mAppUrl = data.getString("appUrl");
                    // 获取新版app描述
                    String appDescribe = data.getString("appDescribe");
                    // 如果sd卡可用
                    if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
                        // 展示下载对话框
                        showUpDataDialog(appDescribe, mAppUrl);
                    }
                }
            } else {
                Logs.i("获取最新app信息失败："+jsonObject.getString("data"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Logs.i("解析最新app信息失败："+e.toString());
        }
    }

    // 显示更新对话框
    protected void showUpDataDialog(String description, final String appUrl) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
        //设置对话框左上角图标
        builder.setIcon(R.mipmap.logo);
        //设置对话框标题
        builder.setTitle("发现新版本");
        //设置对话框内容
        builder.setMessage(description);
        //设置积极的按钮
        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //下载apk
                downLoadApk(appUrl,"宿舍管理.apk");
                // 显示一个进度条对话框
                showDownloadProgressDialog();
            }
        });
        //设置消极的按钮
        builder.setNegativeButton("暂不更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    // 下载文件
    protected void downLoadApk(String appUrl,String fileName) {
        if (!appUrl.contains("http")) {
            appUrl = "http://" + appUrl;
        }
        OkHttpUtils
                .get()
                .url(appUrl)
                .build()
                .execute(new FileCallBack(getExternalFilesDir("apk").getPath(),fileName) {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("下载失败："+e.toString());
                        Logs.i("下载失败："+e.toString()+","+id);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        ToastUtil.showShort("下载成功,保存路径:"+ getExternalFilesDir("apk").getPath());
                        Logs.i("下载成功,保存路径:"+getExternalFilesDir("apk").getPath());
                        // 安装应用
                        installApk(response);
                        progressDialog.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        // 设置进度
                        progressDialog.setProgress((int) (100 * progress));
                    }
                });
    }

    // 安装应用
    protected void installApk(File file) {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // 判读版本是否在7.0以上
        if (Build.VERSION.SDK_INT >= 24) {
            // 参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(this, "cn.hicc.suguan.dormitory.fileprovider", file);
            // 添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.addCategory("android.intent.category.DEFAULT");
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        }

        startActivityForResult(intent,0);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    // 从安装应用界面返回后的处理
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            checkVersionCode();
        }
    }

    // 下载的进度条对话框
    protected void showDownloadProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.logo);
        progressDialog.setTitle("下载安装包中");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.show();
    }

    // 监听返回键
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exitBy2Click();
        }
        return false;
    }

    // 双击退出程序
    protected void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true; // 准备退出
            ToastUtil.showShort("再按一次退出");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            finish();
            System.exit(0);
        }
    }
}
