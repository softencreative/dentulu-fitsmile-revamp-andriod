package com.app.fitsmile.book.stripe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.book.VideoAppointmentCheckout;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.getstripe.StripeCustomerResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.Stripe;
import com.stripe.android.TokenCallback;
import com.stripe.android.model.Card;
import com.stripe.android.model.Token;
import com.stripe.android.view.CardInputWidget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.card.payment.CardIOActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.PUBLISHABLE_KEY;
import static com.app.fitsmile.BuildConfig.STRIPE_SECRET_KEY;
import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;

public class StripePayment extends BaseActivity implements PaymentCardListAdapter.OnItemClicked {

    public static ArrayList<String> arrayList = new ArrayList<>();
    public static String customer_id = "";
    public static String newdelete = "";
    RecyclerView recyclerView;
    int MY_SCAN_REQUEST_CODE = 100;
    Button scanButton;
    String email = "";
    int position = -1;
    ArrayList<PaymentCardListDate> simplePayment = new ArrayList<>();
    CardInputWidget cardInputWidget;
    PaymentCardListAdapter locationListAdapter;
    Button addCard;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stripe_payment);
        LocaleManager.setLocale(this);
        setUpToolBar();
        init();
    }

    private void init() {
        if (Utils.isOnline(this)) {
            getStripeCustomerId();
        } else {
            Utils.showToast(this, getResources().getString(R.string.please_check_your_network));
        }

        cardInputWidget = findViewById(R.id.card_input_widget);
        scanButton = findViewById(R.id.scanbutton);
        recyclerView = findViewById(R.id.recyclerView);
        addCard = findViewById(R.id.addCard);
        addCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (cardInputWidget.getCard().validateCard()) {
                        vice();
                    } else {
                        Utils.showToast(StripePayment.this, getString(R.string.please_enter_card_details));
                    }

                } catch (Exception e) {
                    Utils.showToast(StripePayment.this, getString(R.string.please_enter_card_details));
                }
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent scanIntent = new Intent(StripePayment.this, CardIOActivity.class);
                    scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
                    scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
                    scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
                    startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
                } catch (Exception e) {
                    Utils.showToast(StripePayment.this, getString(R.string.please_enter_card_details));
                }

            }
        });

        PaymentConfiguration.init(PUBLISHABLE_KEY);
        LinearLayoutManager layoutManager = new LinearLayoutManager(StripePayment.this, LinearLayoutManager.VERTICAL, false);
        DividerItemDecoration itemDecor = new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL);
        itemDecor.setDrawable(ContextCompat.getDrawable(getBaseContext(), R.drawable.divider));
        recyclerView.addItemDecoration(itemDecor);
        locationListAdapter = new PaymentCardListAdapter(this, simplePayment);
        locationListAdapter.setOnClick(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(locationListAdapter);
    }

    private void getStripeCustomerId() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<StripeCustomerResponse> mService = mApiService.getStripeCustomerId(jsonObj);
        mService.enqueue(new Callback<StripeCustomerResponse>() {
            @Override
            public void onResponse(Call<StripeCustomerResponse> call, Response<StripeCustomerResponse> response) {
                Utils.closeProgressDialog();
                StripeCustomerResponse customerResponse = response.body();
                if (Utils.getStr(customerResponse.getStatus()).equals(ONE)) {
                    email = customerResponse.getEmail();

                    customer_id = customerResponse.getStripeCustomerId();
                    // Log.d("str", response.body().toString());


                    if (customer_id.equals("")) {

                        // vlc();
                        if (Utils.isOnline(StripePayment.this)) {
                            webApiCreateCustomerId();
                        } else {
                            Utils.showToast(StripePayment.this, getResources().getString(R.string.please_check_your_network));
                        }


                    } else {
                        if (Utils.isOnline(StripePayment.this)) {


                            CardList();

                        } else {

                            Utils.showToast(StripePayment.this, getString(R.string.please_check_your_network));
                        }


                    }
                } else if (Utils.getStr(customerResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, getString(R.string.something_went_wrong));
                }
            }

            @Override
            public void onFailure(Call<StripeCustomerResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }


    private void webApiCreateCustomerId() {
        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers",
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.equals(null)) {
                            Log.e("Your Array Response", response);

                            JSONObject obj = null;

                            try {
                                obj = new JSONObject(response);

                                String pageName = obj.getString("id");

                                customer_id = pageName;
                                Log.d("Your Array Response", "customer_id >>>>> " + customer_id);
                                if (!customer_id.isEmpty()) {
                                    webApiStoreCustomerId(customer_id);
                                } else {
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {
                            Log.e("Your Array Response", "Data Null");
                        }
                    }

                }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                return params;
            }

            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(request);
    }

    private void webApiStoreCustomerId(String Customer_id) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("customerId", Customer_id);
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<StripeSaveCustomerId> mService = mApiService.getSaveStripeId(jsonObj);
        mService.enqueue(new Callback<StripeSaveCustomerId>() {
            @Override
            public void onResponse(Call<StripeSaveCustomerId> call, Response<StripeSaveCustomerId> response) {
                StripeSaveCustomerId saveCustomerId = response.body();
                if (saveCustomerId.equals(1)) {
                    CardList();
                } else if (Utils.getStr(saveCustomerId.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                }
            }

            @Override
            public void onFailure(Call<StripeSaveCustomerId> call, Throwable t) {

                if (!StripePayment.this.isFinishing()) {
                }
                call.cancel();
            }
        });
    }

    private void CardList() {
        simplePayment.clear();
        StringRequest request = new StringRequest(Request.Method.GET, "https://api.stripe.com/v1/customers/" + customer_id + "/sources?object=card", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (!response.equals(null)) {
                    Log.e("Your Array Response", response);

                    JSONObject obj = null;
                    try {

                        obj = new JSONObject(response);

                        //   String pageName = obj.getString("id");
                        JSONArray jsonArray = obj.getJSONArray("data");

                        if (jsonArray.length() == 0) {

                        } else {


                            for (int i = 0; i < jsonArray.length(); i++) {

                                JSONObject jsonObject = jsonArray.getJSONObject(i);


                                PaymentCardListDate paymentCardListDate = new PaymentCardListDate();

                                String brand = jsonObject.getString("brand");
                                String expmonth = jsonObject.getString("exp_month");
                                String expyear = jsonObject.getString("exp_year");
                                String getlast = jsonObject.getString("last4");
                                String id = jsonObject.getString("id");


                                paymentCardListDate.setBrand(brand);
                                paymentCardListDate.setExp_month(expmonth);
                                paymentCardListDate.setExp_year(expyear);
                                paymentCardListDate.setLast4(getlast);
                                paymentCardListDate.setId(id);
                                paymentCardListDate.setIs_selected("0");

                                simplePayment.add(paymentCardListDate);
                                locationListAdapter.notifyDataSetChanged();
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);

                if (!StripePayment.this.isFinishing()) {
                }
            }
        }) {

            //This is for Headers If You Needed
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                // params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                return params;
            }


        };
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        queue.add(request);
    }

    private void vice() {
        Card card = cardInputWidget.getCard();
        Utils.openProgressDialog(actCon);
        Stripe stripe = new Stripe(StripePayment.this, PUBLISHABLE_KEY);
        stripe.createToken(card,
                new TokenCallback() {
                    public void onSuccess(Token token) {


                        Utils.closeProgressDialog();
                        // Send token to your server

                        Log.e("Token", token.getId());

                        cardInputWidget.clear();

                        addCard(token.getId());

                    }

                    public void onError(Exception error) {

                        Utils.showToast(StripePayment.this, "Invalid Card ");
                        if (!StripePayment.this.isFinishing()) {
                            Utils.closeProgressDialog();
                        }
                        // Show localized error message

                    }
                });

    }

    private void addCard(final String token) {

        Utils.openProgressDialog(actCon);

        StringRequest request = new StringRequest(Request.Method.POST, "https://api.stripe.com/v1/customers/" + customer_id + "/sources", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Utils.closeProgressDialog();
                if (!response.equals(null)) {
                    Log.e("Your Array Response", response);

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);

                        Utils.showToast(StripePayment.this, getString(R.string.card_added_successfully));

                        CardList();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.e("Your Array Response", "Data Null");
                }
            }

        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error is ", "" + error);

                if (!StripePayment.this.isFinishing()) {
                    Utils.closeProgressDialog();
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + STRIPE_SECRET_KEY);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("source", token);
                return params;
            }


        };
        request.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(request);
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_payment));
    }

    @Override
    public void onItemClick(int position) {
        this.position = position;
        paymentsucess(position);
    }

    void paymentsucess(int position){
        Intent intent = new Intent(getApplicationContext(), VideoAppointmentCheckout.class);
        intent.putExtra("cardId",simplePayment.get(position).getId());
        intent.putExtra("customer_id", customer_id);
        intent.putExtra("status","1");
//        intent.putExtra("cardends", simplePayment.get(position).getBrand()+" ending in "+simplePayment.get(position).getLast4());
        intent.putExtra("cardends", "**** **** **** "+simplePayment.get(position).getLast4());
        setResult(RESULT_OK,intent);
        finish();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent mIntent = new Intent();
                mIntent.putExtra("status","2");
                mIntent.putExtra("deletedCards",arrayList);
                setResult(RESULT_OK, mIntent);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
