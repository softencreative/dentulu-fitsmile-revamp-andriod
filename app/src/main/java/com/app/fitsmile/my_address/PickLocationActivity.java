package com.app.fitsmile.my_address;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.app.fitsmile.BuildConfig;
import com.app.fitsmile.R;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static com.app.fitsmile.common.Utils.showToast;
import static com.app.fitsmile.my_address.AddressConstants.EXISTING_LATITUDE;
import static com.app.fitsmile.my_address.AddressConstants.EXISTING_LONGITUDE;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_ADDRESS;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_CITY;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LAT;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_LONG;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_PIN_CODE;
import static com.app.fitsmile.my_address.AddressConstants.LOCATION_SHORT_ADDRESS;

public class PickLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int PERMISSION_REQUEST_CODE_SEARCH = 100;
    private static final int PERMISSION_REQUEST_CODE_CURRENT = 101;
    private static final String TAG = "Address";
    private GoogleMap mMap;
    private Address address;
    // Specify the fields to return.
    private List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);
    private PlacesClient placesClient;
    private Button btnConfirm;
    private Marker currentMarker;
    private AutocompleteSupportFragment autocompleteFragment;
    private double existingLatitude = -1;
    private double existingLongitude = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);
        LocaleManager.setLocale(this);
        btnConfirm = findViewById(R.id.btn_confirm);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            existingLatitude = bundle.getDouble(EXISTING_LATITUDE);
            existingLongitude = bundle.getDouble(EXISTING_LONGITUDE);
        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        Places.initialize(getApplicationContext(), getResources().getString(R.string.google_api_key));
        // Create a new Places client instance.
        placesClient = Places.createClient(this);
        // Initialize the AutocompleteSupportFragment.
        autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
    }

    public void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE_CURRENT);
            return;
        }
        FindCurrentPlaceRequest findCurrentPlaceRequest = FindCurrentPlaceRequest.builder(placeFields).build();
        placesClient.findCurrentPlace(findCurrentPlaceRequest).addOnSuccessListener((response) -> {
            List<PlaceLikelihood> likelihoods = response.getPlaceLikelihoods();
            if (likelihoods.size() > 0) {
                showPlaceOnMap(likelihoods.get(0).getPlace().getLatLng());
            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                int statusCode = apiException.getStatusCode();
                // Handle error with given status code.
                Log.e(TAG, "Place not found: " + exception.getMessage());
                showToast(PickLocationActivity.this, "Could not get the location. Reason :" + exception.getMessage());
            }
        });
        searchLocation();
    }

    private void showPlaceOnMap(LatLng latLng) {
        btnConfirm.setVisibility(View.VISIBLE);
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            if (latLng != null) {
                try {
                    address = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1).get(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onResult: lat: " + latLng.latitude);
                Log.d(TAG, "onResult: long: " + latLng.longitude);
            }

            if (currentMarker != null && currentMarker.isVisible()) {
                currentMarker.remove();
            }
            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng).title(address.getLocality()));
            currentMarker.setDraggable(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(12).tilt(30).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            autocompleteFragment.setText(address.getAddressLine(0));
        } catch (NullPointerException e) {
            Log.e(TAG, "onResult: NullPointerException: " + e.getMessage());
        }
    }

    public void confirmLocation(View v) {
        if (address != null) {
            String fullAddress = "";
            for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                if (!fullAddress.isEmpty()) {
                    fullAddress += "\n";
                }
                fullAddress += address.getAddressLine(i);
            }
            String state = address.getAdminArea();
            String country = address.getCountryName();
            String city = address.getLocality();
            String postalCode = address.getPostalCode();

            if (city == null || city.isEmpty()) {
                city = address.getSubAdminArea();
            }

            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            String knownName = address.getFeatureName(); // Only if available else return NULL
            bundle.putString(LOCATION_CITY, city);
            bundle.putString(LOCATION_PIN_CODE, postalCode);
            bundle.putString(LOCATION_LAT, String.valueOf(address.getLatitude()));
            bundle.putString(LOCATION_LONG, String.valueOf(address.getLongitude()));
            bundle.putString(LOCATION_ADDRESS, fullAddress);
            bundle.putString(LOCATION_SHORT_ADDRESS, knownName);
            intent.putExtras(bundle);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    public void searchLocation() {
        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(placeFields);

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NotNull Place selectedPlace) {
                Log.i(TAG, "Place: " + selectedPlace.getName() + ", " + selectedPlace.getId());
                // Construct a request object, passing the place ID and fields array.
                if (selectedPlace.getId() == null) {
                    return;
                }
                FetchPlaceRequest request = FetchPlaceRequest.builder(selectedPlace.getId(), placeFields)
                        .build();
                placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
                    showPlaceOnMap(response.getPlace().getLatLng());
                }).addOnFailureListener((exception) -> {
                    if (exception instanceof ApiException) {
                        ApiException apiException = (ApiException) exception;
                        int statusCode = apiException.getStatusCode();
                        // Handle error with given status code.
                        Log.e(TAG, "Place not found: " + exception.getMessage());
                        showToast(PickLocationActivity.this, "Could not get the location. Reason :" + exception.getMessage());
                    }
                });
            }

            @Override
            public void onError(@NotNull Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == PERMISSION_REQUEST_CODE_SEARCH) {
                searchLocation();
            } else if (requestCode == PERMISSION_REQUEST_CODE_CURRENT) {
                getCurrentLocation();
            }
        } else {
            showToast(PickLocationActivity.this, "Location permission is required to get the address");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                showPlaceOnMap(marker.getPosition());
            }
        });
        if (existingLatitude != -1 && existingLongitude != -1) {
            showPlaceOnMap(new LatLng(existingLatitude, existingLongitude));
            searchLocation();
        } else {
            getCurrentLocation();
        }
    }
}