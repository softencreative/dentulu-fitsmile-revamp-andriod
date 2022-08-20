package com.app.fitsmile.appointment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.util.List;

public class TrayMinderAppointmentAdapter extends RecyclerView.Adapter<TrayMinderAppointmentAdapter.ViewHolder> {

    private Context context;
    private List<TrayMinderResult> mList;
    RecyclerItemClickListener mClickListener;

    public TrayMinderAppointmentAdapter(Context context, List<TrayMinderResult> list, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        mList = list;
        mClickListener = recyclerItemClickListener;
    }


    @NonNull
    @Override
    public TrayMinderAppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_tray_minder_appointment, parent, false);
        return new TrayMinderAppointmentAdapter.ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TrayMinderAppointmentAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        holder.mTextDays.setText(mList.get(position).getNo_of_days() + " Days");
        String strDate = Utils.dateFormat(mList.get(position).getRemind_time(), Utils.outputTime, Utils.inputTime);
        holder.mTextTime.setText(strDate);
        holder.mTextType.setText(mList.get(position).getType());
        holder.mTextPatientName.setText(mList.get(position).getFamily_member_name());
        if (mList.get(position).getStatus().equals("0") && mList.get(position).getAssigned_id().equals(mList.get(position).getPatient_id())) {
            holder.mLayoutAcceptReject.setVisibility(View.VISIBLE);
            holder.mTextDelete.setVisibility(View.GONE);
        } else if (mList.get(position).getStatus().equals("2")) {
            holder.mLayoutAcceptReject.setVisibility(View.VISIBLE);
            holder.mTextDelete.setVisibility(View.GONE);
            holder.mLayoutAccept.setVisibility(View.INVISIBLE);
            holder.mTextReject.setText(R.string.rejected);
        } else {
            holder.mTextDelete.setVisibility(View.GONE);
            holder.mLayoutAcceptReject.setVisibility(View.GONE);
        }
        holder.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(mList.get(position).getStatus().equals("0") && mList.get(position).getAssigned_id().equals(mList.get(position).getPatient_id()))) {
                    if(mList.get(position).getStatus().equals("3")){
                        Toast.makeText(context, R.string.treatment_completed, Toast.LENGTH_SHORT).show();
                    }else if (!mList.get(position).getStatus().equals("2")) {
                      //  mClickListener.setClicks(position, 1);
                    }
                }

            }
        });

        holder.mTextDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(position, 2);
            }
        });

        holder.mLayoutAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(position, 3);
            }
        });

        holder.mLayoutReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!holder.mTextReject.getText().equals(context.getString(R.string.rejected))) {
                    mClickListener.setClicks(position, 4);
                }

            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextType;
        TextView mTextTime;
        TextView mTextDays;
        LinearLayout mCardView;
        RelativeLayout mTextDelete;
        TextView mTextPatientName;
        TextView mTextReject;
        LinearLayout mLayoutAcceptReject;
        RelativeLayout mLayoutAccept, mLayoutReject;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.layoutAppointmentDetails);
            mTextTime = itemView.findViewById(R.id.tv_switch_time);
            mTextType = itemView.findViewById(R.id.typeAligner);
            mTextDays = itemView.findViewById(R.id.days_aligner);
            mTextDelete = itemView.findViewById(R.id.layoutDeleteReminder);
            mTextPatientName = itemView.findViewById(R.id.patientAligner);
            mLayoutAcceptReject = itemView.findViewById(R.id.layoutAcceptReject);
            mLayoutAccept = itemView.findViewById(R.id.layoutAccept);
            mLayoutReject = itemView.findViewById(R.id.layoutReject);
            mTextReject = itemView.findViewById(R.id.tvReject);
        }
    }
}

