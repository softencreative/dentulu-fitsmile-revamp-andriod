package com.app.fitsmile.myaccount;

import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;

import java.util.Objects;

import static com.app.fitsmile.BuildConfig.BASE_URL;


public class TermsConditionsActivity extends AppCompatActivity {
    private WebView webView;
    private
    String status = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        LocaleManager.setLocale(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        webView = (WebView) findViewById(R.id.webView);
        setWebView();
    }

    void setWebView() {
        Objects.requireNonNull(getSupportActionBar()).setTitle(R.string.invite_term_and_condition);
        webView.loadUrl(BASE_URL + "/htmlContent?type=invite_earn_terms_condition");


        Utils.openProgressDialog(this);

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
                Utils.showToast(TermsConditionsActivity.this, "Error:" + description);

            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
