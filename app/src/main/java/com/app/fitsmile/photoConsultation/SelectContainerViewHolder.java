package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;


public class SelectContainerViewHolder extends RecyclerView.ViewHolder {
    public final TextView tvError;
    public TextView tvQuestionTitle;
    public RecyclerView rvOptionsList;

    public SelectContainerViewHolder(View itemView) {
        super(itemView);
        tvQuestionTitle = itemView.findViewById(R.id.tv_question_title);
        rvOptionsList = itemView.findViewById(R.id.rv_options_list);
        tvError = itemView.findViewById(R.id.tv_error);

    }
}//end of holder