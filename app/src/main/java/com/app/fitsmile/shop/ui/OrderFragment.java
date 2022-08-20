package com.app.fitsmile.shop.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
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

public class OrderFragment extends BaseFragment implements IOrderListener {
    private RecyclerView mOrderRecyclerView;
    Activity activity;
    TextView mNoData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisiblesItems = 0;
    boolean loading = false;
    ArrayList<OrderListResponse.OrderData> mListOrder = new ArrayList<>();
    OrderAdapter mAdapter;
    private ShimmerFrameLayout shimmerLayout;
    String base_url;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, container, false);
        LocaleManager.setLocale(getActivity());
        initView(view);
        return view;
    }


    private void initView(View view) {
        mNoData = view.findViewById(R.id.nodata);
        shimmerLayout = view.findViewById(R.id.shimmer_layout);
        mOrderRecyclerView = view.findViewById(R.id.recyclerOrderList);
        mAdapter = new OrderAdapter(getActivity(), mListOrder,base_url,this);
        mOrderRecyclerView.setPadding(10, 10, 10, 10);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mOrderRecyclerView.setLayoutManager(layoutManager);
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.setAdapter(mAdapter);


        mSwipeRefreshLayout = view.findViewById(R.id.swipeOrder);
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
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
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
                base_url=shopListPojo.getBase_url();
                if (shopListPojo == null || shopListPojo.getDatas() == null || shopListPojo.getDatas().size() == 0) {
                    mNoData.setVisibility(View.VISIBLE);
                    mOrderRecyclerView.setVisibility(View.GONE);
                } else if (shopListPojo.getStatus().equals("401")) {
                    Utils.showSessionAlert(getActivity());
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
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).loadFragment(OrderDetailFragment.newInstance(order,base_url));
        }
    }
}
