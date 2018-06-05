package cn.hicc.suguan.dormitory.view;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * 柱状图
 * 对字符串类型的坐标轴标记进行格式化  学部班级对比
 */
public class DivisionClassStringAxisValueFormatter implements IAxisValueFormatter {

    //区域值
    private List<String> mStrs;

    /**
     * 对字符串类型的坐标轴标记进行格式化
     * @param strs
     */
    public DivisionClassStringAxisValueFormatter(List<String> strs){
        this.mStrs =strs;
    }

    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
        String[] split = mStrs.get((int) v).split(" ");
        return split[0];
    }
}
