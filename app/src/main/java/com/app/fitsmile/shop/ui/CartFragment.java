package com.app.fitsmile.shop.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.GetCartProductsResponse;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.adapter.CartAdapter;
import com.app.fitsmile.shop.inter.ICartUpdateListener;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartFragment extends BaseFragment implements IProductSelectionListener, ICartUpdateListener {
    private RecyclerView mOrderRecyclerView;
    Activity activity;
    TextView mNoData;
    SwipeRefreshLayout mSwipeRefreshLayout;
    int visibleItemCount = 0;
    int totalItemCount = 0;
    int pastVisiblesItems = 0;
    boolean loading = false;
    ArrayList<ProductResult> mListCartResult = new ArrayList<>();
    CartAdapter mAdapter;
    Button mBtnCheckOut;
    TextView mTextCartTotal, mTextCartSubTotal;
    ConstraintLayout mCardTotal;
    private ShimmerFrameLayout shimmerLayout;
    String mTotalPrice;


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        LocaleManager.setLocale(getActivity());
        initView(view);
        setClicks();
        getCartProducts();
        return view;
    }


    private void initView(View view) {
        mNoData = view.findViewById(R.id.nodata);
        mBtnCheckOut = view.findViewById(R.id.btnCheckout);
        mOrderRecyclerView = view.findViewById(R.id.recyclerCartList);
        mCardTotal = view.findViewById(R.id.layoutCartTotal);
        mTextCartSubTotal = view.findViewById(R.id.textSubTotalPrice);
        mTextCartTotal = view.findViewById(R.id.textGrandTotalPrice);
        shimmerLayout=view.findViewById(R.id.shimmer_layout);
        shimmerLayout.startShimmerAnimation();
        mAdapter = new CartAdapter(getActivity(), mListCartResult, this);
        mOrderRecyclerView.setPadding(10, 10, 10, 10);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        mOrderRecyclerView.setLayoutManager(layoutManager);
        mOrderRecyclerView.setHasFixedSize(true);
        mOrderRecyclerView.setAdapter(mAdapter);


        mSwipeRefreshLayout = view.findViewById(R.id.swipeCart);
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
    }


    void setClicks() {
        mBtnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CheckoutActivity.class);
                intent.putExtra(AppConstants.PRODUCT_MODEL,mListCartResult);
                intent.putExtra(AppConstants.PRODUCT_TOTAL_PRICE,mTotalPrice);
                getActivity().startActivityForResult(intent,3);
            }
        });
    }

    public void getCartProducts() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<GetCartProductsResponse> mService = mApiService.cartItems(jsonObj);
        mService.enqueue(new Callback<GetCartProductsResponse>() {
            @Override
            public void onResponse(Call<GetCartProductsResponse> call, Response<GetCartProductsResponse> response) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                GetCartProductsResponse data = response.body();
                if (data != null && data.getStatus().equals("401")) {
                    Utils.showSessionAlert(getActivity());
                    return;
                }
                assert data != null;
                if (data.getDatas().size() > 0) {
                    mListCartResult.clear();
                    mListCartResult.addAll(data.getDatas());
                    mAdapter.notifyDataSetChanged();
                    mCardTotal.setVisibility(View.VISIBLE);
                    mBtnCheckOut.setVisibility(View.VISIBLE);
                    mOrderRecyclerView.setVisibility(View.VISIBLE);
                    mTotalPrice=data.getTotal_amount();
                    mTextCartSubTotal.setText(String.format("$ %s", data.getTotal_amount()));
                    mTextCartTotal.setText(String.format("$ %s", data.getTotal_amount()));
                } else {
                    mCardTotal.setVisibility(View.GONE);
                    mNoData.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<GetCartProductsResponse> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                mCardTotal.setVisibility(View.GONE);
                mNoData.setVisibility(View.VISIBLE);
                Utils.showToast(getActivity(), getResources().getString(R.string.something_went_wrong));
                call.cancel();
            }
        });
    }

    @Override
    public void onProductSelected(ProductResult productResult) {

    }

    @Override
    public void onAddToCart(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).addProductToCart(productResult, position, this);
        }
    }

    @Override
    public void onRemoveFromCart(ProductResult productResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeProductFromCart(productResult, position, this);
        }
    }


    @Override
    public void onAddToFavourite(ProductResult productResult, int position) {

    }

    @Override
    public void onRemoveFromFavourite(ProductResult productResult, int position) {

    }

    @Override
    public void onProductAddedToCart(ProductResult productResult, int position) {
        getCartItemAfterChange();
    }

    @Override
    public void onProductRemovedFromCart(ProductResult productResult, int position) {
        getCartItemAfterChange();
    }

    void getCartItemAfterChange(){
        shimmerLayout.startShimmerAnimation();
        shimmerLayout.setVisibility(View.VISIBLE);
        mOrderRecyclerView.setVisibility(View.GONE);
        mCardTotal.setVisibility(View.GONE);
        mNoData.setVisibility(View.GONE);
        getCartProducts();
    }


}
