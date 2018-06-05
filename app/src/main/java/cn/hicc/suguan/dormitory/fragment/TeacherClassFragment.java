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
 * Created by 陈帅 on 2018/5/31/031.
 * 班级对比
 */

public class TeacherClassFragment extends Fragment {
    private List<Score> mClassScoreList;
    private List<String> xAxisValues;
    private List<Float> yAxisValues;
    private BarChart barChart1;
    private int weekCode;

    public TeacherClassFragment() {
    }

    @SuppressLint("ValidFragment")
    public TeacherClassFragment(int weekCode, List<Score> mClassScoreList) {
        this.mClassScoreList = mClassScoreList;
        this.weekCode = weekCode;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_teacher_class,container,false);

        initView(view);

        initData();

        return view;

    }

    private void initData() {
        xAxisValues = new ArrayList<>();
        yAxisValues = new ArrayList<>();
        for (Score score : mClassScoreList) {
            xAxisValues.add(score.getTypeName());
            yAxisValues.add((float) score.getScore());
        }

        IAxisValueFormatter xAxisFormatter = new StringAxisValueFormatter(xAxisValues);//设置自定义的x轴值格式化器
        MPChartHelper.setBarChart(getContext(),barChart1,xAxisValues,yAxisValues,"第" + weekCode + "周",xAxisFormatter);
    }

    private void initView(View view) {
        barChart1 = (BarChart) view.findViewById(R.id.chart1);
    }
}
