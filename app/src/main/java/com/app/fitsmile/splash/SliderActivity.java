package com.app.fitsmile.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.CommanClass;
import com.app.fitsmile.utils.LocaleManager;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;

public class SliderActivity extends BaseActivity implements View.OnClickListener {

    private Button btnRegister;
    private Button btnLogin,btnSelectLanguage;
    int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
        LocaleManager.setLocale(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        initView();
    }

    private void initView() {
        btnLogin = findViewById(R.id.btn_login);
        btnRegister = findViewById(R.id.btn_register);
        btnLogin.setOnClickListener(this);
        btnRegister.setOnClickListener(this);
        btnSelectLanguage=findViewById(R.id.btn_select_language);
        btnSelectLanguage.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_login:
                moveToLogin();
                break;
            case R.id.btn_register:
                moveToRegister();
                break;

            case R.id.btn_select_language:
                CommanClass.openChangeLanguagePopup(SliderActivity.this,"slider");
                break;
        }
    }

    private void moveToRegister() {
        Intent intent = new Intent(SliderActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    private void moveToLogin() {
        Intent intent = new Intent(SliderActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
