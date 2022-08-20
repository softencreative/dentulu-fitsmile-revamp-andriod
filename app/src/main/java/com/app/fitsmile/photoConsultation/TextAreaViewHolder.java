package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;


public class TextAreaViewHolder extends RecyclerView.ViewHolder {
    public TextView tvQuestionTitle;
    public TextView tvError;
    public EditText etTextArea;

    public TextAreaViewHolder(View itemView) {
        super(itemView);
        tvQuestionTitle = itemView.findViewById(R.id.tv_question_title);
        tvError = itemView.findViewById(R.id.tv_error);
        etTextArea = itemView.findViewById(R.id.et_text_area);
    }
}//end of holder