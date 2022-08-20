package com.app.fitsmile.shop.ui;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.response.shop.MyOrderInfo;
import com.app.fitsmile.response.shop.OrderListResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.app.fitsmile.app.AppConstants.ARG_ORDER;

public class OrderDetailActivity extends BaseActivity {
    private ImageView productImage;
    private TextView mTextDeliveryAddress, mTextOrderId, mTextQuantity;
    private TextView mTextProductName, mTextProductPrice, mTextOrderPlacedOn, mTextOrderStatus;
    private String strURL = "";
    private OrderListResponse.OrderData orderData;
    private static String ARG_BASE_URL = "base_url";

    Button btnCancel, btnFeedback;
    LinearLayout llFeedback, llDescription, llDetails;

    RatingBar rb;
    androidx.appcompat.app.AlertDialog alert;
    androidx.appcompat.app.AlertDialog.Builder dialogBuilder;
    int mOpenFrom;
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        LocaleManager.setLocale(this);
        mOpenFrom = getIntent().getIntExtra(Constants.OPEN_FROM, 1);
        orderData = (OrderListResponse.OrderData) getIntent().getSerializableExtra(ARG_ORDER);
        strURL = getIntent().getStringExtra(ARG_BASE_URL);
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
        getSupportActionBar().setTitle("My orders");
    }


    private void initView() {
        productImage=findViewById(R.id.product_image);
        mTextOrderId = findViewById(R.id.textOrderIdValue);
        mTextOrderStatus = findViewById(R.id.textProductNameValue);
        mTextOrderPlacedOn = findViewById(R.id.textOrderPlacedValue);
        mTextProductName = findViewById(R.id.textOrderProductNameValue);
        mTextProductPrice = findViewById(R.id.textOrderProductPriceValue);
        mTextQuantity = findViewById(R.id.textNosValue);
        mTextDeliveryAddress = findViewById(R.id.textDeliveryAddressValue);
        btnCancel = findViewById(R.id.btnViewOrder);
        btnFeedback = findViewById(R.id.btnFeedbackOrder);


        getItemInfo();

        btnCancel.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(OrderDetailActivity.this);
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                    .setTitle("Confirm")
                    .setMessage("Are you sure you want to cancel this order?")
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getItemCancel();
                        }
                    }).setNegativeButton("NO", null).show();
        });


    }

    void getItemInfo() {
        Utils.openProgressDialog(OrderDetailActivity.this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("order_id", orderData.getId());
        jsonObj.addProperty("product_id", orderData.getDetails().getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<MyOrderInfo> mService = mApiService.myProductOrderDetail(jsonObj);
        mService.enqueue(new Callback<MyOrderInfo>() {
            @Override
            public void onResponse(Call<MyOrderInfo> call, Response<MyOrderInfo> response) {
                Utils.closeProgressDialog();
                if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(OrderDetailActivity.this);
                } else {
                    MyOrderInfo.Datas orderData = response.body().getDatas();
                    Picasso.get().load(strURL + orderData.getDetails().getImage()).placeholder(R.drawable.friends_sends_pictures_no).into(productImage);

                    productImage.setOnClickListener(v -> {
                        Intent intent = new Intent(OrderDetailActivity.this, ZoomImageActivity.class);
                        intent.putExtra("internal", "2");
                        intent.putExtra("ImageName", strURL + orderData.getDetails().getImage());
                        startActivity(intent);
                    });
                    mTextOrderStatus.setText(orderData.getOrder_status_name());
                    mTextDeliveryAddress.setText(orderData.getShipping_address());
                    mTextOrderId.setText(orderData.getOrder_ref_id());
                    mTextOrderPlacedOn.setText(orderData.getCreated_date());
                    mTextQuantity.setText(orderData.getDetails().getQuantity());
                    mTextProductName.setText(orderData.getDetails().getName());
                    mTextProductPrice.setText(orderData.getDetails().getPrice());
                    if (orderData.getOrder_status_name().equalsIgnoreCase("Confirmed")) {
                        btnCancel.setVisibility(VISIBLE);
                        btnFeedback.setVisibility(GONE);
                    } else if (orderData.getOrder_status_name().equalsIgnoreCase("Cancelled")) {
                        btnCancel.setVisibility(GONE);
                        btnFeedback.setVisibility(GONE);
                    } else if (orderData.getOrder_status_name().equalsIgnoreCase("Shipped")) {
                        btnCancel.setVisibility(GONE);
                        btnFeedback.setVisibility(GONE);
                    } else {
                        btnCancel.setVisibility(GONE);
                        if (orderData.getComments().equalsIgnoreCase("")) {
                            //showFeedbackAlert();
                            llFeedback.setVisibility(GONE);
                            btnFeedback.setVisibility(VISIBLE);
                        } else {
                            btnFeedback.setVisibility(GONE);
                            llFeedback.setVisibility(VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<MyOrderInfo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(OrderDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                call.cancel();
            }
        });
    }

    void getItemCancel() {
        Utils.openProgressDialog(OrderDetailActivity.this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("order_id", orderData.getId());
        jsonObj.addProperty("product_id", orderData.getDetails().getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<MyOrderInfo> mService = mApiService.cancelOrder(jsonObj);
        mService.enqueue(new Callback<MyOrderInfo>() {
            @Override
            public void onResponse(Call<MyOrderInfo> call, Response<MyOrderInfo> response) {
                Utils.closeProgressDialog();
                MyOrderInfo shopListPojo = response.body();
                if (shopListPojo.getStatus().equalsIgnoreCase("1")) {
                    Utils.showToast(OrderDetailActivity.this, shopListPojo.getMessage());
                    getFragmentManager().popBackStack();
                }
            }

            @Override
            public void onFailure(Call<MyOrderInfo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(OrderDetailActivity.this, getResources().getString(R.string.something_went_wrong));
                call.cancel();
            }
        });
    }


}
