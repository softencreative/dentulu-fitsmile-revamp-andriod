package com.app.fitsmile.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.OrderListResponse;
import com.app.fitsmile.shop.inter.IOrderListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<OrderListResponse.OrderData> orderList;
    IOrderListener mOrderListener;
    String mBaseUrl;

    public OrderAdapter(Activity context, ArrayList<OrderListResponse.OrderData> list,String base_url, IOrderListener listener) {
        this.context = context;
        orderList = list;
        mOrderListener=listener;
        mBaseUrl=base_url;
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_order, parent, false);
        OrderAdapter.ViewHolder viewHolder = new OrderAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(OrderAdapter.ViewHolder holder, final int position) {
        OrderListResponse.OrderData mOrderData = orderList.get(position);
        holder.mTextPrice.setText("$ " + mOrderData.getDetails().getPrice());
        holder.mTextOrderStatus.setText(mOrderData.getOrder_status_name());
        holder.mTextOrderId.setText(mOrderData.getOrder_ref_id());
        holder.mTextNos.setText(mOrderData.getDetails().getQuantity());
        Picasso.get().load(mBaseUrl + mOrderData.getDetails().getImage()).placeholder(R.drawable.friends_sends_pictures_no).into(holder.mImageProduct);
        holder.mBtnViewOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               mOrderListener.onViewOrder(mOrderData,"");
            }
        });
        holder.mCardOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOrderListener.onViewOrder(mOrderData,"");
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        Button mBtnViewOrder;
        TextView mTextOrderId, mTextNos, mTextOrderStatus, mTextPrice;
        ImageView mImageProduct;
        CardView mCardOrder;

        public ViewHolder(View itemView) {
            super(itemView);
            mCardOrder=itemView.findViewById(R.id.cardOrderItem);
            mBtnViewOrder = itemView.findViewById(R.id.btnViewOrder);
            mTextOrderId = itemView.findViewById(R.id.textOrderIdValue);
            mTextNos = itemView.findViewById(R.id.textNosValue);
            mTextOrderStatus = itemView.findViewById(R.id.textOrderStatusValue);
            mTextPrice = itemView.findViewById(R.id.textUnitPriceValue);
            mImageProduct=itemView.findViewById(R.id.product_image);

        }
    }


}

