package com.app.fitsmile.shop.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ShopRecyclerViewHolder> implements Filterable {

    private Activity context;
    private ArrayList<ProductResult> allProducts;
    private ArrayList<ProductResult> allProductsFiltered;
    IProductSelectionListener mClickListener;

    public ProductAdapter(Activity context, ArrayList<ProductResult> allProducts, IProductSelectionListener clickListener) {
        this.context = context;
        this.allProducts = allProducts;
        this.allProductsFiltered = allProducts;
        mClickListener = clickListener;
    }

    @Override
    public ProductAdapter.ShopRecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_shop_product, parent, false);
        ProductAdapter.ShopRecyclerViewHolder productHolder = new ProductAdapter.ShopRecyclerViewHolder(layoutView);
        return productHolder;
    }

    @Override
    public void onBindViewHolder(ProductAdapter.ShopRecyclerViewHolder holder, final int position) {
        ProductResult productResult = allProductsFiltered.get(position);

        holder.productName.setText(allProductsFiltered.get(position).getName());
        holder.productPrice.setText("$ " + allProductsFiltered.get(position).getPrice());
        holder.itemView.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onProductSelected(productResult);
            }
        });
        holder.produceImage.setOnClickListener(v -> {
            if (mClickListener != null) {
                mClickListener.onProductSelected(productResult);
            }
        });
        holder.mImageCart.setOnClickListener(v -> {
            if (mClickListener != null) {
                if (productResult.getIn_cart() == 0) {
                    mClickListener.onAddToCart(productResult, position);
                } else {
                    mClickListener.onRemoveFromCart(productResult, position);
                }
            }
        });
        Glide.with(context).load(productResult.getImageS3()).placeholder(R.drawable.ic_splash_fit).into(holder.produceImage);
        holder.mImageFav.setOnClickListener(v -> {
            if (productResult.getIsFav() == 1) {
                mClickListener.onRemoveFromFavourite(productResult, position);
            } else {
                mClickListener.onAddToFavourite(productResult, position);
            }
        });
        holder.mImageFav.setSelected(productResult.getIsFav() == 1);
        holder.mImageCart.setSelected(productResult.getIn_cart() == 1);
    }

    @Override
    public int getItemCount() {
        return allProductsFiltered.size();
    }


    public class ShopRecyclerViewHolder extends RecyclerView.ViewHolder {

        public ImageView produceImage;
        public RelativeLayout linItem;
        public TextView productName, productPrice;
        private ImageView mImageCart;
        private ImageView mImageFav;

        public ShopRecyclerViewHolder(View itemView) {
            super(itemView);
            produceImage = itemView.findViewById(R.id.product_image);
            productName = itemView.findViewById(R.id.product_name);
            productPrice = itemView.findViewById(R.id.product_price);
            linItem = itemView.findViewById(R.id.lin_item);
            mImageFav = itemView.findViewById(R.id.imageFav);
            mImageCart = itemView.findViewById(R.id.imageCart);

        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    allProductsFiltered = allProducts;
                } else {
                    ArrayList<ProductResult> filteredList = new ArrayList<>();
                    for (ProductResult row : allProducts) {
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getName().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    allProductsFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = allProductsFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                allProductsFiltered = (ArrayList<ProductResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public void addProductToFavourite(int position) {
        allProductsFiltered.get(position).setIs_fav(1);
        notifyDataSetChanged();
    }

    public void removeProductFromFavourite(int position, boolean removeFromList) {
        if (removeFromList) {
            allProductsFiltered.remove(position);
        } else {
            allProductsFiltered.get(position).setIs_fav(0);
        }
        notifyDataSetChanged();
    }

    public void addProductToCart(int position) {
        allProductsFiltered.get(position).setIn_cart(1);
        notifyDataSetChanged();
    }

    public void removeProductFromCart(int position) {
        allProductsFiltered.get(position).setIn_cart(0);
        notifyDataSetChanged();
    }
}

