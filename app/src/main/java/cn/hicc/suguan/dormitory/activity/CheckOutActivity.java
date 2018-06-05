package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

/**
 * 查宿人员
 */
public class CheckOutActivity extends MainBaseActivity {

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
    private FloatingActionButton fab;
    private int mCheckType;

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
        fab = (FloatingActionButton) findViewById(R.id.fab);

        tv_main_name.setText(SpUtil.getString(Constant.ASSISTANT_NAME) + " 你好!");
        tv_main_time.setText(Utils.GetShortDate());

        // 设置按钮文字
        setButtonText();

        // 设置RecyclerView
        InitBuildRecycler();

        // 设置FAB点击事件
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择检查类型对话框
                showChooseDialog();
            }
        });

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

    // 选择检查类型
    private void showChooseDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setSingleChoiceItems(new String[]{"普查","抽查"}, mCheckType-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mCheckType = which+1;
            }
        });
        builder.setTitle("选择查宿类型");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 重新获取数据
                swipeRefresh.setRefreshing(true);
                getUserBuild();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private void getActivityIntent() {
        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode", 0);
        mCheckType = intent.getIntExtra("type", 0);
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
        //mlist.clear();
        OkHttpUtils
                .get()
                .url(URL.Get_User_Build)
                .addParams("user", SpUtil.getString(Constant.USERNAME))
                //.addParams("date", Utils.GetTime())
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        //showFalseData();
                        swipeRefresh.setRefreshing(false);
                        ToastUtil.showShort("获取信息失败，请稍后重试");
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Logs.d(response);
                        swipeRefresh.setRefreshing(false);
                        try {
                            Cursor c = db.query(SQLITE.TABLE_HELPER_CHECK, null, null, null, null, null, null);
                            if (c.getCount() != 0) {
                                db.delete(SQLITE.TABLE_HELPER_CHECK, null, null);
                            }
                            c.close();
                            JSONArray array = new JSONArray(response);
                            List<Hostel> list = new ArrayList<>();
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obs = array.getJSONObject(i);
                                JSONObject ob = obs.getJSONObject("data");
                                int Building = Integer.valueOf(ob.getString("dormitoryBuildingCode"));
                                String DormitoryState = ob.getString("dormitoryState");
                                int totalScore = ob.getInt("totalScore");
                                int Hostel = ob.getInt("dormitoryId");
                                int checkType = ob.getInt("checkType");

                                if (checkType == mCheckType) {
                                    ContentValues cv = new ContentValues();
                                    cv.put("Building", TextUtils.GetBuildName(Building));
                                    cv.put("Hostel", Hostel);
                                    cv.put("DormitoryState", DormitoryState);
                                    db.insert(SQLITE.TABLE_HELPER_CHECK, null, cv);
                                    if (DormitoryState.equals("0")) {
                                        list.add(new Hostel(Building, Hostel, totalScore, false, i + 1, checkType, weekCode));
                                    } else {
                                        count++;
                                        list.add(new Hostel(Building, Hostel, totalScore, true, i + 1, checkType, weekCode));
                                    }
                                }
                            }
                            //adapter.notifyDataSetChanged();
                            adapter.setItems(list);

                            double progress = (double) count / (double) list.size() * 100;
                            progressBar.setProgress((int) progress);
                            tv_main_jd.setText(count + "/" + list.size() + "");
                        } catch (JSONException e) {
                            Logs.d(e.toString());
                            e.printStackTrace();
                        }
                    }
                });
    }

    // 测试用的假数据
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
        //adapter.notifyDataSetChanged();

        // 设置recycleview滑动监听事件
        mRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // 隐藏或者显示fab
                if (dy > 0) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });

        adapter.setOnItemClickListener(new BuildRecylerAdapter.OnRecyclerViewItemClickListener() {
            @Override
            public void onItemClick(View view, int position, List<Hostel> mlist) {
                Hostel hostel = mlist.get(position);
                boolean ischeck = hostel.isCheck();
                if (ischeck) {
                    Toast.makeText(CheckOutActivity.this, "当前宿舍已有成绩,不可评分", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.setClass(CheckOutActivity.this, ScoreActivity.class);
                    int buildCode = hostel.getBuilding();
                    int buildNum = hostel.getHostel();
                    int checkType = hostel.getCheckType();
                    intent.putExtra("buildCode", buildCode);
                    intent.putExtra("buildNum", buildNum);
                    intent.putExtra("checkType", checkType);
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
            startActivity(new Intent(CheckOutActivity.this, LoginActivity.class));
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


    private void showUpImageDialog() {
        if (mDialog == null) {
            mDialog = new ProgressDialog(CheckOutActivity.this);
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
}
