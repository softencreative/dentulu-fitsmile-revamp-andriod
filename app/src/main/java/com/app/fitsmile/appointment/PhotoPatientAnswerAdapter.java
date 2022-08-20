package com.app.fitsmile.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;

import java.util.List;

public class PhotoPatientAnswerAdapter extends RecyclerView.Adapter<PhotoPatientAnswerAdapter.ViewHolder> {

    private Context context;
    private List<String> mList;

    public PhotoPatientAnswerAdapter(Context context, List<String> list) {
        this.context = context;
        mList=list;
    }


    @NonNull
    @Override
    public PhotoPatientAnswerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_answer, parent, false);
        return new PhotoPatientAnswerAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPatientAnswerAdapter.ViewHolder holder, int position) {
        holder.mTextAnswer.setText(mList.get(position));

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAnswer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mTextAnswer=itemView.findViewById(R.id.textAnswerPhoto);
        }
    }
}

