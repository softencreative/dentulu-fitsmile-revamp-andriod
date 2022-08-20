package com.app.fitsmile.shop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.ProductResult;

import java.util.ArrayList;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<ProductResult> orderList;

    public FeedBackAdapter(Activity context, ArrayList<ProductResult> list) {
        this.context = context;
        orderList = list;
    }

    @Override
    public FeedBackAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_feedback_order, parent, false);
        FeedBackAdapter.ViewHolder viewHolder = new FeedBackAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FeedBackAdapter.ViewHolder holder, final int position) {


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

