package com.app.fitsmile.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;

import static com.app.fitsmile.BuildConfig.BASE_URL;

public class WebConsentForm extends BaseActivity implements View.OnClickListener {

    private WebView webView;
    private Button btnAgree, btnCancel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_consent);
        LocaleManager.setLocale(this);
        setUpToolBar();
        initView();
    }

    private void initView() {

        webView = findViewById(R.id.web_view);
        btnAgree = findViewById(R.id.btn_agree);
        btnCancel = findViewById(R.id.btn_cancel);

        btnAgree.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        if (Utils.isOnline(this)) {
            webView.loadUrl(BASE_URL + "htmlContent?type=TeleDentistryConsentForm");
        } else {
            Utils.showToast(this, getResources().getString(R.string.please_check_your_network));
        }

        Utils.openProgressDialog(actCon);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Utils.closeProgressDialog();
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                Utils.showToast(actCon, "Error:" + description);
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
        getSupportActionBar().setTitle(getResources().getString(R.string.title_web_consent_form));
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_agree:
                Intent intent = new Intent(this, UpdateSignature.class);
                intent.putExtra("servicebooking","1");
                intent.putExtra("booking_id","0");
                startActivity(intent);
                finish();
                break;
            case R.id.btn_cancel:
                onBackPressed();
                break;
        }
    }
}
