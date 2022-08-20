package com.app.fitsmile.shop.adapter;

import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.shop.CategoryListResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.util.ArrayList;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<CategoryListResult> categoryList;
    RecyclerItemClickListener mCLicksListener;
    private int selectedCategoryPos = -1;


    public CategoryAdapter(Activity context, ArrayList<CategoryListResult> list, RecyclerItemClickListener clickListener) {
        this.context = context;
        categoryList = list;
        mCLicksListener = clickListener;
    }

    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view_category, parent, false);
        CategoryAdapter.ViewHolder viewHolder = new CategoryAdapter.ViewHolder(layoutView);
        return viewHolder;
    }

    @Override
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onBindViewHolder(CategoryAdapter.ViewHolder holder, final int position) {
        if (selectedCategoryPos == position) {
            holder.mCardCategory.setCardBackgroundColor(context.getColor(R.color.colorPrimary));
        } else {
            holder.mCardCategory.setCardBackgroundColor(context.getColor(R.color.colorPrimaryDark));
        }
        holder.categoryName.setText(categoryList.get(position).getName());

        holder.mCardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCategoryPos = position;
                notifyDataSetChanged();
                mCLicksListener.setClicks(position, 1);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView categoryName;
        CardView mCardCategory;

        public ViewHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.textCategoryName);
            mCardCategory = itemView.findViewById(R.id.cardCategory);

        }
    }


}

