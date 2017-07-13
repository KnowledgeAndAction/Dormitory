package cn.hicc.suguan.dormitory.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import cn.hicc.suguan.dormitory.R;
import cn.hicc.suguan.dormitory.model.HostelPerson;


public class HostelPersonAdapter extends RecyclerView.Adapter<HostelPersonAdapter.ViewHolder> {
    private List<HostelPerson> mlist;

    @Override
    public HostelPersonAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_hostel_person, parent, false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    public HostelPersonAdapter(List<HostelPerson> mlist) {
        this.mlist = mlist;
    }

    @Override
    public void onBindViewHolder(HostelPersonAdapter.ViewHolder holder, int position) {
        HostelPerson hostelPerson = mlist.get(position);
        holder.tv_adapter_hostel_bed.setText(hostelPerson.getBedNumber() + "号床");
        holder.tv_adapter_hostel_name.setText(hostelPerson.getStudentName());
    }

    @Override
    public int getItemCount() {
        return mlist == null ? 0 : mlist.size();
    }

    // 重写的自定义ViewHolder
    public static class ViewHolder
            extends RecyclerView.ViewHolder {
        public TextView tv_adapter_hostel_name;
        public TextView tv_adapter_hostel_bed;

        public ViewHolder(View v) {
            super(v);
            tv_adapter_hostel_bed = (TextView) v.findViewById(R.id.tv_adapter_hostel_bed);
            tv_adapter_hostel_name = (TextView) v.findViewById(R.id.tv_adapter_hostel_name);
        }
    }
}
