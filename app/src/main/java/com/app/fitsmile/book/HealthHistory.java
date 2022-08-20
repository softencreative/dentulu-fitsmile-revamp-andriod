package com.app.fitsmile.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.appointmentlist.HealthHistoryQuestion;
import com.app.fitsmile.response.tempschedule.ReScheduleBookingpojoo;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HealthHistory extends BaseActivity {
    String user_id;
    int pageCount = 0;
    int curPage = 0;
    Activity activity;
    ScrollView mainView;
    List<LinearLayout> pages;
    List<LinearLayout> questionLayout;
    TextView btnNextStep, btnBackStep, btnUpdateHistory;
    HealthHistoryQuestion question;
    List<HealthHistoryQuestion.QuestionItem> qitem;
    String strBookType;
    ArrayList<Boolean> flags;
    ArrayList<String> titles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_history);
        LocaleManager.setLocale(this);
        activity = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(R.string.title_health_history);

        if (getIntent() != null && getIntent().getExtras() != null) {

            strBookType = getIntent().getStringExtra("book_type");
        }

        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        flags = new ArrayList<>();
        titles = new ArrayList<>();
        strBookType = getIntent().getStringExtra("book_type");
        user_id = appPreference.getUserId();
        mainView = findViewById(R.id.mainContainer);
        btnNextStep = findViewById(R.id.btnNextStep);
        btnBackStep = findViewById(R.id.btnBackStep);
        btnUpdateHistory = findViewById(R.id.btnUpdateHistory);
        pages = new ArrayList<LinearLayout>();
        questionLayout = new ArrayList<>();
        loadQuestion();
        btnNextStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curPage < pages.size() - 1) {
                    if (!checkForChosenAnswer(curPage) && !flags.get(curPage)) {
                        Utils.showToast(HealthHistory.this, getResources().getString(R.string.choose_option_to_proceed));
                        return;
                    }
                    curPage++;
                    showPage(curPage);
                }
                if (curPage == (pages.size() - 1)) {
                    btnNextStep.setVisibility(View.GONE);
                    btnUpdateHistory.setVisibility(View.VISIBLE);
                }
                btnBackStep.setVisibility(View.VISIBLE);
            }
        });

        btnBackStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curPage > 0) {
                    curPage--;
                    showPage(curPage);
                    btnUpdateHistory.setVisibility(View.GONE);
                    btnNextStep.setVisibility(View.VISIBLE);
                }
                if (curPage == 0) {
                    btnBackStep.setVisibility(View.GONE);
                }
            }
        });

        btnUpdateHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!checkForChosenAnswer(curPage) && !flags.get(curPage)) {
                    Utils.showToast(HealthHistory.this, getResources().getString(R.string.choose_option_to_proceed));
                    return;
                }
                processAnswer();
                if (strBookType.equalsIgnoreCase("new_emergency")) {
                    Intent intent = new Intent();
                    intent.putExtra("status", "true");
                    setResult(200, intent);
                    //intent.putExtra("str_message","Health History (Filled)");
                    updateHealthRecord();
                }
            }
        });
    }

    public Boolean checkForChosenAnswer(int pageNumber) {
        Boolean result = false;
        for (HealthHistoryQuestion.OptionItem item : qitem.get(pageNumber).getOptions()) {
            if (item.getType().equals("label") && item.getSelected().equals("1")) {
                result = true;
                break;
            }
            if (item.getType().equals("text") && item.getSelected().length() > 0) {
                result = true;
                break;
            }
        }
        return result;
    }

    public void loadQuestion() {
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", user_id);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<HealthHistoryQuestion> mService = mApiService.getHealthRecordQuestions(jsonObj);
        mService.enqueue(new Callback<HealthHistoryQuestion>() {
            @Override
            public void onResponse(Call<HealthHistoryQuestion> call, Response<HealthHistoryQuestion> response) {
                if (response.body().getStatus().equals(AppConstants.ONE)) {
                    question = response.body();
                    qitem = question.getData();
                    pageCount = qitem.size();
                    LinearLayout pageLayout = new LinearLayout(HealthHistory.this);
                    pageLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                    pageLayout.setOrientation(LinearLayout.VERTICAL);
                    for (int i = 0; i < qitem.size(); i++) {
                        flags.add(false);
                        titles.add(qitem.get(i).getPage());
                        LinearLayout parent = new LinearLayout(HealthHistory.this);
                        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        parent.setOrientation(LinearLayout.VERTICAL);
                        int id = View.generateViewId();
                        parent.setId(id);
                        pages.add(parent);
                        TextView tv1 = new TextView(HealthHistory.this);
                        tv1.setText(qitem.get(i).getPage());
                        Utils.setBoldText(HealthHistory.this, tv1);
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        );
                        params.setMargins(0, 15, 0, 10);
                        tv1.setLayoutParams(params);
                        tv1.setTextColor(getResources().getColor(R.color.colorPrimary));
                        parent.addView(tv1);
                        CheckBox chkBox = new CheckBox(HealthHistory.this);
                        HealthHistoryQuestion.PageOption pgOption = qitem.get(i).getPage_option();
                        chkBox.setText(pgOption.getCode());
                        final int k = i;
                        chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked) {
                                    flags.set(k, true);
                                    questionLayout.get(k).setVisibility(View.GONE);
                                } else {
                                    flags.set(k, false);
                                    questionLayout.get(k).setVisibility(View.VISIBLE);
                                }

                            }
                        });
                        parent.addView(chkBox);
                        pageLayout.addView(parent);
                        LinearLayout questionLay = new LinearLayout(HealthHistory.this);
                        questionLay.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        questionLay.setOrientation(LinearLayout.VERTICAL);
                        for (HealthHistoryQuestion.OptionItem item : qitem.get(i).getOptions()) {
                            if (item.getCategory() == null) {
                                item.setCategory("1");
                            }
                            addWidget(questionLay, item.getType(), item.getText(), i, item.getCategory());
                        }
                        questionLayout.add(questionLay);
                        parent.addView(questionLay);
                    }

                    mainView.addView(pageLayout);
                    Utils.closeProgressDialog();
                    showPage(0);
                } else if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(HealthHistory.this);
                }


            }

            @Override
            public void onFailure(Call<HealthHistoryQuestion> call, Throwable t) {
                Log.e("RESPONSE", t.getMessage());
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    public void showPage(int i) {
        for (int j = 0; j < pages.size(); j++) {
            if (j == i) {
                pages.get(j).setVisibility(View.VISIBLE);
            } else {
                pages.get(j).setVisibility(View.GONE);
            }
        }
        curPage = i;
        //getSupportActionBar().setTitle("Step " + (i+1));
        getSupportActionBar().setTitle(titles.get(i));
    }

    public void processAnswer() {
        Utils.allergiesList.clear();
        Utils.otherAllergiesList.clear();
        Utils.medicationsList.clear();
        Utils.diagnosedList.clear();
        Utils.medicalProceduresList.clear();
        Utils.memberDiagnosedList.clear();
        for (int i = 0; i < qitem.size(); i++) {
            for (HealthHistoryQuestion.OptionItem item : qitem.get(i).getOptions()) {
                if (i == 0 && !flags.get(i)) {   //All Allergies & Other Allergies
                    if (item.getType().equals("label") && item.getSelected().equals("1") && item.getCategory().equals("1")) {
                        Utils.allergiesList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("1")) {
                        Utils.allergiesList.add(item.getSelected());
                    }
                    if (item.getType().equals("label") && item.getSelected().equals("1") && item.getCategory().equals("2")) {
                        Utils.otherAllergiesList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("2")) {
                        Utils.otherAllergiesList.add(item.getSelected());
                    }
                } else if (i == 1 && !flags.get(i)) {   //Medication List
                    if (item.getType().equals("label") && item.getSelected().equals("1")) {
                        Utils.medicationsList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("1")) {
                        Utils.medicationsList.add(item.getSelected());
                    }
                } else if (i == 2 && !flags.get(i)) {   //Diagnosed List
                    if (item.getType().equals("label") && item.getSelected().equals("1")) {
                        Utils.diagnosedList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("1")) {
                        Utils.diagnosedList.add(item.getSelected());
                    }
                } else if (i == 3 && !flags.get(i)) {   //MedicalProcedures List
                    if (item.getType().equals("label") && item.getSelected().equals("1")) {
                        Utils.medicalProceduresList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("1")) {
                        Utils.medicalProceduresList.add(item.getSelected());
                    }
                } else if (i == 4 && !flags.get(i)) {   //Member Diagnosed List
                    if (item.getType().equals("label") && item.getSelected().equals("1")) {
                        Utils.memberDiagnosedList.add(item.getText());
                    }
                    if (item.getType().equals("text") && item.getSelected().length() > 0 && item.getCategory().equals("1")) {
                        Utils.memberDiagnosedList.add(item.getSelected());
                    }
                }
            }
        }
    }

    public void addWidget(LinearLayout view, String widgetType, String text, int pageNumber, String category) {
        if (widgetType.equals("info")) {
            TextView tv1 = new TextView(HealthHistory.this);
            tv1.setText(text);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 35, 0, 10);
            Utils.setBoldText(HealthHistory.this, tv1);
            tv1.setLayoutParams(params);
            tv1.setTextColor(getResources().getColor(R.color.colorPrimary));
            view.addView(tv1);
        } else if (widgetType.equals("label")) {
            CheckBox chkBox = new CheckBox(HealthHistory.this);
            chkBox.setText(text);
            chkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        for (HealthHistoryQuestion.OptionItem item : qitem.get(pageNumber).getOptions()) {
                            if (item.getText().equals(chkBox.getText()) && item.getCategory().equals(category)) {
                                item.setSelected("1");
                            }
                        }
                    } else {
                        for (HealthHistoryQuestion.OptionItem item : qitem.get(pageNumber).getOptions()) {
                            if (item.getText().equals(chkBox.getText()) && item.getCategory().equals(category)) {
                                item.setSelected("0");
                            }
                        }
                    }

                }
            });
            view.addView(chkBox);
        } else if (widgetType.equals("text")) {
            EditText editText = new EditText(HealthHistory.this);
            editText.setHint(text);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    for (HealthHistoryQuestion.OptionItem item : qitem.get(pageNumber).getOptions()) {
                        if (item.getText().equals(editText.getHint()) && item.getCategory().equals(category)) {
                            item.setSelected(editText.getText().toString());
                        }
                    }
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start,
                                              int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start,
                                          int before, int count) {

                }
            });
            view.addView(editText);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (curPage == 0) {
                    finish();
                } else {
                    if (curPage > 0) {
                        curPage--;
                        showPage(curPage);
                        btnUpdateHistory.setVisibility(View.GONE);
                        btnNextStep.setVisibility(View.VISIBLE);
                    }
                    if (curPage == 0) {
                        btnBackStep.setVisibility(View.GONE);
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    void updateHealthRecord() {
        btnUpdateHistory.setVisibility(View.GONE);
        Utils.openProgressDialog(activity);
        JsonObject jsonObj = new JsonObject();

        JSONArray jsonObject1 = new JSONArray(Utils.allergiesList);
        JSONArray jsonObject2 = new JSONArray(Utils.otherAllergiesList);
        JSONArray jsonObject3 = new JSONArray(Utils.medicationsList);
        JSONArray jsonObject4 = new JSONArray(Utils.diagnosedList);
        JSONArray jsonObject5 = new JSONArray(Utils.medicalProceduresList);
        JSONArray jsonObject6 = new JSONArray(Utils.memberDiagnosedList);

        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("appointment_id", "0");
        jsonObj.addProperty("allergies", String.valueOf(jsonObject1));
        jsonObj.addProperty("indicate_other_allergies", String.valueOf(jsonObject2));
        jsonObj.addProperty("medications", String.valueOf(jsonObject3));
        jsonObj.addProperty("diagnosed", String.valueOf(jsonObject4));
        jsonObj.addProperty("medical_procedures", String.valueOf(jsonObject5));
        jsonObj.addProperty("family_member_diagnosed", String.valueOf(jsonObject6));
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<ReScheduleBookingpojoo> mService = mApiService.getUpdateHealthHistory(jsonObj);
        mService.enqueue(new Callback<ReScheduleBookingpojoo>() {
            @Override
            public void onResponse(Call<ReScheduleBookingpojoo> call, Response<ReScheduleBookingpojoo> response) {
                ReScheduleBookingpojoo patientListPojo = response.body();
                Utils.closeProgressDialog();
                if (patientListPojo != null) {
                    if (Utils.getStr(patientListPojo.getStatus()).equals("1")) {
                        Utils.showToast(activity, patientListPojo.getMessage());
                        finish();
                    } else if (Utils.getStr(patientListPojo.getStatus()).equals("401")) {
                        Utils.showSessionAlert(activity);
                        btnUpdateHistory.setVisibility(View.VISIBLE);
                    } else {
                        Utils.showToast(activity, patientListPojo.getMessage());
                        btnUpdateHistory.setVisibility(View.VISIBLE);
                    }
                }


            }

            @Override
            public void onFailure(Call<ReScheduleBookingpojoo> call, Throwable t) {
                Utils.allergiesList.clear();
                Utils.otherAllergiesList.clear();
                Utils.medicationsList.clear();
                Utils.diagnosedList.clear();
                Utils.medicalProceduresList.clear();
                Utils.memberDiagnosedList.clear();
                Utils.closeProgressDialog();
                call.cancel();
                btnUpdateHistory.setVisibility(View.VISIBLE);
            }
        });
    }

}