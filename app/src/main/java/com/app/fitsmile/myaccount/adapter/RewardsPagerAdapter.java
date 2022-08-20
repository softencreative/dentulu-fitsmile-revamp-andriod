package com.app.fitsmile.myaccount.adapter;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.app.AppPreference;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.myaccount.TermsConditionsActivity;
import com.app.fitsmile.response.rewards.EarningResponse;
import com.app.fitsmile.response.rewards.EarningResult;
import com.app.fitsmile.response.rewards.SettingResponse;
import com.app.fitsmile.response.rewards.SettingResult;
import com.app.fitsmile.response.rewards.TransactionResponse;
import com.app.fitsmile.response.rewards.TransactionResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RewardsPagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mInflater;
    private Context context;
    private AppPreference mAppPreference;

    private final int EARNING_DETAILS = 0,
            RECENT_TRANSACTIONS = 1,
            INVITE_AND_EARN = 2;

    public RewardsPagerAdapter(Context context, AppPreference appPreference) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
        mAppPreference = appPreference;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case EARNING_DETAILS:
                return new EarningDetailsViewHolder(mInflater.inflate(R.layout.item_earning_details, parent, false));
            case RECENT_TRANSACTIONS:
                return new RecentTransactionsViewHolder(mInflater.inflate(R.layout.item_recent_transactions, parent, false));
            default:
                return new InviteAndEarnViewHolder(mInflater.inflate(R.layout.item_invite_and_earn, parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof EarningDetailsViewHolder) {
            EarningDetailsViewHolder viewHolder = ((EarningDetailsViewHolder) holder);
            earningList(viewHolder.recyclerView, mAppPreference.getUserId(), viewHolder.noDataTV, viewHolder.loadingProgress);
        } else if (holder instanceof RecentTransactionsViewHolder) {
            RecentTransactionsViewHolder viewHolder = ((RecentTransactionsViewHolder) holder);
            transactionList(viewHolder.recyclerView, mAppPreference.getUserId(), viewHolder.noDataTV, viewHolder.loadingProgress);
        } else {
            InviteAndEarnViewHolder viewHolder = ((InviteAndEarnViewHolder) holder);
            String referralCode = mAppPreference.getreferalcode();
            viewHolder.shareBtn.setOnClickListener(v -> {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = mAppPreference.getUserName() + " shared a Coupon/Dentist Code.Use this code while make booking. The code is '" + referralCode + "'";
                String shareSub = "Please use below code";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                context.startActivity(Intent.createChooser(sharingIntent, "Share using"));
            });
            viewHolder.couponCodeTV.setText(referralCode);
            viewHolder.couponCodeTV.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("couponCode", referralCode);
                if (clipboard != null) {
                    clipboard.setPrimaryClip(clip);
                    Utils.showToast(context, "Coupon code copied");
                }
            });
            viewHolder.t_and_c_TV.setOnClickListener(v -> {
                Intent intent = new Intent(context, TermsConditionsActivity.class);
                context.startActivity(intent);
            });
            getShareInfo(viewHolder.loadingProgress, viewHolder.dataLayout, viewHolder.earnAmountTV);
        }
    }

    void getShareInfo(ProgressBar progressBar, LinearLayout dataLayout, TextView earnAmountLabel) {
        progressBar.setVisibility(View.VISIBLE);
        dataLayout.setVisibility(View.GONE);
        ApiInterface mApiService = AppController.getInstance().getClient().create(ApiInterface.class);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", mAppPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(context).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        Call<SettingResponse> mService = mApiService.getShare(jsonObject);
        mService.enqueue(new Callback<SettingResponse>() {
            @Override
            public void onResponse(Call<SettingResponse> call, Response<SettingResponse> response) {
                dataLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                ArrayList<SettingResult> mSettingList = response.body().getSettings();
                for (int i = 0; i < mSettingList.size(); i++) {
                    if (mSettingList.get(i).getCode().equals("sharing_amount")) {
                        String value = mSettingList.get(i).getValue();
                        earnAmountLabel.setText("Earn $" + value);
                    }
                }

            }

            @Override
            public void onFailure(Call<SettingResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                dataLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {
        super.getItemViewType(position);
        switch (position) {
            case 0:
                return EARNING_DETAILS;
            case 1:
                return RECENT_TRANSACTIONS;
            default:
                return INVITE_AND_EARN;
        }
    }

    public class EarningDetailsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView noDataTV;
        ProgressBar loadingProgress;

        EarningDetailsViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            noDataTV = itemView.findViewById(R.id.noDataTV);
            loadingProgress = itemView.findViewById(R.id.loadingProgress);
        }
    }

    public class RecentTransactionsViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView noDataTV;
        ProgressBar loadingProgress;

        RecentTransactionsViewHolder(View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerView);
            noDataTV = itemView.findViewById(R.id.noDataTV);
            loadingProgress = itemView.findViewById(R.id.loadingProgress);
        }
    }

    public class InviteAndEarnViewHolder extends RecyclerView.ViewHolder {
        ImageView shareBtn;
        TextView couponCodeTV, earnAmountTV, t_and_c_TV;
        ProgressBar loadingProgress;
        LinearLayout dataLayout;

        InviteAndEarnViewHolder(View itemView) {
            super(itemView);
            shareBtn = itemView.findViewById(R.id.shareBtn);
            couponCodeTV = itemView.findViewById(R.id.couponCodeTV);
            earnAmountTV = itemView.findViewById(R.id.earnAmountTV);
            loadingProgress = itemView.findViewById(R.id.loadingProgress);
            dataLayout = itemView.findViewById(R.id.dataLayout);
            t_and_c_TV = itemView.findViewById(R.id.t_and_c_TV);
        }
    }

    void earningList(RecyclerView recyclerView, String user_id, TextView noDataTV, ProgressBar loadingProgress) {
        loadingProgress.setVisibility(View.VISIBLE);
        ArrayList<EarningResult> productDatasArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        EarningAdapter earniningAdapter = new EarningAdapter(context, productDatasArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(earniningAdapter);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", user_id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(context).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = AppController.getInstance().getClient().create(ApiInterface.class);
        Call<EarningResponse> mService = mApiService.getEarning(jsonObj);
        mService.enqueue(new Callback<EarningResponse>() {
            @Override
            public void onResponse(Call<EarningResponse> call, Response<EarningResponse> response) {
                loadingProgress.setVisibility(View.GONE);
                EarningResponse TransactionResult = response.body();
               /* EarningResponse TransactionResult=new EarningResponse();
                EarningResult earningResult=new EarningResult();
                earningResult.setAmount("100");
                earningResult.setCreated_date("2020-10-20 10:20:10");
                earningResult.setEmail("raman@gmail.com");
                earningResult.setFirstname("raman");
                earningResult.setLastname("raman");
                ArrayList<EarningResult> mList=new ArrayList<>();
                mList.add(earningResult);
                TransactionResult.setEarnings(mList);
               */
                if (TransactionResult == null || TransactionResult.getEarnings().size() == 0) {
                    noDataTV.setVisibility(View.VISIBLE);
                    noDataTV.setText(R.string.no_earning_yet);
                } else {

                    productDatasArrayList.addAll(TransactionResult.getEarnings());
                    earniningAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<EarningResponse> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                call.cancel();
            }
        });

    }

    void transactionList(RecyclerView recyclerView, String user_id, TextView noDataTV, ProgressBar loadingProgress) {
        loadingProgress.setVisibility(View.VISIBLE);
        ArrayList<TransactionResult> productDatasArrayList = new ArrayList<>();
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        TransactionAdapter transactionAdapter = new TransactionAdapter(context, productDatasArrayList);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(transactionAdapter);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", user_id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(context).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = AppController.getInstance().getClient().create(ApiInterface.class);
        Call<TransactionResponse> mService = mApiService.getTransactionList(jsonObj);
        mService.enqueue(new Callback<TransactionResponse>() {
            @Override
            public void onResponse(Call<TransactionResponse> call, Response<TransactionResponse> response) {
                loadingProgress.setVisibility(View.GONE);
                    if (response.body().getTransactions().size() == 0) {
                        noDataTV.setVisibility(View.VISIBLE);
                        noDataTV.setText(R.string.no_transcation);
                    } else {
                        productDatasArrayList.addAll(response.body().getTransactions());
                        transactionAdapter.notifyDataSetChanged();
                    }
            }

            @Override
            public void onFailure(Call<TransactionResponse> call, Throwable t) {
                loadingProgress.setVisibility(View.GONE);
                call.cancel();
            }
        });
    }
}

