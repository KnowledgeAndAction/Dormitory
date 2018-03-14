package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import okhttp3.Call;

public class LeaderActivity extends AppCompatActivity {

    private ProgressDialog downloadprogressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        initView();

        // 检查更新
        checkVersionCode();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("宿舍管理");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 注销
        if (id == R.id.action_settings) {
            SpUtil.remove(Constant.PASSWORD);
            SpUtil.remove(Constant.ASSISTANT_NAME);
            SpUtil.remove(Constant.IS_LOGIN);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 检测更新
    private void checkVersionCode() {
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
    private void getAppInfoJson(String response) {
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
                downLoadApk(appUrl);
                // 显示一个进度条对话框
                showProgressDialog();
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
    private void downLoadApk(String appUrl) {
        if (!appUrl.contains("http")) {
            appUrl = "http://" + appUrl;
        }
        OkHttpUtils
                .get()
                .url(appUrl)
                .build()
                .execute(new FileCallBack(getExternalFilesDir("apk").getPath(),"宿舍管理.apk") {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("下载失败："+e.toString());
                        Logs.i("下载失败："+e.toString()+","+id);
                        downloadprogressDialog.dismiss();
                    }

                    @Override
                    public void onResponse(File response, int id) {
                        ToastUtil.showShort("下载成功,保存路径:"+ getExternalFilesDir("apk").getPath());
                        Logs.i("下载成功,保存路径:"+getExternalFilesDir("apk").getPath());
                        // 安装应用
                        installApk(response);
                        downloadprogressDialog.dismiss();
                    }

                    @Override
                    public void inProgress(float progress, long total, int id) {
                        // 设置进度
                        downloadprogressDialog.setProgress((int) (100 * progress));
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
    protected void showProgressDialog() {
        downloadprogressDialog = new ProgressDialog(this);
        downloadprogressDialog.setIcon(R.mipmap.logo);
        downloadprogressDialog.setTitle("下载安装包中");
        downloadprogressDialog.setCanceledOnTouchOutside(false);
        downloadprogressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                return;
            }
        });
        downloadprogressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        downloadprogressDialog.show();
    }
}
