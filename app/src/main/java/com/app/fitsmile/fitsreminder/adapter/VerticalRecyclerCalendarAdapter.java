package com.app.fitsmile.fitsreminder.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import com.app.fitsmile.R;
import com.app.fitsmile.fitsreminder.SimpleEvent;
import com.tejpratapsingh.recyclercalendar.adapter.RecyclerCalendarBaseAdapter;
import com.tejpratapsingh.recyclercalendar.model.RecyclerCalendarConfiguration;
import com.tejpratapsingh.recyclercalendar.model.RecyclerCalenderViewItem;
import com.tejpratapsingh.recyclercalendar.utilities.CalendarUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import org.jetbrains.annotations.NotNull;


public final class VerticalRecyclerCalendarAdapter extends RecyclerCalendarBaseAdapter {
    private RecyclerCalendarConfiguration configuration;
    private Map<String, SimpleEvent> eventMap;
    Context context;

    @NotNull
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_calendar_vertical, parent, false);
        return new VerticalRecyclerCalendarAdapter.MonthCalendarViewHolder(view);
    }

    @SuppressLint({"SimpleDateFormat", "ResourceType"})
    public void onBindViewHolder(@NotNull ViewHolder holder, int position, @NotNull final RecyclerCalenderViewItem calendarItem) {
        MonthCalendarViewHolder monthViewHolder = (MonthCalendarViewHolder) holder;
        monthViewHolder.setIsRecyclable(false);
        View itemView = monthViewHolder.itemView;
        MonthCalendarViewHolder monthCalendarViewHolder = (MonthCalendarViewHolder) holder;
        monthViewHolder.itemView.setVisibility(View.VISIBLE);
        monthViewHolder.viewEvent.setVisibility(View.INVISIBLE);


        if (calendarItem.isHeader()) {
            Calendar selectedCalendar = Calendar.getInstance(Locale.getDefault());
            selectedCalendar.setTime(calendarItem.getDate());

            String month = CalendarUtils.dateStringFromFormat(configuration.getCalendarLocale(), selectedCalendar.getTime(), CalendarUtils.getDISPLAY_MONTH_FORMAT());
            int year = selectedCalendar.get(Calendar.YEAR);

            monthViewHolder.textViewDay.setText(month +" " + year);
            monthViewHolder.textViewDay.setVisibility(View.VISIBLE);
            monthViewHolder.textViewDate.setText(month + " " + year);
            monthViewHolder.textViewDate.setVisibility(View.GONE);
        } else if (calendarItem.isEmpty()) {
            monthViewHolder.itemView.setVisibility(View.GONE);
            monthViewHolder.textViewDay.setText("");
            monthViewHolder.textViewDate.setText("");
        } else {
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat outFormatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat outFormatDay = new SimpleDateFormat("dd");
            SimpleDateFormat outFormatYear = new SimpleDateFormat("yyyy");
            Calendar calendarDate = Calendar.getInstance(Locale.getDefault());
            calendarDate.setTime(calendarItem.getDate());

            String day = CalendarUtils.dateStringFromFormat(configuration.getCalendarLocale(), calendarDate.getTime(), CalendarUtils.getDISPLAY_WEEK_DAY_FORMAT()
            );

            monthViewHolder.textViewDay.setText(day);

            String dateInt = CalendarUtils.dateStringFromFormat(
                    configuration.getCalendarLocale(),
                    calendarDate.getTime(),
                    CalendarUtils.getDISPLAY_DATE_FORMAT()
            );
            int eventDate = Integer.parseInt(outFormatDay.format(calendarItem.getDate()));
            int eventMonth = Integer.parseInt(outFormatMonth.format(calendarItem.getDate()));
            int eventYear = Integer.parseInt(outFormatYear.format(calendarItem.getDate()));
            String dateTitle = eventDate + "-" + eventMonth + "-" + eventYear;
            if (eventMap.containsKey("2-8-21")) {
                monthViewHolder.viewEvent.setVisibility(View.VISIBLE);
                if (Objects.requireNonNull(eventMap.get(dateTitle)).title.equals("Goal Reached")) {
                    monthCalendarViewHolder.viewEvent.setImageResource(R.color.colorGreen);
                    monthViewHolder.mImageGoalIndicator.setImageResource(R.drawable.ic_check_24);
                }
                if (Objects.requireNonNull(eventMap.get(dateTitle)).title.equals("Goal Missed")) {
                    monthCalendarViewHolder.viewEvent.setImageResource(R.color.colorRed);
                    monthViewHolder.mImageGoalIndicator.setImageResource(R.drawable.ic_close_24);
                }
                if (Objects.requireNonNull(eventMap.get(dateTitle)).title.equals("Switch Aligner")) {
                    monthCalendarViewHolder.viewEvent.setImageResource(R.color.colorBlue);
                    monthViewHolder.mImageGoalIndicator.setImageResource(R.drawable.ic_teeth);
                }
                if (Objects.requireNonNull(eventMap.get(dateTitle)).title.equals("Treatment Complete")) {
                    monthCalendarViewHolder.viewEvent.setImageResource(R.color.colorBlue);
                    monthViewHolder.mImageGoalIndicator.setImageResource(R.drawable.ic_star);
                } if (Objects.requireNonNull(eventMap.get(dateTitle)).title.equals("Today")) {
                    monthCalendarViewHolder.viewEvent.setImageResource(R.color.colorBlue);
                    monthCalendarViewHolder.mLayoutCircleDay.setBackgroundResource(R.drawable.bg_circle_stroke_gray);
                }
                if (Objects.requireNonNull(eventMap.get(dateTitle)).currentAligner!=null) {
                    if (Objects.requireNonNull(eventMap.get(dateTitle)).currentAligner.equals("Current First")) {
                        monthViewHolder.mLayoutDay.setBackgroundResource(R.drawable.bg_round_calendar_left);
                    }if (Objects.requireNonNull(eventMap.get(dateTitle)).currentAligner.equals("Current")) {
                        monthViewHolder.mLayoutDay.setBackgroundResource(R.drawable.bg_round_calendar_center);
                    }if (Objects.requireNonNull(eventMap.get(dateTitle)).currentAligner.equals("Current Last")) {
                        monthViewHolder.mLayoutDay.setBackgroundResource(R.drawable.bg_round_calendar_right);
                    }
                }

            }


            monthViewHolder.textViewDate.setText(dateInt);

        }

    }


    public VerticalRecyclerCalendarAdapter(Context cont, @NotNull Date startDate, @NotNull Date endDate, @NotNull RecyclerCalendarConfiguration configuration, @NotNull Map<String, SimpleEvent> eventMap) {
        super(startDate, endDate, configuration);
        context = cont;
        this.configuration = configuration;
        this.eventMap = eventMap;
    }


    public static final class MonthCalendarViewHolder extends ViewHolder {
        private TextView textViewDay;

        private TextView textViewDate;

        private CircleImageView viewEvent;
        ImageView mImageGoalIndicator;
        LinearLayout mLayoutDay;
        LinearLayout mLayoutCircleDay;


        public MonthCalendarViewHolder(@NotNull View itemView) {
            super(itemView);
            textViewDay = itemView.findViewById(R.id.textCalenderItemVerticalDay);
            textViewDate = itemView.findViewById(R.id.textCalenderItemVerticalDate);
            viewEvent = itemView.findViewById(R.id.circleGoalIndicator);
            mImageGoalIndicator=itemView.findViewById(R.id.imageGoalIndicator);
            mLayoutDay=itemView.findViewById(R.id.layoutDayCalendar);
            mLayoutCircleDay=itemView.findViewById(R.id.layoutCircleToday);
        }
    }
}
