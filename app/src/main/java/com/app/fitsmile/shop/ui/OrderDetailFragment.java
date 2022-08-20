package com.app.fitsmile.shop.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.shop.MyOrderInfo;
import com.app.fitsmile.response.shop.OrderListResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.ARG_ORDER;

public class OrderDetailFragment extends BaseFragment {
    private ImageView productImage;
    private TextView mTextDeliveryAddress,mTextOrderId,mTextQuantity;
    private TextView mTextProductName,mTextProductPrice,mTextOrderPlacedOn,mTextOrderStatus;
    Activity activity;
    private String strURL = "";
    private OrderListResponse.OrderData orderData;
    private static String ARG_BASE_URL = "base_url";

    Button btnCancel, btnFeedback;
    LinearLayout llFeedback, llDescription, llDetails;

    RatingBar rb;
    androidx.appcompat.app.AlertDialog alert;
    androidx.appcompat.app.AlertDialog.Builder dialogBuilder;

    public static OrderDetailFragment newInstance(OrderListResponse.OrderData order, String url) {
        OrderDetailFragment orderDetailFragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_ORDER, order);
        args.putString(ARG_BASE_URL, url);
        orderDetailFragment.setArguments(args);
        return orderDetailFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_order_details, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LocaleManager.setLocale(getActivity());
        productImage = view.findViewById(R.id.product_image);
        mTextOrderId = view.findViewById(R.id.textOrderIdValue);
        mTextOrderStatus = view.findViewById(R.id.textProductNameValue);
        mTextOrderPlacedOn = view.findViewById(R.id.textOrderPlacedValue);
        mTextProductName= view.findViewById(R.id.textOrderProductNameValue);
        mTextProductPrice = view.findViewById(R.id.textOrderProductPriceValue);
        mTextQuantity = view.findViewById(R.id.textNosValue);
        mTextDeliveryAddress = view.findViewById(R.id.textDeliveryAddressValue);
        orderData = (OrderListResponse.OrderData) getArguments().getSerializable(ARG_ORDER);
        strURL = getArguments().getString(ARG_BASE_URL);
        btnCancel=view.findViewById(R.id.btnViewOrder);
        btnFeedback=view.findViewById(R.id.btnFeedbackOrder);


        getItemInfo();

        btnCancel.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
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

        btnFeedback.setOnClickListener(v -> showFeedbackAlert());
    }

    void getItemInfo() {
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("order_id", orderData.getId());
        jsonObj.addProperty("product_id", orderData.getDetails().getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
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
                      Utils.showSessionAlert(getActivity());
                } else {
                    MyOrderInfo.Datas orderData = response.body().getDatas();
                    Picasso.get().load(strURL + orderData.getDetails().getImage()).placeholder(R.drawable.friends_sends_pictures_no).into(productImage);

                    productImage.setOnClickListener(v -> {
                        Intent intent = new Intent(activity, ZoomImageActivity.class);
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
                        btnCancel.setVisibility(View.VISIBLE);
                        btnFeedback.setVisibility(View.GONE);
                    } else if (orderData.getOrder_status_name().equalsIgnoreCase("Cancelled")) {
                        btnCancel.setVisibility(View.GONE);
                        btnFeedback.setVisibility(View.GONE);
                    } else if (orderData.getOrder_status_name().equalsIgnoreCase("Shipped")) {
                        btnCancel.setVisibility(View.GONE);
                        btnFeedback.setVisibility(View.GONE);
                    } else {
                        btnCancel.setVisibility(View.GONE);
                        if (orderData.getComments().equalsIgnoreCase("")) {
                            //showFeedbackAlert();
                            llFeedback.setVisibility(View.GONE);
                            btnFeedback.setVisibility(View.VISIBLE);
                        } else {
                            btnFeedback.setVisibility(View.GONE);
                            llFeedback.setVisibility(View.VISIBLE);
                        }
                    }
                }

            }
            @Override
            public void onFailure(Call<MyOrderInfo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(activity, "Something went wrong please try again");
                call.cancel();
            }
        });
    }

    void getItemCancel() {
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("order_id", orderData.getId());
        jsonObj.addProperty("product_id", orderData.getDetails().getId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
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
                    Utils.showToast(activity, shopListPojo.getMessage());
                    getFragmentManager().popBackStack();
                }else if (shopListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(getActivity());
                }
            }

            @Override
            public void onFailure(Call<MyOrderInfo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(activity, "Something went wrong please try again");
                call.cancel();
            }
        });
    }

    void showFeedbackAlert() {
        dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(getActivity());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_feedback, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        alert = dialogBuilder.create();
        alert.show();
        TextView tvTitle = dialogView.findViewById(R.id.tv_title);

        tvTitle.setText(getString(R.string.str_feedback_order));
        final EditText comments = dialogView.findViewById(R.id.comments);
        rb = dialogView.findViewById(R.id.simpleRatingBar);

        ImageView iv_close = dialogView.findViewById(R.id.iv_close);
        iv_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
            }
        });

        final Button submitButton = dialogView.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(v -> {
            String strComment = comments.getText().toString();
            int ratings = (int) rb.getRating();
            if (Float.parseFloat(String.valueOf(ratings)) > 0) {
                String strRating = String.valueOf(ratings);
                updateFeedback(strRating, strComment);
            } else {
                Utils.showToast(activity, "Please provide the rating");
            }
        });
    }

    void updateFeedback(String strRating, String strComment) {
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("order_id", orderData.getId());
        jsonObj.addProperty("product_id", orderData.getDetails().getId());
        jsonObj.addProperty("rating", strRating);
        jsonObj.addProperty("comments", strComment);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<MyOrderInfo> mService = mApiService.orderFeedback(jsonObj);
        mService.enqueue(new Callback<MyOrderInfo>() {
            @Override
            public void onResponse(Call<MyOrderInfo> call, Response<MyOrderInfo> response) {
                Utils.closeProgressDialog();
                MyOrderInfo shopListPojo = response.body();
                if (shopListPojo.getStatus().equalsIgnoreCase("1")) {
                    Utils.showToast(activity, shopListPojo.getMessage());
                    getItemInfo();
                }else if (shopListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(getActivity());
                }
            }

            @Override
            public void onFailure(Call<MyOrderInfo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(activity,  getResources().getString(R.string.something_went_wrong));
                call.cancel();
            }
        });
    }
}
