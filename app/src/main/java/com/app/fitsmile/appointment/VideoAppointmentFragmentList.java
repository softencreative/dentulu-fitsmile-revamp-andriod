package com.app.fitsmile.appointment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.CheckDoctorAvailablePojo;
import com.app.fitsmile.response.VideoListPojo;
import com.app.fitsmile.response.appointmentdetails.VideoDetailPojo;
import com.app.fitsmile.response.appointmentdetails.VideoDetailPojoData;
import com.app.fitsmile.response.appointmentlist.DoctorAppointmentListPojo;
import com.app.fitsmile.response.appointmentlist.DoctorAppointmentlistdataPojo;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.common.Utils.getChatIntent;
import static com.app.fitsmile.photoConsultation.Constants.ERROR_IN_RESPONSE;

public class VideoAppointmentFragmentList extends BaseFragment implements VirtualVideoAppAdapter.AppointmentItemClick {

    private final String TAG = VideoAppointmentFragmentList.class.getSimpleName();
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    ArrayList<DoctorAppointmentlistdataPojo> doctorAppointmentlistdataPojoArrayList = new ArrayList<>();
    String strLimit;
    private RecyclerView mRecyAppointmentList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView tvNodata;
    private VirtualVideoAppAdapter mAdapter;
    private VideoDetailPojo appointmentDetailResponse = null;
    private LinearLayoutManager layoutManager;
    private boolean loading = true;
    private BottomSheetBehavior bottomSheetFilterPopup;
    TextView mTextFirstFilter, mTextSecFilter, mTextThirdFilter, mTextFourthFilter;
    Button mBtnApply;
    int mSelectFilter = 0;
    ImageView mImageFilter;
    Boolean isVisible = false;

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isVisible = true;
        } else {
            isVisible = false;
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_video_appointment_list, container, false);
        initView(view);
        LocaleManager.setLocale(getActivity());
        return view;
    }

    private void initView(View view) {
        mRecyAppointmentList = view.findViewById(R.id.recy_appointment);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeToRefresh);
        tvNodata = view.findViewById(R.id.nodata);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        LinearLayout llAddAddressBottomSheet = view.findViewById(R.id.ll_layoutFilterPopup);
        bottomSheetFilterPopup = BottomSheetBehavior.from(llAddAddressBottomSheet);
        bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_HIDDEN);
        mTextFirstFilter = view.findViewById(R.id.textFirstFilter);
        mTextFirstFilter.setText(R.string.waiting_for_confirm);
        mTextSecFilter = view.findViewById(R.id.textSecFilter);
        mTextSecFilter.setText(R.string.cancel);
        mTextThirdFilter = view.findViewById(R.id.textThirdFilter);
        mTextFourthFilter = view.findViewById(R.id.textFourthFilter);
        mImageFilter = view.findViewById(R.id.imageFilter);
        mBtnApply = view.findViewById(R.id.btn_apply);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                doctorAppointmentlistdataPojoArrayList.clear();
                getDoctorAppointmentList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mRecyAppointmentList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            getDoctorAppointmentList();
                        }
                    }
                }
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
        setupAdapter();

        if (Utils.isOnline(mActivity)) {
            //if (isVisible) {
                getDoctorAppointmentList();
            //}
        } else {
            Utils.showToast(mActivity, getResources().getString(R.string.please_check_your_network));
        }

    }

    void getDoctorAppointmentList() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
//        Utils.openProgressDialog(mActivity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("limitStart", "" + doctorAppointmentlistdataPojoArrayList.size());
        jsonObj.addProperty("by_code", "0");
        jsonObj.addProperty("patient_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DoctorAppointmentListPojo> mService = mApiService.getDoctorAppointmentList(jsonObj);
        mService.enqueue(new Callback<DoctorAppointmentListPojo>() {
            @Override
            public void onResponse(Call<DoctorAppointmentListPojo> call, Response<DoctorAppointmentListPojo> response) {
                try {
//                    Utils.closeProgressDialog();
                    if (response.body() != null) {
                        DoctorAppointmentListPojo doctorAppointmentListPojo = response.body();
                        if (doctorAppointmentListPojo.getStatus().equals(ONE)) {
                            strLimit = doctorAppointmentListPojo.getLimit_reach();
                            if (doctorAppointmentListPojo.getDatas() != null)
                                doctorAppointmentlistdataPojoArrayList.addAll(doctorAppointmentListPojo.getDatas());
                            mAdapter.notifyDataSetChanged();
                            //1 reached limit 0 next page available
                            loading = Utils.getStr(strLimit).equals(AppConstants.ZERO);

                            if (doctorAppointmentlistdataPojoArrayList.size() == 0) {
                                tvNodata.setVisibility(View.VISIBLE);
                                tvNodata.setText(getString(R.string.no_appointment_yet));
                                mImageFilter.setVisibility(View.GONE);
                            } else {
                                tvNodata.setVisibility(View.GONE);
                                mImageFilter.setVisibility(View.VISIBLE);
                            }
                        } else if (doctorAppointmentListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                            Utils.showSessionAlert(getActivity());
                        } else {
                            if (doctorAppointmentlistdataPojoArrayList.size()==0) {
                                // Utils.showToast(mActivity, Utils.getStr(doctorAppointmentListPojo.getMessage().trim()));
                                tvNodata.setVisibility(View.VISIBLE);
                                tvNodata.setText(getString(R.string.no_appointment_yet));
                                mImageFilter.setVisibility(View.GONE);
                            }
                        }
                    } else {
                        //Utils.showToast(mActivity, ERROR_IN_RESPONSE);
                        tvNodata.setVisibility(View.VISIBLE);
                        tvNodata.setText(getString(R.string.no_appointment_yet));
                        mImageFilter.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<DoctorAppointmentListPojo> call, Throwable t) {
//                Utils.closeProgressDialog();
                Utils.showToast(mActivity, ERROR_IN_RESPONSE);
                call.cancel();

            }
        });
    }

    private void setupAdapter() {
        layoutManager = new LinearLayoutManager(mActivity, LinearLayoutManager.VERTICAL, false);
        mRecyAppointmentList.setLayoutManager(layoutManager);
        mAdapter = new VirtualVideoAppAdapter(mActivity, doctorAppointmentlistdataPojoArrayList, this, false);
        mRecyAppointmentList.setAdapter(mAdapter);
    }

    private void showPopUp(VideoDetailPojo res) {
        appointmentDetailResponse = res;
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_video_appointment_detail, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvAppointmentId = alert.findViewById(R.id.tv_appointment_id);
        TextView tvDateTime = alert.findViewById(R.id.tv_date_time);
        TextView tvService = alert.findViewById(R.id.tv_service);
        TextView tvDentistName = alert.findViewById(R.id.tv_dentist_name);
        TextView tvDuration = alert.findViewById(R.id.tv_duration);
        TextView tvDiscountAmt = alert.findViewById(R.id.tv_discount_amt);
        TextView tvPaidAmt = alert.findViewById(R.id.tv_paid_amt);
        TextView tvStatus = alert.findViewById(R.id.tv_status_of_consult);
        ImageView chatImageView = alert.findViewById(R.id.imv_chat);
        ImageView imvCall = alert.findViewById(R.id.imv_call);

        if (res != null && res.getDatas() != null) {
            VideoDetailPojoData data = res.getDatas();
            tvAppointmentId.setText(Utils.getStr(data.getRef_id()));
            String strDate = Utils.dateFormat(data.getBooking_date(), Utils.inputOnlyDateSmall, Utils.dateDisplayFormatFull);
            tvDateTime.setText(Utils.getStr(strDate) + "\n" + Utils.getStr(data.getBooking_time()));
            tvService.setText(Utils.getStr(data.getSpeacialist_category()));
            tvDentistName.setText(Utils.getStr(data.getSpecialist_name()));
            tvStatus.setText(Utils.getStr(data.getAppointment_status()));

            if (!Utils.getStr(data.getDuration()).isEmpty() || Utils.getStr(data.getDuration()).equals("0")) {
                tvDuration.setVisibility(View.VISIBLE);
                tvDuration.setText(Utils.getStr(data.getDuration() + " " + getString(R.string.mins_value)));
            } else {
                tvDuration.setText(Utils.getStr("0 " + getString(R.string.mins_value)));
            }

            if (!data.getDiscount_coupon().isEmpty()) {
                tvDiscountAmt.setText(Utils.displayPrice(data.getDiscount_coupon()));
            } else {
                tvDiscountAmt.setText(Utils.displayPrice("0"));
            }

            if (!data.getTotal_amount().equalsIgnoreCase("")) {
                tvPaidAmt.setText(Utils.displayPrice(data.getTotal_amount()));
            } else {
                tvPaidAmt.setText(Utils.displayPrice("0"));
            }
            chatImageView.setOnClickListener(v -> startActivity(getChatIntent(getActivity(), res.getDatas().getFirebase_uid(), res.getDatas().getSpecialist_name(), "")));

            imvCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (data != null && !Utils.getStr(data.getDoctor_id()).isEmpty()) {
                        checkDoctorAvailable(Utils.getStr(data.getDoctor_id()), Utils.getStr(data.getId()));
                    }
                }
            });
        }
    }

    String strAppointmentID = "";
    public String strCompareDate = "";
    public String strWaitingDuration = "";
    long seconds;
    public String video_id = "";
    public String booking_date = "";
    Date book_date;
    Date current_date;

    private void checkDoctorAvailable(String doctorId, String appointmentID) {
        Utils.openProgressDialog(mActivity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("doctor_id", doctorId);
        jsonObj.addProperty("appointment_id", appointmentID);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CheckDoctorAvailablePojo> mService = mApiService.getCheckDoctorPojo(jsonObj);
        mService.enqueue(new Callback<CheckDoctorAvailablePojo>() {
            @Override
            public void onResponse(Call<CheckDoctorAvailablePojo> call, Response<CheckDoctorAvailablePojo> response) {
                Utils.closeProgressDialog();
                CheckDoctorAvailablePojo doctorListPojo = response.body();
                if (Utils.getStr(doctorListPojo.getStatus()).equalsIgnoreCase("1")) {
                    strAppointmentID = doctorListPojo.getVideo_id();
                    String channel_id = Utils.random(10);
                    video_id = doctorListPojo.getVideo_id();
                    //booking_date = doctorListPojo.getBooking_date();
                    strCompareDate = doctorListPojo.getCompare_date();
                    strWaitingDuration = doctorListPojo.getWaiting_duration();
                    SimpleDateFormat sdf = new SimpleDateFormat(Utils.outputDate);
                    String currentDateandTime = sdf.format(new Date());
                    try {
                        book_date = convertDate(Utils.getNewDate(mActivity, strCompareDate, Utils.inputDate, Utils.outputDate));
                        current_date = new SimpleDateFormat(Utils.outputDate).parse(currentDateandTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (book_date.compareTo(current_date) > 0) {
                        seconds = (current_date.getTime() - book_date.getTime()) / 1000;
                    } else if (book_date.compareTo(current_date) < 0) {
                        seconds = (current_date.getTime() - book_date.getTime()) / 1000;
                        int WaitingDuration = Integer.parseInt(strWaitingDuration);
                        if (seconds > WaitingDuration) {
                            videoCallActivity(channel_id, doctorId);
                        } else {
                         /*   Intent intent=new Intent(mActivity, CallNowActivity.class);
                            intent.putExtra("doctor_id", doctorId);
                            intent.putExtra("seconds", seconds);
                            startActivity(intent);*/
                        }
                    }

                } else if (doctorListPojo.getStatus().equalsIgnoreCase(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                } else if (Utils.getStr(doctorListPojo.getStatus()).equalsIgnoreCase("0")) {
                    Utils.showToast(mActivity, doctorListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CheckDoctorAvailablePojo> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    void videoCallActivity(final String channel_id, final String doctor_id) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("channel_id", channel_id);
        jsonObj.addProperty("caller_id", appPreference.getUserId());
        jsonObj.addProperty("receiver_id", doctor_id);
        jsonObj.addProperty("is_emergency", "0");
        jsonObj.addProperty("limitedDuration ", "1");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoListPojo> mService = mApiService.sendVideoCallInvitation(jsonObj);
        mService.enqueue(new Callback<VideoListPojo>() {
            @Override
            public void onResponse(Call<VideoListPojo> call, Response<VideoListPojo> response) {
                VideoListPojo videoCallPojoData = response.body();
                if (videoCallPojoData.getStatus().equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                } else {
                    String strToken = videoCallPojoData.getToken();
                    String strChannelId = videoCallPojoData.getChannel_id();
                    String strUsrId = videoCallPojoData.getUidstr();
                    String strFirstName = videoCallPojoData.getDatas().getFirstname();
                    String strLastName = videoCallPojoData.getDatas().getLastname();

                    appPreference.setAgoraCallDetails(strToken, strChannelId,
                            appPreference.getUserId(), doctor_id, strFirstName, strLastName, strAppointmentID,
                            (((appointmentDetailResponse != null && Utils.getStr(appointmentDetailResponse.getDatas().getSpecialist_category()).equals("1"))) ? "1" : "0"),
                            "1");
                    Intent intent = new Intent(mActivity, VideoCallActivity.class);
                    intent.putExtra("view_status", "sender");
                    startActivity(intent);


                }
            }

            @Override
            public void onFailure(Call<VideoListPojo> call, Throwable t) {
                Utils.showToast(mActivity, getString(R.string.please_check_your_network));
                call.cancel();
            }
        });
    }

    public Date convertDate(String date123) {
        Date date = null;
        SimpleDateFormat format = new SimpleDateFormat(Utils.outputDate);
        try {
            date = format.parse(date123);
            System.out.println(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @Override
    public void onAppointmentItemClick(String what, DoctorAppointmentlistdataPojo item, int position) {
        // callAppointmentDetails(Utils.getStr(item.getId()), Utils.getStr(item.getAppointment_type()));
        Intent intent = new Intent(getActivity(), AppointmentDetailsActivity.class);
        intent.putExtra(AppConstants.APPOINTMENT_ID, Utils.getStr(item.getId()));
        intent.putExtra(AppConstants.APPOINTMENT_TYPE, Utils.getStr(item.getAppointment_type()));
        startActivity(intent);
    }

    @Override
    public void onEmptyList(int position) {
        if (position == 0)
            tvNodata.setVisibility(View.VISIBLE);
        else
            tvNodata.setVisibility(View.GONE);
    }


    private void callAppointmentDetails(String strAppointmentId, String strAppointmentType) {
        if (Utils.isOnline(mActivity)) {
            if (!strAppointmentId.isEmpty())
                getAppointmentDetails(strAppointmentId, strAppointmentType);
        } else {
            Utils.showToast(mActivity, getString(R.string.please_check_your_network));
        }
    }

    private void getAppointmentDetails(String Appointment_ID, String strAppointmentType) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(mActivity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("appointment_id", Appointment_ID);
        jsonObj.addProperty("type", strAppointmentType);
        jsonObj.addProperty("by_code", "0");
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<VideoDetailPojo> mService = mApiService.getVideoCallDetail(jsonObj);
        mService.enqueue(new Callback<VideoDetailPojo>() {
            @Override
            public void onResponse(Call<VideoDetailPojo> call, Response<VideoDetailPojo> response) {
                Utils.closeProgressDialog();
                VideoDetailPojo res = response.body();
                if (res != null) {
                    if (res.getStatus().equals(ONE)) {
                        showPopUp(res);
                    } else if (res.getStatus().equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(getActivity());
                    } else {
                        Utils.showToast(mActivity, Utils.getStr(res.getMessage()));
                    }
                } else {
                    Utils.showToast(mActivity, ERROR_IN_RESPONSE);
                }
            }

            @Override
            public void onFailure(Call<VideoDetailPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(mActivity, ERROR_IN_RESPONSE);
                call.cancel();
            }
        });
    }


}
