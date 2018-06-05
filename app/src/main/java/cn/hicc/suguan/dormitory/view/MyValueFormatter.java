package cn.hicc.suguan.dormitory.view;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import cn.hicc.suguan.dormitory.utils.StringUtils;

/**
 * 柱状图 数值格式化
 */

public class MyValueFormatter implements IValueFormatter {

    @Override
    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
        return StringUtils.double2String(value, 2);
    }
}
