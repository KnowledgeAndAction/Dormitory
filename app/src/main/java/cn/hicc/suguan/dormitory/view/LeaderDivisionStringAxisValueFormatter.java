package cn.hicc.suguan.dormitory.view;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * 柱状图
 * 对字符串类型的坐标轴标记进行格式化 领导学部对比
 */
public class LeaderDivisionStringAxisValueFormatter implements IAxisValueFormatter {

    //区域值
    private List<String> mStrs;

    /**
     * 对字符串类型的坐标轴标记进行格式化
     * @param strs
     */
    public LeaderDivisionStringAxisValueFormatter(List<String> strs){
        this.mStrs =strs;
    }

    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
        String s = mStrs.get((int) v);
        if (s.equals("国际文化交流学部")) {
            s = "国交学部";
        }
        return s;
    }
}
