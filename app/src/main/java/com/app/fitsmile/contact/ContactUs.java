package com.app.fitsmile.contact;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ContactUs extends BaseActivity implements OnMapReadyCallback {
    TextView textSendEmail,textCall,textWebsite;
    ImageView imvTwiter, imvFacebook, imvInstagram;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        LocaleManager.setLocale(this);
        setUpToolBar();
        initUI();
    }

    void initUI(){
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        imvTwiter = findViewById(R.id.image_twitter);
        imvFacebook = findViewById(R.id.image_facebook);
        imvInstagram = findViewById(R.id.image_instagram);
        textSendEmail = findViewById(R.id.textSupportEmail);
        textCall = findViewById(R.id.textMobile);
        textWebsite = findViewById(R.id.textWebsite);
        imvTwiter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/dentuluapp"));
                startActivity(browserIntent);
            }
        });

        imvFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/fits-Smile-105253514942116"));
                startActivity(browserIntent);
            }
        });

        imvInstagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/fitssmile/"));
                startActivity(browserIntent);
            }
        });

        textSendEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(ContactUs.this);
                View dialogView = li.inflate(R.layout.dialog_email_send, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ContactUs.this);
                alertDialogBuilder.setTitle("Fitsmile");
                alertDialogBuilder.setIcon(R.mipmap.ic_launcher);
                alertDialogBuilder.setView(dialogView);
                final TextView userInput = (TextView) dialogView.findViewById(R.id.emailiddefalut);
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        if (Utils.isOnline(ContactUs.this)) {
                                            sendEmail();
                                        } else {
                                            Utils.showToast(ContactUs.this, getResources().getString(R.string.please_check_your_network));
                                        }
                                    }
                                })
                        .setNegativeButton("Cancel",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        textCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:8000832900"));
                if (ActivityCompat.checkSelfPermission(ContactUs.this,
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(callIntent);
            }

        });
        textWebsite.setMovementMethod(LinkMovementMethod.getInstance());

    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_contact));
    }

    @SuppressLint("LongLogTag")
    protected void sendEmail() {
        Log.i("Send email", "");
        String[] TO = {
                "ayuda@fits-smile.com"
        };

        String[] CC = {
                " "
        };

        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, new String[] {  "ayuda@fits-smile.com" });
        email.putExtra(Intent.EXTRA_SUBJECT, "");
        email.putExtra(Intent.EXTRA_TEXT, "");
        email.setType("message/rfc822");
        startActivity(Intent.createChooser(email, "Send mail..."));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //LatLng sydney = new LatLng(34.066981, -118.271777);
        LatLng sydney = new LatLng( 19.4194607,-99.1725194);
        googleMap.addMarker(new MarkerOptions().position(sydney).title("Fitsmile"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));
    }
}
