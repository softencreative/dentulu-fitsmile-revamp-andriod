package com.app.fitsmile.photoConsultation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.DynamicViewResponse;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetQuestionDataRequest;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetQuestionDataResponse;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetRequest;
import com.app.fitsmile.response.photoConsultancy.AddPhotoConsultancy.InsertWidgetResponse;
import com.app.fitsmile.utils.LocaleManager;
import com.app.fitsmile.utils.NonSwipeableViewPager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static com.app.fitsmile.photoConsultation.Constants.DOCTOR_TYPE;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_CHECK_BOX;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_IMAGE;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_RADIO;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_TEXT_AREA;
import static com.app.fitsmile.photoConsultation.Constants.TYPE_TEXT_INPUT;


public class DentistQuestionsActivity extends BaseActivity {
    private NonSwipeableViewPager vpQuestions;
    private AppCompatButton btnNext;
    private AppCompatButton btnBack;
    private List<StepData> dataList;
    private List<QuestionsFragment> questionsFragmentList;
    private DynamicViewResponse dynamicViewResponse;
    private String TAG = "DentistQuestionsActivity";
    private String strConsultType = "";
    private String strUserID = "";
    private TextView tvNoData;
    private LinearLayout llNextButtonLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dentist_question);
        LocaleManager.setLocale(this);
        setToolBar();
        vpQuestions = findViewById(R.id.vp_questions);
        btnNext = findViewById(R.id.btn_next);
        btnBack = findViewById(R.id.btn_back);
        tvNoData = findViewById(R.id.tv_no_data);
        llNextButtonLayout = findViewById(R.id.ll_next_button_layout);

        strUserID = appPreference.getUserId();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            strConsultType = bundle.getString(DOCTOR_TYPE);
        }

        getFormData();
        setOnClickListeners();

    }


    private void setToolBar() {
        //setuptool bar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.dash_ask_my_dentist));
        toolbar.setNavigationOnClickListener((view) -> {
            backButtonClick();
        });
    }

    private void setOnClickListeners() {
        btnNext.setOnClickListener((view) -> {
            if (dataList != null && vpQuestions.getCurrentItem() < dataList.size() - 1) {
                if (questionsFragmentList.get(vpQuestions.getCurrentItem()).checkValidations()) {
                    vpQuestions.setCurrentItem(vpQuestions.getCurrentItem() + 1, true);
                }
            } else if (dataList != null && vpQuestions.getCurrentItem() == dataList.size() - 1) {
                boolean allowToProceed=true;
                for (int i = 0; i < dataList.size(); i++) {
                    for (int j = 0; j < dataList.get(i).getQuestions().size(); j++) {
                        if (dataList.get(i).getQuestions().get(j).getRequired().equalsIgnoreCase("1")) {
                            if (!dataList.get(i).getQuestions().get(j).isSelected()) {
                                allowToProceed=false;
                                Toast.makeText(actCon, "Por favor seleccione los campos obligatorios", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            else {
                                allowToProceed=true;
                            }
                        }
                    }

                }
                if(allowToProceed){
                    insertWidget();
                }

            }
        });
        btnBack.setOnClickListener((view) -> {
            backButtonClick();
        });
    }

    private void backButtonClick() {
        if (dataList != null && vpQuestions.getCurrentItem() > 0) {
            vpQuestions.setCurrentItem(vpQuestions.getCurrentItem() - 1, true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (questionsFragmentList != null && vpQuestions != null) {
            questionsFragmentList.get(vpQuestions.getCurrentItem()).onActivityResult(requestCode, resultCode, data);
        }
    }

    private void insertWidgetQuestionData(InsertWidgetResponse insertWidgetResponse) {
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        InsertWidgetQuestionDataRequest insertWidgetQuestionDataRequest = new InsertWidgetQuestionDataRequest();
        insertWidgetQuestionDataRequest.setPatient_id(appPreference.getUserId());
        insertWidgetQuestionDataRequest.setWidget_id(insertWidgetResponse.getWidget_id());
        List<InsertWidgetQuestionDataRequest.Data> dataArrayList = new ArrayList<>();
        for (int i = 0; i < dataList.size(); i++) {
            InsertWidgetQuestionDataRequest.Data insertWidgetRequestData = new InsertWidgetQuestionDataRequest.Data();
            insertWidgetRequestData.setId(dataList.get(i).getId());
            List<InsertWidgetQuestionDataRequest.Questions> questionsArrayList = new ArrayList<>();
            for (int j = 0; j < dataList.get(i).getQuestions().size(); j++) {
                InsertWidgetQuestionDataRequest.Questions jsonObjectQuestion = new InsertWidgetQuestionDataRequest.Questions();
                jsonObjectQuestion.setId(dataList.get(i).getQuestions().get(j).getId());
                List<InsertWidgetQuestionDataRequest.Value> valueArrayList = new ArrayList<>();
                switch (dataList.get(i).getQuestions().get(j).getType()) {
                    case TYPE_TEXT_AREA:
                    case TYPE_TEXT_INPUT:
                        InsertWidgetQuestionDataRequest.Value value = new InsertWidgetQuestionDataRequest.Value();
                        value.setValue(dataList.get(i).getQuestions().get(j).getInputData());
                        valueArrayList.add(value);
                        break;
                    case TYPE_CHECK_BOX:
                        for (OptionValue optionValue : dataList.get(i).getQuestions().get(j).getValue()) {
                            if (optionValue.isChecked()) {
                                value = new InsertWidgetQuestionDataRequest.Value();
                                value.setValue(optionValue.getTitle());
                                valueArrayList.add(value);
                            }
                        }
                        break;
                    case TYPE_RADIO:
                        for (OptionValue optionValue : dataList.get(i).getQuestions().get(j).getValue()) {
                            if (optionValue.isChecked()) {
                                value = new InsertWidgetQuestionDataRequest.Value();
                                value.setValue(optionValue.getTitle());
                                valueArrayList.add(value);
                                break;
                            }
                        }
                        break;
                    case TYPE_IMAGE:
                        value = new InsertWidgetQuestionDataRequest.Value();
                        value.setValue("data:image/png;base64," + dataList.get(i).getQuestions().get(j).getBase64Encoded());
                        valueArrayList.add(value);
                        break;
                }
                jsonObjectQuestion.setValue(valueArrayList);
                questionsArrayList.add(jsonObjectQuestion);
            }
            insertWidgetRequestData.setQuestions(questionsArrayList);
            dataArrayList.add(insertWidgetRequestData);
        }
        insertWidgetQuestionDataRequest.setData(dataArrayList);
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        insertWidgetQuestionDataRequest.setLanguage(currentLanguage);

        Gson gson = new Gson();
        String jsonInString = gson.toJson(insertWidgetQuestionDataRequest);
        Call<InsertWidgetQuestionDataResponse> mService = mApiService.insertWidgetQuestionData(insertWidgetQuestionDataRequest);
        mService.enqueue(new Callback<InsertWidgetQuestionDataResponse>() {
            @Override
            public void onResponse(Call<InsertWidgetQuestionDataResponse> call, Response<InsertWidgetQuestionDataResponse> response) {
                Utils.closeProgressDialog();
                InsertWidgetQuestionDataResponse insertWidgetQuestionDataResponse = response.body();
                if (insertWidgetQuestionDataResponse != null) {
                    if (insertWidgetQuestionDataResponse.getStatus() == 1) {
                        Intent intent = new Intent(DentistQuestionsActivity.this, PatientProfileCompleteActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (insertWidgetQuestionDataResponse.getStatus() == 401) {
                        Utils.showSessionAlert(DentistQuestionsActivity.this);
                    } else {
                        Toast.makeText(DentistQuestionsActivity.this, insertWidgetQuestionDataResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(DentistQuestionsActivity.this,getResources().getString(R.string.something_went_wrong), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onFailure(Call<InsertWidgetQuestionDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                Log.e("data ", "3 : " + t.getMessage());

            }
        });
    }
                                                                                                                                                                    
    @Override
    public void onBackPressed() {
        backButtonClick();
    }

    private void insertWidget() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        InsertWidgetRequest insertWidgetRequest = new InsertWidgetRequest();
        insertWidgetRequest.setPatient_id(appPreference.getUserId());
        insertWidgetRequest.setLanguage(currentLanguage);
        Call<InsertWidgetResponse> mService = mApiService.insertWidget(insertWidgetRequest);
        mService.enqueue(new Callback<InsertWidgetResponse>() {
            @Override
            public void onResponse(Call<InsertWidgetResponse> call, Response<InsertWidgetResponse> response) {
                final InsertWidgetResponse insertWidgetResponse = response.body();
                if (insertWidgetResponse != null && insertWidgetResponse.getStatus() == 401) {
                    Utils.closeProgressDialog();
                    Utils.showSessionAlert(DentistQuestionsActivity.this);
                    return;
                }
                if (insertWidgetResponse != null && insertWidgetResponse.getWidget_id() != null) {
                    insertWidgetQuestionData(insertWidgetResponse);
                } else {
                    Utils.showToast(DentistQuestionsActivity.this, "Something went wrong");
                    Utils.closeProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<InsertWidgetResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });
    }


    private void getFormData() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("language", currentLanguage);
        Call<DynamicViewResponse> mService = mApiService.getFormData(jsonObj);
        mService.enqueue(new Callback<DynamicViewResponse>() {
            @Override
            public void onResponse(Call<DynamicViewResponse> call, Response<DynamicViewResponse> response) {
                Utils.closeProgressDialog();
                final DynamicViewResponse dynamicViewResponse = response.body();
                questionsFragmentList = new ArrayList<>();
                ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
                if (dynamicViewResponse != null && dynamicViewResponse.getStatus() == 401) {
                    setEmptyDataUI(true);
                    Utils.showSessionAlert(DentistQuestionsActivity.this);
                    return;
                }
                if (dynamicViewResponse != null && dynamicViewResponse.getData() != null) {
                    for (StepData stepData : dynamicViewResponse.getData()) {
                        for (Question question : stepData.getQuestions()) {
                            if (question.getType().equals(TYPE_TEXT_INPUT)) {
                                stepData.getQuestions().remove(question);
                            }
                        }
                        if (stepData.getQuestions().size() <= 0) {
                            dynamicViewResponse.getData().remove(stepData);
                        }
                    }
                    dataList = dynamicViewResponse.getData();
                    for (StepData stepData : dynamicViewResponse.getData()) {
                        QuestionsFragment questionsFragment = new QuestionsFragment(stepData);
                        questionsFragmentList.add(questionsFragment);
                        viewPagerAdapter.addFragement(questionsFragment, "");
                    }
                    setEmptyDataUI(questionsFragmentList.isEmpty());
                    vpQuestions.setOffscreenPageLimit(dynamicViewResponse.getData().size());
                } else {
                    setEmptyDataUI(true);
                }
                vpQuestions.setAdapter(viewPagerAdapter);
            }

            @Override
            public void onFailure(Call<DynamicViewResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                setEmptyDataUI(true);
            }
        });
    }


    private void setEmptyDataUI(boolean isSetEmptyData) {
        if (isSetEmptyData) {
            tvNoData.setVisibility(View.VISIBLE);
            llNextButtonLayout.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            llNextButtonLayout.setVisibility(View.VISIBLE);
        }
    }


}
