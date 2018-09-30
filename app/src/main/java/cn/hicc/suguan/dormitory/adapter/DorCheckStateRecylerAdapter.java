package cn.hicc.suguan.dormitory.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.DorCheckState;
import cn.hicc.suguan.dormitory.utils.ToastUtil;


public class DorCheckStateRecylerAdapter extends RecyclerView.Adapter<DorCheckStateRecylerAdapter.ViewHolder> {
    private List<DorCheckState> mlist;

    public DorCheckStateRecylerAdapter(List<DorCheckState> mlist) {//构造方法
        this.mlist = mlist;
    }

    @Override
    public DorCheckStateRecylerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_item_dor_check_state, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(DorCheckStateRecylerAdapter.ViewHolder holder, int position) {
        DorCheckState dorCheckState = mlist.get(position);
        holder.tv_shuyuan.setText(dorCheckState.getShuyuan());
        holder.tv_lou.setText(dorCheckState.getLou());
        holder.tv_hao.setText(dorCheckState.getHao());
        holder.tv_pucha.setText(dorCheckState.getPucha() + "分");
        holder.tv_choucha.setText(dorCheckState.getChoucha() + "分");
    }

    public void setItems(List<DorCheckState> newItems) {
        int preSize = this.mlist.size();
        if(preSize > 0) {
            this.mlist.clear();
            notifyItemRangeRemoved(0, preSize);
        }
        this.mlist.addAll(newItems);
        notifyItemRangeChanged(0, newItems.size());
        if (newItems.size() == 0) {
            ToastUtil.showShort("没有符合条件的数据，请检查筛选条件是否正确");
        }
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_shuyuan;
        public TextView tv_lou;
        public TextView tv_hao;
        private TextView tv_pucha;
        private TextView tv_choucha;

        public ViewHolder(View v) {
            super(v);
            tv_shuyuan = (TextView) v.findViewById(R.id.tv_shuyuan);
            tv_lou = (TextView) v.findViewById(R.id.tv_lou);
            tv_hao = (TextView) v.findViewById(R.id.tv_hao);
            tv_pucha = (TextView) v.findViewById(R.id.tv_pucha);
            tv_choucha = (TextView) v.findViewById(R.id.tv_choucha);
        }
    }
}
