package com.app.fitsmile.shop.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<ProductResult> cartList;
    IProductSelectionListener mProductSelectListener;

    public CartAdapter(Activity context, ArrayList<ProductResult> list, IProductSelectionListener productSelectionListener) {
        this.context = context;
        cartList = list;
        mProductSelectListener=productSelectionListener;
    }

    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_cart, parent, false);
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(CartAdapter.ViewHolder holder, final int position) {
        ProductResult productResult=cartList.get(position);
        holder.mProductName.setText(cartList.get(position).getName());
        holder.mProductPrice.setText("$ "+cartList.get(position).getPrice());
        Glide.with(context).load(productResult.getImageS3()).placeholder(R.color.light_grey_color).into(holder.mImageProduct);
        holder.mImageAddProduct.setOnClickListener(v -> {
            if (mProductSelectListener != null) {
                mProductSelectListener.onAddToCart(productResult, position);
            }
        });
        holder.mImageRemoveProduct.setOnClickListener(v -> {
            if (productResult.quantity >= 1) {
                if (mProductSelectListener != null) {
                    mProductSelectListener.onRemoveFromCart(productResult, position);
                }
            }
        });
        if (productResult.quantity > 0) {
            holder.mTextQuantity.setText(String.valueOf(productResult.quantity));
        } else {
            holder.mTextQuantity.setText("1");
        }

    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mProductName;
        public TextView mProductPrice;
        private TextView mTextQuantity;
        ImageView mImageAddProduct,mImageRemoveProduct,mImageProduct;

        public ViewHolder(View itemView) {
            super(itemView);
            mProductName = itemView.findViewById(R.id.product_name);
            mProductPrice = itemView.findViewById(R.id.product_price);
            mTextQuantity=itemView.findViewById(R.id.textProductQuantity);
            mImageAddProduct=itemView.findViewById(R.id.imageAddProduct);
            mImageRemoveProduct=itemView.findViewById(R.id.imageMinusProduct);
            mImageProduct=itemView.findViewById(R.id.product_image);
        }
    }


}

