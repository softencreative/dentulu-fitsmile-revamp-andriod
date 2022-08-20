package com.app.fitsmile.fitsreminder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.appointment.ReminderDetailsActivity;
import com.app.fitsmile.appointment.TrayMinderAppointmentAdapter;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TreatmentListActivity extends BaseActivity implements RecyclerItemClickListener {

    TrayMinderAppointmentAdapter mAdapter;
    RecyclerView mRecyclerAppointment;
    List<TrayMinderResult> mTrayMinderList = new ArrayList<>();
    private TextView tvNoData;
    int mOpenFrom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_treatment_list);
        LocaleManager.setLocale(this);
        mOpenFrom = getIntent().getIntExtra(AppConstants.OPEN_FROM, 1);
        setAdapter();
        setUpToolBar();
    }

    void setAdapter() {
        tvNoData = findViewById(R.id.nodata);
        mRecyclerAppointment = findViewById(R.id.recycler_tray_minder);
        mAdapter = new TrayMinderAppointmentAdapter(this, mTrayMinderList, this);
        mRecyclerAppointment.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerAppointment.setAdapter(mAdapter);
        getReminderList();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.treatmentList);
    }

    @Override
    public void setClicks(int pos, int open) {
        if (mOpenFrom == 2) {
            if (open == 1) {
                Intent intent = new Intent(this, AlignerSmileProgressActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, mTrayMinderList.get(pos).getId());
                intent.putExtra(AppConstants.ALIGNER_NO, mTrayMinderList.get(pos).getCurrent_aligner_no());
                startActivity(intent);
            } else if (open == 3) {
                updateReminderStatus(pos, mTrayMinderList.get(pos).getId(), "1");
            } else if (open == 4) {
                updateReminderStatus(pos, mTrayMinderList.get(pos).getId(), "2");
            } else {
                showDeleteAlert(pos, mTrayMinderList.get(pos).getId());
            }
        } else {
            if (open == 1) {
                Intent intent = new Intent(this, ReminderDetailsActivity.class);
                intent.putExtra("id", Integer.parseInt(mTrayMinderList.get(pos).getId()));
                startActivity(intent);
            } else if (open == 3) {
                updateReminderStatus(pos, mTrayMinderList.get(pos).getId(), "1");
            } else if (open == 4) {
                updateReminderStatus(pos, mTrayMinderList.get(pos).getId(), "2");
            } else {
                showDeleteAlert(pos, mTrayMinderList.get(pos).getId());
            }
        }
    }

    void showDeleteAlert(int pos, String id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                .setMessage(getString(R.string.are_you_sure_you_want_to_delete_this_reminder))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReminder(pos, id);
                    }
                }).setNegativeButton(getString(R.string.no), null).show();
    }

    void deleteReminder(int pos, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.deleteReminder(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    mTrayMinderList.remove(pos);
                    if (mTrayMinderList.size() > 0) {
                        appPreference.setFitsReminderId(mTrayMinderList.get(0).getId());
                    } else {
                        appPreference.setFitsReminderId("");
                    }
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(TreatmentListActivity.this);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


    void getReminderList() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderResponse> mService = mApiService.fetchFitsReminder(jsonObject);
        mService.enqueue(new Callback<TrayMinderResponse>() {
            @Override
            public void onResponse(Call<TrayMinderResponse> call, Response<TrayMinderResponse> response) {
                Utils.closeProgressDialog();
                TrayMinderResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus() == 1) {
                    if (mResponse.getData().size() > 0) {
                        tvNoData.setVisibility(View.GONE);
                        mTrayMinderList.addAll(mResponse.getData());
                    } else {
                        tvNoData.setVisibility(View.VISIBLE);
                    }
                } else if (mResponse.getStatus() == 401) {
                    Utils.showSessionAlert(TreatmentListActivity.this);
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    // Utils.showToast(context,mResponse.getMessage());
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<TrayMinderResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }

    void updateReminderStatus(int pos, String id, String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("status", status);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAppointmentStatus(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    mTrayMinderList.get(pos).setStatus(status);
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(TreatmentListActivity.this);
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }

}