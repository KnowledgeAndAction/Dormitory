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
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.ToastUtil;
import cn.hicc.suguan.dormitory.view.StringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 学部导员对比
 */

public class DivisionTeacherFragment extends Fragment {

    private HorizontalBarChart mChart;
    private List<Score> mTeacherScoreList;
    private double avg;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;
    private SmartTable table;

    public DivisionTeacherFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_division_teacher,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mChart = (HorizontalBarChart) view.findViewById(R.id.chart1);
        table = (SmartTable) view.findViewById(R.id.table_all_check_score);
        //table.setZoom(true,2,0.6f); // 设置缩放比例
        table.getConfig().setShowXSequence(false); //不显示顶部序号
        table.getConfig().setFixedYSequence(true);  // 固定左侧
        // 获取屏幕宽度
        int screenWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        table.getConfig().setMinTableWidth(screenWidth);
        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
        table.getConfig().setCountBackground(new BaseBackgroundFormat(getResources().getColor(R.color.windows_bg)));
    }

    // 设置数据
    public void setData(double avg, int weekCode, List<Score> mTeacherScoreList, String dataType) {
        this.avg = avg;
        this.mTeacherScoreList = mTeacherScoreList;

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + weekCode + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        if (mChart != null) {
            initData(name);
        }

        initTableData(name);
    }

    // 切换展示形式
    public void changeView() {
        // 如果柱状图可见 表格不可见
        if (mChart.getVisibility() == View.VISIBLE && table.getVisibility() == View.GONE) {
            mChart.setVisibility(View.GONE);
            table.setVisibility(View.VISIBLE);
        } else if (mChart.getVisibility() == View.GONE && table.getVisibility() == View.VISIBLE){
            mChart.setVisibility(View.VISIBLE);
            table.setVisibility(View.GONE);
        }
    }

    // 设置表格
    private void initTableData(String name) {
        //普通列
        Column<String> column1 = new Column<>("导员", "typeName");
        Column<Integer> column2 = new Column<>("成绩", "score");
        column2.setReverseSort(true);
        //表格数据 datas是需要填充的数据
        TableData<Score> tableData = new TableData<>(name+"成绩对比",mTeacherScoreList,column1,column2);
        tableData.setYSequenceFormat(new NumberSequenceFormat(){
            @Override
            public String format(Integer position) {
                return position-1+"";
            }
        });
        tableData.setSortColumn(column2);
        if (table != null) {
            table.setTableData(tableData);
        }
    }

    public void clearData() {
        mChart.clear();
        ToastUtil.showLong("暂无成绩");
    }

    private void initData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mTeacherScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,name + "\n学部总平均分：" + avg ,iAxisValueFormatter,true);
    }
}
