package com.app.fitsmile.book;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.dentistlist.DoctorListDatas;
import com.app.fitsmile.response.dentistlist.DoctorListPojo;
import com.app.fitsmile.utils.EndlessRecyclerviewScrollListner;
import com.app.fitsmile.utils.LocaleManager;
import com.google.firebase.database.core.utilities.Utilities;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.Timer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchDentistList extends BaseActivity implements SearchDentistAdapter.ItemClickListener, TextWatcher {
    private RecyclerView recyclerSearchDentist;
    private SearchDentistAdapter searchDentistAdapter;
    private TextView mTvNoData;
    private LinearLayoutManager layoutManager;
    ImageView mImageDentistSearch;
    String search;
    EditText mEditDentistSearch;
    String baseUrl;
    boolean isFetchPatientDentistApi = true;
    private Timer timer;
    private AutoCompleteTextView autoCompleteTextView;
    private SearchView searchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_dentist_list);
        LocaleManager.setLocale(this);
        initView();
        setUpToolBar();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.lbl_select_doctor);
    }

    private void initView() {
        recyclerSearchDentist = findViewById(R.id.recycler_search_dentist);
        mTvNoData = findViewById(R.id.no_doctors);
        // autoCompleteTextView =findViewById(R.id.searchAutoComplete);
        searchView = findViewById(R.id.search_view);
        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);
        layoutManager = new LinearLayoutManager(actCon, RecyclerView.VERTICAL, false);
        recyclerSearchDentist.setLayoutManager(layoutManager);
//        if (searchDentistAdapter == null) {
//            searchDentistAdapter = new SearchDentistAdapter(actCon, this, mDocList);
//            recyclerSearchDentist.setAdapter(searchDentistAdapter);
//        } else {
//            searchDentistAdapter.setUpdatedList(mDocList);
//        }

        //autoCompleteTextView.setAdapter(searchDentistAdapter);
        mImageDentistSearch = findViewById(R.id.iv_dentist_name);
        mEditDentistSearch = findViewById(R.id.et_dentist_name);
        mEditDentistSearch.addTextChangedListener(this);

        fetchPatientDentist();

//        if (getIntent() != null) {
//            if (getIntent().getBooleanExtra("isFromBookAppointment", false)) {
//                searchDentistList("", "1");
//            } else {
//                fetchPatientDentist();
//            }
//        }

        //Utilities.hideKeyBoard(this);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String newText) {
                //isSearchOpen = true;
                searchDentistAdapter.getFilter().filter(newText);
                if (searchDentistAdapter.getItemCount() < 1) {
                    mTvNoData.setVisibility(View.VISIBLE);
                    recyclerSearchDentist.setVisibility(View.GONE);
                } else {
                    mTvNoData.setVisibility(View.GONE);
                    recyclerSearchDentist.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(newText)) {
                    // isSearchOpen = false;
                    mTvNoData.setVisibility(View.GONE);
                    recyclerSearchDentist.setVisibility(View.VISIBLE);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // isSearchOpen = true;
                searchDentistAdapter.getFilter().filter(newText);
                if (searchDentistAdapter.getItemCount() < 1) {
                    mTvNoData.setVisibility(View.VISIBLE);
                    recyclerSearchDentist.setVisibility(View.GONE);
                } else {
                    mTvNoData.setVisibility(View.GONE);
                    recyclerSearchDentist.setVisibility(View.VISIBLE);
                }
                if (TextUtils.isEmpty(newText)) {
                    // isSearchOpen = false;
                    mTvNoData.setVisibility(View.GONE);
                    recyclerSearchDentist.setVisibility(View.VISIBLE);
                }

                return false;
            }
        });

        recyclerSearchDentist.addOnScrollListener(new EndlessRecyclerviewScrollListner(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (doctorListPojo != null && !isFetchPatientDentistApi) {
                    if (doctorListPojo.getLimit_reach() != null && doctorListPojo.getLimit_reach().equals("0")) {
                        searchDentistList("", String.valueOf(current_page));
                    }
                }
//                else {
//                    searchDentistList("", String.valueOf(current_page));
//                }

            }
        });


        mImageDentistSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search != null && search.length() > 0) {
                    searchDentistBycode(search);
                }
            }
        });
    }


    @Override
    public void onItemViewProfileClick(DoctorListDatas data) {
        Intent intent = new Intent(actCon, DentistProfile.class);
        intent.putExtra("DENTIST_ID", Utils.getStr(data.getId()));
        startActivity(intent);
    }

    @Override
    public void onItemScheduleNowClick(DoctorListDatas data) {
        Intent intent = new Intent(actCon, VideoAppointmentCheckout.class);
        intent.putExtra("DENTIST_ID", Utils.getStr(data.getId()));
        startActivity(intent);
    }


    ArrayList<DoctorListDatas> mDocList = new ArrayList<>();
    private DoctorListPojo doctorListPojo = null;


    void searchDentistBycode(String searchCode) {
        mDocList.clear();
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("code", searchCode);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DoctorListPojo> mService = mApiService.getExistingDocList(jsonObj);
        mService.enqueue(new Callback<DoctorListPojo>() {
            @Override
            public void onResponse(Call<DoctorListPojo> call, Response<DoctorListPojo> response) {
                Utils.closeProgressDialog();
                doctorListPojo = response.body();
                if (doctorListPojo == null)
                    return;
                if (Utils.getStr(doctorListPojo.getStatus()).equals("1")) {
                    if (doctorListPojo.getDatas().size() > 0) {
                        mTvNoData.setVisibility(View.GONE);
                        mDocList.addAll(doctorListPojo.getDatas());
                        searchDentistAdapter.notifyDataSetChanged();
                    } else {
                        mTvNoData.setVisibility(View.VISIBLE);
                    }

                    /*if (doctorListPojo.getLimit_reach().equalsIgnoreCase("0")){
                        loading = true;
                    }
                    recyclerView.scrollToPosition(arrayList.size());*/

                } else if (Utils.getStr(doctorListPojo.getStatus()).equals("401")) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, doctorListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DoctorListPojo> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                    Utils.showToast(actCon, getString(R.string.please_check_your_network));
                }
                call.cancel();
            }
        });
    }

    void fetchPatientDentist() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        mDocList.clear();
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DoctorListPojo> mService = mApiService.fetchPatientDentistList(jsonObj);
        mService.enqueue(new Callback<DoctorListPojo>() {
            @Override
            public void onResponse(Call<DoctorListPojo> call, Response<DoctorListPojo> response) {
                Utils.closeProgressDialog();
                isFetchPatientDentistApi = true;

                doctorListPojo = response.body();
                if (doctorListPojo == null)
                    return;
                if (Utils.getStr(doctorListPojo.getStatus()).equals("1")) {
                    if (doctorListPojo.getDatas().size() > 0) {
                        baseUrl = doctorListPojo.getBase_url();
                        mTvNoData.setVisibility(View.GONE);
                        mDocList.addAll(doctorListPojo.getDatas());
                       // searchDentistAdapter.notifyDataSetChanged();
                        setDocListAdapter();
                    } else {
                        mTvNoData.setVisibility(View.VISIBLE);
                    }

                } else if (Utils.getStr(doctorListPojo.getStatus()).equals("401")) {
                    Utils.showSessionAlert(actCon);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
                    Utils.showToast(actCon, doctorListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DoctorListPojo> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                    Utils.showToast(actCon, getString(R.string.please_check_your_network));
                }
                call.cancel();
            }
        });
    }

    private void setDocListAdapter(){
        if (searchDentistAdapter == null) {
            searchDentistAdapter = new SearchDentistAdapter(actCon, this, mDocList);
            recyclerSearchDentist.setAdapter(searchDentistAdapter);
        } else {
            searchDentistAdapter.setUpdatedList(mDocList);
        }
    }


    void searchDentistList(String str_value, String pageNo) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        mDocList.clear();
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("category_id", "2"); // 1 is for emergency booking 2 is for scheduled booking
        jsonObj.addProperty("search", str_value);
        jsonObj.addProperty("page_no", pageNo);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DoctorListPojo> mService = mApiService.getOnlineDoctorListByCode(jsonObj);
        mService.enqueue(new Callback<DoctorListPojo>() {
            @Override
            public void onResponse(Call<DoctorListPojo> call, Response<DoctorListPojo> response) {
                Utils.closeProgressDialog();
                isFetchPatientDentistApi = false;
                doctorListPojo = response.body();
                if (doctorListPojo == null)
                    return;
                if (Utils.getStr(doctorListPojo.getStatus()).equals("1")) {
                    if (doctorListPojo.getDatas().size() > 0) {
                        baseUrl = doctorListPojo.getBase_url();
                        mTvNoData.setVisibility(View.GONE);
                        mDocList.addAll(doctorListPojo.getDatas());
                        searchDentistAdapter.notifyDataSetChanged();
                    } else {
                        mTvNoData.setVisibility(View.VISIBLE);
                    }

                } else if (Utils.getStr(doctorListPojo.getStatus()).equals("401")) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, doctorListPojo.getMessage());
                }
            }

            @Override
            public void onFailure(Call<DoctorListPojo> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                    Utils.showToast(actCon, getString(R.string.please_check_your_network));
                }
                call.cancel();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public void afterTextChanged(Editable s) {
        search = s.toString();
        if (s.length() == 0) {
            if (getIntent().getBooleanExtra("isFromBookAppointment", false)) {
                searchDentistList("", "1");
            } else {
                fetchPatientDentist();
            }
        }

//        if (s.length() >= 1) {
//            searchDentistList("", "1");
//        } else if(s.length()==0){
//            fetchPatientDentist();
//        }

    }
}
