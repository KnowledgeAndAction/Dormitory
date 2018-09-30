package cn.hicc.suguan.dormitory.adapter;

import android.app.Activity;
import android.support.annotation.Nullable;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.sequence.NumberSequenceFormat;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.table.TableData;
import com.bin.david.form.utils.DensityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.DivisionClassScore;
import cn.hicc.suguan.dormitory.model.Score;

/**
 * Created by huang on 2017/10/13.
 */

public class TableListAdapter extends BaseQuickAdapter<List<Score>,BaseViewHolder> {

    private Activity activity;
    private String name;

    public TableListAdapter(@Nullable List<List<Score>> data,Activity activity, String name) {
        super(R.layout.item_table, data);
        this.activity = activity;
        this.name = name;
    }

    @Override
    protected void convert(BaseViewHolder helper, List<Score> item) {
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(mContext,15));
        final SmartTable<DivisionClassScore> table =  helper.getView(R.id.table);

        table.getConfig().setShowXSequence(false); //不显示顶部序号
        table.getConfig().setFixedYSequence(true);  // 固定左侧
        // 获取屏幕宽度
        int screenWidth = activity.getWindowManager().getDefaultDisplay().getWidth();
        table.getConfig().setMinTableWidth(screenWidth);
        table.getConfig().setColumnTitleBackground(new BaseBackgroundFormat(activity.getResources().getColor(R.color.windows_bg)));
        table.getConfig().setCountBackground(new BaseBackgroundFormat(activity.getResources().getColor(R.color.windows_bg)));

        // 转换数据形式
        List<DivisionClassScore> scoreList = new ArrayList<>();
        for (Score score : item) {
            String[] split = score.getTypeName().split(" ");
            DivisionClassScore divisionClassScore = new DivisionClassScore(split[1],split[2],score.getScore());
            scoreList.add(divisionClassScore);
        }

        // 获取年级，先得到年级分数集合，然后获取第一个数据，再获取他的类型名字，然后截取第一个字符串
        String grade = item.get(0).getTypeName().split(" ")[0];

        //普通列
        Column<String> column1 = new Column<>("班级", "className");
        Column<String> column2 = new Column<>("导员", "teacher");
        Column<Integer> column3 = new Column<>("成绩", "score");
        column3.setReverseSort(true);   // 反序排序
        //表格数据 datas是需要填充的数据
        TableData<DivisionClassScore> tableData = new TableData<>(name + grade + "班级成绩",scoreList,column1,column2,column3);
        tableData.setSortColumn(column3);   // 设置列排序
        tableData.setYSequenceFormat(new NumberSequenceFormat(){
            @Override
            public String format(Integer position) {
                return position-1+"";
            }
        });
        table.setTableData(tableData);
    }

}
