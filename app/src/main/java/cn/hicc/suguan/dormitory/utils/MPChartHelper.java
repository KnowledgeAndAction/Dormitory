package cn.hicc.suguan.dormitory.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.view.MyValueFormatter;
import cn.hicc.suguan.dormitory.view.XYMarkerView;

/**
 * 柱状图 初始化工具类
 */

public class MPChartHelper {

    private static Typeface mTfLight;

    /**
     * 单数据集。设置柱状图样式，X轴为字符串，Y轴为数值
     * @param context
     * @param barChart
     * @param xAxisValue
     * @param yAxisValue
     * @param title 图例文字
     * @param xAxisFormatter 自定义的x轴值格式化器
     * @param isZoom 是否缩放
     */
    public static void setBarChart(Context context,BarChart barChart, List<String> xAxisValue, List<Float> yAxisValue,
                                   String title, IAxisValueFormatter xAxisFormatter,boolean isZoom) {
        barChart.getDescription().setEnabled(false);//设置描述
        barChart.setPinchZoom(isZoom);//设置按比例放缩柱状图
        barChart.setScaleEnabled(isZoom);     //启用/禁用缩放图表上的两个轴。
        barChart.setNoDataText("暂无数据");


        // 字体
        mTfLight = Typeface.createFromAsset(context.getAssets(), "OpenSans-Light.ttf");
        barChart.setNoDataTextTypeface(mTfLight);

        //x坐标轴设置
        XAxis xAxis = barChart.getXAxis();//获取x轴
        xAxis.setTypeface(mTfLight); // 设置字体
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴标签显示位置
        xAxis.setDrawGridLines(false);//制格网线
        xAxis.setGranularity(1f);//设置最小间隔，防止当放大时，出现重复标签。
        xAxis.setValueFormatter(xAxisFormatter); // 设置自定义的x轴值格式化器
        xAxis.setTextSize(10);//设置x轴字体大小
        xAxis.setLabelCount(xAxisValue.size());//设置标签显示的个数  xAxisValue.size()

        //y轴设置
        YAxis leftAxis = barChart.getAxisLeft();//获取左侧y轴
        leftAxis.setTypeface(mTfLight);
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);//设置y轴标签显示在外侧
        leftAxis.setAxisMinimum(0f);//设置Y轴最小值
        leftAxis.setDrawGridLines(true);
        leftAxis.setDrawLabels(true);//绘制y轴标签
        leftAxis.setDrawAxisLine(true);//绘制y轴

        //barChart.getAxisRight().setEnabled(true);//绘制用右侧y轴
        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setTypeface(mTfLight);
        rightAxis.setDrawGridLines(false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replas setStartAtZero(true)


        //设置自定义的markerViewce
        XYMarkerView mv = new XYMarkerView(barChart.getContext(), xAxisValue);
        mv.setChartView(barChart); // For bounds control
        barChart.setMarker(mv); // Set the marker to the chart

        //图例设置
        Legend l = barChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);//图例在图表下方
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);//图例在图表右边
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);//图例的方向为水平
        l.setDrawInside(false);//绘制在chart的外侧
        l.setForm(Legend.LegendForm.SQUARE);//图例窗体的形状
        l.setFormSize(9f);//图例窗体的大小
        l.setTextSize(11f);//图例文字的大小
        l.setXEntrySpace(4f);

        //设置柱状图数据
        setBarChartData(barChart, yAxisValue, title);

        barChart.setExtraBottomOffset(10);//距视图窗口底部的偏移，类似与paddingbottom
        barChart.setExtraTopOffset(30);//距视图窗口顶部的偏移，类似与paddingtop
        barChart.setFitBars(true);//使两侧的柱图完全显示
        barChart.animateX(1500);//数据显示动画，从左往右依次显示
    }

    /**
     * 设置柱图
     * @param barChart
     * @param yAxisValue
     * @param title
     */
    private static void setBarChartData(BarChart barChart, List<Float> yAxisValue, String title) {

        ArrayList<BarEntry> entries = new ArrayList<>();

        for (int i = 0, n = yAxisValue.size(); i < n; ++i) {
            entries.add(new BarEntry(i, yAxisValue.get(i)));
        }

        BarDataSet set1;

        if (barChart.getData() != null && barChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) barChart.getData().getDataSetByIndex(0);
            set1.setLabel(title); // 设置图例文字
            set1.setValues(entries);
            barChart.getData().notifyDataChanged();
            barChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(entries, title);
            //if (barColor == null) {
                set1.setColor(ContextCompat.getColor(barChart.getContext(), R.color.bar));//设置set1的柱的颜色
            //} else {
                //set1.setColor(barColor);
            //}

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTypeface(mTfLight); // 设置字体
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());

            barChart.setData(data);
        }
    }
}
