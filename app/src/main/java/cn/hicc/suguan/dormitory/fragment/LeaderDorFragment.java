package cn.hicc.suguan.dormitory.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.Score;
import cn.hicc.suguan.dormitory.utils.Constant;
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.utils.SpUtil;
import cn.hicc.suguan.dormitory.view.StringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 领导宿舍楼对比
 */

public class LeaderDorFragment extends Fragment {

    private HorizontalBarChart mChart;
    private List<Score> mDorScoreList;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public LeaderDorFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_dor,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mChart = (HorizontalBarChart) view.findViewById(R.id.chart1);
    }

    // 设置数据
    public void setData(int weekCode, List<Score> mDorScoreList, String dataType) {
        this.mDorScoreList = mDorScoreList;

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
        for (Score score : mDorScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,name ,iAxisValueFormatter,false);
    }
}
