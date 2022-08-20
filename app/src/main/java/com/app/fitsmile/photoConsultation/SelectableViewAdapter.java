package com.app.fitsmile.photoConsultation;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;

import java.util.ArrayList;
import java.util.List;

import static com.app.fitsmile.common.Utils.isEmptyOrNull;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_CHECK_BOX;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_INT_RADIO;

public class SelectableViewAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<OptionValue> optionValueList;
    private Question question;
    private boolean isMultiSelect;
    private List<CheckBoxViewHolder> checkBoxHolders;
    private List<RadioViewHolder> radioViewHolders;
    private int radioSelectedPosition=-1;


    public SelectableViewAdapter(Context context,Question question, boolean isMultiSelect) {
        this.context = context;
        this.optionValueList = question.getValue();
        this.question=question;
        this.isMultiSelect = isMultiSelect;
        checkBoxHolders=new ArrayList<>();
        radioViewHolders=new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        if (isMultiSelect)
            return TYPE_INT_CHECK_BOX;
        else
            return TYPE_INT_RADIO;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TYPE_INT_CHECK_BOX:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.checkbox_adapter_layout, parent, false);
                return new CheckBoxViewHolder(view);
            case TYPE_INT_RADIO:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radio_adapter_layout, parent, false);
                return new RadioViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        OptionValue optionValue = optionValueList.get(position);
        if (optionValue != null) {
            if (isMultiSelect) {
                CheckBoxViewHolder checkBoxViewHolder = ((CheckBoxViewHolder) holder);
                checkBoxHolders.add(checkBoxViewHolder);
                if (!isEmptyOrNull(optionValue.getTitle())) {
                    String upperString = optionValue.getTitle().substring(0, 1).toUpperCase() + optionValue.getTitle().substring(1).toLowerCase();
                    checkBoxViewHolder.tvOptionTitle.setText(upperString);
                }else
                    checkBoxViewHolder.tvOptionTitle.setText(getString(R.string.na));

                checkBoxViewHolder.cbOption.setChecked(optionValue.isChecked());

                checkBoxViewHolder.llSelectOption.setOnClickListener((view) -> {
                    checkBoxViewHolder.cbOption.setChecked(!checkBoxViewHolder.cbOption.isChecked());
                    optionValueList.get(position).setChecked(checkBoxViewHolder.cbOption.isChecked());
                    for (OptionValue optionValue1:optionValueList) {
                     if (optionValue1.isChecked()) {
                         question.setSelected(true);
                         break;
                     }else {
                         question.setSelected(false);
                     }
                    }
                });

            } else {
                RadioViewHolder radioViewHolder = ((RadioViewHolder) holder);
                radioViewHolders.add(radioViewHolder);
                if (!isEmptyOrNull(optionValue.getTitle()))
                    radioViewHolder.tvOptionTitle.setText(optionValue.getTitle());
                else
                    radioViewHolder.tvOptionTitle.setText(getString(R.string.na));
                radioViewHolder.llSelectOption.setOnClickListener((view) -> {
                    radioViewHolder.rbOption.setChecked(true);
                    optionValueList.get(position).setChecked(radioViewHolder.rbOption.isChecked());
                    question.setSelected(radioViewHolder.rbOption.isChecked());
                    if (radioSelectedPosition>-1 && radioSelectedPosition!=position){
                        radioViewHolders.get(radioSelectedPosition).rbOption.setChecked(false);
                    }
                        radioSelectedPosition = position;
                });
            }
        }//end of If
    }

    private String getString(int id) {
        return context.getString(id);
    }

    @Override
    public int getItemCount() {
        return optionValueList.size();
    }

    public void reloadData(List<OptionValue> optionValueList) {
        this.optionValueList = optionValueList;
        notifyDataSetChanged();
    }

    public boolean isCheckSelected(){
        boolean isCheck=false;
        for (CheckBoxViewHolder holder:checkBoxHolders){
            if (holder.cbOption.isChecked()){
                isCheck=true;
                break;
            }
        }
        return isCheck;
    }

    public boolean isRadioSelected(){
        boolean isCheck=false;
        for (RadioViewHolder holder:radioViewHolders){
            if (holder.rbOption.isChecked()){
                isCheck=true;
                break;
            }
        }
        return isCheck;
    }

}
