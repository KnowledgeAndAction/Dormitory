package cn.hicc.suguan.dormitory.fragment;

import android.annotation.SuppressLint;
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
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.view.LeaderDivisionStringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 领导学部对比   领导书院对比
 */

public class LeaderDivisionFragment extends Fragment {

    private BarChart mChart;
    private List<Score> mDivisionScoreList;
    private int weekCode;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public LeaderDivisionFragment() {
    }

    @SuppressLint("ValidFragment")
    public LeaderDivisionFragment(int weekCode, List<Score> mDivisionScoreList) {
        this.mDivisionScoreList = mDivisionScoreList;
        this.weekCode = weekCode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leader_division,container,false);

        initView(view);

        initData();

        return view;
    }

    private void initView(View view) {
        mChart = (BarChart) view.findViewById(R.id.chart1);
    }

    private void initData() {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mDivisionScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new LeaderDivisionStringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,"第" + weekCode + "周" ,iAxisValueFormatter);
    }
}
