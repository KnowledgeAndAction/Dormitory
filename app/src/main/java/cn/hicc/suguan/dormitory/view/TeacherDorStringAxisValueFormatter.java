package cn.hicc.suguan.dormitory.view;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.List;

/**
 * 柱状图
 * 对字符串类型的坐标轴标记进行格式化  宿舍对比
 */
public class TeacherDorStringAxisValueFormatter implements IAxisValueFormatter {

    //区域值
    private List<String> mStrs;

    /**
     * 对字符串类型的坐标轴标记进行格式化
     * @param strs
     */
    public TeacherDorStringAxisValueFormatter(List<String> strs){
        this.mStrs =strs;
    }

    @Override
    public String getFormattedValue(float v, AxisBase axisBase) {
        if (mStrs.size() > 0) {
            String[] split = mStrs.get((int) v).split(" ");
            return split[1] + split[2];
        }
        return "";
    }
}
