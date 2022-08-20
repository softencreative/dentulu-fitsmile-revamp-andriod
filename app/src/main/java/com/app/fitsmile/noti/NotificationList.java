package com.app.fitsmile.noti;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationList extends BaseActivity implements NotificationAdapter.NotificationClickListener {


    private RecyclerView mRecyclerNotification;
    private ShimmerFrameLayout shimmerLayout;
    private NotificationAdapter notificationAdapter;
    private ArrayList<NotificationResponse> notificationList = new ArrayList<NotificationResponse>();
    NotificationPojo notificationPojo;
    TextView tvNotification;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    SwipeRefreshLayout mSwipeRefreshLayout;
    private boolean loading = true;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setUpToolBar();
        init();
    }

    private void init() {
        mRecyclerNotification = findViewById(R.id.recycler_notification);
        LinearLayoutManager layoutManager = new LinearLayoutManager(actCon, LinearLayoutManager.VERTICAL, false);
        notificationAdapter = new NotificationAdapter(actCon, notificationList,this);
        mRecyclerNotification.setLayoutManager(layoutManager);
        mRecyclerNotification.setAdapter(notificationAdapter);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        tvNotification = findViewById(R.id.nodata);
        tvNotification.setText(getString(R.string.no_notification_yet));


        if (Utils.isOnline(actCon)) {
            notificationList();
        } else {
            Utils.showToast(actCon, getString(R.string.please_check_your_network));
        }

        mRecyclerNotification.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            notificationList();
                        }
                    }
                }
            }
        });

        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                notificationList();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    void notificationList() {
        String limitLength = String.valueOf(notificationList.size());
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("limitStart", limitLength);
        jsonObj.addProperty("page_no", limitLength);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<NotificationPojo> mService = mApiService.getNotificationList(jsonObj);
        mService.enqueue(new Callback<NotificationPojo>() {
            @Override
            public void onResponse(Call<NotificationPojo> call, Response<NotificationPojo> response) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                notificationPojo = response.body();
                if (notificationPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(NotificationList.this);
                } else {
                    if (notificationPojo.getNotifications() != null)
                        notificationList.addAll(notificationPojo.getNotifications());
                    if (notificationList.size() == 0) {
                        tvNotification.setVisibility(View.VISIBLE);
                    } else {
                        tvNotification.setVisibility(View.GONE);
                    }
                    notificationAdapter.notifyDataSetChanged();
                    if (Utils.getStr(notificationPojo.getLimit_reach()).equalsIgnoreCase("0")) {
                        loading = true;
                    }
                    mRecyclerNotification.scrollToPosition(notificationList.size());
                }
            }

            @Override
            public void onFailure(Call<NotificationPojo> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                call.cancel();
            }
        });
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_notification));
    }

    @Override
    public void onNotificationItemClick(NotificationResponse item,int pos) {
           if (item.getIs_read().equals("0")){
               setNotificationRead(item.getId(),pos);
           }

    }

    void setNotificationRead(String id, final int position) {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("notification_id", id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.getCheckNotification(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                CommonResponse mResponse = response.body();
                if (mResponse.getStatus().equalsIgnoreCase("1")) {
                    NotificationResponse notificationData = new NotificationResponse();
                    notificationData = notificationList.get(position);
                    notificationData.setIs_read("1");
                    notificationList.set(position, notificationData);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            notificationAdapter.notifyDataSetChanged();
                        }
                    }, 300);
                }else  if (notificationPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(NotificationList.this);
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                call.cancel();
            }
        });

    }
}
