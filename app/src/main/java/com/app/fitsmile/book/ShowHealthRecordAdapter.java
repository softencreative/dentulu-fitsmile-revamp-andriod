package com.app.fitsmile.book;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.healthrec.HealthHIstoryItem;

import java.util.ArrayList;
import java.util.List;

public class ShowHealthRecordAdapter extends RecyclerView.Adapter<ShowHealthRecordAdapter.ViewHolder> {

    private Activity activity;
    private List<HealthHIstoryItem> items;


    public ShowHealthRecordAdapter(Activity activity) {
        this.activity = activity;
        items = new ArrayList<>();
    }

    public void setCommonList(List<HealthHIstoryItem> item){
        if(item == null)
            return;
        items.clear();
        items.addAll(item);
        notifyDataSetChanged();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_health_record_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HealthHIstoryItem healthHIstoryItem = items.get(position);
        holder.tvHeading.setText(Utils.getStr(healthHIstoryItem.getName()));
        holder.healthRecordSubList.setCommonList(healthHIstoryItem.getValues());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvHeading;
        RecyclerView recyMediList;
        HealthRecordSubList healthRecordSubList;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHeading = itemView.findViewById(R.id.tv_heading);
            recyMediList = itemView.findViewById(R.id.recy_medi_list);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false);
            recyMediList.setLayoutManager(linearLayoutManager);
            healthRecordSubList = new HealthRecordSubList(activity);
            recyMediList.setAdapter(healthRecordSubList);
        }


    }
}
