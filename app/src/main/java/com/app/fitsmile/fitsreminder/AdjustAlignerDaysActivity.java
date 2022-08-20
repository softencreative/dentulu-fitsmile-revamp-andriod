package com.app.fitsmile.fitsreminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.Toast;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.AlignerListAdapter;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.AlignerListResponse;
import com.app.fitsmile.response.trayMinder.AlignerListResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdjustAlignerDaysActivity extends BaseActivity implements RecyclerItemClickListener {
    RecyclerView mRecyclerAlignerList;
    AlignerListAdapter mAdapter;
    String mFitSmileId;
    private final List<AlignerListResult> mListAligner = new ArrayList<>();
    private String strAligerdays;
    String mCurrentAligner, mCurrentAlignerDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adjust_aligner_days);
        LocaleManager.setLocale(this);
        setUpToolBar();
        initUI();
        setAdapter();
        getAlignerList();

    }

    void initUI() {
        mRecyclerAlignerList = findViewById(R.id.recycler_aligner);
        mFitSmileId = getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mCurrentAligner = getIntent().getStringExtra(AppConstants.ALIGNER_NO);
        mCurrentAlignerDay = getIntent().getStringExtra(AppConstants.ALIGNER_DAY);
    }


    void setAdapter() {
        mRecyclerAlignerList.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new AlignerListAdapter(this, mListAligner, this);
        mRecyclerAlignerList.setAdapter(mAdapter);
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.adjust_aligner_days);
    }

    @Override
    public void setClicks(int pos, int open) {
        alignerNumberPopup(Integer.parseInt(mListAligner.get(pos).getNo_of_days()), mListAligner.get(pos).getAligner_no());
    }

    void getAlignerList() {
        mListAligner.clear();
        Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", mFitSmileId);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);

        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<AlignerListResponse> mService = mApiService.fetchAlignerList(jsonObject);
        mService.enqueue(new Callback<AlignerListResponse>() {
            @Override
            public void onResponse(Call<AlignerListResponse> call, Response<AlignerListResponse> response) {
                Utils.closeProgressDialog();
                AlignerListResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    mListAligner.addAll(mResponse.getData());
                }else if (mResponse.getStatus()==401) {
                    Utils.showSessionAlert(AdjustAlignerDaysActivity.this);
                } else {
                    Utils.showToast(AdjustAlignerDaysActivity.this, mResponse.getMessage());
                }
                for (int i = 0; i < mListAligner.size(); i++) {
                    if (Integer.parseInt(mCurrentAligner)+1 > Integer.parseInt(mListAligner.get(i).getAligner_no())) {
                        mListAligner.get(i).setIs_completed("1");
                    }
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<AlignerListResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

    public void alignerNumberPopup(int alignerDays, String alignerNumber) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdjustAlignerDaysActivity.this);
        alertDialog.setTitle(getString(R.string.slide_4_title));
        List<String> genders = new ArrayList<String>();
        int daysPass = 1;
        if (alignerNumber.equals(mCurrentAligner)) {
            daysPass = Integer.parseInt(mCurrentAlignerDay) + 1;
        }

        for (int i = daysPass; i < 151; i++) {
            genders.add("" + i);
        }

        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        int days = alignerDays;
        days = days - daysPass;
        alertDialog.setSingleChoiceItems(items, days, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strAligerdays = items[which].toString();
            }
        });
        alertDialog.setPositiveButton("CONFIRM", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                updateAlignerDaysReminder(alignerNumber, strAligerdays);
            }
        });
        alertDialog.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    void updateAlignerDaysReminder(String alignerNumber, String alignerDays) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("aligner_no", alignerNumber);
        jsonObject.addProperty("id", mFitSmileId);
        jsonObject.addProperty("no_of_days", alignerDays);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAlignerDays(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    getAlignerList();
                    Toast.makeText(actCon, mResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(AdjustAlignerDaysActivity.this);
                }
            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }


}