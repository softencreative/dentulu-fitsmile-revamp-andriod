package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;


public class RadioViewHolder extends RecyclerView.ViewHolder {
    public TextView tvOptionTitle;
    public RadioButton rbOption;
    public LinearLayout llSelectOption;
    public RadioViewHolder(View itemView) {
        super(itemView);
        rbOption = itemView.findViewById(R.id.rb_option);
        tvOptionTitle = itemView.findViewById(R.id.tv_option_title);
        llSelectOption = itemView.findViewById(R.id.ll_select_option);
    }
}//end of holder

