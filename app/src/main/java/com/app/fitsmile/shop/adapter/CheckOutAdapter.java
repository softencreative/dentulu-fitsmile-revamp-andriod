package com.app.fitsmile.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.ProductResult;

import java.util.ArrayList;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<ProductResult> categoryList;

    public CheckOutAdapter(Activity context, ArrayList<ProductResult> list) {
        this.context = context;
        categoryList = list;
    }

    @Override
    public CheckOutAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_checkout_adapter, parent, false);
        CheckOutAdapter.ViewHolder viewHolder = new CheckOutAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CheckOutAdapter.ViewHolder holder, final int position) {
        holder.mProductName.setText(categoryList.get(position).getName()+" * "+categoryList.get(position).quantity );
        holder.mProductPrice.setText(String.format("$ %s", categoryList.get(position).getPrice()));

    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mProductName;
        public TextView mProductPrice;

        public ViewHolder(View itemView) {
            super(itemView);
            mProductName=itemView.findViewById(R.id.textProducts);
            mProductPrice=itemView.findViewById(R.id.textCartTotal);

        }
    }


}

