package com.app.fitsmile.appointment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.photoConsultancy.appointmentList.PhotoConsultancyListResponse;
import com.app.fitsmile.response.photoConsultancy.appointmentList.PhotoConsultancyListResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PhotoConsultationFragmentList extends BaseFragment implements PhotoConsultationAppAdapter.ItemClickPhotoConsultation {

    private String TAG = PhotoConsultationFragmentList.class.getSimpleName();
    private RecyclerView mRecyAppointmentList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvNodata;
    private List<PhotoConsultancyListResult> mPhotoConsultancyList = new ArrayList<>();
    private PhotoConsultationAppAdapter mAdapter;
    ImageView mImageFilter;
    private BottomSheetBehavior bottomSheetFilterPopup;
    private LinearLayoutManager layoutManager;
    TextView mTextFirstFilter, mTextSecFilter, mTextThirdFilter, mTextFourthFilter;
    Button mBtnApply;
    int mSelectFilter = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_appointment_list, container, false);
        initView(view);
        LocaleManager.setLocale(getActivity());
        return view;
    }


    private void initView(View view) {
        mRecyAppointmentList = view.findViewById(R.id.recy_appointment);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        mImageFilter = view.findViewById(R.id.imageFilter);
        tvNodata = view.findViewById(R.id.nodata);
        LinearLayout llAddAddressBottomSheet = view.findViewById(R.id.ll_layoutFilterPopup);
        bottomSheetFilterPopup = BottomSheetBehavior.from(llAddAddressBottomSheet);
        bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_HIDDEN);
        mTextFirstFilter = view.findViewById(R.id.textFirstFilter);
        mTextSecFilter = view.findViewById(R.id.textSecFilter);
        mTextThirdFilter = view.findViewById(R.id.textThirdFilter);
        mTextFourthFilter = view.findViewById(R.id.textFourthFilter);
        mBtnApply = view.findViewById(R.id.btn_apply);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
        setClicks();
        setFilterData();
    }

    void setClicks() {
        mImageFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bottomSheetFilterPopup.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    void setSelectFilter() {
        setAllTextWithoutFilter();
        if (mSelectFilter == 1) {
            mTextFirstFilter.setBackgroundResource(R.drawable.bg_filter_selected);
            mTextFirstFilter.setTextColor(getActivity().getColor(R.color.white));
        }
        if (mSelectFilter == 2) {
            mTextSecFilter.setBackgroundResource(R.drawable.bg_filter_selected);
            mTextSecFilter.setTextColor(getActivity().getColor(R.color.white));
        }
        if (mSelectFilter == 3) {
            mTextThirdFilter.setBackgroundResource(R.drawable.bg_filter_selected);
            mTextThirdFilter.setTextColor(getActivity().getColor(R.color.white));
        }
        if (mSelectFilter == 4) {
            mTextFourthFilter.setBackgroundResource(R.drawable.bg_filter_selected);
            mTextFourthFilter.setTextColor(getActivity().getColor(R.color.white));
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("ResourceAsColor")
    void setAllTextWithoutFilter() {
        mTextFirstFilter.setBackgroundResource(R.drawable.bg_grey_filter);
        mTextSecFilter.setBackgroundResource(R.drawable.bg_grey_filter);
        mTextThirdFilter.setBackgroundResource(R.drawable.bg_grey_filter);
        mTextFourthFilter.setBackgroundResource(R.drawable.bg_grey_filter);
        mTextFourthFilter.setTextColor(getActivity().getColor(R.color.colorPrimary));
        mTextSecFilter.setTextColor(getActivity().getColor(R.color.colorPrimary));
        mTextThirdFilter.setTextColor(getActivity().getColor(R.color.colorPrimary));
        mTextFirstFilter.setTextColor(getActivity().getColor(R.color.colorPrimary));

    }

    void setFilterData() {
        mTextFirstFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mSelectFilter = 1;
                setSelectFilter();
            }
        });

        mTextSecFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mSelectFilter = 2;
                setSelectFilter();
            }
        });

        mTextThirdFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mSelectFilter = 3;
                setSelectFilter();
            }
        });

        mTextFourthFilter.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                mSelectFilter = 4;
                setSelectFilter();
            }
        });

        mBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mSelectFilter == 1) {
                    mAdapter.getFilter().filter(mTextFirstFilter.getText().toString());
                } else if (mSelectFilter == 2) {
                    mAdapter.getFilter().filter(mTextSecFilter.getText().toString());
                } else if (mSelectFilter == 3) {
                    mAdapter.getFilter().filter(mTextThirdFilter.getText().toString());
                } else if (mSelectFilter == 4) {
                    mAdapter.getFilter().filter(mTextFourthFilter.getText().toString());
                }
                bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getPhotoConsultationList();
        setupAdapter();
    }


    void getPhotoConsultationList() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(getActivity());
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("patient_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        Call<PhotoConsultancyListResponse> mServicePhotoConsultancyList = mApiService.getPhotoConsultationList(jsonObj);
        mServicePhotoConsultancyList.enqueue(new Callback<PhotoConsultancyListResponse>() {
            @Override
            public void onResponse(Call<PhotoConsultancyListResponse> call, Response<PhotoConsultancyListResponse> response) {
                Utils.closeProgressDialog();
                final PhotoConsultancyListResponse photoConsultancyListResponse = response.body();
                if (photoConsultancyListResponse != null && photoConsultancyListResponse.getStatus() == 401) {
                    tvNodata.setVisibility(View.GONE);
                    Utils.closeProgressDialog();
                    Utils.showSessionAlert(getActivity());
                    return;
                } else if (photoConsultancyListResponse != null && photoConsultancyListResponse.getStatus() == 1) {
                    tvNodata.setVisibility(View.GONE);
                    mPhotoConsultancyList.clear();
                    mPhotoConsultancyList.addAll(photoConsultancyListResponse.getData());
                    mAdapter.notifyDataSetChanged();
                    if (mPhotoConsultancyList.size()>0){
                        mImageFilter.setVisibility(View.VISIBLE);
                    }else {
                        mImageFilter.setVisibility(View.INVISIBLE);
                    }
                } else if (photoConsultancyListResponse != null && photoConsultancyListResponse.getStatus() == 0) {
                    // Utils.showToast(getActivity(), photoConsultancyListResponse.getMessage());
                    tvNodata.setVisibility(View.VISIBLE);
                    mImageFilter.setVisibility(View.GONE);
                } else {
                    tvNodata.setVisibility(View.VISIBLE);
                    mImageFilter.setVisibility(View.GONE);
                    //  Utils.showToast(getActivity(), "Something went wrong");
                }
                Utils.closeProgressDialog();
            }

            @Override
            public void onFailure(Call<PhotoConsultancyListResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    private void setupAdapter() {
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyAppointmentList.setLayoutManager(layoutManager);
        mAdapter = new PhotoConsultationAppAdapter(mActivity, this, mPhotoConsultancyList);
        mRecyAppointmentList.setAdapter(mAdapter);
    }

    @Override
    public void onItemClickPhotoConsultation(String id) {
        Intent intent = new Intent(mActivity, PhotoConsultationDetailActivity.class);
        intent.putExtra(Constants.PATIENT_DETAIL_ID, id);
        startActivity(intent);
    }
}
