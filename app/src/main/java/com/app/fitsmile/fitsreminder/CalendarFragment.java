package com.app.fitsmile.fitsreminder;

import android.os.Bundle;

import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.VerticalRecyclerCalendarAdapter;
import com.app.fitsmile.response.trayMinder.TimeLineResponse;
import com.app.fitsmile.response.trayMinder.TimeLineResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.tejpratapsingh.recyclercalendar.model.RecyclerCalendarConfiguration;
import com.tejpratapsingh.recyclercalendar.views.SimpleRecyclerCalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CalendarFragment extends BaseFragment {
    List<TimeLineResult> mTimeLineList = new ArrayList<>();

    private Map<String, SimpleEvent> eventMap = new HashMap<>();

    private SimpleRecyclerCalendarView mCalendarView;
    String mFitSmileId;
    String mAlignerNo;
    Boolean isFirst = false;

    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(String param1, String param2) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        LocaleManager.setLocale(getActivity());
        assert getArguments() != null;
        mFitSmileId = getArguments().getString(AppConstants.TREATMENT_ID);
        mAlignerNo = getArguments().getString(AppConstants.ALIGNER_NO);
        initUi(view);
        return view;
    }

    void initUi(View view) {
        mCalendarView = view.findViewById(R.id.calendarRecyclerView);
        getTimeLineData();
    }

    void getTimeLineData() {
        mTimeLineList.clear();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mFitSmileId);
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TimeLineResponse> mService = mApiService.fetchTimeLineData(jsonObject);
        mService.enqueue(new Callback<TimeLineResponse>() {
            @Override
            public void onResponse(Call<TimeLineResponse> call, Response<TimeLineResponse> response) {
                TimeLineResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    for (int i = 0; i < mResponse.getData().size(); i++) {
                        if (mResponse.getData().get(i).getTimingData().size() > 0) {
                            mTimeLineList.addAll(mResponse.getData().get(i).getTimingData());
                        }
                    }
                    for (int i = 0; i < mTimeLineList.size(); i++) {
                        if (mTimeLineList.get(i).getDay().equals("1")) {
                            if (i != 0) {
                                mTimeLineList.get(i - 1).setSwitch(true);
                                if (mTimeLineList.get(i-1).getAlignerNo().equals(mAlignerNo)) {
                                    if (mTimeLineList.get(i-1).getSwitch() != null && mTimeLineList.get(i-1).getSwitch()) {
                                        mTimeLineList.get(i-1).setCurrent("Current Last");
                                    }
                                }
                            }
                            if (!isFirst) {
                                if (mTimeLineList.get(i).getAlignerNo().equals(mAlignerNo)) {
                                    mTimeLineList.get(i).setCurrent("Current First");
                                    isFirst = true;
                                }
                            }
                        } else {
                            if (mTimeLineList.get(i).getAlignerNo().equals(mAlignerNo)) {
                                mTimeLineList.get(i).setCurrent("Current");

                            }
                        }
                    }
                    setData();
                } else if (mResponse.getStatus()==401) {
                    Utils.showSessionAlert(getActivity());
                }else {
                    Utils.showToast(getActivity(), mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<TimeLineResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void setData() {
        if (mTimeLineList.size() > 0) {
            SimpleDateFormat inFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            Date dateLast = null;
            try {
                date = inFormat.parse(mTimeLineList.get(0).getDate());
                dateLast = inFormat.parse(mTimeLineList.get(mTimeLineList.size() - 1).getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar startCal = Calendar.getInstance();
            SimpleDateFormat outFormatMonth = new SimpleDateFormat("MM");
            SimpleDateFormat outFormatDay = new SimpleDateFormat("dd");
            SimpleDateFormat outFormatYear = new SimpleDateFormat("yyyy");

            startCal.setTime(date);
            int monthStart = Integer.parseInt(outFormatMonth.format(date));
            monthStart--;
            startCal.set(Calendar.MONTH, monthStart);
            startCal.set(Calendar.YEAR, Integer.parseInt(outFormatYear.format(date)));


            Calendar endCal = Calendar.getInstance();
            endCal.setTime(date);
            int monthLast = Integer.parseInt(outFormatMonth.format(dateLast));
            monthLast--;
            endCal.set(Calendar.MONTH, monthLast);
            endCal.set(Calendar.YEAR, Integer.parseInt(outFormatYear.format(dateLast)));
            RecyclerCalendarConfiguration configuration =
                    new RecyclerCalendarConfiguration(
                            RecyclerCalendarConfiguration.CalenderViewType.VERTICAL,
                            Locale.getDefault(), true
                    );

            // Some Random Events
            for (int i = 0; i < mTimeLineList.size(); i++) {
                Calendar eventCal = Calendar.getInstance();
                Date dateEvent = null;
                try {
                    dateEvent = inFormat.parse(mTimeLineList.get(i).getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                eventCal.setTime(dateEvent);
                int eventDate = Integer.parseInt(outFormatDay.format(dateEvent));
                int eventMonth = Integer.parseInt(outFormatMonth.format(dateEvent));
                int eventYear = Integer.parseInt(outFormatYear.format(dateEvent));
                String mToday = inFormat.format(Calendar.getInstance().getTime());
                String mCompareDate = inFormat.format(dateEvent);
                if (mTimeLineList.get(i).getGoalReached().equals("1")) {
                    eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                            eventCal.getTime(),
                            getResources().getString(R.string.goal_reached),
                            ContextCompat.getColor(requireActivity(), R.color.colorGreen), 0,mTimeLineList.get(i).getCurrent()
                    ));
                }
                if (!Calendar.getInstance().getTime().before(eventCal.getTime())) {
                    if (mTimeLineList.get(i).getGoalReached().equals("0")) {
                        eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                                eventCal.getTime(),
                                getResources().getString(R.string.goal_missed),
                                ContextCompat.getColor(requireActivity(), R.color.colorRed), 0,mTimeLineList.get(i).getCurrent()
                        ));
                    }
                } else {
                    if (mTimeLineList.get(i).getGoalReached().equals("0")) {
                        eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                                eventCal.getTime(),
                                getResources().getString(R.string.not_tracked),
                                ContextCompat.getColor(requireActivity(), R.color.colorRed), 0,mTimeLineList.get(i).getCurrent()
                        ));
                    }
                }
                if (mTimeLineList.get(i).getSwitch() != null) {
                    eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                            eventCal.getTime(),
                            getResources().getString(R.string.switch_aligner_title),
                            ContextCompat.getColor(requireActivity(), R.color.colorBlue), 0,mTimeLineList.get(i).getCurrent()
                    ));
                }
                if (i == mTimeLineList.size() - 1) {
                    eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                            eventCal.getTime(),
                            getResources().getString(R.string.treatment_complete),
                            ContextCompat.getColor(requireActivity(), R.color.colorBlue), 0,mTimeLineList.get(i).getCurrent()
                    ));
                }

                if (mCompareDate.equals(mToday)) {
                    eventMap.put(eventDate + "-" + eventMonth + "-" + eventYear, new SimpleEvent(
                            eventCal.getTime(),
                            getResources().getString(R.string.today),
                            ContextCompat.getColor(requireActivity(), R.color.colorBlue), 0,mTimeLineList.get(i).getCurrent()
                    ));
                }
            }

            VerticalRecyclerCalendarAdapter calendarAdapterVertical =
                    new VerticalRecyclerCalendarAdapter(getActivity(), startCal.getTime(), endCal.getTime(),
                            configuration, eventMap);

            mCalendarView.setAdapter(calendarAdapterVertical);
        }
    }


}