package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.FileCallBack;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.BuildRecylerAdapter;
import cn.hicc.suguan.dormitory.model.Hostel;
import cn.hicc.suguan.dormitory.model.SQLITE;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.TextUtils;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class MainActivity extends AppCompatActivity {

    private TextView tv_main_name;
    private TextView tv_main_time;
    private Button main_week_btn_1;
    private Button main_week_btn_2;
    private Button main_week_btn_3;
    private Button main_week_btn_4;
    private ProgressBar progressBar;
    private TextView tv_main_jd;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout swipeRefresh;

    private int weekCode;
    private SQLiteDatabase db;
    private ProgressDialog mDialog;

    private int count;

    private List<Hostel> mlist = new ArrayList<>();
    private BuildRecylerAdapter adapter;
    private File path;
    private MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM);
    private static Boolean isExit = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getActivityIntent();

        initView();

        // 创建数据库
        createSqliteDB();

        // 获取数据
        getUserBuild();

        // 检查更新
        checkVersionCode();

        Logs.d(getExternalFilesDir("apk").getPath());
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("宿舍管理");
        setSupportActionBar(toolbar);

        tv_main_name = (TextView) findViewById(R.id.tv_main_name);
        tv_main_time = (TextView) findViewById(R.id.tv_main_time);

        main_week_btn_1 = (Button) findViewById(R.id.main_week_btn_1);
        main_week_btn_2 = (Button) findViewById(R.id.main_week_btn_2);
        main_week_btn_3 = (Button) findViewById(R.id.main_week_btn_3);
        main_week_btn_4 = (Button) findViewById(R.id.main_week_btn_4);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        tv_main_jd = (TextView) findViewById(R.id.tv_main_jd);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv_main_list);

        tv_main_name.setText(SpUtil.getString(Constant.ASSISTANT_NAME) + " 你好!");
        tv_main_time.setText(Utils.GetShortDate());

        // 设置按钮文字
        setButtonText();

        // 设置RecyclerView
        InitBuildRecycler();

        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        // 配置swipeRefresh
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary , R.color.colorAccent, R.color.colorPrimaryDark);
        // 设置刷新事件
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // 获取数据
                getUserBuild();
            }
        });

        swipeRefresh.setRefreshing(true);
    }

    private void getActivityIntent() {
        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode", 0);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getUserBuild();
    }

    // 设置按钮文字
    private void setButtonText() {
        int a = weekCode - 1;
        int b = weekCode;
        int c = weekCode + 1;
        int d = weekCode + 2;
        main_week_btn_1.setText(a + "");
        main_week_btn_2.setText(b + "");
        main_week_btn_3.setText(c + "");
        main_week_btn_4.setText(d + "");
    }

    private void createSqliteDB() {
        db = openOrCreateDatabase(SQLITE.db, MODE_APPEND, null);
        db.execSQL(SQLITE.TABLE_HELPER_CREATE);
    }

    public void getUserBuild() {
        count = 0;
        mlist.clear();
        OkHttpUtils
                .post()
                .url(URL.Get_User_Build)
                .addParams("num", SpUtil.getString(Constant.USERNAME))
                .addParams("date", Utils.GetTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        showFalseData();
                        swipeRefresh.setRefreshing(false);
                        ToastUtil.showShort("获取信息失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        swipeRefresh.setRefreshing(false);
                        try {
                            Cursor c = db.query(SQLITE.TABLE_HELPER_CHECK, null, null, null, null, null, null);
                            if (c.getCount() != 0) {
                                db.delete(SQLITE.TABLE_HELPER_CHECK, null, null);
                            }
                            c.close();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject ob = array.getJSONObject(i);
                                int Building = ob.getInt("dormitoryBuildingCode");
                                String DormitoryState = ob.getString("dormitoryState");
                                int totalScore = ob.getInt("totalScore");
                                int Hostel = ob.getInt("dormitoryId");
                                int checkType = ob.getInt("checkType");
                                ContentValues cv = new ContentValues();
                                cv.put("Building", TextUtils.GetBuildName(Building));
                                cv.put("Hostel", Hostel);
                                cv.put("DormitoryState", DormitoryState);
                                db.insert(SQLITE.TABLE_HELPER_CHECK, null, cv);
                                if (DormitoryState.equals("0")) {
                                    mlist.add(new Hostel(Building, Hostel, totalScore, false, i + 1, checkType, weekCode));
                                } else {
                                    count++;
                                    mlist.add(new Hostel(Building, Hostel, totalScore, true, i + 1, checkType, weekCode));
                                }
                            }
                            adapter.notifyDataSetChanged();
                            double progress = (double) count / (double) array.length() * 100;
                            progressBar.setProgress((int) progress);
                            tv_main_jd.setText(count + "/" + array.length() + "");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void showFalseData() {
        for (int i=0; i<15; i++) {
            int Building = i;
            String DormitoryState = ""+i%2;
            int totalScore = i+80;
            int Hostel = i;
            int checkType = i;
            ContentValues cv = new ContentValues();
            cv.put("Building", TextUtils.GetBuildName(Building));
            cv.put("Hostel", Hostel);
            cv.put("DormitoryState", DormitoryState);
            db.insert(SQLITE.TABLE_HELPER_CHECK, null, cv);
            if (DormitoryState.equals("0")) {
                mlist.add(new Hostel(Building, Hostel, totalScore, false, i + 1, checkType, weekCode));
            } else {
                count++;
                mlist.add(new Hostel(Building, Hostel, totalScore, true, i + 1, checkType, weekCode));
            }
        }
        adapter.notifyDataSetChanged();
        double progress = (double) count / (double) 15 * 100;
        progressBar.setProgress((int) progress);
        tv_main_jd.setText(count + "/" + 15 + "");
    }

    // 设置RecyclerView
    private void InitBuildRecycler() {
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        adapter = new BuildRecylerAdapter(mlist);
        // 为mRecyclerView设置适配
        mRecyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        adapter.setOnItemClickListener(new BuildRecylerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, List<Hostel> mlist) {
                Hostel hostel = mlist.get(position);
                boolean ischeck = hostel.isCheck();
                if (ischeck) {
                    Toast.makeText(MainActivity.this, "当前宿舍已有成绩,不可评分", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(MainActivity.this, ScoreActivity.class);
                    int buildCode = hostel.getBuilding();
                    int buildNum = hostel.getHostel();
                    intent.putExtra("buildCode", buildCode);
                    intent.putExtra("buildNum", buildNum);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            SpUtil.remove(Constant.PASSWORD);
            SpUtil.remove(Constant.ASSISTANT_NAME);
            SpUtil.remove(Constant.IS_LOGIN);
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return true;
        }
        if (id == R.id.action_upload_photo) {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                String mFiles = Environment.getExternalStorageDirectory().getPath();
                mFiles = mFiles + "/" + "dormitory/";
                path = new File(mFiles);
                Logs.i("mFiles=" + path.toString());
                getAllFiles(path);
            } else {
                Toast.makeText(this, "没有SD卡", Toast.LENGTH_LONG).show();
            }
            UpImage();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAllFiles(File root) {
        File files[] = root.listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                Logs.i(file.toString());
                builder.addFormDataPart("file" + i, null, RequestBody.create(MediaType.parse("image/jpg"), file));
            }
        }
    }

    private void UpImage() {
        showUpImageDialog();

        OkHttpClient client = new OkHttpClient();
        FormBody paramsBody=new FormBody.Builder()
                .add("username",SpUtil.getString(Constant.USERNAME))
                .build();
        builder.addPart(paramsBody);
        Request request = new Request.Builder()
                .url(URL.UP_IMAGE)
                .post(builder.build())
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeUpImageDialog();
                ToastUtil.showShort("上传失败，请稍后重试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                try {
                    Logs.i(result);
                    JSONObject ob = new JSONObject(result);
                    int code = ob.getInt("bool");
                    if (code == 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort("上传失败");
                            }
                        });
                        Toast.makeText(MainActivity.this, "上传失败", Toast.LENGTH_SHORT).show();
                    } else if (code == 1) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort("上传成功");
                            }
                        });
                        DeleteFile();
                    } else if (code == 2) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort("未上传成绩,不能上传照片");
                            }
                        });
                    }
                    closeUpImageDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtil.showShort("上传失败，请稍后重试");
                        }
                    });
                    closeUpImageDialog();
                }
            }
        });
    }

    private void DeleteFile() {
        String mFiles = Environment.getExternalStorageDirectory().getPath();
        mFiles = mFiles + "/" + "dormitory/";
        File dir = new File(mFiles);
        if (dir == null || !dir.exists() || !dir.isDirectory())
            return;
        for (File file : dir.listFiles()) {
            if (file.isFile())
                file.delete(); // 删除所有文件
            else if (file.isDirectory())
                DeleteFile(); // 递规的方式删除文件夹
        }
        dir.delete();// 删除目录本身
    }

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

    private void getAppInfoJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean falg = jsonObject.getBoolean("falg");
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
        OkHttpUtils
                .get()
                .url(appUrl)
                .build()
                .execute(new FileCallBack(getExternalFilesDir("apk").getPath(),"宿舍管理.apk") {
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
        intent.addCategory("android.intent.category.DEFAULT");
        //文件作为数据源
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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

    private void showUpImageDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(MainActivity.this);
        }
        mDialog.setMessage("上传图片中...");
        mDialog.setCanceledOnTouchOutside(false);
        mDialog.show();
    }

    private void closeUpImageDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
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
    private void exitBy2Click() {
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
