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

public class TimerPushNotificationListAdapter extends RecyclerView.Adapter<TimerPushNotificationListAdapter.ViewHolder> {

    private Context context;
    private List<TimerPushNotificationResult> mList;
    RecyclerItemClickListener mClickListener;

    public TimerPushNotificationListAdapter(Context context, List<TimerPushNotificationResult> list, RecyclerItemClickListener itemClickListener) {
        this.context = context;
        mList=list;
        mClickListener=itemClickListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_reminder_time, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mTextName.setText(mList.get(position).getName());
        holder.mTextTime.setText(mList.get(position).getTime());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String timeText=holder.mTextTime.getText().toString();
                String mins[];
                int minsApi;
                if (timeText.contains("hour")){
                    mins=timeText.split(" ");
                    int hour=Integer.parseInt(mins[0])*60;
                    if (mins.length>2) {
                        int min = Integer.parseInt(mins[2]);
                        minsApi = hour + min;
                    }else {
                        minsApi = hour;
                    }
                }else {
                    mins=timeText.split(" ");
                    int min=Integer.parseInt(mins[0]);
                    minsApi=min;
                }

                mClickListener.setClicks(position,minsApi);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextName;
        TextView mTextTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
             mTextName=itemView.findViewById(R.id.textHeaderReminder);
             mTextTime=itemView.findViewById(R.id.textReminderTime);

        }
    }
}

