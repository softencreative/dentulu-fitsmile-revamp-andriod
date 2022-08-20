package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.trayMinder.SmileProgressImages;

import java.util.List;

public class TeethImageAdapter extends RecyclerView.Adapter<TeethImageAdapter.ViewHolder> {

    private Context context;
    private List<SmileProgressImages> mList;

    public TeethImageAdapter(Context context, List<SmileProgressImages> list) {
        this.context = context;
        mList=list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_aligner_images, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextAlignerNumber.setText(mList.get(position).getImageName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAlignerNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mTextAlignerNumber=itemView.findViewById(R.id.textAlignerImageNumber);
        }
    }
}

