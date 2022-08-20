package com.app.fitsmile.shop.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.FavouriteProductsResponse;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.shop.adapter.FavoriteAdapter;
import com.app.fitsmile.shop.inter.ICartUpdateListener;
import com.app.fitsmile.shop.inter.IFavouritesUpdateListener;
import com.app.fitsmile.shop.inter.IProductSelectionListener;
import com.app.fitsmile.utils.LocaleManager;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavProductFragment extends BaseFragment implements IProductSelectionListener, IFavouritesUpdateListener, ICartUpdateListener {
    private FavoriteAdapter ProductResultsAdapter;
    private FavouriteProductsResponse body;
    private TextView noData;
    private RecyclerView favoritesProductsRV;
    private ShimmerFrameLayout shimmerLayout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_fav_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocaleManager.setLocale(getActivity());
        setProducts();
    }

    private void setProducts() {
        View view = getView();
        if (view != null) {
            favoritesProductsRV = view.findViewById(R.id.recyclerListFavourite);
            shimmerLayout = view.findViewById(R.id.shimmer_layout);
            noData = view.findViewById(R.id.nodata);
            ProductResultsAdapter = new FavoriteAdapter(getActivity(), new ArrayList<>(), this);
            GridLayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
            favoritesProductsRV.setLayoutManager(layoutManager);
            favoritesProductsRV.setAdapter(ProductResultsAdapter);
            showProducts();
            getFavouritesProducts();
        }
    }

    private void getFavouritesProducts() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
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
        Call<FavouriteProductsResponse> mService;
        mService = mApiService.getFavouriteProducts(jsonObj);
        mService.enqueue(new Callback<FavouriteProductsResponse>() {
            @Override
            public void onResponse(Call<FavouriteProductsResponse> call, Response<FavouriteProductsResponse> response) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                body = response.body();
                if (body != null && body.getStatus().equals("401")) {
                    Utils.showSessionAlert(getActivity());
                    return;
                }
                showProducts();
            }

            @Override
            public void onFailure(Call<FavouriteProductsResponse> call, Throwable t) {
                shimmerLayout.setVisibility(View.GONE);
                shimmerLayout.stopShimmerAnimation();
                body = null;
                showProducts();
                call.cancel();
            }
        });
    }

    private void showProducts() {
        if (body != null && body.getDatas() != null && body.getDatas().size() > 0) {
            List<ProductResult> ProductResults = body.getDatas();
            ProductResultsAdapter.addData(ProductResults);
            noData.setVisibility(View.GONE);
            favoritesProductsRV.setVisibility(View.VISIBLE);
        } else {
            noData.setVisibility(View.VISIBLE);
            favoritesProductsRV.setVisibility(View.GONE);
        }
    }

    @Override
    public void onProductSelected(ProductResult productResult) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity)getActivity()).setToolBar();
            ((ShopActivity)getActivity()).setShowFrom(2);
            ((ShopActivity) getActivity()).loadFragment(ProductDetailFragment.newInstance(productResult));

        }
    }

    @Override
    public void onAddToCart(ProductResult ProductResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).addProductToCart(ProductResult, position, this);
        }
    }

    @Override
    public void onRemoveFromCart(ProductResult ProductResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeProductFromCart(ProductResult, position, this);
        }
    }

    @Override
    public void onAddToFavourite(ProductResult ProductResult, int position) {

    }

    @Override
    public void onRemoveFromFavourite(ProductResult ProductResult, int position) {
        if (getActivity() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeFromFavourite(ProductResult, position, this);
        }
    }

    @Override
    public void onProductAddedToFavourite(ProductResult ProductResult, int position) {

    }

    @Override
    public void onProductRemovedFromFavourite(ProductResult ProductResult, int position) {
        getFavouritesProducts();
    }

    @Override
    public void onProductAddedToCart(ProductResult ProductResult, int position) {

    }

    @Override
    public void onProductRemovedFromCart(ProductResult ProductResult, int position) {

    }
}
