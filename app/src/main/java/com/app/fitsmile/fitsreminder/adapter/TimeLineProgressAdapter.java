package com.app.fitsmile.fitsreminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.response.trayMinder.TimeLineResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TimeLineProgressAdapter extends RecyclerView.Adapter<TimeLineProgressAdapter.ViewHolder> {

    private Context context;
    private List<TimeLineResult> mList;
    RecyclerItemClickListener mClickListener;
    String lastAligner;
    String currentAligner;

    public TimeLineProgressAdapter(Context context, List<TimeLineResult> list, String aligner, String currentAligner,RecyclerItemClickListener recyclerItemClickListener) {
        this.context = context;
        mList = list;
        lastAligner = aligner;
        mClickListener = recyclerItemClickListener;
        this.currentAligner=currentAligner;
    }


    @NonNull
    @Override
    public TimeLineProgressAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view_timeline_progress, parent, false);
        return new TimeLineProgressAdapter.ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceAsColor"})
    @Override
    public void onBindViewHolder(@NonNull TimeLineProgressAdapter.ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        if (mList.get(position).getDay().equals("0")) {
            holder.mTextAlignerNumber.setText(String.format("%s/%s", mList.get(position).getAlignerNo(), lastAligner));
            holder.mTextAlignerNumber.setVisibility(View.VISIBLE);
            holder.mTextAligner.setVisibility(View.VISIBLE);
            holder.mLayoutDays.setVisibility(View.GONE);
            holder.mTextCurrentAligner.setVisibility(View.GONE);
        }else {
            holder.mTextCurrentAligner.setVisibility(View.GONE);
            holder.mLayoutDays.setVisibility(View.VISIBLE);
            int switchAligner = -1;
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            try {
                date = inFormat.parse(mList.get(position).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat outFormat = new SimpleDateFormat("EEE");
            SimpleDateFormat outFormatMonth = new SimpleDateFormat("MMM");
            SimpleDateFormat outFormatDate = new SimpleDateFormat("dd");
            String mDate = inFormat.format(date);
            String mToday = inFormat.format(Calendar.getInstance().getTime());
            holder.mTextDay.setText(outFormat.format(date));
            holder.mTextMonth.setText(outFormatMonth.format(date));
            holder.mTextDate.setText(outFormatDate.format(date));
            if (mList.get(position).getDay().equals("1")) {
                holder.mTextAlignerNumber.setText(String.format("%s/%s", mList.get(position).getAlignerNo(), lastAligner));
                holder.mTextAlignerNumber.setVisibility(View.VISIBLE);
                holder.mTextAligner.setVisibility(View.VISIBLE);
                if(mList.get(position).getAlignerNo().equals(currentAligner)) {
                    holder.mTextCurrentAligner.setVisibility(View.VISIBLE);
                    holder.mTextAlignerNumber.setTextColor(context.getResources().getColor(R.color.colorBlue));
                    holder.mTextAligner.setTextColor(context.getResources().getColor(R.color.colorBlue));
                }
                if (position != 0) {
                    switchAligner = position;
                }
            } else {
                holder.mTextAlignerNumber.setVisibility(View.GONE);
                holder.mTextAligner.setVisibility(View.GONE);
            }

            if (mList.get(position).getTotalInTime() != null) {
                holder.mCustomProgress.setProgress(getPercentage(mList.get(position).getTotalInTime()));
            }
            if (mList.get(position).getTotalInTime() == null) {
                holder.mCircleIndicator.setImageResource(R.color.title_text_color_light);
                holder.mImageIndicator.setVisibility(View.INVISIBLE);
                holder.mTextGoalName.setText(R.string.not_tracked);
                holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_blue));
                holder.mCustomProgress.setProgress(0);
                holder.mTextGoalTime.setText(" ");
                Calendar eventCal = Calendar.getInstance();
                eventCal.setTime(date);
                if (!Calendar.getInstance().getTime().before(eventCal.getTime())) {
                    holder.mCircleIndicator.setImageResource(R.color.colorRed);
                    holder.mImageIndicator.setVisibility(View.VISIBLE);
                    holder.mImageIndicator.setImageResource(R.drawable.ic_close_24);
                    holder.mTextGoalName.setText(R.string.goal_missed);
                    holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                    holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_goal));
                    holder.mCustomProgress.setProgress(0);
                }
                if (mDate.equals(mToday)) {
                    holder.mCircleIndicator.setImageResource(R.color.colorBlue);
                    holder.mImageIndicator.setVisibility(View.INVISIBLE);
                    holder.mTextGoalName.setText(R.string.today);
                    holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.colorBlue));
                    holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_blue));
                    if (mList.get(position).getGoalReached().equals("1")) {
                        holder.mCustomProgress.setProgress(0);
                    }
                }

            } else {
                if (mDate.equals(mToday)) {
                    holder.mCircleIndicator.setImageResource(R.color.title_text_color_light);
                    holder.mImageIndicator.setVisibility(View.INVISIBLE);
                    holder.mTextGoalName.setText(R.string.today);
                    holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.colorBlue));
                    holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_blue));
                    if (mList.get(position).getGoalReached().equals("1")) {
                        holder.mCustomProgress.setProgress(100);
                    }else {
                        if (mList.get(position).getTotalInTime() != null) {
                            holder.mCustomProgress.setProgress(getPercentage(mList.get(position).getTotalInTime()));
                        }
                    }
                } else if (mList.get(position).getGoalReached().equals("1")) {
                    holder.mCircleIndicator.setImageResource(R.color.colorGreen);
                    holder.mImageIndicator.setVisibility(View.VISIBLE);
                    holder.mImageIndicator.setImageResource(R.drawable.ic_check_24);
                    holder.mTextGoalName.setText(R.string.goal_reached);
                    holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                    holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_green));
                    holder.mCustomProgress.setProgress(100);
                } else {
                    holder.mCircleIndicator.setImageResource(R.color.colorRed);
                    holder.mImageIndicator.setVisibility(View.VISIBLE);
                    holder.mImageIndicator.setImageResource(R.drawable.ic_close_24);
                    holder.mTextGoalName.setText(R.string.goal_missed);
                    holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                    holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_goal));

                    if (mList.get(position).getTotalInTime() != null) {
                        holder.mCustomProgress.setProgress(getPercentage(mList.get(position).getTotalInTime()));
                    }
                }
                holder.mTextGoalTime.setText(mList.get(position).getTotalInTime());
            }
          /*  if (mList.get(position).getSwitch() != null) {
                holder.mCircleIndicator.setImageResource(R.color.colorBlue);
                holder.mImageIndicator.setVisibility(View.VISIBLE);
                holder.mImageIndicator.setImageResource(R.drawable.ic_teeth);
                holder.mTextGoalName.setText(R.string.switch_aligner_title);
                holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_blue));
                if (mList.get(position).getTotalInTime() != null) {
                    holder.mCustomProgress.setProgress(getPercentage(mList.get(position).getTotalInTime()));
                }
            }*/

            if (position == mList.size() - 1) {
                holder.mCircleIndicator.setImageResource(R.color.colorBlue);
                holder.mImageIndicator.setVisibility(View.VISIBLE);
                holder.mImageIndicator.setImageResource(R.drawable.ic_star);
                holder.mTextGoalName.setText(R.string.treatment_complete);
                holder.mTextGoalName.setTextColor(context.getResources().getColor(R.color.black));
                holder.mCustomProgress.setProgressDrawable(context.getDrawable(R.drawable.custom_progress_goal));
                if (mList.get(position).getTotalInTime() != null) {
                    holder.mCustomProgress.setProgress(getPercentage(mList.get(position).getTotalInTime()));
                }
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mTextDay, mTextDate, mTextMonth;
        TextView mTextGoalName, mTextGoalTime;
        ProgressBar mCustomProgress;
        CircleImageView mCircleIndicator;
        ImageView mImageIndicator;
        TextView mTextAligner;
        TextView mTextAlignerNumber;
        RelativeLayout mLayoutDays;
        TextView mTextCurrentAligner;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            mTextMonth = itemView.findViewById(R.id.textMonth);
            mTextDate = itemView.findViewById(R.id.textDate);
            mTextDay = itemView.findViewById(R.id.textDay);
            mTextGoalName = itemView.findViewById(R.id.textGoalName);
            mTextGoalTime = itemView.findViewById(R.id.textGoalTime);
            mCustomProgress = itemView.findViewById(R.id.customProgress);
            mCircleIndicator = itemView.findViewById(R.id.circleGoalIndicator);
            mImageIndicator = itemView.findViewById(R.id.imageGoalIndicator);
            mTextAligner = itemView.findViewById(R.id.textAligner);
            mTextAlignerNumber = itemView.findViewById(R.id.textAlignerNumber);
            mLayoutDays=itemView.findViewById(R.id.layoutAlignerDays);
            mTextCurrentAligner=itemView.findViewById(R.id.textCurrentAligner);
        }
    }

    int getPercentage(String time) {
        String[] split = time.split(":");
        int mins = Integer.parseInt(split[0]) * 60 + Integer.parseInt(split[1]);
        return (mins * 100 / 1440);
    }

}

