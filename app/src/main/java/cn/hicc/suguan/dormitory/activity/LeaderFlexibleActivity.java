package cn.hicc.suguan.dormitory.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bigkoo.pickerview.OptionsPickerView;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.ScoreRecylerAdapter;
import cn.hicc.suguan.dormitory.model.FlexibleScore;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.utils.URL;
import okhttp3.Call;

/**
 * 领导灵活查询界面
 */
public class LeaderFlexibleActivity extends AppCompatActivity implements View.OnClickListener{

    private List<String> pickerTimeTypes;
    private List<ArrayList<Integer>> pickerNumbers;
    private Toolbar toolbar;
    private RecyclerView mRecyclerView;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefresh;
    private List<FlexibleScore> mFlexibleScoreList = new ArrayList<>();
    private List<FlexibleScore> firstData = new ArrayList<>();  // 原始数据
    private ScoreRecylerAdapter adapter;

    private int mSeleteDate = 1;
    private String mSeleteDateType = "WeekCode";
    private String mSeleteCondition1 = "[Division]";
    private String mSeleteCondition2 = "NULL";
    private String mSeleteCondition3 = "NULL";
    private int checkedItem1 = 0;
    private int checkedItem2 = 0;
    private int checkedItem3 = 0;

    private Button bt_condition1;
    private Button bt_condition2;
    private Button bt_condition3;
    private LinearLayout ll_search;
    private Button bt_condition1_list;
    private Button bt_condition2_list;
    private Button bt_condition3_list;
    private Button bt_select;
    private Button bt_send;

    // 具体筛选内容
    private String[] mDetailedCondition1;
    private String[] mDetailedCondition2;
    private String[] mDetailedCondition3;
    private String mSeleteDetailedCondition1 = "全部";
    private String mSeleteDetailedCondition2 = "全部";
    private String mSeleteDetailedCondition3 = "全部";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_flexible);

        initView();

        // 初始化选择器数据
        initPickerViewData();
    }

    // 获取平均分数据
    private void getScoreData() {
        // 刷新时隐藏进一步筛选条件
        ll_search.setVisibility(View.GONE);
        swipeRefresh.setRefreshing(true);
        OkHttpUtils
                .get()
                .url(URL.CHECK_SCORE)
                .addParams("seleteCondition1", mSeleteCondition1)
                .addParams("seleteCondition2", mSeleteCondition2)
                .addParams("seleteCondition3", mSeleteCondition3)
                .addParams("seleteDate", mSeleteDate + "")
                .addParams("seleteDateType","[" + mSeleteDateType + "]")
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtil.showShort("获取数据失败，请稍后重试");
                        Logs.e("获取平均分失败:"+e.toString());
                        swipeRefresh.setRefreshing(false);
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        // 解析json
                        parseJson(response);
                    }
                });
    }

    // 解析json
    private void parseJson(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            // 响应后清空数据
            mFlexibleScoreList.clear();
            firstData.clear();
            if (jsonObject.getBoolean("flag")) {
                // 选择项具体内容
                JSONObject condition = jsonObject.getJSONObject("condition");
                // 先将原始json数据换个格式，选项之间以 - 分割
                String condition1 = condition.getString("condition1").replace("|","-");
                String condition2 = condition.getString("condition2").replace("|","-");
                String condition3 = condition.getString("condition3").replace("|","-");

                // 每个选择加个 全部 然后在根据 - 分割成数组
                mDetailedCondition1 = ("全部-"+condition1).split("-");
                bt_condition1_list.setText("筛选条件1");
                if (!condition2.equals("null")) {
                    mDetailedCondition2 = ("全部-"+condition2).split("-");
                    bt_condition2_list.setClickable(true);
                    bt_condition2_list.setText("筛选条件2");
                } else {
                    bt_condition2_list.setText("无");
                    bt_condition2_list.setClickable(false);
                }
                if (!condition3.equals("null")) {
                    mDetailedCondition3 = ("全部-"+condition3).split("-");
                    bt_condition3_list.setClickable(true);
                    bt_condition3_list.setText("筛选条件3");
                } else {
                    bt_condition3_list.setText("无");
                    bt_condition3_list.setClickable(false);
                }
                // 显示进一步筛选linearlayout
                ll_search.setVisibility(View.VISIBLE);
                // 初始化数据
                mSeleteDetailedCondition1 = "全部";
                mSeleteDetailedCondition2 = "全部";
                mSeleteDetailedCondition3 = "全部";


                // 具体数据
                JSONArray data = jsonObject.getJSONArray("data");
                for (int i=0; i<data.length(); i++) {
                    JSONObject scoreData = data.getJSONObject(i);

                    String condition1Des = scoreData.getString("condition1");
                    String condition2Des = scoreData.getString("condition2");
                    String condition3Des = scoreData.getString("condition3");
                    double score = scoreData.getDouble("score");

                    FlexibleScore flexibleScoreBean = new FlexibleScore();
                    flexibleScoreBean.setScore(score);
                    flexibleScoreBean.setSeleteCondition1(condition1Des);
                    flexibleScoreBean.setSeleteCondition2(condition2Des);
                    flexibleScoreBean.setSeleteCondition3(condition3Des);

                    mFlexibleScoreList.add(flexibleScoreBean);
                    // 保留原始数据
                    firstData.add(flexibleScoreBean);
                }
                // 更新数据
                adapter.notifyDataSetChanged();
            } else {
                ToastUtil.showShort("暂无数据，请检查选择信息是否有误");
            }
            swipeRefresh.setRefreshing(false);
        } catch (JSONException e) {
            e.printStackTrace();
            ToastUtil.showShort("获取数据失败，请稍后重试");
            Logs.e("获取平均分失败:"+e.toString());
            swipeRefresh.setRefreshing(false);
        }
    }

    private void initPickerViewData() {
        // 填充时间类型
        pickerTimeTypes = new ArrayList<>();
        pickerTimeTypes.add("周");
        pickerTimeTypes.add("月");

        // 具体第几
        pickerNumbers = new ArrayList<>();
        ArrayList<Integer> num = new ArrayList<>();
        for (int i=1; i<=20;i++) {
            num.add(i);
        }
        ArrayList<Integer> num2 = new ArrayList<>();
        for (int i=1; i<=12;i++) {
            num2.add(i);
        }
        pickerNumbers.add(num);
        pickerNumbers.add(num2);
    }

    // 显示选择器
    private void showPickerView() {
        //条件选择器
        OptionsPickerView pvOptions = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = pickerNumbers.get(options1).get(option2) + pickerTimeTypes.get(options1);
                toolbar.setTitle("宿舍管理 第"+ tx);

                // 设置URL参数信息
                String[] type = {"WeekCode","ScoreTimeMonth"};
                mSeleteDate = pickerNumbers.get(options1).get(option2);
                mSeleteDateType = type[options1];
                // 重新获取分数
                getScoreData();

            }
        }).setTitleText("选择时间范围")
                .build();
        pvOptions.setPicker(pickerTimeTypes, pickerNumbers);
        pvOptions.show();
    }

    private void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle("灵活查询");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        adapter = new ScoreRecylerAdapter(mFlexibleScoreList);
        // 为mRecyclerView设置适配
        mRecyclerView.setAdapter(adapter);
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


        fab = (FloatingActionButton) findViewById(R.id.fab);
        // 设置FAB点击事件
        fab.setRippleColor(getResources().getColor(R.color.colorPrimaryDark));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 弹出选择器
                showPickerView();
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
                getScoreData();
            }
        });

        // 配置按钮
        initButton();
    }

    // 配置按钮
    private void initButton() {
        bt_condition1 = (Button) findViewById(R.id.bt_condition1);
        bt_condition2 = (Button) findViewById(R.id.bt_condition2);
        bt_condition3 = (Button) findViewById(R.id.bt_condition3);
        bt_send = (Button) findViewById(R.id.bt_send);

        ll_search = (LinearLayout) findViewById(R.id.ll_search);
        bt_condition1_list = (Button) findViewById(R.id.bt_shuyuan);
        bt_condition2_list = (Button) findViewById(R.id.bt_sushe);
        bt_condition3_list = (Button) findViewById(R.id.bt_wuchengji);
        bt_select = (Button) findViewById(R.id.bt_select);

        bt_condition1.setOnClickListener(this);
        bt_condition2.setOnClickListener(this);
        bt_condition3.setOnClickListener(this);
        bt_condition1_list.setOnClickListener(this);
        bt_condition2_list.setOnClickListener(this);
        bt_condition3_list.setOnClickListener(this);
        bt_select.setOnClickListener(this);
        bt_send.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_condition1:
                // 弹出单选框
                showConditionSigleDialog(1,checkedItem1);
                break;
            case R.id.bt_condition2:
                // 弹出单选框
                showConditionSigleDialog(2, checkedItem2);
                break;
            case R.id.bt_condition3:
                // 弹出单选框
                showConditionSigleDialog(3, checkedItem3);
                break;
            case R.id.bt_shuyuan:
                showDetailedSingleDialog(1,mDetailedCondition1);
                break;
            case R.id.bt_sushe:
                showDetailedSingleDialog(2,mDetailedCondition2);
                break;
            case R.id.bt_wuchengji:
                showDetailedSingleDialog(3,mDetailedCondition3);
                break;
            // 进一步筛选
            case R.id.bt_select:
                selectScore();
                break;
            // 查询
            case R.id.bt_send:
                getScoreData();
                break;
        }
    }

    // 进一步筛选分数
    private void selectScore() {
        List<FlexibleScore> newList = new ArrayList<>();
        newList.clear();

        // 选择条件如果都为全部  查询全部
        if (mSeleteDetailedCondition1.equals("全部") && mSeleteDetailedCondition2.equals("全部") && mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                newList.add(flexibleScore);
            }
            // 根据条件1查询
        } else if (!mSeleteDetailedCondition1.equals("全部") && mSeleteDetailedCondition2.equals("全部") && mSeleteDetailedCondition3.equals("全部")){
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition1().equals(mSeleteDetailedCondition1)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据条件2查询
        } else if (mSeleteDetailedCondition1.equals("全部") && !mSeleteDetailedCondition2.equals("全部") && mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition2().equals(mSeleteDetailedCondition2)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据条件3查询
        } else if (mSeleteDetailedCondition1.equals("全部") && mSeleteDetailedCondition2.equals("全部") && !mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition3().equals(mSeleteDetailedCondition3)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据条件1 条件2 查询
        } else if (!mSeleteDetailedCondition1.equals("全部") && !mSeleteDetailedCondition2.equals("全部") && mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition1().equals(mSeleteDetailedCondition1) && flexibleScore.getSeleteCondition2().equals(mSeleteDetailedCondition2)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据条件1 条件3 查询
        } else if (!mSeleteDetailedCondition1.equals("全部") && mSeleteDetailedCondition2.equals("全部") && !mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition1().equals(mSeleteDetailedCondition1) && flexibleScore.getSeleteCondition3().equals(mSeleteDetailedCondition3)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据条件2 条件3 查询
        } else if (mSeleteDetailedCondition1.equals("全部") && !mSeleteDetailedCondition2.equals("全部") && !mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition2().equals(mSeleteDetailedCondition2) && flexibleScore.getSeleteCondition3().equals(mSeleteDetailedCondition3)) {
                    newList.add(flexibleScore);
                }
            }
            // 根据 条件1 条件2 条件3 查询
        } else if (!mSeleteDetailedCondition1.equals("全部") && !mSeleteDetailedCondition2.equals("全部") && !mSeleteDetailedCondition3.equals("全部")) {
            for (FlexibleScore flexibleScore : firstData) {
                if (flexibleScore.getSeleteCondition1().equals(mSeleteDetailedCondition1) && flexibleScore.getSeleteCondition2().equals(mSeleteDetailedCondition2) && flexibleScore.getSeleteCondition3().equals(mSeleteDetailedCondition3)) {
                    newList.add(flexibleScore);
                }
            }
        }
        adapter.setItems(newList);
    }

    // 弹出单选框 选择查询条件
    private void showConditionSigleDialog(final int condition, int checkedItem) {
        final String[] items = {"学部","书院","楼宇","年级","导员","性别","班级","查宿方式"};
        final String[] params = {"[Division]","[Academy]","[DormitoryBuilding]","[TimesCode]","[Instructor]","[Gender]","[ClassNu]","[Description]"};
        if (condition != 1) {
            items[0] = "无";
            params[0] = "NULL";
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择查询条件");
        builder.setIcon(R.mipmap.logo);
        builder.setSingleChoiceItems(items, checkedItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        checkedItem1 = which;
                        break;
                    case 2:
                        checkedItem2 = which;
                        break;
                    case 3:
                        checkedItem3 = which;
                        break;
                }
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        mSeleteCondition1 = params[checkedItem1];
                        bt_condition1.setText(items[checkedItem1]);
                        break;
                    case 2:
                        mSeleteCondition2 = params[checkedItem2];
                        bt_condition2.setText(items[checkedItem2]);
                        break;
                    case 3:
                        mSeleteCondition3 = params[checkedItem3];
                        bt_condition3.setText(items[checkedItem3]);
                        break;
                }
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

    // 弹出单选框 选择进一步查询条件
    private void showDetailedSingleDialog(final int condition, final String[] items) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择具体筛选条件");
        builder.setIcon(R.mipmap.logo);
        builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        mSeleteDetailedCondition1 = items[which];
                        break;
                    case 2:
                        mSeleteDetailedCondition2 = items[which];
                        break;
                    case 3:
                        mSeleteDetailedCondition3 = items[which];
                        break;
                }
            }
        });
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (condition) {
                    case 1:
                        bt_condition1_list.setText(mSeleteDetailedCondition1);
                        break;
                    case 2:
                        bt_condition2_list.setText(mSeleteDetailedCondition2);
                        break;
                    case 3:
                        bt_condition3_list.setText(mSeleteDetailedCondition3);
                        break;
                }
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
}
