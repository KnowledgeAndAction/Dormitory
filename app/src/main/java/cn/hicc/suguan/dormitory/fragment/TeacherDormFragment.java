package cn.hicc.suguan.dormitory.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.activity.DorDetailScore2Activity;
import cn.hicc.suguan.dormitory.adapter.TeacherDormRecylerAdapter;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.model.TeacherDorScore;
import cn.hicc.suguan.dormitory.utils.Logs;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.view.TeacherDorStringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/5/31/031.
 * 宿舍对比
 */

public class TeacherDormFragment extends Fragment {

    private HorizontalBarChart mChart;
    private List<Score> mDorScoreList;
    private int weekCode;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;
    private TextView tv_title;
    private RecyclerView mRecyclerView;
    private List<TeacherDorScore> scoreList = new ArrayList<>();
    private TeacherDormRecylerAdapter adapter;
    private LinearLayout ll_table;
    private String dataType;

    public TeacherDormFragment() {
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_dorm,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mChart = (HorizontalBarChart) view.findViewById(R.id.chart1);

        ll_table = (LinearLayout) view.findViewById(R.id.ll_table);

        tv_title = (TextView) view.findViewById(R.id.tv_title);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        // 设置LinearLayoutManager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // 设置ItemAnimator
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        // 设置固定大小
        mRecyclerView.setHasFixedSize(true);
        // 初始化自定义的适配器
        adapter = new TeacherDormRecylerAdapter(scoreList);
        // 为mRecyclerView设置适配
        mRecyclerView.setAdapter(adapter);
    }

    // 设置数据
    public void setData(int weekCode, List<Score> mDorScoreList, String dataType) {
        this.mDorScoreList = mDorScoreList;
        this.weekCode = weekCode;
        this.dataType = dataType;

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + weekCode + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        // 设置柱状图
        if (mChart != null) {
            initData(name);
        }

        // 设置表格
        initTableData(name);
    }

    // 切换展示形式
    public void changeView() {
        // 如果柱状图可见 表格不可见
        if (mChart.getVisibility() == View.VISIBLE && ll_table.getVisibility() == View.GONE) {
            mChart.setVisibility(View.GONE);
            ll_table.setVisibility(View.VISIBLE);
        } else if (mChart.getVisibility() == View.GONE && ll_table.getVisibility() == View.VISIBLE){
            mChart.setVisibility(View.VISIBLE);
            ll_table.setVisibility(View.GONE);
        }
    }

    // 设置表格
    private void initTableData(String name) {
        tv_title.setText(name+"成绩");

        // 转换数据格式
        for (Score score : mDorScoreList) {
            String typeName = score.getTypeName();
            String[] split = typeName.split(" ");
            TeacherDorScore teacherDorScore = new TeacherDorScore(split[0],split[1]+" "+split[2],score.getScore());
            scoreList.add(teacherDorScore);
        }

        adapter.notifyDataSetChanged();

        // 设置行点击事件
        adapter.setOnItemClickLister(new TeacherDormRecylerAdapter.OnItemClickLister() {
            @Override
            public void OnItemClick(int position) {
                if (dataType.equals("week")) {
                    TeacherDorScore teacherDorScore = scoreList.get(position);
                    Logs.d(teacherDorScore.getDorNum());
                    String[] dor = teacherDorScore.getDorNum().split(" ");
                    // 跳转到宿舍成绩详情页
                    Intent intent = new Intent(getContext(), DorDetailScore2Activity.class);
                    intent.putExtra("dorBui",dor[0]);
                    intent.putExtra("dorNo",dor[1]);
                    intent.putExtra("weekCode",weekCode);
                    startActivity(intent);
                } else {
                    ToastUtil.showShort("请切换到周数据查看宿舍评分详情");
                }
            }
        });
    }

    public void clearData() {
        mChart.clear();
        ToastUtil.showLong("暂无数据");
    }

    // 设置柱状图
    private void initData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mDorScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new TeacherDorStringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,name,iAxisValueFormatter,true);

        // TODO 设置图表选中监听事件
        mChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, Highlight highlight) {
                String[] dor = xAxisValues.get((int) entry.getX()).split(" ");
                Logs.d(dor[1] + " " + dor[2]);
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }
}
