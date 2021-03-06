package cn.hicc.suguan.dormitory.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.sequence.NumberSequenceFormat;
import com.bin.david.form.data.table.TableData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.view.StringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/5/31/031.
 * 班级对比
 */

public class TeacherClassFragment extends Fragment {
    private List<Score> mClassScoreList;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;
    private BarChart barChart1;
    private SmartTable table;

    public TeacherClassFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_class,container,false);

        initView(view);

        return view;

    }

    // 设置数据
    public void setData(int weekCode, List<Score> mClassScoreList, String dataType) {
        this.mClassScoreList = mClassScoreList;

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + SpUtil.getInt(Constant.SEMESTER_WEEK) + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        if (barChart1 != null) {
            initData(name);
        }

        initTableData(name);
    }

    // 切换展示形式
    public void changeView() {
        // 如果柱状图可见 表格不可见
        if (barChart1.getVisibility() == View.VISIBLE && table.getVisibility() == View.GONE) {
            barChart1.setVisibility(View.GONE);
            table.setVisibility(View.VISIBLE);
        } else if (barChart1.getVisibility() == View.GONE && table.getVisibility() == View.VISIBLE){
            barChart1.setVisibility(View.VISIBLE);
            table.setVisibility(View.GONE);
        }
    }

    // 设置表格
    private void initTableData(String name) {
        //普通列
        Column<String> column1 = new Column<>("班级", "typeName");
        Column<Integer> column2 = new Column<>("成绩", "score");
        column2.setReverseSort(true);
        //表格数据 datas是需要填充的数据
        TableData<Score> tableData = new TableData<>(name+"成绩对比",mClassScoreList,column1,column2);
        tableData.setYSequenceFormat(new NumberSequenceFormat(){
            @Override
            public String format(Integer position) {
                return position-1+"";
            }
        });
        tableData.setSortColumn(column2);
        table.setTableData(tableData);
    }

    public void clearData() {
        barChart1.clear();
        ToastUtil.showLong("暂无数据");
    }

    private void initData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mClassScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValues);//设置自定义的x轴值格式化器
        MPChartHelper.setBarChart(getContext(),barChart1,xAxisValues,yAxisValues,name,xAxisFormatter,true);
    }

    private void initView(View view) {
        barChart1 = (BarChart) view.findViewById(R.id.chart1);
        table = (SmartTable) view.findViewById(R.id.table_all_check_score);
        table.setZoom(false); // 设置缩放比例
        table.getConfig().setShowXSequence(false); //不显示顶部序号
        table.getConfig().setFixedYSequence(true);  // 固定左侧
        // 获取屏幕宽度
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        table.getConfig().setMinTableWidth(screenWidth);
        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
    }
}
