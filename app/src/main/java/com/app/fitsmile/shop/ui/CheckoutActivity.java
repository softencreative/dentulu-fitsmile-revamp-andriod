package com.app.fitsmile.shop.ui;


import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;


import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.book.stripe.StripePayment;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.my_address.Address;
import com.app.fitsmile.my_address.MyAddressActivity;
import com.app.fitsmile.response.shop.ProductResult;
import com.app.fitsmile.response.shop.ShopListPojo;
import com.app.fitsmile.shop.adapter.CheckOutAdapter;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckoutActivity extends BaseActivity {
    Toolbar mToolBar;
    CheckOutAdapter mCheckOutAdapter;
    RecyclerView mRecyclerProductTotal;
    ArrayList<ProductResult> mListProduct = new ArrayList<>();
    String mTotalPrice;
    TextView mTextTotalPrice;
    TextView mTextShippingAddress, mTextBillingAddress;
    CheckBox checkShippingAddress;
    Address mShippingAddress = new Address();
    Address mBillingAddress = new Address();
    TextView mTextSaveCards;
    /*card*/
    String strCardID = "";
    String strCustomerID = "";
    String cardends = "";
    Button mBtnCheckOut;
    TextView mTextGrandTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        LocaleManager.setLocale(this);
        mListProduct = (ArrayList<ProductResult>) getIntent().getSerializableExtra(AppConstants.PRODUCT_MODEL);
        mTotalPrice = getIntent().getStringExtra(AppConstants.PRODUCT_TOTAL_PRICE);
        initUI();
        setToolBar();
        setAdapter();
        setClicks();
    }

    private void setClicks() {
        mTextShippingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, MyAddressActivity.class);
                intent.putExtra(AppConstants.OPEN_FROM, 2);
                startActivityForResult(intent, 2);
            }
        });
        mTextBillingAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CheckoutActivity.this, MyAddressActivity.class);
                intent.putExtra(AppConstants.OPEN_FROM, 2);
                startActivityForResult(intent, 1);
            }
        });

        checkShippingAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(checkShippingAddress.isChecked()) {
                    if (mBillingAddress.getAddress() != null) {
                        checkShippingAddress.setChecked(true);
                        mTextShippingAddress.setText(mTextBillingAddress.getText());
                        mShippingAddress = mBillingAddress;
                    } else {
                        Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.select_billing_address));
                        checkShippingAddress.setChecked(false);
                    }
                }else {
                    checkShippingAddress.setChecked(false);
                    mTextShippingAddress.setText("");
                    mShippingAddress = new Address();
                }
            }
        });

        mTextSaveCards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(actCon, StripePayment.class);
                startActivityForResult(i, 3);
            }
        });

        mBtnCheckOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mBillingAddress.getAddress() == null) {
                    Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.please_add_billing_Address));
                } else if (mShippingAddress.getAddress() == null) {
                    Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.please_add_shipping_Address));
                } else if (strCardID.isEmpty()) {
                    Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.please_add_card));
                } else {
                    if (Utils.isOnline(CheckoutActivity.this)) {
                        makeBooking(mBillingAddress.getId(), mShippingAddress.getId(), strCardID, strCustomerID);
                    } else {
                        Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.please_check_your_network));
                    }
                }
            }
        });

    }

    void makeBooking(String str_location_id, String billing_location_id, String str_card_id, String str_customer_id) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("location_id", str_location_id);
        jsonObj.addProperty("billing_location_id", billing_location_id);
        jsonObj.addProperty("cardId", str_card_id);
        jsonObj.addProperty("customerId", str_customer_id);
        jsonObj.addProperty("is_sandbox", BuildConfig.SANDBOX);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ShopListPojo> mService = mApiService.orderCheckout(jsonObj);
        mService.enqueue(new Callback<ShopListPojo>() {
            @Override
            public void onResponse(Call<ShopListPojo> call, Response<ShopListPojo> response) {
                Utils.closeProgressDialog();
                ShopListPojo shopListPojo = response.body();
                if (shopListPojo.getStatus().equalsIgnoreCase("1")) {
                    Utils.showToast(CheckoutActivity.this, shopListPojo.getMessage());
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }else if(shopListPojo.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(CheckoutActivity.this);
                }else {
                    Utils.showToast(CheckoutActivity.this, shopListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<ShopListPojo> call, Throwable t) {
                Utils.closeProgressDialog();
                Utils.showToast(CheckoutActivity.this, getResources().getString(R.string.something_went_wrong));
                call.cancel();
            }
        });
    }

    private void initUI() {
        mTextTotalPrice = findViewById(R.id.textTotalPrice);
        mTextShippingAddress = findViewById(R.id.textShippingAddressValue);
        mTextBillingAddress = findViewById(R.id.textBillingAddressValue);
        checkShippingAddress = findViewById(R.id.checkShippingAddress);
        mTextSaveCards = findViewById(R.id.textSavedCards);
        mTextTotalPrice.setText(String.format("$ %s", mTotalPrice));
        mBtnCheckOut = findViewById(R.id.btnAddCart);
        mTextGrandTotal=findViewById(R.id.textGrandTotal);
        mTextGrandTotal.setText(String.format(R.string.grand_total+" $ %s", mTotalPrice));
    }


    void setAdapter() {
        mRecyclerProductTotal = findViewById(R.id.recyclerProductTotal);
        mCheckOutAdapter = new CheckOutAdapter(this, mListProduct);
        mRecyclerProductTotal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerProductTotal.setAdapter(mCheckOutAdapter);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.shop));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            mBillingAddress = (Address) data.getSerializableExtra(AppConstants.ADDRESS_MODEL);
            assert mBillingAddress != null;
            mTextBillingAddress.setText(mBillingAddress.getAddress());
        }
        if (requestCode == 2 && resultCode == RESULT_OK) {
            mShippingAddress = (Address) data.getSerializableExtra(AppConstants.ADDRESS_MODEL);
            assert mShippingAddress != null;
            mTextShippingAddress.setText(mShippingAddress.getAddress());
        }
        if (requestCode == 3 && resultCode == RESULT_OK) {
            String status = data.getStringExtra("status");
            if (status.equals("1")) {
                strCardID = data.getStringExtra("cardId");
                strCustomerID = data.getStringExtra("customer_id");
                cardends = data.getStringExtra("cardends");
                mTextSaveCards.setText(cardends);
            } else if (StripePayment.newdelete.equals(strCardID)) {
                strCardID = "";
                strCustomerID = "";
                mTextSaveCards.setText(R.string.txt_add_card);
            }
        }
    }
}



