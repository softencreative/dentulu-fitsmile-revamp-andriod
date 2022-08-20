package com.app.fitsmile.shop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<ProductResult> mProductResult;
    IProductSelectionListener mProductListener;

    public FavoriteAdapter(Activity context, ArrayList<ProductResult> list, IProductSelectionListener productSelectionListener) {
        this.context = context;
        mProductResult = list;
        mProductListener=productSelectionListener;
    }

    public void addData(List<ProductResult> productResults) {
        if (mProductResult == null) {
            mProductResult = new ArrayList<>();
        }
        mProductResult.clear();
        if (productResults != null && productResults.size() > 0) {
           mProductResult.addAll(productResults);
        }
        notifyDataSetChanged();
    }

    public void addProductToFavourite(int position) {
        mProductResult.get(position).setIs_fav(1);
        notifyDataSetChanged();
    }

    public void removeProductFromFavourite(int position, boolean removeFromList) {
        if (removeFromList) {
            mProductResult.remove(position);
        } else {
            mProductResult.get(position).setIs_fav(0);
        }
        notifyDataSetChanged();
    }

    @Override
    public FavoriteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_favorite, parent, false);
        FavoriteAdapter.ViewHolder viewHolder = new FavoriteAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(FavoriteAdapter.ViewHolder holder, final int position) {
        ProductResult productResult=mProductResult.get(position);
        Glide.with(context).load(mProductResult.get(position).getImageS3()).placeholder(R.color.light_grey_color).into(holder.mImageProduct);
        holder.mImageProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductListener.onProductSelected(productResult);
            }
        });
        holder.mImageFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductListener.onRemoveFromFavourite(productResult,position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mProductResult.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageProduct,mImageFav;

        public ViewHolder(View itemView) {
            super(itemView);
            mImageProduct = itemView.findViewById(R.id.product_image);
            mImageFav=itemView.findViewById(R.id.imageFav);

        }
    }


}

