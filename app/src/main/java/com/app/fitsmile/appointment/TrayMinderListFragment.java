package com.app.fitsmile.appointment;

import androidx.appcompat.app.AlertDialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseFragment;
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

public class TrayMinderListFragment extends BaseFragment implements RecyclerItemClickListener {

    private Context context;
    TrayMinderAppointmentAdapter mAdapter;
    RecyclerView mRecyclerAppointment;
    List<TrayMinderResult> mTrayMinderList = new ArrayList<>();
    private TextView tvNoData;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_appointment, container, false);
        context = getActivity();
        LocaleManager.setLocale(context);
        setAdapter(view);
        return view;
    }

    void setAdapter(View view) {
        tvNoData = view.findViewById(R.id.nodata);
        mRecyclerAppointment = view.findViewById(R.id.recycler_tray_minder);
        mAdapter = new TrayMinderAppointmentAdapter(context, mTrayMinderList, this);
        mRecyclerAppointment.setLayoutManager(new LinearLayoutManager(context));
        mRecyclerAppointment.setAdapter(mAdapter);
        getReminderList();
    }

    @Override
    public void setClicks(int pos, int open) {
        if (open == 1) {
            Intent intent = new Intent(context, ReminderDetailsActivity.class);
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
    void updateReminderStatus(int pos, String id, String status) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        jsonObject.addProperty("status", status);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(getActivity());
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateAppointmentStatus(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                   // mTrayMinderList.get(pos).setStatus(status);
                    if (status.equals("1") && appPreference.getFitsReminderId().equals("")){
                        appPreference.setFitsReminderId(id);
                    }
                       getReminderList();

                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }
    void showDeleteAlert(int pos, String id) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setIcon(R.mipmap.ic_launcher)
                .setMessage(context.getString(R.string.are_you_sure_you_want_to_delete_this_reminder))
                .setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteReminder(pos, id);
                    }
                }).setNegativeButton(context.getString(R.string.no), null).show();
    }

    void deleteReminder(int pos, String id) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Utils.openProgressDialog(context);
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
                        tvNoData.setVisibility(View.VISIBLE);
                        appPreference.setFitsReminderId("");
                    }
                } else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(getActivity());
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
        mTrayMinderList.clear();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(getActivity()).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
      //  Utils.openProgressDialog(getActivity());
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderResponse> mService = mApiService.fetchFitsReminder(jsonObject);
        mService.enqueue(new Callback<TrayMinderResponse>() {
            @Override
            public void onResponse(Call<TrayMinderResponse> call, Response<TrayMinderResponse> response) {
            //    Utils.closeProgressDialog();
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
                    Utils.showSessionAlert(getActivity());
                } else {
                    tvNoData.setVisibility(View.VISIBLE);
                    // Utils.showToast(context,mResponse.getMessage());
                }
                mAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(Call<TrayMinderResponse> call, Throwable t) {
           //     Utils.closeProgressDialog();
            }
        });

    }
}
