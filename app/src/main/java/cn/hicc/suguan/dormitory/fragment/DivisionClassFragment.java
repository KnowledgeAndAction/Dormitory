package cn.hicc.suguan.dormitory.fragment;

import android.annotation.SuppressLint;
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
import cn.hicc.suguan.dormitory.utils.MPChartHelper;
import cn.hicc.suguan.dormitory.view.DivisionClassStringAxisValueFormatter;

/**
 * Created by 陈帅 on 2018/6/5/031.
 * 学部班级对比
 */

public class DivisionClassFragment extends Fragment {

    private HorizontalBarChart mChart;
    private List<Score> mClassScoreList;
    private int weekCode;
    private double avg;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;

    public DivisionClassFragment() {
    }

    @SuppressLint("ValidFragment")
    public DivisionClassFragment(double avg, int weekCode, List<Score> mClassScoreList) {
        this.avg = avg;
        this.mClassScoreList = mClassScoreList;
        this.weekCode = weekCode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_division_class,container,false);

        initView(view);

        initData();

        return view;
    }

    private void initView(View view) {
        mChart = (HorizontalBarChart) view.findViewById(R.id.chart1);
    }

    private void initData() {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mClassScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        // 格式化x轴信息
        IAxisValueFormatter iAxisValueFormatter = new DivisionClassStringAxisValueFormatter(xAxisValues);
        MPChartHelper.setBarChart(getContext(),mChart,xAxisValues,yAxisValues,"第" + weekCode + "周\n学部总平均分："+avg,iAxisValueFormatter);
    }
}
