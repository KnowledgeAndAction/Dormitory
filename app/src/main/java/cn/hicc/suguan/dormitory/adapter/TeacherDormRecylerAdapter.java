package cn.hicc.suguan.dormitory.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.TeacherDorScore;


public class TeacherDormRecylerAdapter extends RecyclerView.Adapter<TeacherDormRecylerAdapter.ViewHolder> {
    private List<TeacherDorScore> mlist;
    private OnItemClickLister onItemClickLister;

    public TeacherDormRecylerAdapter(List<TeacherDorScore> mlist) {//构造方法
        this.mlist = mlist;
    }

    @Override
    public TeacherDormRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_teacher_dorm, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(TeacherDormRecylerAdapter.ViewHolder holder, final int position) {
        TeacherDorScore score = mlist.get(position);
        holder.tv_class.setText(score.getClassName());
        holder.tv_dor.setText(score.getDorNum());
        holder.tv_score.setText(score.getScore() + "分");

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickLister.OnItemClick(position);
            }
        });
    }

    public void setItems(List<TeacherDorScore> newItems) {
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
        public View view;
        public TextView tv_class;
        public TextView tv_dor;
        private TextView tv_score;

        public ViewHolder(View v) {
            super(v);
            view = v;
            tv_class = (TextView) v.findViewById(R.id.tv_class);
            tv_dor = (TextView) v.findViewById(R.id.tv_dor);
            tv_score = (TextView) v.findViewById(R.id.tv_score);
        }
    }

    public void setOnItemClickLister(OnItemClickLister onItemClickLister) {
        this.onItemClickLister = onItemClickLister;
    }

    public interface OnItemClickLister {
        void OnItemClick(int position);
    }
}
