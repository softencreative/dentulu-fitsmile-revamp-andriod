package com.app.fitsmile.my_address;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;
import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_ADDRESS;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_CITY;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LAT;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LONG;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_PIN_CODE;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_SHORT_ADDRESS;
import static com.app.fitsmile.my_address.AddressConstants.SELECT_ADDRESS_FROM_MAP;

public class MyAddressActivity extends BaseActivity implements MyAddressListAdapter.ItemClickAddressList {
    boolean loadAddressesAgain = false;
    private RecyclerView recyclerAddressList;
    private MyAddressListAdapter myAccountMemberListAdapter;
    private TextView lblNoAddress, lblNoAddressType;
    private EditText etAddressDescription, etAddressName;
    private BottomSheetBehavior bottomSheetBehaviorAddress;
    private RadioGroup addressTypeRg;
    private String strPincode = "", strCity = "", strLatitude = "", strLongitude = "", strAddressShort = "", strAddress = "";
    private boolean isAddressSelected = false;
    private String selectedAddressType = "";
    private int selectedAddressTypeIndex = -1;
    private int mOpenFrom = 1;
    List<Address> mAddress=new ArrayList<>();

    private void showAddressOnBottomSheet(String fullAddress) {
        etAddressDescription.setText(fullAddress);
    }

    private void clearAddressFromBottomSheet() {
        etAddressDescription.setText("");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_address);
        LocaleManager.setLocale(this);
        mOpenFrom = getIntent().getIntExtra(AppConstants.OPEN_FROM, 1);
        setUpToolBar();
        recyclerAddressList = findViewById(R.id.recycler_address_list);
        addressTypeRg = findViewById(R.id.address_type_rg);
        Button btnConfirmAdd = findViewById(R.id.btn_confirm);
        lblNoAddress = findViewById(R.id.lbl_no_address);
        lblNoAddressType = findViewById(R.id.lbl_no_address_type);
        LinearLayout llAddAddressBottomSheet = findViewById(R.id.ll_parent_add_address);
        bottomSheetBehaviorAddress = BottomSheetBehavior.from(llAddAddressBottomSheet);
        bottomSheetBehaviorAddress.setState(BottomSheetBehavior.STATE_HIDDEN);
        ImageView addAddressBtn = findViewById(R.id.add_address);
        addAddressBtn.setOnClickListener(v -> {
            if (selectedAddressTypeIndex == -1) {
                showToast(this, getResources().getString(R.string._select_Address_type));
                return;
            }
            if (bottomSheetBehaviorAddress.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehaviorAddress.setState(BottomSheetBehavior.STATE_EXPANDED);
            } else {
                bottomSheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        etAddressDescription = findViewById(R.id.et_address_description);
        etAddressName = findViewById(R.id.et_address_name);
        etAddressDescription.setOnClickListener(v -> selectAddressFromMap());
        btnConfirmAdd.setOnClickListener(v -> addAddress());
        setupAdapter();
        getMyAddresses();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (loadAddressesAgain) {
            getMyAddresses();
        }
        loadAddressesAgain = false;
    }

    private void selectAddressFromMap() {
        startActivityForResult(new Intent(this, PickLocationActivity.class), SELECT_ADDRESS_FROM_MAP);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_ADDRESS_FROM_MAP) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getExtras() != null) {
                    Bundle bundle = data.getExtras();
                    strAddress = bundle.getString(LOCATION_ADDRESS);
                    strLatitude = bundle.getString(LOCATION_LAT);
                    strLongitude = bundle.getString(LOCATION_LONG);
                    strCity = bundle.getString(LOCATION_CITY);
                    strPincode = bundle.getString(LOCATION_PIN_CODE);
                    strAddressShort = bundle.getString(LOCATION_SHORT_ADDRESS);
                    isAddressSelected = true;
                    showAddressOnBottomSheet(strAddress);
                } else {
                    isAddressSelected = false;
                    clearAddressFromBottomSheet();
                }
            } else {
                isAddressSelected = false;
                clearAddressFromBottomSheet();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehaviorAddress != null && bottomSheetBehaviorAddress.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
        super.onBackPressed();
    }

    void addAddress() {
        if (etAddressName.getText().toString().trim().isEmpty()) {
            showToast(this, getResources().getString(R.string.enter_name_to_save_address));
            return;
        }
        if (!isAddressSelected) {
            showToast(this, getResources().getString(R.string._select_Address));
            return;
        }

        if (selectedAddressTypeIndex == -1 || selectedAddressType.trim().isEmpty()) {
            showToast(this, getResources().getString(R.string.get_address_type_from_rb));
            return;
        }
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }

        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("name", etAddressName.getText().toString());
        jsonObj.addProperty("category", selectedAddressType);
        jsonObj.addProperty("address_id", "0");
        jsonObj.addProperty("address", strAddress);
        jsonObj.addProperty("latitude", strLatitude);
        jsonObj.addProperty("longitude", strLongitude);
        jsonObj.addProperty("city", strCity);
        jsonObj.addProperty("pincode", strPincode);
        jsonObj.addProperty("short_address", strAddressShort);
        jsonObj.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<Update_addressuser> mService = mApiService.updateAddress(jsonObj);
        mService.enqueue(new Callback<Update_addressuser>() {

            @Override
            public void onResponse(Call<Update_addressuser> call, Response<Update_addressuser> response) {
                Utils.closeProgressDialog();
                Update_addressuser newAddress = response.body();
                Log.d("str", response.body().toString());
                if (newAddress.getStatus().equals("1")) {
                    showToast(MyAddressActivity.this, newAddress.getMessage());
                    if (bottomSheetBehaviorAddress != null && bottomSheetBehaviorAddress.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehaviorAddress.setState(BottomSheetBehavior.STATE_COLLAPSED);
                        clearAddressFromBottomSheet();
                        etAddressName.setText("");
                    }
                    getMyAddresses();
                }else if(newAddress.getStatus().equals(FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(MyAddressActivity.this);
                } else {
                    showToast(MyAddressActivity.this, "" + newAddress.getMessage());
                }
            }

            @Override
            public void onFailure(Call<Update_addressuser> call, Throwable t) {

                if (!isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    private void setupAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(actCon, LinearLayoutManager.VERTICAL, false);
        recyclerAddressList.setLayoutManager(layoutManager);
        myAccountMemberListAdapter = new MyAddressListAdapter(actCon, this);
        recyclerAddressList.setAdapter(myAccountMemberListAdapter);
        initSwipe();
    }

    private void getMyAddresses() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        selectedAddressTypeIndex = -1;
        selectedAddressType = "";
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<MyAddressListResponse> mService = mApiService.getMyAddresses(jsonObj);
        mService.enqueue(new Callback<MyAddressListResponse>() {
            @Override
            public void onResponse(Call<MyAddressListResponse> call, Response<MyAddressListResponse> response) {
                call.cancel();
                Utils.closeProgressDialog();
                MyAddressListResponse myAddressListResponse = response.body();
                if (myAddressListResponse != null && Utils.getStr(myAddressListResponse.getStatus()).equals(ONE)) {
                    mAddress=myAddressListResponse.getAddress();
                    showAddresses();
                    showAddressTypes(myAddressListResponse.getDatas());
                } else if (myAddressListResponse != null && Utils.getStr(myAddressListResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    showToast(actCon, Utils.getStr(myAddressListResponse.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<MyAddressListResponse> call, Throwable t) {
                call.cancel();
                Utils.closeProgressDialog();
            }
        });
    }

    private void showAddresses() {
        if (mAddress != null && !mAddress.isEmpty()) {
            myAccountMemberListAdapter.setCommonList(mAddress);
            lblNoAddress.setVisibility(View.GONE);
            recyclerAddressList.setVisibility(View.VISIBLE);
        } else {
            lblNoAddress.setVisibility(View.VISIBLE);
            recyclerAddressList.setVisibility(View.GONE);
        }
    }

    private void showAddressTypes(List<Address> datas) {
        if (datas != null && !datas.isEmpty()) {
            addressTypeRg.removeAllViews();
            addressTypeRg.setVisibility(View.VISIBLE);
            lblNoAddressType.setVisibility(View.GONE);
            for (int i = 0; i < datas.size(); i++) {
                Address address = datas.get(i);
                View radioButtonView = View.inflate(this, R.layout.address_type_radio, null);
                RadioButton radioButton = radioButtonView.findViewById(R.id.radioBtn);
                radioButton.setText(address.getName());
                int finalI = i;
                radioButton.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        uncheckPreviousSelectedAddressType();
                        selectedAddressType = address.getId();
                        selectedAddressTypeIndex = finalI;
                    }
                });
                addressTypeRg.addView(radioButtonView);
            }
        } else {
            addressTypeRg.setVisibility(View.GONE);
            lblNoAddressType.setVisibility(View.VISIBLE);
        }
    }

    private void uncheckPreviousSelectedAddressType() {
        if (selectedAddressTypeIndex != -1) {
            ((RadioButton) addressTypeRg.getChildAt(selectedAddressTypeIndex).findViewById(R.id.radioBtn)).setChecked(false);
            selectedAddressType = "";
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.lbl_my_address));
    }

    @Override
    public void onAddressClick(Address address) {
        if (mOpenFrom == 2) {
            webApiCheckAvailability(address.getPincode(),address);
        } else {
            loadAddressesAgain = true;
            Intent intent = new Intent(this, ViewAddressActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("address", address);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    void webApiCheckAvailability(String Zip_Code,Address address) {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("zipcode", Zip_Code);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.getZipCode(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Utils.closeProgressDialog();
                CommonResponse CommonResponse = response.body();
                if (CommonResponse.getStatus().equals("1")) {
                    Intent intent = new Intent();
                    intent.putExtra(AppConstants.ADDRESS_MODEL, address);
                    setResult(Activity.RESULT_OK, intent);
                    finish();
                }else if(CommonResponse.getStatus().equals(FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(MyAddressActivity.this);
                }
                else {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MyAddressActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.popup_common, null);
                    dialogBuilder.setView(dialogView);
                    dialogBuilder.setCancelable(false);
                    final AlertDialog alert = dialogBuilder.create();
                    alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    alert.show();

                    TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
                    TextView tvContent = dialogView.findViewById(R.id.tvContent);
                    TextView tvNo = dialogView.findViewById(R.id.tvNo);
                    TextView tvYes = dialogView.findViewById(R.id.tvYes);

                    tvTitle.setText(R.string.warning);
                    tvContent.setText(R.string.service_not_available);
                    tvNo.setVisibility(View.GONE);
                    tvYes.setText(R.string.got_it);

                    tvYes.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alert.cancel();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                {
                    Utils.closeProgressDialog();
                    Utils.showToast(MyAddressActivity.this, getResources().getString(R.string.something_went_wrong));
                }
                call.cancel();
            }
        });
    }

    private void initSwipe() {
        Paint p = new Paint();
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                if (direction == ItemTouchHelper.LEFT) {
                    showDelete(position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                Bitmap icon;

                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && dX <= 0) {
                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background, p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_delete_white);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                    c.drawBitmap(icon, null, icon_dest, p);
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerAddressList);
    }



    void showDelete(int pos) {
        final androidx.appcompat.app.AlertDialog.Builder dialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_common, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final androidx.appcompat.app.AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        TextView tvTitle = dialogView.findViewById(R.id.tvTitle);
        TextView tvContent = dialogView.findViewById(R.id.tvContent);
        TextView tvNo = dialogView.findViewById(R.id.tvNo);
        TextView tvYes = dialogView.findViewById(R.id.tvYes);

        tvTitle.setText("Confirm");
        tvContent.setText("Are you sure you want to delete?");

        tvYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alert.cancel();
                webApiRemove(mAddress.get(pos).getId(),pos);
            }
        });

        tvNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myAccountMemberListAdapter.notifyDataSetChanged();
                alert.cancel();
            }
        });
    }

    void webApiRemove(String addressId,int pos) {
       Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("address_id", addressId);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DeleteAddressResponse> mService = mApiService.deleteAddress(jsonObj);
        mService.enqueue(new Callback<DeleteAddressResponse>() {
            @Override
            public void onResponse(Call<DeleteAddressResponse> call, Response<DeleteAddressResponse> response) {
               Utils.closeProgressDialog();
                DeleteAddressResponse deleteAddressResponse = response.body();
                if (deleteAddressResponse != null && Utils.getStr(deleteAddressResponse.getStatus()).equals(ONE)) {
                   Utils.showToast(actCon,deleteAddressResponse.Message);
                   mAddress.remove(pos);
                   showAddresses();
                } else if (deleteAddressResponse != null && Utils.getStr(deleteAddressResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    showToast(actCon, Utils.getStr(deleteAddressResponse.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<DeleteAddressResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }
}
