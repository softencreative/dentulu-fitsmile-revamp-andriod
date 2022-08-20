package com.app.fitsmile.photoConsultation;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.utils.LocaleManager;


public class PatientProfileCompleteActivity extends AppCompatActivity {

    private Button btnDone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile_complete);
        LocaleManager.setLocale(this);
        setToolBar();
        initialization();
    }

    private void initialization() {

        btnDone = findViewById(R.id.btn_done);
        TextView txtCompleteMessage = findViewById(R.id.txtCompleteMessage);
//        String message = getResources().getString(R.string.dr) +" "+ getResources().getString(R.string.successfull_screen_message);
        String message = getResources().getString(R.string.successfull_screen_message);
        txtCompleteMessage.setText(message);
        onclickListeners();
    }

    private void onclickListeners() {
        btnDone.setOnClickListener(view -> {
            finish();
        });
    }

    private void setToolBar() {
        //setuptool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.dash_ask_my_dentist));
        toolbar.setNavigationOnClickListener((view) -> {
            onBackPressed();
        });
    }



}
