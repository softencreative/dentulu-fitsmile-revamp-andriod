package com.app.fitsmile.fitsreminder;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.TimeLineProgressAdapter;
import com.app.fitsmile.response.trayMinder.TimeLineResponse;
import com.app.fitsmile.response.trayMinder.TimeLineResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TimelineFragment extends BaseFragment implements RecyclerItemClickListener {
    RecyclerView mRecyclerTimeLine;
    TimeLineProgressAdapter mAdapter;
    List<TimeLineResult> mTimeLineList = new ArrayList<>();
    String mFitSmileId;
    String mAlignerNo;

    public TimelineFragment() {
        // Required empty public constructor
    }

    public static TimelineFragment newInstance(String param1, String param2) {
        TimelineFragment fragment = new TimelineFragment();
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
        View view = inflater.inflate(R.layout.fragment_timeline, container, false);
        LocaleManager.setLocale(getActivity());
        mFitSmileId = getArguments().getString(AppConstants.TREATMENT_ID);
        mAlignerNo = getArguments().getString(AppConstants.ALIGNER_NO);
        initUI(view);
        getTimeLineData();
        return view;
    }

    void initUI(View view) {
        mRecyclerTimeLine = view.findViewById(R.id.recyclerTimeline);
    }

    void setAdapter() {
        mRecyclerTimeLine.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new TimeLineProgressAdapter(getActivity(), mTimeLineList, mTimeLineList.get(mTimeLineList.size() - 1).getAlignerNo(), mAlignerNo,this);
        mRecyclerTimeLine.setAdapter(mAdapter);
    }

    @Override
    public void setClicks(int pos, int open) {

    }

    void getTimeLineData() {
        mTimeLineList.clear();
        Utils.openProgressDialog(getActivity());
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
        Log.d("TEST", jsonObject.toString());
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TimeLineResponse> mService = mApiService.fetchTimeLineData(jsonObject);
        mService.enqueue(new Callback<TimeLineResponse>() {
            @Override
            public void onResponse(Call<TimeLineResponse> call, Response<TimeLineResponse> response) {
                Utils.closeProgressDialog();
                TimeLineResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    for (int i = 0; i < mResponse.getData().size(); i++) {
                        if (mResponse.getData().get(i).getTimingData().size() > 0) {
                            mTimeLineList.addAll(mResponse.getData().get(i).getTimingData());
                        }else {
                            mResponse.getData().get(i).setDay("0");
                            mTimeLineList.add(mResponse.getData().get(i));
                        }
                    }

                    for (int i = 0; i < mTimeLineList.size(); i++) {
                        if (mTimeLineList.get(i).getDay().equals("1")) {
                            if (i != 0) {
                                mTimeLineList.get(i - 1).setSwitch(true);
                            }
                        }
                    }
                    setAdapter();
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(getActivity());
                } else {
                    Utils.showToast(getActivity(), mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<TimeLineResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }
}