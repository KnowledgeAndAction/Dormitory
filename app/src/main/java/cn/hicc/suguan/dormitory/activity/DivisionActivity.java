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
import cn.hicc.suguan.dormitory.fragment.DivisionClassFragment;
import cn.hicc.suguan.dormitory.fragment.DivisionBasisFragment;
import cn.hicc.suguan.dormitory.fragment.DivisionTeacherFragment;
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
 * 学部界面
 */

public class DivisionActivity extends MainBaseActivity {

    private ProgressDialog mProgressDialog;
    private int weekCode;
    private List<Score> mTeacherScoreList = new ArrayList<>();
    private List<Score> mGradeScoreList = new ArrayList<>();
    private List<Score> mSexScoreList = new ArrayList<>();
    private List<Score> mCheckTypeScoreList = new ArrayList<>();
    private List<Score> mClassScoreList = new ArrayList<>();
    private ScrollViewPager viewpager;
    private double avg;
    private MyFragmentPagerAdapter adapter;
    private ArrayList<Integer> weekNum;
    private int currentWeekCode;
    private int currentMonth;
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
        setContentView(R.layout.activity_division);

        Intent intent = getIntent();
        weekCode = intent.getIntExtra("weekCode", 0);
        currentWeekCode = weekCode;
        currentMonth = Utils.getMonth();

        initView();

        // 初始化选择器数据
        initPickerViewData();

        initData();
    }

    // 当获取到数据后，在设置viewpager适配器
    private void setUI() {
        DivisionBasisFragment g = (DivisionBasisFragment) adapter.getItem(0);
        g.setData(weekCode,mGradeScoreList,mSexScoreList,mCheckTypeScoreList,dateType);

        DivisionTeacherFragment t = (DivisionTeacherFragment) adapter.getItem(1);
        t.setData(avg,weekCode,mTeacherScoreList,dateType);

        DivisionClassFragment c = (DivisionClassFragment) adapter.getItem(2);
        c.setData(avg,weekCode,mClassScoreList,dateType,mGradeScoreList);
    }

    // 当获取到数据后，调取fragment方法更新数据
    private void setUINoData() {
        DivisionBasisFragment g = (DivisionBasisFragment) adapter.getItem(0);
        g.clearData();

        DivisionTeacherFragment t = (DivisionTeacherFragment) adapter.getItem(1);
        t.clearData();

        DivisionClassFragment c = (DivisionClassFragment) adapter.getItem(2);
        c.clearData();
    }

    // 获取数据
    private void initData() {
        showDialog();
        mTeacherScoreList.clear();
        mClassScoreList.clear();
        mGradeScoreList.clear();
        mSexScoreList.clear();
        mCheckTypeScoreList.clear();
        OkHttpUtils
                .get()
                .url(URL.DIVISION_CHECK_SCORE)
                .addParams("divisionName",SpUtil.getString(Constant.ASSISTANT_NAME))
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
                                // 班级平均成绩信息
                                case "Class":
                                    mClassScoreList.add(score);
                                    break;
                                // 导员平均成绩信息
                                case "Instructor":
                                    mTeacherScoreList.add(score);
                                    break;
                                // 年级平均成绩信息
                                case "TimesCode":
                                    mGradeScoreList.add(score);
                                    break;
                                // 性别平均成绩信息
                                case "Gender":
                                    mSexScoreList.add(score);
                                    break;
                                // 总平均分
                                case "Avg":
                                    avg = info.getDouble("avgSocre");
                                    break;
                            }
                        }
                        if (data.length() > 0) {
                            // 请求数据成功
                            mHandler.sendEmptyMessage(0);
                        } else {
                            // 无数据
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
        toolbar.setTitle(SpUtil.getString(Constant.ASSISTANT_NAME));
        setSupportActionBar(toolbar);

        // 初始化viewpager
        viewpager = (ScrollViewPager) findViewById(R.id.viewpager);
        // 设置viewpager是否禁止滑动
        viewpager.setNoScroll(true);
        viewpager.setOffscreenPageLimit(4);
        // 初始化viewpager
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DivisionBasisFragment(),"基础项目成绩对比");
        adapter.addFrag(new DivisionTeacherFragment(),"导员成绩对比");
        adapter.addFrag(new DivisionClassFragment(),"班级成绩对比");
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
        getMenuInflater().inflate(R.menu.menu_division, menu);
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
        // 选择周数
        if (id == R.id.action_week) {
            showWeekPickerView();
            return true;
        }
        // 选择月份
        if (id == R.id.action_month) {
            showMonthPickerView();
            return true;
        }
        // 切换图标展示形式
        if (id == R.id.action_list) {
            DivisionTeacherFragment t = (DivisionTeacherFragment) adapter.getItem(1);
            t.changeView();

            DivisionClassFragment c = (DivisionClassFragment) adapter.getItem(2);
            c.changeView();
            return true;
        }
        // 修改宿舍成绩
        if (id == R.id.action_change) {
            Intent intent = new Intent(this, ChangeScoreActivity.class);
            intent.putExtra("weekCode",currentWeekCode);
            startActivity(intent);
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
