package com.app.fitsmile.shop.ui;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.response.shop.ShopListInfoPojo;
import com.app.fitsmile.shop.inter.ICartUpdateListener;
import com.app.fitsmile.utils.LocaleManager;
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends BaseFragment implements ICartUpdateListener {
    String product_id = "";
    TextView mTextPrice, mTextName, mTextDescription;
    ImageView mImageProduct;
    Button mButtonAddCart, mButtonRemoveCart;
    ProductResult productResult;

    public static ProductDetailFragment newInstance(ProductResult shopNowProduct) {
        ProductDetailFragment productDetailFragment = new ProductDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(AppConstants.PRODUCT_MODEL, shopNowProduct);
        productDetailFragment.setArguments(args);
        return productDetailFragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_product_details, container, false);
        LocaleManager.setLocale(getActivity());
        initUI(view);
        getProductInfo();
        setClicks();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        productResult = (ProductResult) getArguments().getSerializable(AppConstants.PRODUCT_MODEL);
        assert productResult != null;
        product_id=productResult.getId();
    }

    void initUI(View view) {
        mTextPrice = view.findViewById(R.id.textProductPrice);
        mTextName = view.findViewById(R.id.textProductName);
        mTextDescription = view.findViewById(R.id.textProductInfo);
        mImageProduct = view.findViewById(R.id.imageProduct);
        mButtonAddCart = view.findViewById(R.id.btnAddCart);
        mButtonRemoveCart = view.findViewById(R.id.btnRemoveCart);
    }

    void setClicks() {
        mButtonAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addProductToCart(productResult);
            }
        });
        mButtonRemoveCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeProductFromCart(productResult);
            }
        });
    }


    void getProductInfo() {
        Utils.openProgressDialog(getContext());
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty(AppConstants.USER_ID, appPreference.getUserId());
        jsonObj.addProperty(AppConstants.PRODUCT_ID, product_id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ShopListInfoPojo> mService = mApiService.getProductDetails(jsonObj);
        mService.enqueue(new Callback<ShopListInfoPojo>() {
            @Override
            public void onResponse(Call<ShopListInfoPojo> call, Response<ShopListInfoPojo> response) {
                Utils.closeProgressDialog();
                final ShopListInfoPojo shopListPojo = response.body();
                if (shopListPojo.getStatus().equals("1")) {
                    setData(shopListPojo);
                }else if(shopListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(getActivity());
                } else {
                    Utils.showToast(getContext(), shopListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ShopListInfoPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(getContext(), "Something went wrong please try again");
                call.cancel();
            }
        });
    }

    void setData(ShopListInfoPojo shopListPojo) {
        mTextPrice.setText("Price : $" + shopListPojo.getDatas().getPrice());
        mTextName.setText("Product : " + shopListPojo.getDatas().getName());
        mTextDescription.setText(Html.fromHtml(shopListPojo.getDatas().getInfo()));
        Glide.with(ProductDetailFragment.this).load(shopListPojo.getDatas().getImage()).placeholder(R.color.light_grey_color).into(mImageProduct);
        if (shopListPojo.getDatas().getIn_cart().equals("1")) {
            showAddToCartBtn(false);
        } else {
            showAddToCartBtn(true);
        }
    }

    private void showAddToCartBtn(boolean show) {
        if (!show) {
            mButtonRemoveCart.setVisibility(View.VISIBLE);
            mButtonAddCart.setVisibility(View.GONE);
        } else {
            mButtonRemoveCart.setVisibility(View.GONE);
            mButtonAddCart.setVisibility(View.VISIBLE);
        }
    }

    public void removeProductFromCart(ProductResult ProductResult) {
        if (getContext() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).removeProductFromCart(productResult, -1, this);
        }
    }


    public void addProductToCart(ProductResult ProductResult) {
        if (getContext() instanceof ShopActivity) {
            ((ShopActivity) getActivity()).addProductToCart(productResult, -1, this);
        }
    }


    @Override
    public void onProductAddedToCart(ProductResult productResult, int position) {
        showAddToCartBtn(false);
    }

    @Override
    public void onProductRemovedFromCart(ProductResult productResult, int position) {
        showAddToCartBtn(true);
    }
}
