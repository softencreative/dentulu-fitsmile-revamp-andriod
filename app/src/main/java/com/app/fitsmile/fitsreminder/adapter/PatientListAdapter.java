package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.patientlist.PatientListData;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    private Context context;
    private List<PatientListData> mList;
    RecyclerItemClickListener mClickListener;

    public PatientListAdapter(Context context, List<PatientListData> list, RecyclerItemClickListener itemClickListener) {
        this.context = context;
        mList = list;
        mClickListener = itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_patient_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextPatientName.setText(mList.get(position).getFirstName());
        holder.mTextPatientType.setText(mList.get(position).getRelationshipType());
        if (mList.get(position).getFitsReminderAppointmentExist().equals("1")) {
            holder.mTextPatientName.setTextColor(context.getResources().getColor(R.color.profile_grey));
            holder.mTextPatientType.setTextColor(context.getResources().getColor(R.color.profile_grey));
        }
        if (!mList.get(position).getImage().equals(""))
            Picasso.get().load(BuildConfig.BASE_IMAGEURL + mList.get(position).getImage()).placeholder(R.drawable.ic_profile_placeholder).into(holder.mImageUser);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mList.get(position).getFitsReminderAppointmentExist().equals("0")) {
                    mClickListener.setClicks(position, 1);
                }else {
                    Utils.showToast(context,context.getResources().getString(R.string.not_able_to_add_treatment));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextPatientName;
        TextView mTextPatientType;
        CircleImageView mImageUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextPatientName = itemView.findViewById(R.id.name);
            mTextPatientType = itemView.findViewById(R.id.type);
            mImageUser = itemView.findViewById(R.id.imageUser);
        }
    }
}

