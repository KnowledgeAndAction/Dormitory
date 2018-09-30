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
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.MyFragmentPagerAdapter;
import cn.hicc.suguan.dormitory.fragment.TeacherClassFragment;
import cn.hicc.suguan.dormitory.fragment.TeacherDormFragment;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import cn.hicc.suguan.dormitory.utils.Utils;
import cn.hicc.suguan.dormitory.view.ScrollViewPager;
import okhttp3.Call;

/**
 * 导员界面
 */

public class TeacherActivity extends MainBaseActivity {

    private ProgressDialog mProgressDialog;
    private int weekCode;
    private List<Score> mDorScoreList = new ArrayList<>();
    private List<Score> mClassScoreList = new ArrayList<>();
    private ScrollViewPager viewpager;
    private MyFragmentPagerAdapter adapter;
    private ArrayList<Integer> weekNum;
    private int currentWeekCode;    // 当前周数
    private int currentMonth;    // 当前周数
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
                // 无数据
                case 3:
                    setUINoData();
                    closeDialog();
                    break;
            }
        }
    };
    private ArrayList<Integer> monthNum;
    private String dateType = "week";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode", 0);
        currentWeekCode = weekCode;
        currentMonth = Utils.getMonth();

        initView();

        // 初始化选择器数据
        initPickerViewData();

        initData();
    }

    // 当获取到数据后，调取fragment方法更新数据
    private void setUI() {
        TeacherClassFragment c = (TeacherClassFragment) adapter.getItem(0);
        c.setData(weekCode,mClassScoreList,dateType);
        TeacherDormFragment t = (TeacherDormFragment) adapter.getItem(1);
        t.setData(weekCode,mDorScoreList,dateType);
    }

    // 当获取到数据后，调取fragment方法更新数据
    private void setUINoData() {
        TeacherClassFragment c = (TeacherClassFragment) adapter.getItem(0);
        c.clearData();
        TeacherDormFragment t = (TeacherDormFragment) adapter.getItem(1);
        t.clearData();
    }

    // 获取数据
    private void initData() {
        showDialog();
        mDorScoreList.clear();
        mClassScoreList.clear();
        OkHttpUtils
                .get()
                .url(URL.TEACHER_CHECK_SCORE)
                .addParams("instructorName",SpUtil.getString(Constant.ASSISTANT_NAME))
                .addParams("weekCode",weekCode+"")
                .addParams("dateType",dateType)
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
                        Logs.d(response);
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
                                // 宿舍成绩信息
                                case "dormitory":
                                    mDorScoreList.add(score);
                                    break;
                                // 班级平均成绩信息
                                case "class":
                                    mClassScoreList.add(score);
                                    break;
                            }
                        }
                        if (data.length() > 0) {
                            // 请求数据成功
                            mHandler.sendEmptyMessage(0);
                        } else {
                            // 没有要查询的数据
                            mHandler.sendEmptyMessage(3);
                        }
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
        toolbar.setTitle(SpUtil.getString(Constant.ASSISTANT_NAME) + "导员");
        setSupportActionBar(toolbar);

        // 初始化viewpager
        viewpager = (ScrollViewPager) findViewById(R.id.viewpager);
        // 设置viewpager是否禁止滑动
        viewpager.setNoScroll(true);
        // 设置适配器
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new TeacherClassFragment(),"班级成绩对比");
        adapter.addFrag(new TeacherDormFragment(),"宿舍成绩对比");
        viewpager.setAdapter(adapter);

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
        getMenuInflater().inflate(R.menu.menu_teacher, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // 注销
        if (id == R.id.action_exit) {
            SpUtil.remove(Constant.PASSWORD);
            SpUtil.remove(Constant.ASSISTANT_NAME);
            SpUtil.remove(Constant.IS_LOGIN);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        // 切换周数
        if (id == R.id.action_week) {
            showWeekPickerView();
            return true;
        }
        if (id == R.id.action_month) {
            showMonthPickerView();
            return true;
        }
        // 列表展示
        if (id == R.id.action_list) {
            TeacherClassFragment c = (TeacherClassFragment) adapter.getItem(0);
            c.changeView();
            TeacherDormFragment t = (TeacherDormFragment) adapter.getItem(1);
            t.changeView();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // 设置选择器数据
    private void initPickerViewData() {
        weekNum = new ArrayList<>();
        for (int i=1; i<=17;i++) {
            weekNum.add(i);
        }

        monthNum = new ArrayList<>();
        for (int i=1; i<=12; i++) {
            monthNum.add(i);
        }
    }

    // 显示周数选择器
    private void showWeekPickerView() {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                weekCode = weekNum.get(options1);
                dateType = "week";

                // 重新获取分数
                initData();
            }
        }).setTitleText("选择周数(本周第"+currentWeekCode+"周)")
                .setTitleSize(14)
                .build();

        pvOptions.setSelectOptions(currentWeekCode-1);
        pvOptions.setPicker(weekNum);
        pvOptions.show();
    }

    // 显示月数选择器
    private void showMonthPickerView() {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                weekCode = monthNum.get(options1);
                dateType = "month";

                // 重新获取分数
                initData();
            }
        }).setTitleText("选择月份(本月"+currentMonth+"月)")
                .setTitleSize(14)
                .build();

        pvOptions.setSelectOptions(currentMonth-1);
        pvOptions.setPicker(monthNum);
        pvOptions.show();
    }
}
