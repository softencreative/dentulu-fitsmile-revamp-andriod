package com.app.fitsmile.photoConsultation;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;


public class CheckBoxViewHolder extends RecyclerView.ViewHolder {
    public TextView tvOptionTitle;
    public CheckBox cbOption;
    public LinearLayout llSelectOption;

    public CheckBoxViewHolder(View itemView) {
        super(itemView);
        cbOption = itemView.findViewById(R.id.cb_option);
        tvOptionTitle = itemView.findViewById(R.id.tv_option_title);
        llSelectOption = itemView.findViewById(R.id.ll_select_option);
    }
}//end of holder
