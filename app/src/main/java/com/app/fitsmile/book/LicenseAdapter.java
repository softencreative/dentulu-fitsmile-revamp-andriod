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
import com.app.fitsmile.response.dentisdetails.LicensesItem;

import java.util.ArrayList;
import java.util.List;

public class LicenseAdapter extends RecyclerView.Adapter<LicenseAdapter.ViewHolder> {

    private Activity context;
    private List<LicensesItem> items;

    public LicenseAdapter(Activity context) {
        this.context = context;
        items = new ArrayList<>();
    }

    public void setCommonList( List<LicensesItem> item){
        if(item == null)
            return;
        items.clear();
        items.addAll(item);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_licenses_nuber,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        LicensesItem licensesItem = items.get(position);

        holder.tvLicense.setText(Utils.getStr(licensesItem.getLicenseNo()));
        holder.tvState.setText(Utils.getStr(licensesItem.getState()));


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvLicense, tvState, tvLabelLicenseNo, tvLabelState;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvLicense = itemView.findViewById(R.id.tv_license);
            tvState = itemView.findViewById(R.id.tv_state);
            tvLabelLicenseNo = itemView.findViewById(R.id.tv_label_license_no);
            tvLabelState = itemView.findViewById(R.id.tv_label_state);
        }
    }
}

