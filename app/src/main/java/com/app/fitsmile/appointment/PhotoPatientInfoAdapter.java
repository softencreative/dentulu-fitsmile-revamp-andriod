package com.app.fitsmile.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.photoConsultancy.PhotoConsultancyDetail.QuestionData;

import java.util.List;

public class PhotoPatientInfoAdapter extends RecyclerView.Adapter<PhotoPatientInfoAdapter.ViewHolder> {

    private Context context;
    private PhotoConsultationAppAdapter.ItemClickPhotoConsultation listener;
    private List<QuestionData> mList;

    public PhotoPatientInfoAdapter(Context context,List<QuestionData> list) {
        this.context = context;
        mList = list;
    }


    @NonNull
    @Override
    public PhotoPatientInfoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_photo_patient_info, parent, false);
        return new PhotoPatientInfoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoPatientInfoAdapter.ViewHolder holder, int position) {
        holder.mTextTitle.setText(mList.get(position).getTitle());
        String title=mList.get(position).getTitle();
        String title2=title.substring(0, 1).toUpperCase() +title.substring(1).toLowerCase();
        holder.mTextTitle.setText(title2);
        if (mList.get(position).getType().equals(Constants.TYPE_TEXT_AREA)) {
            holder.mTextPatient.setVisibility(View.VISIBLE);
            holder.mTextPatient.setText(mList.get(position).getValue().get(0));
        } else {
            holder.mTextPatient.setVisibility(View.GONE);
        }
        if (mList.get(position).getType().equals(Constants.TYPE_CHECK_BOX)) {
            holder.mRecyclerPatientAnswer.setVisibility(View.VISIBLE);
            setAdapter(holder.mRecyclerPatientAnswer,mList.get(position).getValue());
        } else {
            holder.mRecyclerPatientAnswer.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextTitle, mTextPatient;
        RecyclerView mRecyclerPatientAnswer;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextTitle = itemView.findViewById(R.id.textQuestionTitle);
            mRecyclerPatientAnswer = itemView.findViewById(R.id.recyclerAnswer);
            mTextPatient = itemView.findViewById(R.id.textPatientInfo);
        }
    }


    private void setAdapter(RecyclerView recyclerView, List<String> mList) {
        PhotoPatientAnswerAdapter mAdapter;
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        mAdapter = new PhotoPatientAnswerAdapter(context, mList);
        recyclerView.setAdapter(mAdapter);
    }
}

