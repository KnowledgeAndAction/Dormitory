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
import cn.hicc.suguan.dormitory.view.StringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 学部检查类型对比
 */

public class DivisionCheckTypeFragment extends Fragment {

    private BarChart mChart;
    private List<Score> mCheckTypeScoreList;
    private int weekCode;
    private double avg;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public DivisionCheckTypeFragment() {
    }

    @SuppressLint("ValidFragment")
    public DivisionCheckTypeFragment(double avg, int weekCode, List<Score> mCheckTypeScoreList) {
        this.avg = avg;
        this.mCheckTypeScoreList = mCheckTypeScoreList;
        this.weekCode = weekCode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_division_checktype,container,false);

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
        for (Score score : mCheckTypeScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter iAxisValueFormatter = new StringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,"第" + weekCode + "周\n学部总平均分：" + avg ,iAxisValueFormatter);
    }
}
