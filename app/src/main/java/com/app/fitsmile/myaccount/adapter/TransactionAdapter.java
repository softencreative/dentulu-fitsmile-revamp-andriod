package com.app.fitsmile.myaccount.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.rewards.TransactionResult;

import java.util.ArrayList;

/**
 * Created by JOSEPH on 8/21/2018.
 */

public class TransactionAdapter extends  RecyclerView.Adapter<TransactionAdapter.MyViewHolder> {

        Context context;
private ArrayList<TransactionResult> arrayListitem = new ArrayList<>();

private OnItemClicked onClick;

public interface OnItemClicked {

    void onItemClick(int position);
}

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transactionlist_item, parent, false);

        return new MyViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {





        holder.name.setText(arrayListitem.get(position).getName());
        holder.amountgained.setText("Amount Spennt" +" "+"$"+arrayListitem.get(position).getDiscount_reward_amt());
        holder.date.setText(arrayListitem.get(position).getBooking_time() + "," + arrayListitem.get(position).getCreated_date());



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onClick.onItemClick(position);


            }
        });

    }


    @Override
    public int getItemCount() {

        return arrayListitem.size();
    }


    public TransactionAdapter(Context context, ArrayList<TransactionResult> arrayListitem) {

        this.arrayListitem = arrayListitem;
        this.context = context;

    }

public class MyViewHolder extends RecyclerView.ViewHolder {
    public TextView name, date, amountgained,timing;


    public MyViewHolder(View view) {
        super(view);
        name = (TextView) view.findViewById(R.id.name);
        date = (TextView) view.findViewById(R.id.date);
        amountgained = view.findViewById(R.id.amountgained);
    }
}

    public void setOnClick(OnItemClicked onClick) {
        this.onClick = onClick;
    }


    {


            }
            {

}}
