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
import cn.hicc.suguan.dormitory.view.StringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 学部年级对比  性别对比  检查类型对比
 */

public class LeaderBasisFragment extends Fragment {

    private BarChart mChartGrade;
    private BarChart mChartSex;
    private BarChart mChartCheckType;
    private List<Score> mGradeScoreList;
    private List<Score> mSexScoreList;
    private List<Score> mCheckTypeScoreList;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public LeaderBasisFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_basis,container,false);

        initView(view);

        return view;
    }

    private void initView(View view) {
        mChartGrade = (BarChart) view.findViewById(R.id.chart_grade);
        mChartSex = (BarChart) view.findViewById(R.id.chart_sex);
        mChartCheckType = (BarChart) view.findViewById(R.id.chart_check_type);
    }

    // 设置数据
    public void setData(int weekCode, List<Score> mGradeScoreList,List<Score> mSexScoreList,
                        List<Score> mCheckTypeScoreList, String dataType) {
        this.mGradeScoreList = mGradeScoreList;
        this.mSexScoreList = mSexScoreList;
        this.mCheckTypeScoreList = mCheckTypeScoreList;

        String name = "";
        switch (dataType) {
            case "week":
                name = "第" + SpUtil.getInt(Constant.SEMESTER_WEEK) + "周";
                break;
            case "month":
                name = weekCode + "月";
                break;
        }

        if (mChartGrade != null) {
            initGradeData(name);
        }
        if (mChartSex != null) {
            initSexData(name);
        }
        if (mChartCheckType != null) {
            initCheckTypeData(name);
        }
    }

    public void clearData() {
        mChartGrade.clear();
        mChartSex.clear();
        mChartCheckType.clear();
    }

    private void initGradeData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mGradeScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChartGrade,xAxisValues,yAxisValues, name + "  年级对比",iAxisValueFormatter,false);
    }
    private void initSexData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mSexScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChartSex,xAxisValues,yAxisValues,name + "  性别对比",iAxisValueFormatter,false);
    }
    private void initCheckTypeData(String name) {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mCheckTypeScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChartCheckType,xAxisValues,yAxisValues,name + "  检查类型对比",iAxisValueFormatter,false);
    }
}
