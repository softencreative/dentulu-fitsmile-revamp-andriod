package com.app.fitsmile.my_address;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.my_address.AddressConstants.EXISTING_LATITUDE;
import static com.app.fitsmile.my_address.AddressConstants.EXISTING_LONGITUDE;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_ADDRESS;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_CITY;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LAT;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LONG;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_PIN_CODE;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_SHORT_ADDRESS;
import static com.app.fitsmile.my_address.AddressConstants.SELECT_ADDRESS_FROM_MAP;

public class ViewAddressActivity extends BaseActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private Address address;
    private Marker currentMarker;
    private String strPincode = "", strCity = "", strLatitude = "", strLongitude = "", strAddressShort = "", strAddress = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_address);
        LocaleManager.setLocale(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            address = (Address) bundle.getSerializable("address");
        }
        setUpToolBar();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    public void editLocation(View v) {
        Intent intent = new Intent(this, PickLocationActivity.class);
        Bundle bundle = new Bundle();
        bundle.putDouble(EXISTING_LATITUDE, Double.parseDouble(address.getLatitude()));
        bundle.putDouble(EXISTING_LONGITUDE, Double.parseDouble(address.getLongitude()));
        intent.putExtras(bundle);
        startActivityForResult(intent, SELECT_ADDRESS_FROM_MAP);
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
                    updateAddress();
                }
            }
        }
    }

    private void updateAddress() {
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("name", address.getName());
        jsonObj.addProperty("category", address.getCategory());
        jsonObj.addProperty("address_id", address.getId());
        jsonObj.addProperty("address", strAddress);
        jsonObj.addProperty("latitude", strLatitude);
        jsonObj.addProperty("longitude", strLongitude);
        jsonObj.addProperty("city", strCity);
        jsonObj.addProperty("pincode", strPincode);
        jsonObj.addProperty("short_address", strAddressShort);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<Update_addressuser> mService = mApiService.updateAddress(jsonObj);
        mService.enqueue(new Callback<Update_addressuser>() {

            @Override
            public void onResponse(Call<Update_addressuser> call, Response<Update_addressuser> response) {
                Utils.closeProgressDialog();
                address.setAddress(strAddress);
                address.setLatitude(strLatitude);
                address.setLongitude(strLongitude);
                address.setCity(strCity);
                address.setPincode(strPincode);
                address.setShort_address(strAddressShort);
                Update_addressuser newAddress = response.body();
                Log.d("str", response.body().toString());
                if (newAddress.getStatus().equals("1")) {
                    showToast(ViewAddressActivity.this, newAddress.getMessage());
                    showPlaceOnMap(new LatLng(Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude())), address.getName());
                }else if(newAddress.getStatus().equals(AppConstants.FOUR_ZERO_ONE)){
                    Utils.showSessionAlert(ViewAddressActivity.this);
                } else {
                    showToast(ViewAddressActivity.this, "" + newAddress.getMessage());
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

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_view_address));
    }

    private void showPlaceOnMap(LatLng latLng, String title) {
        if (currentMarker != null && currentMarker.isVisible()) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(title));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).tilt(30).build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        showPlaceOnMap(new LatLng(Double.parseDouble(address.getLatitude()), Double.parseDouble(address.getLongitude())), address.getName());
    }
}