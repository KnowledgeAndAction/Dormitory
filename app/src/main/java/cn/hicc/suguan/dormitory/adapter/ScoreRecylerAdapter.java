package cn.hicc.suguan.dormitory.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.FlexibleScore;


public class ScoreRecylerAdapter extends RecyclerView.Adapter<ScoreRecylerAdapter.ViewHolder> {
    private List<FlexibleScore> mlist;

    public ScoreRecylerAdapter(List<FlexibleScore> mlist) {//构造方法
        this.mlist = mlist;
    }

    @Override
    public ScoreRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_score, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(ScoreRecylerAdapter.ViewHolder holder, int position) {
        FlexibleScore flexibleScore = mlist.get(position);
        holder.tv_condition1.setText(flexibleScore.getSeleteCondition1());
        holder.tv_condition2.setText(flexibleScore.getSeleteCondition2());
        holder.tv_condition3.setText(flexibleScore.getSeleteCondition3());
        holder.tv_score.setText(flexibleScore.getScore() + "分");
    }

    public void setItems(List<FlexibleScore> newItems) {
        int preSize = this.mlist.size();
        if(preSize > 0) {
            this.mlist.clear();
            notifyItemRangeRemoved(0, preSize);
        }
        this.mlist.addAll(newItems);
        notifyItemRangeChanged(0, newItems.size());
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_condition1;
        public TextView tv_condition2;
        public TextView tv_condition3;
        private TextView tv_score;

        public ViewHolder(View v) {
            super(v);
            tv_condition1 = (TextView) v.findViewById(R.id.tv_condition1);
            tv_condition2 = (TextView) v.findViewById(R.id.tv_condition2);
            tv_condition3 = (TextView) v.findViewById(R.id.tv_condition3);
            tv_score = (TextView) v.findViewById(R.id.tv_score);
        }
    }
}
