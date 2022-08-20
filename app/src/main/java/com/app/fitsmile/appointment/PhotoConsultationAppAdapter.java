package com.app.fitsmile.appointment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.photoConsultancy.appointmentList.PhotoConsultancyListResult;

import java.util.ArrayList;
import java.util.List;

public class PhotoConsultationAppAdapter extends RecyclerView.Adapter<PhotoConsultationAppAdapter.ViewHolder> implements Filterable {

    private Context context;
    private ItemClickPhotoConsultation listener;
    private List<PhotoConsultancyListResult> mList;
    private List<PhotoConsultancyListResult> mFilteredList;

    public PhotoConsultationAppAdapter(Context context, ItemClickPhotoConsultation listener, List<PhotoConsultancyListResult> list) {
        this.context = context;
        this.listener = listener;
        mFilteredList = list;
        mList = list;
    }

    interface ItemClickPhotoConsultation {
        void onItemClickPhotoConsultation(String id);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_photo_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextNumber.setText(mFilteredList.get(position).getReference_id());
        holder.mTextPatientName.setText(mFilteredList.get(position).getPatient_name());
        holder.mTextStatus.setText(mFilteredList.get(position).getConsult_status());
        holder.mTextDate.setText(mFilteredList.get(position).getCreated_date());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onItemClickPhotoConsultation(mFilteredList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mFilteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextNumber, mTextPatientName, mTextStatus, mTextDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextNumber = itemView.findViewById(R.id.tv_name);
            mTextPatientName = itemView.findViewById(R.id.tv_patient);
            mTextStatus = itemView.findViewById(R.id.tv_status_of_consult);
            mTextDate = itemView.findViewById(R.id.tv_created_on);
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    mFilteredList = mList;
                } else {
                    ArrayList<PhotoConsultancyListResult> filteredList = new ArrayList<>();
                    for (PhotoConsultancyListResult row : mList) {
                        if (row.getConsult_status().toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mFilteredList = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mFilteredList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mFilteredList = (ArrayList<PhotoConsultancyListResult>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}

