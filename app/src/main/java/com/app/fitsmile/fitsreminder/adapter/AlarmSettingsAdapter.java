package com.app.fitsmile.fitsreminder.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.trayMinder.TimerPushNotificationResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.util.List;

public class AlarmSettingsAdapter extends RecyclerView.Adapter<AlarmSettingsAdapter.ViewHolder> {

    private Context context;
    private List<TimerPushNotificationResult> mList;
    RecyclerItemClickListener mClickListener;

    public AlarmSettingsAdapter(Context context, List<TimerPushNotificationResult> list, RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        mList=list;
        mClickListener=recyclerItemClickListener;
    }


    @NonNull
    @Override
    public AlarmSettingsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_alarm_settings, parent, false);
        return new AlarmSettingsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AlarmSettingsAdapter.ViewHolder holder, int position) {
        holder.mTextAlarmName.setText(mList.get(position).getName());
        holder.mTextAlarmTime.setText(mList.get(position).getTime());
        holder.mTextAlarmTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mClickListener.setClicks(position,2);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextAlarmName;
        TextView mTextAlarmTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextAlarmName=itemView.findViewById(R.id.textAlarmName);
            mTextAlarmTime=itemView.findViewById(R.id.textAlarmTime);

        }
    }
}

