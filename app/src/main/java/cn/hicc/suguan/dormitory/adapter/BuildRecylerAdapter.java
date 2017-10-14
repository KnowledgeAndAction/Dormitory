package cn.hicc.suguan.dormitory.adapter;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.Hostel;
import cn.hicc.suguan.dormitory.utils.TextUtils;


public class BuildRecylerAdapter extends RecyclerView.Adapter<BuildRecylerAdapter.ViewHolder> {
    private List<Hostel> mlist;
    private OnRecyclerViewItemClickListener mOnItemClickListener = null;

    public BuildRecylerAdapter(List<Hostel> mlist) {//构造方法
        this.mlist = mlist;
    }

    @Override
    public BuildRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_build, parent, false);
        ViewHolder holder = new ViewHolder(v);
        holder.content.setOnClickListener(new ItemClick());
        return holder;
    }

    @Override
    public void onBindViewHolder(BuildRecylerAdapter.ViewHolder holder, int position) {
        Hostel hostel = mlist.get(position);
        holder.content.setTag(position + "");
        holder.tv_build_info.setText(TextUtils.GetBuildName(hostel.getBuilding()) + hostel.getHostel() + "宿舍");
        holder.tv_back_code.setText(hostel.getCode() + "");
        holder.tv_build_week.setText("第" + hostel.getWeekCode() + "周");
        if (hostel.isCheck()) {
            holder.iv_check_if.setImageResource(R.mipmap.iv_true);
            if (hostel.getValue() == 0) {
                holder.tv_build_score.setText("已评分");
            } else {
                holder.tv_build_score.setText(hostel.getValue() + "分");
            }
        } else {
            holder.iv_check_if.setImageResource(R.mipmap.iv_false);
            holder.tv_build_score.setText("未评分");
        }
        if (hostel.getCheckType() == 1) {
            holder.tv_build_common.setTextColor(Color.rgb(0, 150, 136));
            holder.tv_build_extracting.setTextColor(Color.rgb(97, 97, 97));
        } else {
            holder.tv_build_common.setTextColor(Color.rgb(97, 97, 97));
            holder.tv_build_extracting.setTextColor(Color.rgb(0, 150, 136));
        }
    }

    public void setItems(List<Hostel> newItems) {
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
        public TextView tv_build_info;
        public ImageView iv_check_if;
        public TextView tv_build_score;
        public TextView tv_back_code;
        public CardView content;
        private TextView tv_build_common;
        private TextView tv_build_extracting;
        private TextView tv_build_week;

        public ViewHolder(View v) {
            super(v);
            content = (CardView) v.findViewById(R.id.cardview_build_adapter);
            tv_build_info = (TextView) v.findViewById(R.id.tv_build_info);
            iv_check_if = (ImageView) v.findViewById(R.id.iv_check_if);
            tv_build_score = (TextView) v.findViewById(R.id.tv_build_score);
            tv_back_code = (TextView) v.findViewById(R.id.tv_back_code);
            tv_build_common = (TextView) v.findViewById(R.id.tv_build_common);
            tv_build_extracting = (TextView) v.findViewById(R.id.tv_build_extracting);
            tv_build_week = (TextView) v.findViewById(R.id.tv_build_week);
        }
    }

    /**
     * 设置条目监听
     *
     * @param listener
     */
    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    /**
     * 条目点击事件接口
     */
    public interface OnRecyclerViewItemClickListener {
        void onItemClick(View view, int position, List<Hostel> mlist);
    }

    /**
     * 条目监听
     */
    class ItemClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (mOnItemClickListener != null) {
                //注意这里使用getTag方法获取数据
                mOnItemClickListener.onItemClick(v, Integer.parseInt(v.getTag().toString()), mlist);
            }
        }
    }
}
