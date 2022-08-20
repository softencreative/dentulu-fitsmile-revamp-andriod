package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.util.List;

public class TrayMinderDefaultAdapter extends RecyclerView.Adapter<TrayMinderDefaultAdapter.ViewHolder> {

    private Context context;
    private List<TrayMinderResult> mList;
    RecyclerItemClickListener mClickListener;

    public TrayMinderDefaultAdapter(Context context, List<TrayMinderResult> list, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        mList = list;
        mClickListener = recyclerItemClickListener;
    }


    @NonNull
    @Override
    public TrayMinderDefaultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_reminder, parent, false);
        return new TrayMinderDefaultAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TrayMinderDefaultAdapter.ViewHolder holder, int position) {
        String strDate = Utils.dateFormat(mList.get(position).getRemind_time(), Utils.outputTime, Utils.inputTime);
        holder.mTextPatientName.setText(mList.get(position).getFamily_member_name());
        holder.mTextTime.setText(strDate);
        holder.mTextType.setText(mList.get(position).getType());
        holder.mLayoutSetDefault.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(Integer.parseInt(mList.get(position).getId()), 3);
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
        TextView mTextPatientName;
        LinearLayout mCardView;
        RelativeLayout mLayoutSetDefault;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mCardView = itemView.findViewById(R.id.layoutAppointmentDetails);
            mTextTime = itemView.findViewById(R.id.tv_switch_time);
            mTextType = itemView.findViewById(R.id.typeAligner);
            mTextPatientName = itemView.findViewById(R.id.patientAligner);
            mLayoutSetDefault = itemView.findViewById(R.id.layoutSetDefault);
        }
    }
}

