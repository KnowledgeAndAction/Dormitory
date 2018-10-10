package cn.hicc.suguan.dormitory.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.adapter.TableListAdapter;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.view.DivisionClassStringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 学部班级对比
 */

public class DivisionClassFragment extends Fragment {

    private double avg;
    private LinearLayout ll_barchart;
    private List<List<Score>> allScoreList = new ArrayList<>();   // 总的成绩集合，包括了各个年级的
    private RecyclerView recyclerView_table;
    private TableListAdapter itemAdapter;
    private ScrollView sv_barchart;

    public DivisionClassFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_division_class,container,false);

        initView(view);

        return view;
    }

    private void addView(List<Score> gradeList) {
        // 动态添加柱状图
        ll_barchart.removeAllViews();
        for (int i=0; i<gradeList.size(); i++) {
            View child = LayoutInflater.from(getContext()).inflate(R.layout.child_division_claass_barchart, ll_barchart,false);
            ll_barchart.addView(child);
        }
    }

    private void initView(View view) {
        ll_barchart = (LinearLayout) view.findViewById(R.id.ll_barchart);
        sv_barchart = (ScrollView) view.findViewById(R.id.sv_barchart);

        recyclerView_table = (RecyclerView) view.findViewById(R.id.recyclerView_table);
        recyclerView_table.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    // 设置数据
    public void setData(double avg, int weekCode, List<Score> mClassScoreList, String dataType, List<Score> gradeList) {
        this.avg = avg;

        // 动态添加图表
        addView(gradeList);

        // 分解数据
        allScoreList.clear();
        for (Score gradeName : gradeList) {
            // 根据有几个年级，创建出几个年级集合来
            List<Score> gradeScoreList = new ArrayList<>();
            // 遍历所有数据，把相同的放入对应的年级集合里
            for (Score score : mClassScoreList) {
                // 获取年级信息
                String[] split = score.getTypeName().split(" ");
                // 如果年级名字与所遍历的数据相同，添加到集合中
                if (gradeName.getTypeName().equals(split[0])) {
                    gradeScoreList.add(score);
                }
            }
            allScoreList.add(gradeScoreList);
        }

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + SpUtil.getInt(Constant.SEMESTER_WEEK) + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        // 设置柱状图数据
        if (ll_barchart != null) {
            initData(name);
        }

        // 设置表格
        initTableData(name);
    }

    // 切换展示形式
    public void changeView() {
        // 如果柱状图可见 表格不可见
        if (sv_barchart.getVisibility() == View.VISIBLE && recyclerView_table.getVisibility() == View.GONE) {
            sv_barchart.setVisibility(View.GONE);
            recyclerView_table.setVisibility(View.VISIBLE);
        } else if (sv_barchart.getVisibility() == View.GONE && recyclerView_table.getVisibility() == View.VISIBLE){
            sv_barchart.setVisibility(View.VISIBLE);
            recyclerView_table.setVisibility(View.GONE);
        }
    }

    // 设置表格
    private void initTableData(String name) {
        itemAdapter = new TableListAdapter(allScoreList,getActivity(),name);
        recyclerView_table.setAdapter(itemAdapter);
        itemAdapter.openLoadAnimation();
    }

    public void clearData() {
        for (int i=0; i<allScoreList.size(); i++) {
            HorizontalBarChart childAt = (HorizontalBarChart) ll_barchart.getChildAt(i);
            childAt.clear();
        }
        ToastUtil.showLong("暂无数据");
    }

    // 设置柱状图数据
    private void initData(String name) {
        for (int i=0; i<allScoreList.size(); i++) {
            LinearLayout childAt = (LinearLayout) ll_barchart.getChildAt(i);
            HorizontalBarChart barChart = (HorizontalBarChart) childAt.getChildAt(0);

            List<String> xAxisValues = new ArrayList<>();
            List<Float> yAxisValues = new ArrayList<>();
            for (Score score : allScoreList.get(i)) {
                xAxisValues.add(score.getTypeName());
                yAxisValues.add((float) score.getScore());
            }

            // 获取年级，先得到年级分数集合，然后获取第一个数据，再获取他的类型名字，然后截取第一个字符串
            String grade = allScoreList.get(i).get(0).getTypeName().split(" ")[0];
            // 格式化x轴信息
            IAxisValueFormatter iAxisValueFormatter = new DivisionClassStringAxisValueFormatter(xAxisValues);
            MPChartHelper.setBarChart(getContext(),barChart,xAxisValues,yAxisValues,name+ " " + grade + "班级对比",iAxisValueFormatter,true);
        }
    }
}
