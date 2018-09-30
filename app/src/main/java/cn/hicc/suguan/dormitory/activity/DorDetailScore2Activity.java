package cn.hicc.suguan.dormitory.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.sequence.NumberSequenceFormat;
import com.bin.david.form.data.table.TableData;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.DetailScore;
import cn.hicc.suguan.dormitory.model.DorMember;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.TextUtils;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import okhttp3.Call;

/**
 * 宿舍详细成绩
 */
public class DorDetailScore2Activity extends AppCompatActivity implements View.OnClickListener{

    private String dorBui;
    private String dorNo;
    private int weekCode;
    private SmartTable<DetailScore> table_all_check_score;
    private SmartTable<DorMember> table_dor_menber;
    private SmartTable<DetailScore> table_spot_check_score;
    private ProgressDialog mProgressDialog;
    private List<DetailScore> mAllCheckScoreList = new ArrayList<>();
    private List<DetailScore> mSpotCheckScoreList = new ArrayList<>();
    private List<DorMember> mDorMemberList = new ArrayList<>();
    private double allCheckScore;   // 普查总分
    private double spotCheckScore;  // 抽查总分
    private Button bt_look_all_score;
    private Button bt_look_spot_score;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                // 普查获取成功
                case 0:
                    setAllCheckScoreData();
                    closeDialog();
                    break;
                // 抽查获取成功
                case 5:
                    setSpotCheckScoreData();
                    closeDialog();
                    break;
                // 普查获取失败
                case 1:
                    for (DetailScore detailScore : mAllCheckScoreList) {
                        detailScore.setScore(0);
                    }
                    setAllCheckScoreData();
                    closeDialog();
                    ToastUtil.showShort("获取普查分数失败");
                    break;
                // 抽查获取失败
                case 2:
                    for (DetailScore detailScore : mSpotCheckScoreList) {
                        detailScore.setScore(0);
                    }
                    setSpotCheckScoreData();
                    closeDialog();
                    ToastUtil.showShort("获取抽查分数失败");
                    break;
                // 获取宿舍成员信息成功
                case 3:
                    // 设置宿舍成员表格数据
                    setDorMemberData();
                    break;
                // 获取宿舍成员信息失败
                case 4:
                    ToastUtil.showShort("获取宿舍成员信息失败");
                    // 设置宿舍成员表格数据
                    setDorMemberData();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dor_detail_score2);

        getActivityIntent();

        initView();

        initData();
    }

    // 从网络获取数据
    private void initData() {
        initTableData();
        showDialog();

        // 获取宿舍成员信息
        initDorMember();

        // 获取普查分数
        initAllCheckScore();

        // 获取抽查分数
        initSpotCheckScore();
    }

    // 获取抽查分数
    private void initSpotCheckScore() {
        OkHttpUtils
                .get()
                .url(URL.GET_DETAILS_SCORE)
                .addParams("dorBui",dorBui)
                .addParams("dorNo",dorNo)
                .addParams("checkType","抽查")
                .addParams("weekCode",weekCode+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取抽查分数失败:"+e.toString());
                        mHandler.sendEmptyMessage(2);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("flag")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String scoreString = data.getString("scoreString").trim();
                                spotCheckScore = data.getDouble("score");
                                String[] scoreStr = scoreString.split(" ");
                                for (int i=0; i<scoreStr.length; i++) {
                                    mSpotCheckScoreList.get(i).setScore((int) Double.parseDouble(scoreStr[i]));
                                }
                                // 抽查请求成功
                                mHandler.sendEmptyMessage(5);
                            } else {
                                Logs.e("获取抽查分数失败:");
                                mHandler.sendEmptyMessage(2);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取抽查分数失败:"+e.toString());
                            mHandler.sendEmptyMessage(2);
                        }
                    }
                });
    }

    // 获取普查分数
    private void initAllCheckScore() {
        OkHttpUtils
                .get()
                .url(URL.GET_DETAILS_SCORE)
                .addParams("dorBui",dorBui)
                .addParams("dorNo",dorNo)
                .addParams("checkType","普查")
                .addParams("weekCode",weekCode+"")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取普查分数失败:"+e.toString());
                        mHandler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("flag")) {
                                JSONObject data = jsonObject.getJSONObject("data");
                                String scoreString = data.getString("scoreString").trim();
                                allCheckScore = data.getDouble("score");
                                String[] scoreStr = scoreString.split(" ");
                                for (int i=0; i<scoreStr.length; i++) {
                                    mAllCheckScoreList.get(i).setScore((int) Double.parseDouble(scoreStr[i]));
                                }
                                // 普查请求成功
                                mHandler.sendEmptyMessage(0);
                            } else {
                                Logs.e("获取普查分数失败:");
                                mHandler.sendEmptyMessage(1);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取普查分数失败:"+e.toString());
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                });
    }

    // 获取宿舍成员信息
    private void initDorMember() {
        OkHttpUtils
                .get()
                .url(URL.GET_DOR_MEMBERS)
                .addParams("dorBui",dorBui)
                .addParams("dorNo",dorNo)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Logs.e("获取宿舍成员信息失败:"+e.toString());
                        mHandler.sendEmptyMessage(4);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.getBoolean("flag")) {
                                JSONArray data = jsonObject.getJSONArray("data");
                                for (int i=0; i<data.length(); i++) {
                                    JSONObject info = data.getJSONObject(i);
                                    String division = info.getString("division");
                                    String gender = info.getString("gender");
                                    String instructor = info.getString("instructor");
                                    String grade = info.getString("grade");
                                    String name = info.getString("name");
                                    String dorBui = info.getString("dorBui");
                                    String studentNum = info.getString("id");
                                    String dorNo = info.getString("dorNo");
                                    String academy = info.getString("academy");
                                    String classNu = info.getString("classNu");

                                    DorMember dorMember = new DorMember(division,gender,instructor,grade,name,
                                            dorBui+" "+dorNo,studentNum,academy,classNu);
                                    mDorMemberList.add(dorMember);
                                }
                                mHandler.sendEmptyMessage(3);
                            } else {
                                mHandler.sendEmptyMessage(4);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Logs.e("获取宿舍成员信息失败:"+e.toString());
                            mHandler.sendEmptyMessage(4);
                        }
                    }
                });
    }

    private void initTableData() {
        // 对普查数据初始化
        mAllCheckScoreList.clear();
        for (int i = 0; i< TextUtils.detailScoreNames.length; i++) {
            DetailScore d = new DetailScore();
            d.setScoreName(TextUtils.detailScoreNames[i]);
            mAllCheckScoreList.add(d);
        }

        // 对抽查数据初始化
        mSpotCheckScoreList.clear();
        for (int i = 0; i< TextUtils.detailScoreNames.length; i++) {
            DetailScore d = new DetailScore();
            d.setScoreName(TextUtils.detailScoreNames[i]);
            mSpotCheckScoreList.add(d);
        }

        // 清空宿舍成员信息
        mDorMemberList.clear();
    }

    // 设置普查表格数据
    private void setAllCheckScoreData() {
        bt_look_all_score.setText("普查评分详情("+allCheckScore+"分)");

        final Column<String> nameColumn = new Column<>("评分项", "scoreName");
        final Column<Integer> scoreColumn = new Column<>("得分", "score");

        final TableData<DetailScore> tableData = new TableData<>("第"+weekCode+"周", mAllCheckScoreList,nameColumn,scoreColumn);

        table_all_check_score.setTableData(tableData);
    }

    // 设置抽查表格数据
    private void setSpotCheckScoreData() {
        bt_look_spot_score.setText("抽查评分详情("+spotCheckScore+"分)");

        final Column<String> nameColumn = new Column<>("评分项", "scoreName");
        final Column<Integer> scoreColumn = new Column<>("得分", "score");

        final TableData<DetailScore> tableData = new TableData<>("第"+weekCode+"周", mSpotCheckScoreList,nameColumn,scoreColumn);

        table_spot_check_score.setTableData(tableData);
    }

    // 设置宿舍成员表格数据
    private void setDorMemberData() {
        final Column<String> dorNameColumn = new Column<>("姓名", "name");
        final Column<String> idColumn = new Column<>("学号", "id");
        final Column<String> genderColumn = new Column<>("性别", "gender");
        final Column<String> classNuColumn = new Column<>("班级", "classNu");
        final Column<String> gradeColumn = new Column<>("年级", "grade");
        final Column<String> divisionColumn = new Column<>("学部", "division");
        final Column<String> instructorColumn = new Column<>("导员", "instructor");
        final Column<String> academyColumn = new Column<>("书院", "academy");
        final Column<String> dorColumn = new Column<>("宿舍", "dor");
        dorNameColumn.setFixed(true);

        final TableData<DorMember> tableDorData = new TableData<>("宿舍成员信息", mDorMemberList,dorNameColumn,idColumn,
                genderColumn,classNuColumn,gradeColumn,divisionColumn,instructorColumn,academyColumn,dorColumn);
        tableDorData.setYSequenceFormat(new NumberSequenceFormat(){
            @Override
            public String format(Integer position) {
                return position-1+"";
            }
        });

        table_dor_menber.setTableData(tableDorData);
    }

    private void getActivityIntent() {
        Intent intent = getIntent();
        dorBui = intent.getStringExtra("dorBui");
        dorNo = intent.getStringExtra("dorNo");
        weekCode = intent.getIntExtra("weekCode",0);
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(dorBui+" "+dorNo+" 第"+weekCode+"周");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 获取屏幕宽度
        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        // 配置普查表格
        table_all_check_score = (SmartTable<DetailScore>) findViewById(R.id.table_all_check_score);
        //table_all_check_score.setZoom(true,1,0.5f); // 设置缩放比例
        table_all_check_score.getConfig().setShowXSequence(false); //不显示顶部序号
        table_all_check_score.getConfig().setShowYSequence(false);  // 不显示左部序号
        table_all_check_score.getConfig().setShowTableTitle(false); // 不显示表名
        table_all_check_score.getConfig().setMinTableWidth(screenWidth);    // 设置最小宽度和屏幕等宽
        table_all_check_score.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table_all_check_score.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));

        // 配置抽查表格
        table_spot_check_score = (SmartTable<DetailScore>) findViewById(R.id.table_spot_check_score);
        //table_all_check_score.setZoom(true,1,0.5f); // 设置缩放比例
        table_spot_check_score.getConfig().setShowXSequence(false); //不显示顶部序号
        table_spot_check_score.getConfig().setShowYSequence(false);  // 不显示左部序号
        table_spot_check_score.getConfig().setShowTableTitle(false); // 不显示表名
        table_spot_check_score.getConfig().setMinTableWidth(screenWidth);    // 设置最小宽度和屏幕等宽
        table_spot_check_score.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table_spot_check_score.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));

        // 配置宿舍成员信息表格
        table_dor_menber = (SmartTable<DorMember>) findViewById(R.id.table_dor_members);
        table_dor_menber.getConfig().setShowXSequence(false); //不显示顶部序号
        table_dor_menber.getConfig().setShowTableTitle(false); // 不显示表名
        table_dor_menber.getConfig().setMinTableWidth(screenWidth);    // 设置最小宽度和屏幕等宽
        table_dor_menber.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table_dor_menber.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));

        // 设置查看宿舍成员信息按钮点击事件
        Button bt_look_dor_member = (Button) findViewById(R.id.bt_look_dor_member);
        bt_look_dor_member.setOnClickListener(this);
        // 设置查看普查信息按钮点击事件
        bt_look_all_score = (Button) findViewById(R.id.bt_look_all_score);
        bt_look_all_score.setOnClickListener(this);
        // 设置查看抽查信息按钮点击事件
        bt_look_spot_score = (Button) findViewById(R.id.bt_look_spot_score);
        bt_look_spot_score.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 查看宿舍成员信息
            case R.id.bt_look_dor_member:
                if (table_dor_menber.getVisibility() == View.VISIBLE) {
                    table_dor_menber.setVisibility(View.GONE);
                } else {
                    table_dor_menber.setVisibility(View.VISIBLE);
                }
                break;
            // 查看普查信息
            case R.id.bt_look_all_score:
                if (table_all_check_score.getVisibility() == View.VISIBLE) {
                    table_all_check_score.setVisibility(View.GONE);
                } else {
                    table_all_check_score.setVisibility(View.VISIBLE);
                }
                break;
            // 查看抽查信息
            case R.id.bt_look_spot_score:
                if (table_spot_check_score.getVisibility() == View.VISIBLE) {
                    table_spot_check_score.setVisibility(View.GONE);
                } else {
                    table_spot_check_score.setVisibility(View.VISIBLE);
                }
                break;
        }
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
}
