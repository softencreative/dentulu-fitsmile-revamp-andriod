package com.app.fitsmile.shop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.shop.OrderListResponse;
import com.app.fitsmile.shop.adapter.OrderAdapter;
import com.app.fitsmile.shop.inter.IOrderListener;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrderActivity extends BaseActivity implements IOrderListener {
    private RecyclerView mOrderRecyclerView;
    Toolbar mToolBar;
    TextView mNoData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisiblesItems = 0;
    boolean loading = false;
    ArrayList<OrderListResponse.OrderData> mListOrder = new ArrayList<>();
    OrderAdapter mAdapter;
    int mOpenFrom = 1;
    private OrderListResponse.OrderData orderData;
    private ShimmerFrameLayout shimmerLayout;
    String base_url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_order);
        LocaleManager.setLocale(this);
        mOpenFrom = getIntent().getIntExtra(Constants.OPEN_FROM, 1);
        setToolBar();
        initView();
    }


    private void setToolBar() {
        //setup toolbar
        mToolBar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setTitle("My Orders");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }


    private void initView() {
        mNoData = findViewById(R.id.nodata);
        shimmerLayout = findViewById(R.id.shimmer_layout);
        mOrderRecyclerView = findViewById(R.id.recyclerViewOrder);
        mAdapter = new OrderAdapter(this,mListOrder,base_url,this);
        mOrderRecyclerView.setPadding(10, 10, 10, 10);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mOrderRecyclerView.setLayoutManager(layoutManager);
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout = findViewById(R.id.swipeViewOrder);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        mOrderRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                        }
                    }
                }
            }
        });
        getOrders();
    }

    void getOrders() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<OrderListResponse> mService = mApiService.myProductOrder(jsonObj);
        mService.enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                mListOrder.clear();
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                OrderListResponse shopListPojo = response.body();
                base_url = shopListPojo.getBase_url();
                if (shopListPojo == null || shopListPojo.getDatas() == null || shopListPojo.getDatas().size() == 0) {
                    mNoData.setVisibility(View.VISIBLE);
                    mOrderRecyclerView.setVisibility(View.GONE);
                } else if (shopListPojo.getStatus().equals("401")) {
                    Utils.showSessionAlert(MyOrderActivity.this);
                } else {
                    mNoData.setVisibility(View.GONE);
                    mOrderRecyclerView.setVisibility(View.VISIBLE);
                    mListOrder.addAll(shopListPojo.getDatas());
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                call.cancel();
            }
        });
    }

    @Override
    public void onViewOrder(OrderListResponse.OrderData order, String url) {
        Intent intent=new Intent(MyOrderActivity.this,OrderDetailActivity.class);
        intent.putExtra(AppConstants.ARG_ORDER,order);
        intent.putExtra(AppConstants.ARG_BASE_URL,url);
        intent.putExtra(AppConstants.OPEN_FROM,1);
        startActivity(intent);
    }
}


