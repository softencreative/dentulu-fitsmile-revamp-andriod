package com.app.fitsmile.myaccount.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.rewards.EarningResult;
import java.util.ArrayList;

public class EarningAdapter extends RecyclerView.Adapter<EarningAdapter.MyViewHolder> {

    Context context;
    private ArrayList<EarningResult> arrayListitem;
    private OnItemClicked onClick;

    public interface OnItemClicked {
        void onItemClick(int position);
    }

    @NonNull
    @Override
    public EarningAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionlist_item, parent, false);
        return new EarningAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EarningAdapter.MyViewHolder holder, final int position) {
        holder.name.setText(arrayListitem.get(position).getFirstname());
        holder.amountgained.setText("Amount Earned" + " " + "$"+ arrayListitem.get(position).getAmount());
        holder.date.setText(Utils.getNewDate(context, arrayListitem.get(position).getCreated_date(),Utils.inputDate,Utils.outputDateChange));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
         //       onClick.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return arrayListitem.size();
    }


    public EarningAdapter(Context context, ArrayList<EarningResult> arrayListitem) {
        this.arrayListitem = arrayListitem;
        this.context = context;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, date, amountgained, timing;


        public MyViewHolder(View view) {
            super(view);
            name =  view.findViewById(R.id.name);
            date =  view.findViewById(R.id.date);
            amountgained = view.findViewById(R.id.amountgained);
        }
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }
}