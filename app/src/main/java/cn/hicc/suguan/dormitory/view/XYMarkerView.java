
package cn.hicc.suguan.dormitory.view;

import android.content.Context;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;
import java.util.List;

import cn.hicc.suguan.dormitory.R;

/**
 * 柱状图标记
 * Custom implementation of the MarkerView.
 */
public class XYMarkerView extends MarkerView {

    private TextView tvContent;
    private List<String> xAxisValue; // 原始的x值

    private DecimalFormat format;

    public XYMarkerView(Context context, List<String> xAxisValue) {
        super(context, R.layout.custom_xy_marker_view);

        this.xAxisValue = xAxisValue;
        tvContent = (TextView) findViewById(R.id.tvContent);
        format = new DecimalFormat("###.0");
    }

    // callbacks everytime the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        //tvContent.setText(xAxisValueFormatter.getFormattedValue(e.getX(), null) + "：" + format.format(e.getY()) + "分") ;
        tvContent.setText(xAxisValue.get((int) e.getX()) + "：" + format.format(e.getY()) + "分") ;

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
