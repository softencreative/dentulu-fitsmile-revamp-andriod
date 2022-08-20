package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;


public class TextInputViewHolder extends RecyclerView.ViewHolder {
    public   TextView tvError;
    public TextView tvQuestionTitle;
    public EditText etInput;

    public TextInputViewHolder(View itemView) {
        super(itemView);
        tvQuestionTitle = itemView.findViewById(R.id.tv_question_title);
        etInput = itemView.findViewById(R.id.et_input);
        tvError = itemView.findViewById(R.id.tv_error);

    }
}//end of holder