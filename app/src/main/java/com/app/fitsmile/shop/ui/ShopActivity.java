package com.app.fitsmile.shop.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.AddToFavouritesResponse;
import com.app.fitsmile.response.shop.GetCartProductsResponse;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.response.shop.ShopListInfoPojo;
import com.app.fitsmile.shop.inter.ICartUpdateListener;
import com.app.fitsmile.shop.inter.IFavouritesUpdateListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ShopActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    BottomNavigationView mBottomNavigation;
    Toolbar mToolBar;
    AppBarLayout mAppBarLayout;
    String currentFragment = null;
    public GetCartProductsResponse data;
    Integer isShowFrom = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);
        LocaleManager.setLocale(this);
        setToolBar();
        setInit();
        mAppBarLayout.setVisibility(View.GONE);
        mToolBar.setVisibility(View.GONE);
        loadFragment(new ProductsFragment());
        getCartProductsCount(false);
    }

    void setInit() {
        mBottomNavigation = findViewById(R.id.bottomView);
        mBottomNavigation.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }


    public void loadFragment(Fragment fragment) {
        currentFragment = fragment.getClass().getName();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(currentFragment);
        transaction.commit();
    }

    public void setShowFrom(int from) {
        isShowFrom = from;
    }

    public void setToolBar() {
        //setup toolbar
        mToolBar = findViewById(R.id.toolbar);
        mAppBarLayout = findViewById(R.id.appBar);
        setSupportActionBar(mToolBar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mToolBar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setTitle(getResources().getString(R.string.shop));
        mToolBar.setNavigationIcon(R.drawable.ic_arrow_back);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                /*if (currentFragment.equals(ProductDetailFragment.class.getName())) {
                    if (mBottomNavigation.getSelectedItemId() == R.id.bottom_fav) {
                        showToolBar();
                        loadFragment(new FavProductFragment());
                    } else {
                        mAppBarLayout.setVisibility(View.GONE);
                        mToolBar.setVisibility(View.GONE);
                        loadFragment(new ProductsFragment());
                    }
                } else {
                    finish();
                }
            }*/
            }
        });
    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bottom_shop:
                mAppBarLayout.setVisibility(View.GONE);
                mToolBar.setVisibility(View.GONE);
                loadFragment(new ProductsFragment());
                return true;
            case R.id.bottom_fav:
                showToolBar();
                loadFragment(new FavProductFragment());
                setToolBar();
                return true;
            case R.id.bottom_my_order:
                showToolBar();
                loadFragment(new OrderFragment());
                setToolBar();
                return true;
            case R.id.bottom_cart:
                showToolBar();
                loadFragment(new CartFragment());
                setToolBar();
                return true;
        }
        return false;
    }

    public void addProductToCart(ProductResult ProductResult, int position, ICartUpdateListener cartUpdateListener) {
        updateCart(ProductResult, position, true, cartUpdateListener);
    }

    void updateCart(ProductResult product, int position, boolean increaseProduct, ICartUpdateListener cartUpdateListener) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        jsonObj.addProperty(AppConstants.PRODUCT_ID, product.getId());
        if (!increaseProduct) {
            if (product.quantity > 0) {
                product.quantity--;
            }
        } else {
            product.quantity++;
        }
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String strIPAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        jsonObj.addProperty("ip_address", strIPAddress);
        jsonObj.addProperty("quantity", product.quantity);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ShopListInfoPojo> mService = mApiService.updateItemToCart(jsonObj);
        mService.enqueue(new Callback<ShopListInfoPojo>() {
            @Override
            public void onResponse(Call<ShopListInfoPojo> call, Response<ShopListInfoPojo> response) {
                Utils.closeProgressDialog();
                ShopListInfoPojo shopListPojo = response.body();
                if (shopListPojo.getStatus().equals("1")) {
                    if (increaseProduct) {
                        Utils.showToast(ShopActivity.this, getString(R.string.product_added_message));
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onProductAddedToCart(product, position);
                        }

                    } else {
                        Utils.showToast(ShopActivity.this, getString(R.string.product_removed_message));
                        if (cartUpdateListener != null) {
                            cartUpdateListener.onProductRemovedFromCart(product, position);
                        }
                    }
                    getCartProductsCount(true);
                }else if (shopListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(ShopActivity.this);
                }else {
                    Utils.showToast(ShopActivity.this, shopListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ShopListInfoPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                if (!isFinishing()) {
                    Utils.showToast(ShopActivity.this, "Something went wrong please try again");
                }
                call.cancel();
            }
        });
    }

    public void getCartProductsCount(boolean showProgress) {
        if (showProgress) {
            Utils.openProgressDialog(this);
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ShopListInfoPojo> mService = mApiService.cartItemsCount(jsonObj);
        mService.enqueue(new Callback<ShopListInfoPojo>() {
            @Override
            public void onResponse(Call<ShopListInfoPojo> call, Response<ShopListInfoPojo> response) {
                Utils.closeProgressDialog();

                if (response.body() != null) {
                    if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                        Utils.showSessionAlert(ShopActivity.this);
                    }else {
                        setBadge(R.id.bottom_cart, response.body().getCartItemCount());
                        if (response.body().getCartItemCount() == 0) {
                            removeBadge(R.id.bottom_cart);
                        }
                    }
                } else {
                    removeBadge(R.id.bottom_cart);
                }
            }

            @Override
            public void onFailure(Call<ShopListInfoPojo> call, Throwable t) {
                if (!isFinishing()) {
                    Utils.closeProgressDialog();
                    Utils.showToast(ShopActivity.this, "Something went wrong please try again");
                }
                call.cancel();
            }
        });
    }

    public void addToFavourite(ProductResult product, int position, IFavouritesUpdateListener favouritesUpdateListener) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        jsonObj.addProperty(AppConstants.PRODUCT_ID, product.getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<AddToFavouritesResponse> mService = mApiService.addProductToFavourites(jsonObj);
        mService.enqueue(new Callback<AddToFavouritesResponse>() {
            @Override
            public void onResponse(Call<AddToFavouritesResponse> call, Response<AddToFavouritesResponse> response) {
                Utils.closeProgressDialog();
                AddToFavouritesResponse body = response.body();
                if (body != null) {
                    if (body.getStatus().equals("1")) {
                        favouritesUpdateListener.onProductAddedToFavourite(product, position);
                        Utils.showToast(ShopActivity.this, body.getMessage());
                    } else if (body.getStatus().equals("401")) {
                        Utils.showSessionAlert(ShopActivity.this);
                    } else {
                        Utils.showToast(ShopActivity.this, body.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddToFavouritesResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                if (!isFinishing()) {
                    Utils.showToast(ShopActivity.this,  getResources().getString(R.string.something_went_wrong));
                }
                call.cancel();
            }
        });
    }

    public void removeFromFavourite(ProductResult product, int position, IFavouritesUpdateListener favouritesUpdateListener) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        jsonObj.addProperty(AppConstants.PRODUCT_ID, product.getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<AddToFavouritesResponse> mService = mApiService.removeProductFromFavourites(jsonObj);
        mService.enqueue(new Callback<AddToFavouritesResponse>() {
            @Override
            public void onResponse(Call<AddToFavouritesResponse> call, Response<AddToFavouritesResponse> response) {
                Utils.closeProgressDialog();
                AddToFavouritesResponse body = response.body();
                if (body != null) {
                    if (body.getStatus().equals("1")) {
                        favouritesUpdateListener.onProductRemovedFromFavourite(product, position);
                        Utils.showToast(ShopActivity.this, body.getMessage());
                    } else if (body.getStatus().equals("401")) {
                        Utils.showSessionAlert(ShopActivity.this);
                    } else {
                        Utils.showToast(ShopActivity.this, body.getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddToFavouritesResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                if (!isFinishing()) {
                    Utils.showToast(ShopActivity.this, "Something went wrong please try again");
                }
                call.cancel();
            }
        });
    }

    public void removeProductFromCart(ProductResult ProductResult, int position, ICartUpdateListener cartUpdateListener) {
        updateCart(ProductResult, position, false, cartUpdateListener);
    }

    void setBadge(int menu, int value) {
        mBottomNavigation.getOrCreateBadge(menu);
        mBottomNavigation.setVisibility(View.VISIBLE);
        Objects.requireNonNull(mBottomNavigation.getBadge(menu)).setNumber(value);

    }

    void removeBadge(int menu) {
        mBottomNavigation.removeBadge(menu);

    }


    @Override
    public void onBackPressed() {
        if (currentFragment.equals(ProductsFragment.class.getName()) || currentFragment.equals(FavProductFragment.class.getName()) || currentFragment.equals(OrderFragment.class.getName()) || currentFragment.equals(CartFragment.class.getName())) {
            finish();
        } else if (currentFragment.equals(ProductDetailFragment.class.getName()) && isShowFrom == 1) {
            mAppBarLayout.setVisibility(View.GONE);
            mToolBar.setVisibility(View.GONE);
            loadFragment(new ProductsFragment());
        } else if (currentFragment.equals(ProductDetailFragment.class.getName()) && isShowFrom == 2) {
            loadFragment(new FavProductFragment());
        } else if (currentFragment.equals(OrderDetailFragment.class.getName())) {
            loadFragment(new OrderFragment());
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK) {
            getCartProductsCount(false);
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            getCartProductsCount(false);
            mBottomNavigation.setSelectedItemId(R.id.bottom_my_order);
        }
    }

    public void showToolBar() {
        mAppBarLayout.setVisibility(View.VISIBLE);
        mToolBar.setVisibility(View.VISIBLE);
    }
}

