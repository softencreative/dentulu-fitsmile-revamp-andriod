package com.app.fitsmile.book;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.utils.LocaleManager;

public class HealthRecordActivity extends BaseActivity implements View.OnClickListener {

    private LinearLayout llAllergies;
    private CheckBox cbAllergiesType1, cbAllergiesType2, cbAllergiesType3, cbAllergiesType4, cbAllergiesType5,
            cbAllergiesType6, cbAllergiesType7, cbAllergiesType8, cbAllergiesType9, cbAllergiesType10, cbAllergiesType11,
            cbAllergiesType12, cbAllergiesType13, cbAllergiesType14, cbAllergiesType15, cbAllergiesType16, cbAllergiesType17,
            cbAllergiesTypes1, cbAllergiesTypes2, cbAllergiesTypes3, cbAllergiesTypes4,
            cbAllergiesTypes5, cbAllergiesTypes6;
    private EditText etAllergyOther, etAllergyOthers;

    private LinearLayout llMedications;
    private CheckBox cbMedicationsType1, cbMedicationsType2, cbMedicationsType3, cbMedicationsType4, cbMedicationsType5,
            cbMedicationsType6, cbMedicationsType7, cbMedicationsType8, cbMedicationsType9, cbMedicationsType10, cbMedicationsType11,
            cbMedicationsType12, cbMedicationsType13, cbMedicationsType14, cbMedicationsType15, cbMedicationsType16, cbMedicationsType17;
    private EditText etMedicationsOther;

    private LinearLayout llMedcondition;
    private CheckBox cbMedconditionType1, cbMedconditionType2, cbMedconditionType3, cbMedconditionType4,
            cbMedconditionType5, cbMedconditionType6, cbMedconditionType7, cbMedconditionType8, cbMedconditionType9,
            cbMedconditionType10, cbMedconditionType11, cbMedconditionType12;
    private EditText etMedconditionOther;

    private LinearLayout llMedicalProcedures;
    private CheckBox cbMedicalProceduresType1, cbMedicalProceduresType2, cbMedicalProceduresType3, cbMedicalProceduresType4,
            cbMedicalProceduresType5, cbMedicalProceduresType6, cbMedicalProceduresType7, cbMedicalProceduresType8,
            cbMedicalProceduresType9, cbMedicalProceduresType10, cbMedicalProceduresType11;
    private EditText etMedicalProceduresOther;

    private LinearLayout llMemberDiagnosed;
    private CheckBox cbMemberDiagnosedType1, cbMemberDiagnosedType2, cbMemberDiagnosedType3, cbMemberDiagnosedType4,
            cbMemberDiagnosedType5, cbMemberDiagnosedType6, cbMemberDiagnosedType7, cbMemberDiagnosedType8,
            cbMemberDiagnosedType9, cbMemberDiagnosedType10, cbMemberDiagnosedType11, cbMemberDiagnosedType12;
    private EditText etMemberDiagnosedOther;

    private LinearLayout llStep1, llStep2, llStep3, llStep4, llStep5;
    private CheckBox switchTouchID1, switchTouchID2, switchTouchID3, switchTouchID4, switchTouchID5;
    private TextView btnBackStep1, btnBackStep2, btnBackStep3, btnBackStep4, btnBackStep5;
    private TextView btnNextStep1, btnNextStep2, btnNextStep3, btnNextStep4, btnNextStep5;

    ScrollView scrollView;
    String strBookType="";
    boolean flagStep1 =false;
    boolean flagStep2 =false;
    boolean flagStep3 =false;
    boolean flagStep4 =false;
    boolean flagStep5 =false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_record);
        LocaleManager.setLocale(this);
        setUpToolBar();

        initView();
    }

    private void initView() {
        scrollView = findViewById(R.id.scrollView);

        llAllergies = findViewById(R.id.ll_allergies);
        cbAllergiesType1 = findViewById(R.id.cb_allergies_type1);
        cbAllergiesType2 = findViewById(R.id.cb_allergies_type2);
        cbAllergiesType3 = findViewById(R.id.cb_allergies_type3);
        cbAllergiesType4 = findViewById(R.id.cb_allergies_type4);
        cbAllergiesType5 = findViewById(R.id.cb_allergies_type5);
        cbAllergiesType6 = findViewById(R.id.cb_allergies_type6);
        cbAllergiesType7 = findViewById(R.id.cb_allergies_type7);
        cbAllergiesType8 = findViewById(R.id.cb_allergies_type8);
        cbAllergiesType9 = findViewById(R.id.cb_allergies_type9);
        cbAllergiesType10 = findViewById(R.id.cb_allergies_type10);
        cbAllergiesType11 = findViewById(R.id.cb_allergies_type11);
        cbAllergiesType12 = findViewById(R.id.cb_allergies_type12);
        cbAllergiesType13 = findViewById(R.id.cb_allergies_type13);
        cbAllergiesType14 = findViewById(R.id.cb_allergies_type14);
        cbAllergiesType15 = findViewById(R.id.cb_allergies_type15);
        cbAllergiesType16 = findViewById(R.id.cb_allergies_type16);
        cbAllergiesType17 = findViewById(R.id.cb_allergies_type17);
        cbAllergiesTypes1 = findViewById(R.id.cb_allergies_types1);
        cbAllergiesTypes2 = findViewById(R.id.cb_allergies_types2);
        cbAllergiesTypes3 = findViewById(R.id.cb_allergies_types3);
        cbAllergiesTypes4 = findViewById(R.id.cb_allergies_types4);
        cbAllergiesTypes5 = findViewById(R.id.cb_allergies_types5);
        etAllergyOther = findViewById(R.id.et_allergy_other);
        etAllergyOthers = findViewById(R.id.et_allergy_others);

        llMedications = findViewById(R.id.ll_medications);
        cbMedicationsType1 = findViewById(R.id.cb_medications_type1);
        cbMedicationsType2 = findViewById(R.id.cb_medications_type2);
        cbMedicationsType3 = findViewById(R.id.cb_medications_type3);
        cbMedicationsType4 = findViewById(R.id.cb_medications_type4);
        cbMedicationsType5 = findViewById(R.id.cb_medications_type5);
        cbMedicationsType6 = findViewById(R.id.cb_medications_type6);
        cbMedicationsType7 = findViewById(R.id.cb_medications_type7);
        cbMedicationsType8 = findViewById(R.id.cb_medications_type8);
        cbMedicationsType9 = findViewById(R.id.cb_medications_type9);
        cbMedicationsType10 = findViewById(R.id.cb_medications_type10);
        cbMedicationsType11 = findViewById(R.id.cb_medications_type11);
        cbMedicationsType12 = findViewById(R.id.cb_medications_type12);
        cbMedicationsType13 = findViewById(R.id.cb_medications_type13);
        cbMedicationsType14 = findViewById(R.id.cb_medications_type14);
        cbMedicationsType15 = findViewById(R.id.cb_medications_type15);
        cbMedicationsType16 = findViewById(R.id.cb_medications_type16);
        cbMedicationsType17 = findViewById(R.id.cb_medications_type17);
        etMedicationsOther = findViewById(R.id.et_medications_other);

        llMedcondition = findViewById(R.id.ll_medcondition);
        cbMedconditionType1 = findViewById(R.id.cb_medcondition_type1);
        cbMedconditionType2 = findViewById(R.id.cb_medcondition_type2);
        cbMedconditionType3 = findViewById(R.id.cb_medcondition_type3);
        cbMedconditionType4 = findViewById(R.id.cb_medcondition_type4);
        cbMedconditionType5 = findViewById(R.id.cb_medcondition_type5);
        cbMedconditionType6 = findViewById(R.id.cb_medcondition_type6);
        cbMedconditionType7 = findViewById(R.id.cb_medcondition_type7);
        cbMedconditionType8 = findViewById(R.id.cb_medcondition_type8);
        cbMedconditionType9 = findViewById(R.id.cb_medcondition_type9);
        cbMedconditionType10 = findViewById(R.id.cb_medcondition_type10);
        cbMedconditionType11 = findViewById(R.id.cb_medcondition_type11);
        cbMedconditionType12 = findViewById(R.id.cb_medcondition_type12);
        etMedconditionOther = findViewById(R.id.et_medcondition_other);

        llMedicalProcedures = findViewById(R.id.ll_medical_procedures);
        cbMedicalProceduresType1 = findViewById(R.id.cb_medical_procedures_type1);
        cbMedicalProceduresType2 = findViewById(R.id.cb_medical_procedures_type2);
        cbMedicalProceduresType3 = findViewById(R.id.cb_medical_procedures_type3);
        cbMedicalProceduresType4 = findViewById(R.id.cb_medical_procedures_type4);
        cbMedicalProceduresType5 = findViewById(R.id.cb_medical_procedures_type5);
        cbMedicalProceduresType6 = findViewById(R.id.cb_medical_procedures_type6);
        cbMedicalProceduresType7 = findViewById(R.id.cb_medical_procedures_type7);
        cbMedicalProceduresType8 = findViewById(R.id.cb_medical_procedures_type8);
        cbMedicalProceduresType9 = findViewById(R.id.cb_medical_procedures_type9);
        cbMedicalProceduresType10 = findViewById(R.id.cb_medical_procedures_type10);
        cbMedicalProceduresType11 = findViewById(R.id.cb_medical_procedures_type11);
        etMedicalProceduresOther = findViewById(R.id.et_medical_procedures_other);

        llMemberDiagnosed = findViewById(R.id.ll_member_diagnosed);
        cbMemberDiagnosedType1 = findViewById(R.id.cb_member_diagnosed_type1);
        cbMemberDiagnosedType2 = findViewById(R.id.cb_member_diagnosed_type2);
        cbMemberDiagnosedType3 = findViewById(R.id.cb_member_diagnosed_type3);
        cbMemberDiagnosedType4 = findViewById(R.id.cb_member_diagnosed_type4);
        cbMemberDiagnosedType5 = findViewById(R.id.cb_member_diagnosed_type5);
        cbMemberDiagnosedType6 = findViewById(R.id.cb_member_diagnosed_type6);
        cbMemberDiagnosedType7 = findViewById(R.id.cb_member_diagnosed_type7);
        cbMemberDiagnosedType8 = findViewById(R.id.cb_member_diagnosed_type8);
        cbMemberDiagnosedType9 = findViewById(R.id.cb_member_diagnosed_type9);
        cbMemberDiagnosedType10 = findViewById(R.id.cb_member_diagnosed_type10);
        cbMemberDiagnosedType11 = findViewById(R.id.cb_member_diagnosed_type11);
        cbMemberDiagnosedType12 = findViewById(R.id.cb_member_diagnosed_type12);
        etMemberDiagnosedOther = findViewById(R.id.et_member_diagnosed_other);

        switchTouchID1 = findViewById(R.id.switchTouchID1);
        switchTouchID2 = findViewById(R.id.switchTouchID2);
        switchTouchID3 = findViewById(R.id.switchTouchID3);
        switchTouchID4 = findViewById(R.id.switchTouchID4);
        switchTouchID5 = findViewById(R.id.switchTouchID5);

        btnBackStep1 = findViewById(R.id.btnBackStep1);
        btnBackStep2 = findViewById(R.id.btnBackStep2);
        btnBackStep3 = findViewById(R.id.btnBackStep3);
        btnBackStep4 = findViewById(R.id.btnBackStep4);
        btnBackStep5 = findViewById(R.id.btnBackStep5);

        btnNextStep1 = findViewById(R.id.btnNextStep1);
        btnNextStep2 = findViewById(R.id.btnNextStep2);
        btnNextStep3 = findViewById(R.id.btnNextStep3);
        btnNextStep4 = findViewById(R.id.btnNextStep4);
        btnNextStep5 = findViewById(R.id.btnNextStep5);

        llStep1 = findViewById(R.id.llStep1);
        llStep2 = findViewById(R.id.llStep2);
        llStep3 = findViewById(R.id.llStep3);
        llStep4 = findViewById(R.id.llStep4);
        llStep5 = findViewById(R.id.llStep5);

        btnBackStep1.setOnClickListener(this);
        btnBackStep2.setOnClickListener(this);
        btnBackStep3.setOnClickListener(this);
        btnBackStep4.setOnClickListener(this);
        btnBackStep5.setOnClickListener(this);

        btnNextStep1.setOnClickListener(this);
        btnNextStep2.setOnClickListener(this);
        btnNextStep3.setOnClickListener(this);
        btnNextStep4.setOnClickListener(this);
        btnNextStep5.setOnClickListener(this);

        switchTouchID1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llAllergies.setVisibility(View.GONE);
                    flagStep1=true;
                    unCheckStep1All();
                } else {
                    llAllergies.setVisibility(View.VISIBLE);
                    flagStep1=false;
                }
            }
        });

        switchTouchID2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llMedications.setVisibility(View.GONE);
                    flagStep2=true;
                    unCheckStep2All();
                } else {
                    llMedications.setVisibility(View.VISIBLE);
                    flagStep2=false;
                }
            }
        });

        switchTouchID3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llMedcondition.setVisibility(View.GONE);
                    flagStep3=true;
                    unCheckStep3All();
                } else {
                    llMedcondition.setVisibility(View.VISIBLE);
                    flagStep3=false;
                }
            }
        });

        switchTouchID4.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llMedicalProcedures.setVisibility(View.GONE);
                    flagStep4=true;
                    unCheckStep4All();
                } else {
                    llMedicalProcedures.setVisibility(View.VISIBLE);
                    flagStep4=false;
                }
            }
        });

        switchTouchID5.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    llMemberDiagnosed.setVisibility(View.GONE);
                    flagStep5=true;
                    unCheckStep5All();
                } else {
                    llMemberDiagnosed.setVisibility(View.VISIBLE);
                    flagStep5=false;
                }
            }
        });

        cbAllergiesType1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType6.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType7.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType8.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType9.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType10.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType11.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType12.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType13.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType14.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType15.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType16.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesType17.setOnCheckedChangeListener(new myCheckBoxChnageClicker());

        cbAllergiesTypes1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesTypes2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesTypes3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesTypes4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbAllergiesTypes5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());

        cbMedicationsType1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType6.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType7.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType8.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType9.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType10.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType11.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType12.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType13.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType14.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType15.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType16.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicationsType17.setOnCheckedChangeListener(new myCheckBoxChnageClicker());

        cbMedconditionType1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType6.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType7.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType8.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType9.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType10.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType11.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedconditionType12.setOnCheckedChangeListener(new myCheckBoxChnageClicker());

        cbMedicalProceduresType1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType6.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType7.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType8.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType9.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType10.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMedicalProceduresType11.setOnCheckedChangeListener(new myCheckBoxChnageClicker());

        cbMemberDiagnosedType1.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType2.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType3.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType4.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType5.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType6.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType7.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType8.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType9.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType10.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType11.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
        cbMemberDiagnosedType12.setOnCheckedChangeListener(new myCheckBoxChnageClicker());
    }


    class myCheckBoxChnageClicker implements CheckBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                if(buttonView==cbAllergiesType1) { Utils.allergiesList.add(getString(R.string.hr_allergies_type1)); }
                if(buttonView==cbAllergiesType2) { Utils.allergiesList.add(getString(R.string.hr_allergies_type2)); }
                if(buttonView==cbAllergiesType3) { Utils.allergiesList.add(getString(R.string.hr_allergies_type3)); }
                if(buttonView==cbAllergiesType4) { Utils.allergiesList.add(getString(R.string.hr_allergies_type4)); }
                if(buttonView==cbAllergiesType5) { Utils.allergiesList.add(getString(R.string.hr_allergies_type5)); }
                if(buttonView==cbAllergiesType6) { Utils.allergiesList.add(getString(R.string.hr_allergies_type6)); }
                if(buttonView==cbAllergiesType7) { Utils.allergiesList.add(getString(R.string.hr_allergies_type7)); }
                if(buttonView==cbAllergiesType8) { Utils.allergiesList.add(getString(R.string.hr_allergies_type8)); }
                if(buttonView==cbAllergiesType9) { Utils.allergiesList.add(getString(R.string.hr_allergies_type9)); }
                if(buttonView==cbAllergiesType10) { Utils.allergiesList.add(getString(R.string.hr_allergies_type10)); }
                if(buttonView==cbAllergiesType11) { Utils.allergiesList.add(getString(R.string.hr_allergies_type11)); }
                if(buttonView==cbAllergiesType12) { Utils.allergiesList.add(getString(R.string.hr_allergies_type12)); }
                if(buttonView==cbAllergiesType13) { Utils.allergiesList.add(getString(R.string.hr_allergies_type13)); }
                if(buttonView==cbAllergiesType14) { Utils.allergiesList.add(getString(R.string.hr_allergies_type14)); }
                if(buttonView==cbAllergiesType15) { Utils.allergiesList.add(getString(R.string.hr_allergies_type15)); }
                if(buttonView==cbAllergiesType16) { Utils.allergiesList.add(getString(R.string.hr_allergies_type16)); }
                if(buttonView==cbAllergiesType17) { Utils.allergiesList.add(getString(R.string.hr_allergies_type17)); }

                if(buttonView==cbAllergiesTypes1) { Utils.otherAllergiesList.add(getString(R.string.hr_allergies_types1)); }
                if(buttonView==cbAllergiesTypes2) { Utils.otherAllergiesList.add(getString(R.string.hr_allergies_types2)); }
                if(buttonView==cbAllergiesTypes3) { Utils.otherAllergiesList.add(getString(R.string.hr_allergies_types3)); }
                if(buttonView==cbAllergiesTypes4) { Utils.otherAllergiesList.add(getString(R.string.hr_allergies_types4)); }
                if(buttonView==cbAllergiesTypes5) { Utils.otherAllergiesList.add(getString(R.string.hr_allergies_types5)); }

                if(buttonView==cbMedicationsType1) { Utils.medicationsList.add(getString(R.string.hr_allergies_type1)); }
                if(buttonView==cbMedicationsType2) { Utils.medicationsList.add(getString(R.string.hr_allergies_type2)); }
                if(buttonView==cbMedicationsType3) { Utils.medicationsList.add(getString(R.string.hr_allergies_type3)); }
                if(buttonView==cbMedicationsType4) { Utils.medicationsList.add(getString(R.string.hr_allergies_type4)); }
                if(buttonView==cbMedicationsType5) { Utils.medicationsList.add(getString(R.string.hr_allergies_type5)); }
                if(buttonView==cbMedicationsType6) { Utils.medicationsList.add(getString(R.string.hr_allergies_type6)); }
                if(buttonView==cbMedicationsType7) { Utils.medicationsList.add(getString(R.string.hr_allergies_type7)); }
                if(buttonView==cbMedicationsType8) { Utils.medicationsList.add(getString(R.string.hr_allergies_type8)); }
                if(buttonView==cbMedicationsType9) { Utils.medicationsList.add(getString(R.string.hr_allergies_type9)); }
                if(buttonView==cbMedicationsType10) { Utils.medicationsList.add(getString(R.string.hr_allergies_type10)); }
                if(buttonView==cbMedicationsType11) { Utils.medicationsList.add(getString(R.string.hr_allergies_type11)); }
                if(buttonView==cbMedicationsType12) { Utils.medicationsList.add(getString(R.string.hr_allergies_type12)); }
                if(buttonView==cbMedicationsType13) { Utils.medicationsList.add(getString(R.string.hr_allergies_type13)); }
                if(buttonView==cbMedicationsType14) { Utils.medicationsList.add(getString(R.string.hr_allergies_type14)); }
                if(buttonView==cbMedicationsType15) { Utils.medicationsList.add(getString(R.string.hr_allergies_type15)); }
                if(buttonView==cbMedicationsType16) { Utils.medicationsList.add(getString(R.string.hr_allergies_type16)); }
                if(buttonView==cbMedicationsType17) { Utils.medicationsList.add(getString(R.string.hr_allergies_type17)); }

                if(buttonView==cbMedconditionType1) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type1)); }
                if(buttonView==cbMedconditionType2) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type2)); }
                if(buttonView==cbMedconditionType3) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type3)); }
                if(buttonView==cbMedconditionType4) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type4)); }
                if(buttonView==cbMedconditionType5) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type5)); }
                if(buttonView==cbMedconditionType6) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type6)); }
                if(buttonView==cbMedconditionType7) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type7)); }
                if(buttonView==cbMedconditionType8) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type8)); }
                if(buttonView==cbMedconditionType9) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type9)); }
                if(buttonView==cbMedconditionType10) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type10)); }
                if(buttonView==cbMedconditionType11) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type11)); }
                if(buttonView==cbMedconditionType12) { Utils.diagnosedList.add(getString(R.string.hr_medcondition_type12)); }

                if(buttonView==cbMedicalProceduresType1) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type1)); }
                if(buttonView==cbMedicalProceduresType2) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type2)); }
                if(buttonView==cbMedicalProceduresType3) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type3)); }
                if(buttonView==cbMedicalProceduresType4) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type4)); }
                if(buttonView==cbMedicalProceduresType5) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type5)); }
                if(buttonView==cbMedicalProceduresType6) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type6)); }
                if(buttonView==cbMedicalProceduresType7) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type7)); }
                if(buttonView==cbMedicalProceduresType8) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type8)); }
                if(buttonView==cbMedicalProceduresType9) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type9)); }
                if(buttonView==cbMedicalProceduresType10) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type10)); }
                if(buttonView==cbMedicalProceduresType11) { Utils.medicalProceduresList.add(getString(R.string.hr_medical_procedures_type11)); }

                if(buttonView==cbMemberDiagnosedType1) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type1)); }
                if(buttonView==cbMemberDiagnosedType2) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type2)); }
                if(buttonView==cbMemberDiagnosedType3) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type3)); }
                if(buttonView==cbMemberDiagnosedType4) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type4)); }
                if(buttonView==cbMemberDiagnosedType5) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type5)); }
                if(buttonView==cbMemberDiagnosedType6) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type6)); }
                if(buttonView==cbMemberDiagnosedType7) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type7)); }
                if(buttonView==cbMemberDiagnosedType8) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type8)); }
                if(buttonView==cbMemberDiagnosedType9) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type9)); }
                if(buttonView==cbMemberDiagnosedType10) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type10)); }
                if(buttonView==cbMemberDiagnosedType11) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type11)); }
                if(buttonView==cbMemberDiagnosedType12) { Utils.memberDiagnosedList.add(getString(R.string.hr_member_diagnosed_type12)); }
            } else {
                if(buttonView==cbAllergiesType1) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType2) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType3) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType4) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType5) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType6) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType7) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType8) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType9) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType10) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType11) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType12) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType13) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType14) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType15) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType16) { Utils.allergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesType17) { Utils.allergiesList.clear(); unCheckStep1All();}

                if(buttonView==cbAllergiesTypes1) { Utils.otherAllergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesTypes2) { Utils.otherAllergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesTypes3) { Utils.otherAllergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesTypes4) { Utils.otherAllergiesList.clear(); unCheckStep1All();}
                if(buttonView==cbAllergiesTypes5) { Utils.otherAllergiesList.clear(); unCheckStep1All();}

                if(buttonView==cbMedicationsType1) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType2) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType3) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType4) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType5) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType6) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType7) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType8) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType9) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType10) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType11) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType12) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType13) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType14) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType15) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType16) { Utils.medicationsList.clear(); unCheckStep2All();}
                if(buttonView==cbMedicationsType17) { Utils.medicationsList.clear(); unCheckStep2All();}

                if(buttonView==cbMedconditionType1) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType2) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType3) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType4) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType5) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType6) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType7) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType8) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType9) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType10) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType11) { Utils.diagnosedList.clear(); unCheckStep3All();}
                if(buttonView==cbMedconditionType12) { Utils.diagnosedList.clear(); unCheckStep3All();}

                if(buttonView==cbMedicalProceduresType1) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType2) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType3) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType4) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType5) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType6) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType7) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType8) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType9) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType10) { Utils.medicalProceduresList.clear(); unCheckStep4All();}
                if(buttonView==cbMedicalProceduresType11) { Utils.medicalProceduresList.clear(); unCheckStep4All();}

                if(buttonView==cbMemberDiagnosedType1) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType2) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType3) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType4) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType5) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType6) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType7) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType8) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType9) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType10) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType11) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
                if(buttonView==cbMemberDiagnosedType12) { Utils.memberDiagnosedList.clear(); unCheckStep5All();}
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == btnBackStep1) {
            finish();
        } else if (v == btnBackStep2) {
            scrollView.scrollTo(0, 0);
            llStep1.setVisibility(View.VISIBLE);
            llStep2.setVisibility(View.GONE);
        } else if (v == btnBackStep3) {
            scrollView.scrollTo(0, 0);
            llStep2.setVisibility(View.VISIBLE);
            llStep3.setVisibility(View.GONE);
        } else if (v == btnBackStep4) {
            scrollView.scrollTo(0, 0);
            llStep3.setVisibility(View.VISIBLE);
            llStep4.setVisibility(View.GONE);
        } else if (v == btnBackStep5) {
            scrollView.scrollTo(0, 0);
            llStep4.setVisibility(View.VISIBLE);
            llStep5.setVisibility(View.GONE);
        } else if (v == btnNextStep1) {
            addStep1Values();
        } else if (v == btnNextStep2) {
            addStep2Values();
        } else if (v == btnNextStep3) {
            addStep3Values();
        } else if (v == btnNextStep4) {
            addStep4Values();
        } else if (v == btnNextStep5) {
            addStep5Values();
        }
    }

    public void addStep1Values() {
        if(!etAllergyOther.getText().toString().equalsIgnoreCase("")) {
            Utils.allergiesList.add(etAllergyOther.getText().toString());
        }
        if(!etAllergyOthers.getText().toString().equalsIgnoreCase("")) {
            Utils.otherAllergiesList.add(etAllergyOthers.getText().toString());
        }
        if(!flagStep1 && Utils.allergiesList.size()==0) {
            Utils.showToast(actCon, "Choose an option to proceed");
        } else {
            scrollView.scrollTo(0, 0);
            llStep2.setVisibility(View.VISIBLE);
            llStep1.setVisibility(View.GONE);
        }
    }

    public void addStep2Values() {
        if(!etMedicationsOther.getText().toString().equalsIgnoreCase("")) {
            Utils.medicationsList.add(etMedicationsOther.getText().toString());
        }
        if(!flagStep2 && Utils.medicationsList.size()==0) {
            Utils.showToast(actCon, "Choose an option to proceed");
        } else {
            scrollView.scrollTo(0, 0);
            llStep3.setVisibility(View.VISIBLE);
            llStep2.setVisibility(View.GONE);
        }
    }

    public void addStep3Values() {
        if(!etMedconditionOther.getText().toString().equalsIgnoreCase("")) {
            Utils.diagnosedList.add(etMedconditionOther.getText().toString());
        }
        if(!flagStep3 && Utils.diagnosedList.size()==0) {
            Utils.showToast(actCon, getResources().getString(R.string.choose_option_to_proceed));
        } else {
            scrollView.scrollTo(0, 0);
            llStep4.setVisibility(View.VISIBLE);
            llStep3.setVisibility(View.GONE);
        }
    }

    public void addStep4Values() {
        if(!etMedicalProceduresOther.getText().toString().equalsIgnoreCase("")) {
            Utils.medicalProceduresList.add(etMedicalProceduresOther.getText().toString());
        }
        if(!flagStep4 && Utils.medicalProceduresList.size()==0) {
            Utils.showToast(actCon, getResources().getString(R.string.choose_option_to_proceed));
        } else {
            scrollView.scrollTo(0, 0);
            llStep5.setVisibility(View.VISIBLE);
            llStep4.setVisibility(View.GONE);
        }
    }

    public void addStep5Values() {
        if(!etMemberDiagnosedOther.getText().toString().equalsIgnoreCase("")) {
            Utils.memberDiagnosedList.add(etMemberDiagnosedOther.getText().toString());
        }
        if(!flagStep5 && Utils.memberDiagnosedList.size()==0) {
            Utils.showToast(actCon, getResources().getString(R.string.choose_option_to_proceed));
        } else {
                Intent intent=new Intent();
                intent.putExtra("status","true");
                intent.putExtra("str_message","Health History (Filled)");
                setResult(200, intent);
                finish();
        }
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_health_history));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void unCheckStep1All() {
        cbAllergiesType1.setChecked(false);
        cbAllergiesType2.setChecked(false);
        cbAllergiesType3.setChecked(false);
        cbAllergiesType4.setChecked(false);
        cbAllergiesType5.setChecked(false);
        cbAllergiesType6.setChecked(false);
        cbAllergiesType7.setChecked(false);
        cbAllergiesType8.setChecked(false);
        cbAllergiesType9.setChecked(false);
        cbAllergiesType10.setChecked(false);
        cbAllergiesType11.setChecked(false);
        cbAllergiesType12.setChecked(false);
        cbAllergiesType13.setChecked(false);
        cbAllergiesType14.setChecked(false);
        cbAllergiesType15.setChecked(false);
        cbAllergiesType16.setChecked(false);
        cbAllergiesType17.setChecked(false);
        cbAllergiesTypes1.setChecked(false);
        cbAllergiesTypes2.setChecked(false);
        cbAllergiesTypes3.setChecked(false);
        cbAllergiesTypes4.setChecked(false);
        cbAllergiesTypes5.setChecked(false);
        etAllergyOther.setText("");
        etAllergyOthers.setText("");
    }

    private void  unCheckStep2All(){
        cbMedicationsType1.setChecked(false);
        cbMedicationsType2.setChecked(false);
        cbMedicationsType3.setChecked(false);
        cbMedicationsType4.setChecked(false);
        cbMedicationsType5.setChecked(false);
        cbMedicationsType6.setChecked(false);
        cbMedicationsType7.setChecked(false);
        cbMedicationsType8.setChecked(false);
        cbMedicationsType9.setChecked(false);
        cbMedicationsType10.setChecked(false);
        cbMedicationsType11.setChecked(false);
        cbMedicationsType12.setChecked(false);
        cbMedicationsType13.setChecked(false);
        cbMedicationsType14.setChecked(false);
        cbMedicationsType15.setChecked(false);
        cbMedicationsType16.setChecked(false);
        cbMedicationsType17.setChecked(false);
        etMedicationsOther.setText("");
    }

    private void unCheckStep3All(){
        cbMedconditionType1.setChecked(false);
        cbMedconditionType2.setChecked(false);
        cbMedconditionType3.setChecked(false);
        cbMedconditionType4.setChecked(false);
        cbMedconditionType5.setChecked(false);
        cbMedconditionType6.setChecked(false);
        cbMedconditionType7.setChecked(false);
        cbMedconditionType8.setChecked(false);
        cbMedconditionType9 .setChecked(false);
        cbMedconditionType10.setChecked(false);
        cbMedconditionType11.setChecked(false);
        cbMedconditionType12.setChecked(false);
        etMedconditionOther.setText("");
    }

    private void unCheckStep4All(){
        cbMedicalProceduresType1.setChecked(false);
        cbMedicalProceduresType2.setChecked(false);
        cbMedicalProceduresType3.setChecked(false);
        cbMedicalProceduresType4.setChecked(false);
        cbMedicalProceduresType5.setChecked(false);
        cbMedicalProceduresType6.setChecked(false);
        cbMedicalProceduresType7.setChecked(false);
        cbMedicalProceduresType8.setChecked(false);
        cbMedicalProceduresType9.setChecked(false);
        cbMedicalProceduresType10.setChecked(false);
        cbMedicalProceduresType11.setChecked(false);
        etMedicalProceduresOther.setText("");
    }

    private void unCheckStep5All(){
        cbMemberDiagnosedType1.setChecked(false);
        cbMemberDiagnosedType2.setChecked(false);
        cbMemberDiagnosedType3.setChecked(false);
        cbMemberDiagnosedType4.setChecked(false);
        cbMemberDiagnosedType5.setChecked(false);
        cbMemberDiagnosedType6.setChecked(false);
        cbMemberDiagnosedType7.setChecked(false);
        cbMemberDiagnosedType8.setChecked(false);
        cbMemberDiagnosedType9.setChecked(false);
        cbMemberDiagnosedType10.setChecked(false);
        cbMemberDiagnosedType11.setChecked(false);
        cbMemberDiagnosedType12.setChecked(false);
        etMemberDiagnosedOther.setText("");
    }
}
