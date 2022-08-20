package com.app.fitsmile.home;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.myaccount.FAQResponse;
import com.app.fitsmile.response.myaccount.FAQResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FAQActivity extends BaseActivity {
    RecyclerView mRecyclerFAQ;
    SwipeRefreshLayout mSwipeFAQ;
    ArrayList<FAQResult> mListFAq=new ArrayList<>();
    FAQuestionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);
        LocaleManager.setLocale(this);
        initUI();
        setToolBar();
        getFAQ();
    }

    void initUI(){
      mRecyclerFAQ=findViewById(R.id.recycler_faq);
      mRecyclerFAQ.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
      mAdapter=new FAQuestionAdapter(this,mListFAq);
      mRecyclerFAQ.setAdapter(mAdapter);
      mSwipeFAQ=findViewById(R.id.swipeToRefresh);
        mSwipeFAQ.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mListFAq.clear();
                getFAQ();
                mSwipeFAQ.setRefreshing(false);
            }
        });
    }
    private void setToolBar() {
        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.faq);
    }

    void getFAQ(){
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<FAQResponse> mService = mApiService.getFAQ(jsonObject);
        mService.enqueue(new Callback<FAQResponse>() {
            @Override
            public void onResponse(Call<FAQResponse> call, Response<FAQResponse> response) {
                Utils.closeProgressDialog();
                FAQResponse mResponse=response.body();
                if (mResponse.getStatus().equals(AppConstants.ONE)){
                    mListFAq.addAll(mResponse.getFaqData());
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(FAQActivity.this);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<FAQResponse> call, Throwable t) {
                 Utils.closeProgressDialog();
            }
        });
    }
}