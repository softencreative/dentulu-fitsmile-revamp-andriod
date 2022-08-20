package com.app.fitsmile.book;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class HealthRecordSubList  extends RecyclerView.Adapter<HealthRecordSubList.ViewHolder> {

    private Activity activity;
    private List<String> items;

    public HealthRecordSubList(Activity activity) {
        this.activity = activity;
        items = new ArrayList<>();
    }

    public void setCommonList(List<String> item) {
        if (item == null)
            return;
        items.clear();
        items.addAll(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_health_sub_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvDesp.setText(Utils.getStr(items.get(position)));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvDesp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesp = itemView.findViewById(R.id.tv_desp);
        }
    }
}

