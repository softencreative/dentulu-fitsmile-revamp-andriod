package com.app.fitsmile.shop.ui;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.OrderListResponse;

import java.util.ArrayList;

public class ViewOrderAdapter extends RecyclerView.Adapter<ViewOrderAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<OrderListResponse.OrderData> orderList;

    public ViewOrderAdapter(Activity context, ArrayList<OrderListResponse.OrderData> list) {
        this.context = context;
        orderList = list;
    }

    @Override
    public ViewOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_order_details, parent, false);
        ViewOrderAdapter.ViewHolder viewHolder = new ViewOrderAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewOrderAdapter.ViewHolder holder, final int position) {

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
    

        public ViewHolder(View itemView) {
            super(itemView);


        }
    }


}

