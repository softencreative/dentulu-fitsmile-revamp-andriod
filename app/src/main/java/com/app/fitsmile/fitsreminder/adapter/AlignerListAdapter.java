package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.trayMinder.AlignerListResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.util.List;

public class AlignerListAdapter extends RecyclerView.Adapter<AlignerListAdapter.ViewHolder> {

    private Context context;
    private List<AlignerListResult> mList;
    RecyclerItemClickListener mClickListener;

    public AlignerListAdapter(Context context, List<AlignerListResult> list, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        mList=list;
        mClickListener=recyclerItemClickListener;
    }


    @NonNull
    @Override
    public AlignerListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_aligner_days, parent, false);
        return new AlignerListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlignerListAdapter.ViewHolder holder, int position) {
        holder.mTextAlignerName.setText(mList.get(position).getName());
        holder.mTextAlignerDays.setText(String.format("%s Days", mList.get(position).getNo_of_days()));
        if (mList.get(position).getIs_completed().equals("1")){
            holder.mTextAlignerName.setTextColor(context.getResources().getColor(R.color.profile_grey));
            holder.mTextAlignerDays.setTextColor(context.getResources().getColor(R.color.colorPrimaryLightProfile));
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mList.get(position).getIs_completed().equals("1")) {
                    mClickListener.setClicks(position, 1);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAlignerName;
        TextView mTextAlignerDays;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextAlignerName=itemView.findViewById(R.id.textAlignerNumber);
            mTextAlignerDays=itemView.findViewById(R.id.textAlignerDays);

        }
    }
}

