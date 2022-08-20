package com.app.fitsmile.my_address;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;

import java.util.ArrayList;
import java.util.List;

public class MyAddressListAdapter extends RecyclerView.Adapter<MyAddressListAdapter.ViewHolder> {

    private Context context;
    private ItemClickAddressList listener;
    private ArrayList<Address> items;


    public MyAddressListAdapter(Context context, ItemClickAddressList listener) {
        this.context = context;
        this.listener = listener;
        items = new ArrayList<>();
    }

    public void setCommonList(List<Address> patientListData) {
        if (patientListData == null)
            return;
        items.clear();
        items.addAll(patientListData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Address listData = items.get(position);
        holder.name.setText(Utils.getStr(listData.getName()));
        holder.itemView.setOnClickListener(v -> listener.onAddressClick(listData));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface ItemClickAddressList {
        void onAddressClick(Address address);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public LinearLayout llParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            llParent = itemView.findViewById(R.id.ll_parent);
        }
    }
}
