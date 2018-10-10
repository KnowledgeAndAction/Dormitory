package cn.hicc.suguan.dormitory.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.view.LeaderDivisionStringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 领导学部对比
 */

public class LeaderDivisionFragment extends Fragment {

    private BarChart mChart;
    private List<Score> mDivisionScoreList;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public LeaderDivisionFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_division,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mChart = (BarChart) view.findViewById(R.id.chart1);
    }

    // 设置数据
    public void setData(int weekCode, List<Score> mDivisionScoreList, String dataType) {
        this.mDivisionScoreList = mDivisionScoreList;

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + SpUtil.getInt(Constant.SEMESTER_WEEK) + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        if (mChart != null) {
            initData(name);
        }
    }

    public void clearData() {
        mChart.clear();
    }

    private void initData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mDivisionScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new LeaderDivisionStringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,name ,iAxisValueFormatter,false);
    }
}
