package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.MyFragmentPagerAdapter;
import cn.hicc.suguan.dormitory.fragment.LeaderDivisionFragment;
import cn.hicc.suguan.dormitory.fragment.LeaderDorFragment;
import cn.hicc.suguan.dormitory.fragment.LeaderGradeFragment;
import cn.hicc.suguan.dormitory.fragment.LeaderSexAndCheckFragment;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.view.ScrollViewPager;
import okhttp3.Call;

/**
 * 领导界面
 */

public class LeaderActivity extends MainBaseActivity {

    private ProgressDialog mProgressDialog;
    private int weekCode;
    private List<Score> mAcademyScoreList = new ArrayList<>();
    private List<Score> mGradeScoreList = new ArrayList<>();
    private List<Score> mSexScoreList = new ArrayList<>();
    private List<Score> mCheckTypeScoreList = new ArrayList<>();
    private List<Score> mDorScoreList = new ArrayList<>();
    private List<Score> mDivisionScoreList = new ArrayList<>();
    private ScrollViewPager viewpager;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 解析数据成功
                case 0:
                    setUI();
                    closeDialog();
                    break;
                // 获取数据失败
                case 1:
                    ToastUtil.showShort("获取数据失败");
                    closeDialog();
                    break;
                // 解析异常
                case 2:
                    ToastUtil.showShort("解析数据失败");
                    closeDialog();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader);

        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode", 0);

        initView();

        initData();
    }

    // 当获取到数据后，在设置viewpager适配器
    private void setUI() {
        // 初始化viewpager
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new LeaderDivisionFragment(weekCode, mDivisionScoreList),"学部成绩对比");
        adapter.addFrag(new LeaderDivisionFragment(weekCode, mAcademyScoreList),"书院成绩对比");
        adapter.addFrag(new LeaderGradeFragment(weekCode, mGradeScoreList),"年级成绩对比");
        adapter.addFrag(new LeaderDorFragment(weekCode,mDorScoreList),"宿舍楼成绩对比");
        adapter.addFrag(new LeaderSexAndCheckFragment(weekCode,mSexScoreList),"性别成绩对比");
        adapter.addFrag(new LeaderSexAndCheckFragment(weekCode,mCheckTypeScoreList),"检查类型成绩对比");
        viewpager.setAdapter(adapter);
    }

    // 获取数据
    private void initData() {
        showDialog();
        OkHttpUtils
                .get()
                .url(URL.LEADER_CHECK_SCORE)
                .addParams("weekCode",weekCode+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("获取数据失败，请稍后重试");
                        Logs.e("获取数据失败："+e.toString());
                        closeDialog();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 解析json
                        resolveJson(response);
                    }
                });
    }

    // 解析json
    private void resolveJson(final String response) {
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getBoolean("flag")) {
                        JSONArray data = jsonObject.getJSONArray("data");
                        for (int i=0; i<data.length(); i++) {
                            JSONObject info = data.getJSONObject(i);
                            String classCodes = info.getString("classCodes");
                            Score score = new Score(info.getString("className"),info.getDouble("avgSocre"));
                            switch (classCodes) {
                                // 检查类型
                                case "Description":
                                    mCheckTypeScoreList.add(score);
                                    break;
                                // 宿舍楼平均成绩信息
                                case "DormitoryBuilding":
                                    mDorScoreList.add(score);
                                    break;
                                // 书院平均成绩信息
                                case "Academy":
                                    mAcademyScoreList.add(score);
                                    break;
                                // 年级平均成绩信息
                                case "TimesCode":
                                    mGradeScoreList.add(score);
                                    break;
                                // 性别平均成绩信息
                                case "Gender":
                                    mSexScoreList.add(score);
                                    break;
                                // 学部平均成绩信息
                                case "Division":
                                    mDivisionScoreList.add(score);
                                    break;
                            }
                        }
                        // 请求数据成功
                        mHandler.sendEmptyMessage(0);

                    } else {
                        // 请求数据失败
                        mHandler.sendEmptyMessage(1);
                        Logs.e("服务器请求失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    mHandler.sendEmptyMessage(2);
                    Logs.e("解析异常："+e.toString());
                }
            }
        }.start();
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(SpUtil.getString(Constant.ASSISTANT_NAME));
        setSupportActionBar(toolbar);

        // 初始化viewpager
        viewpager = (ScrollViewPager) findViewById(R.id.viewpager);
        // 设置viewpager是否禁止滑动
        viewpager.setNoScroll(true);

        // 初始化tablayout
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#9AB4E2"));
        tabLayout.setupWithViewPager(viewpager);
    }

    private void showDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.setMessage("加载数据中...");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();
    }

    private void closeDialog() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_leader, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 灵活查询
        if (id == R.id.action_flexible) {
            startActivity(new Intent(this, LeaderFlexibleActivity.class));
            return true;
        }
        if (id == R.id.action_exit) {
            SpUtil.remove(Constant.PASSWORD);
            SpUtil.remove(Constant.ASSISTANT_NAME);
            SpUtil.remove(Constant.IS_LOGIN);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
