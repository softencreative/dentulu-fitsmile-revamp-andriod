package com.app.fitsmile.myaccount;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.insurance.InsuranceActivity;
import com.app.fitsmile.my_address.MyAddressActivity;
import com.app.fitsmile.response.addpatient.AddPatientResponse;
import com.app.fitsmile.response.common.CommonResponse;
import com.app.fitsmile.response.myaccount.MyAccountResponse;
import com.app.fitsmile.response.patientlist.PatientListData;
import com.app.fitsmile.response.patientlist.PatientListResponse;
import com.app.fitsmile.response.profile.ProfileResponse;
import com.app.fitsmile.shop.ui.MyOrderActivity;
import com.app.fitsmile.utils.ImagePickerActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.BASE_IMAGEURL;
import static com.app.fitsmile.BuildConfig.BASE_URL;
import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;

public class MyAccount extends BaseActivity implements MyAccountMemberListAdapter.ItemClickMemberList, View.OnClickListener {


    public static final int REQUEST_IMAGE = 100;
    public static EditText etDob;
    byte[] image;
    boolean profileImage = false;
    AlertDialog alert;
    private RecyclerView mRecycleMemberList;
    private MyAccountMemberListAdapter myAccountMemberListAdapter;
    private ImageView imvEditProfile;
    private CardView cardInsurance;
    private CardView cardMyAddress;
    private CardView cardSettings, cardLangauge;
    private CardView mCardRewards;
    private CardView mCardMyOrders;
    private TextView mUserName;
    private TextView mEmail;
    private TextView mCode;
    public static CircleImageView mProfileImage;
    private ImageView mImvDentistMale;
    private ImageView mImvDentistFemale;
    private boolean isMemberAvailable = false;
    //add member
    private CircleImageView mPhoto;
    private TextView mTvAddPhoto;
    private EditText etRelationship;
    private String strPatientId = " ";
    static String dateApi = "";


    private BottomSheetBehavior bottomSheetBehavior;
    private LinearLayout llBottomSheet;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount);
        LocaleManager.setLocale(this);
        initView();
        setupAdapter();

    }


    private void initView() {

        llBottomSheet = findViewById(R.id.ll_parent_add_member);
        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        mRecycleMemberList = findViewById(R.id.recycler_member_list);
        imvEditProfile = findViewById(R.id.imv_edit_profile);
        cardInsurance = findViewById(R.id.card_insurance);
        cardMyAddress = findViewById(R.id.card_my_address);
        cardSettings = findViewById(R.id.card_settings);
        cardLangauge = findViewById(R.id.card_language);
        mCardRewards = findViewById(R.id.cardRewards);
        mCardMyOrders = findViewById(R.id.cardMyOrders);

        mUserName = findViewById(R.id.tv_username);
        mCode = findViewById(R.id.tv_code);
        mEmail = findViewById(R.id.tv_email);
        mProfileImage = findViewById(R.id.profile_image);
        mImvDentistMale = findViewById(R.id.imv_dentist_male);
        mImvDentistFemale = findViewById(R.id.imv_dentist_female);

        mImvDentistMale.setOnClickListener(this);
        mImvDentistFemale.setOnClickListener(this);

        cardSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(actCon, Settings.class));
            }
        });

        cardLangauge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChangeLanguagePopup(view);
            }
        });

        cardInsurance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(actCon, InsuranceActivity.class));
            }
        });

        cardMyAddress.setOnClickListener(view -> startActivity(new Intent(actCon, MyAddressActivity.class)));

        imvEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(actCon, EditProfile.class);
                startActivityForResult(intent, 2);
            }
        });

        mCardRewards.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(actCon, MyRewardPoints.class));
            }
        });

        mCardMyOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(actCon, MyOrderActivity.class));
            }
        });

    }

    private void setupAdapter() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(actCon, LinearLayoutManager.HORIZONTAL, false);
        mRecycleMemberList.setLayoutManager(layoutManager);
        myAccountMemberListAdapter = new MyAccountMemberListAdapter(actCon, this);
        mRecycleMemberList.setAdapter(myAccountMemberListAdapter);

        invokeProfileApi();
        invokeMyAccount();
        getPatientList();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClickAddMember() {
        showAddMemberPopUp("NEW", null);
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onItemClickViewMember(PatientListData data) {
        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        showAddMemberPopUp("VIEW", data);
    }

    private void openChangeLanguagePopup(View v) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.language_selection_view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView btnEnglish = dialog.findViewById(R.id.btn_english);
        TextView btnSpanish = dialog.findViewById(R.id.btn_spanish);
        btnEnglish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                LocaleManager.setNewLocale(MyAccount.this, LocaleManager.ENGLISH);
                LocaleManager.setNewLocale(MyAccount.this, LocaleManager.SPANISH);
                dialog.dismiss();
                Intent intent = new Intent(MyAccount.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        });


        btnSpanish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LocaleManager.setNewLocale(MyAccount.this, LocaleManager.SPANISH);
                dialog.dismiss();
                Intent intent = new Intent(MyAccount.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();

            }
        });

        dialog.show();


    }

    public void selectRelations() {
        List<String> genders = new ArrayList<String>();
        // genders.add("Myself");
        genders.add(getResources().getString(R.string.Spouse));
        genders.add(getResources().getString(R.string.child));
        genders.add(getResources().getString(R.string.Relative));
        genders.add(getResources().getString(R.string.Friend));
        genders.add(getResources().getString(R.string.Other));
        //Create sequence of items
        final CharSequence[] Genderss = genders.toArray(new String[genders.size()]);
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(actCon);
        dialogBuilder.setTitle(R.string.hint_relationship_to_patient);
        dialogBuilder.setItems(Genderss, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String selectedText = Genderss[item].toString();  //Selected item in listview
                etRelationship.setText(selectedText);
            }
        });
        android.app.AlertDialog alertDialogObject = dialogBuilder.create();
        alertDialogObject.show();
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void showAddMemberPopUp(String editable, PatientListData data) {

      /*  AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_add_member, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();*/

        EditText etFirstName = findViewById(R.id.et_first_name);
        etFirstName.setText("");
        EditText etLastName = findViewById(R.id.et_last_name);
        etLastName.setText("");
        EditText etEmail = findViewById(R.id.et_email);
        etEmail.setText("");
        etRelationship = findViewById(R.id.tv_relationship);
        TextInputLayout mTextInputFirstName = findViewById(R.id.textInputFirstName);
        TextInputLayout mTextInputLastName = findViewById(R.id.textInputLastName);
        TextInputLayout mTextInputEmailName = findViewById(R.id.textInputEmail);
        TextInputLayout mTextInputDobName = findViewById(R.id.textInputDob);
        TextInputLayout mTextInputRelationship = findViewById(R.id.til_relationshiptopatient);
        etRelationship.setText("");
        etDob = findViewById(R.id.et_dob);
        etDob.setText("");
        TextView mTvMale = findViewById(R.id.tv_male);
        TextView mTvFemale = findViewById(R.id.tv_female);
        TextView mTvOther = findViewById(R.id.tv_other);
        mTvAddPhoto = findViewById(R.id.tv_add_photo);
        mTvAddPhoto.setVisibility(View.VISIBLE);
        mPhoto = findViewById(R.id.profile_image_member);
        mPhoto.setImageResource(R.drawable.ic_profile_placeholder);
        Button btnCancel = findViewById(R.id.btn_cancel);
        Button btnAdd = findViewById(R.id.btn_add);
        btnAdd.setVisibility(View.VISIBLE);
        ImageView imvEdit = findViewById(R.id.imv_edit_member);
        imvEdit.setVisibility(View.GONE);
        ImageView imvDelete = findViewById(R.id.imv_delete_member);
        imvDelete.setVisibility(View.GONE);
        final String[] mStrGender = {""};

        mTvAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPhoto.performClick();
            }
        });


        if (editable.equals("NEW")) {
            imvEdit.performClick();
            imvEdit.setVisibility(View.GONE);
            imvDelete.setVisibility(View.GONE);
            btnAdd.setText(getString(R.string.btn_add));
        } else if (Utils.getStr(data.getRelationshipType()).equalsIgnoreCase("Myself") || Utils.getStr(data.getRelationshipType()).equalsIgnoreCase("Yo mismo")) {
            imvEdit.setVisibility(View.GONE);
            imvDelete.setVisibility(View.GONE);
            btnAdd.setVisibility(View.GONE);
            mTvAddPhoto.setVisibility(View.GONE);
            Picasso.get()
                    .load(Utils.getStr(appPreference.getImage()))
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(mPhoto);

            etFirstName.setText(Utils.getStr(data.getFirstName()));
            etLastName.setText(Utils.getStr(data.getLastName()));
            etEmail.setText(Utils.getStr(data.getEmail()));
            etDob.setText(Utils.getNewDate(this, data.getDob(), Utils.inputOnlyDateSmall, Utils.outputDateChange));
            etRelationship.setText(R.string.myself);
            etFirstName.setTextColor(getColor(R.color.profile_grey));
            etFirstName.setHintTextColor(getColor(R.color.profile_grey));
            etFirstName.setEnabled(false);
            etLastName.setTextColor(getColor(R.color.profile_grey));
            etLastName.setHintTextColor(getColor(R.color.profile_grey));
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);
            etEmail.setTextColor(getColor(R.color.profile_grey));
            etEmail.setHintTextColor(getColor(R.color.profile_grey));
            etRelationship.setTextColor(getColor(R.color.profile_grey));
            etRelationship.setHintTextColor(getColor(R.color.profile_grey));
            etRelationship.setEnabled(false);
            etDob.setEnabled(false);
            etDob.setTextColor(getColor(R.color.profile_grey));
            etDob.setHintTextColor(getColor(R.color.profile_grey));
            mTextInputFirstName.setEnabled(false);
            mTextInputLastName.setEnabled(false);
            mTextInputEmailName.setEnabled(false);
            mTextInputDobName.setEnabled(false);
            mTextInputRelationship.setEnabled(false);
            mTvMale.setEnabled(false);
            mTvFemale.setEnabled(false);
            mTvOther.setEnabled(false);
            mTvAddPhoto.setEnabled(false);
            mPhoto.setEnabled(false);

            String gender_preference = Utils.getStr(data.getSex());
            if (gender_preference.equals("1")) {
                mStrGender[0] = getResources().getString(R.string.male);
                mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            } else if (gender_preference.equals("2")) {
                mStrGender[0] = getResources().getString(R.string.female);
                mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            } else {
                mStrGender[0] = getResources().getString(R.string.other);
                mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            }
        } else {
            btnAdd.setVisibility(View.GONE);
            imvEdit.setVisibility(View.VISIBLE);
            imvDelete.setVisibility(View.VISIBLE);
            if (!Utils.getStr(data.getImage()).isEmpty()) {
                mTvAddPhoto.setVisibility(View.GONE);
                Picasso.get()
                        .load(BASE_IMAGEURL + Utils.getStr(data.getImage()))
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .into(mPhoto);
            } else {
                mTvAddPhoto.setVisibility(View.VISIBLE);
            }

            etFirstName.setText(Utils.getStr(data.getFirstName()));
            etLastName.setText(Utils.getStr(data.getLastName()));
            etEmail.setText(Utils.getStr(data.getEmail()));
            // etDob.setText(Utils.getStr(data.getDob()));
            etDob.setText(Utils.getNewDate(this, data.getDob(), Utils.inputOnlyDateSmall, Utils.outputDateChange));
            //etRelationship.setText(Utils.getStr(data.getRelationshipType()));
            String relation_type = "";
            if (data.getRelationshipType().equalsIgnoreCase("Myself")) {
                relation_type = "Yo mismo";
            } else if (data.getRelationshipType().equalsIgnoreCase("Spouse")) {
                relation_type = "Cónyuge";
            } else if (data.getRelationshipType().equalsIgnoreCase("Child")) {
                relation_type = "Hijo";
            } else if (data.getRelationshipType().equalsIgnoreCase("Relative")) {
                relation_type = "Pariente";
            } else if (data.getRelationshipType().equalsIgnoreCase("Friend")) {
                relation_type = "Amigo";
            } else if (data.getRelationshipType().equalsIgnoreCase("Other")) {
                relation_type = "Otro";
            }
            etRelationship.setText(relation_type);
            etFirstName.setTextColor(getColor(R.color.profile_grey));
            etFirstName.setHintTextColor(getColor(R.color.profile_grey));
            etFirstName.setEnabled(false);
            etLastName.setTextColor(getColor(R.color.profile_grey));
            etLastName.setHintTextColor(getColor(R.color.profile_grey));
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);
            etEmail.setTextColor(getColor(R.color.profile_grey));
            etEmail.setHintTextColor(getColor(R.color.profile_grey));
            etRelationship.setTextColor(getColor(R.color.profile_grey));
            etRelationship.setHintTextColor(getColor(R.color.profile_grey));
            etDob.setTextColor(getColor(R.color.profile_grey));
            etDob.setHintTextColor(getColor(R.color.profile_grey));
            etRelationship.setEnabled(false);
            etFirstName.setEnabled(false);
            etLastName.setEnabled(false);
            etEmail.setEnabled(false);
            etRelationship.setEnabled(false);
            etDob.setEnabled(false);
            mTvMale.setEnabled(false);
            mTvFemale.setEnabled(false);
            mTvOther.setEnabled(false);
            mTvAddPhoto.setEnabled(false);
            mPhoto.setEnabled(false);
            mTextInputFirstName.setEnabled(false);
            mTextInputLastName.setEnabled(false);
            mTextInputEmailName.setEnabled(false);
            mTextInputDobName.setEnabled(false);
            mTextInputRelationship.setEnabled(false);

            String gender_preference = Utils.getStr(data.getSex());
            if (gender_preference.equals("1")) {
                mStrGender[0] = getResources().getString(R.string.male);
                mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            } else if (gender_preference.equals("2")) {
                mStrGender[0] = getResources().getString(R.string.female);
                mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            } else {
                mStrGender[0] = getResources().getString(R.string.other);
                mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_disable_border));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimaryLightProfile));
            }
        }

        imvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showCommonPopup(actCon, getString(R.string.yes),
                        getString(R.string.no), getString(R.string.confirm), getString(R.string.are_you_sure), false, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                                invokeDeleteMemberApi(data.getId());
                            }
                        }, new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        });
            }
        });


        imvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imvDelete.setVisibility(View.GONE);
                btnAdd.setVisibility(View.VISIBLE);
                btnAdd.setText(getString(R.string.btn_update));
                etFirstName.setTextColor(getColor(R.color.side_menu_txt));
                etFirstName.setHintTextColor(getColor(R.color.side_menu_txt));
                etLastName.setTextColor(getColor(R.color.side_menu_txt));
                etLastName.setHintTextColor(getColor(R.color.side_menu_txt));
                etEmail.setTextColor(getColor(R.color.side_menu_txt));
                etEmail.setHintTextColor(getColor(R.color.side_menu_txt));
                etRelationship.setTextColor(getColor(R.color.side_menu_txt));
                etRelationship.setHintTextColor(getColor(R.color.side_menu_txt));
                etDob.setTextColor(getColor(R.color.side_menu_txt));
                etDob.setHintTextColor(getColor(R.color.side_menu_txt));
                etFirstName.setEnabled(true);
                etLastName.setEnabled(true);
                etEmail.setEnabled(true);
                etRelationship.setEnabled(true);
                etDob.setEnabled(true);
                mTvMale.setEnabled(true);
                mTvFemale.setEnabled(true);
                mTvOther.setEnabled(true);
                mTvAddPhoto.setEnabled(true);
                mPhoto.setEnabled(true);
                mTextInputFirstName.setEnabled(true);
                mTextInputLastName.setEnabled(true);
                mTextInputEmailName.setEnabled(true);
                mTextInputDobName.setEnabled(true);
                mTextInputRelationship.setEnabled(true);
                if (data != null && data.getSex() != null) {
                    String gender_preference = Utils.getStr(data.getSex());
                    if (gender_preference.equals("1")) {
                        mStrGender[0] = getResources().getString(R.string.male);
                        mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mTvMale.setTextColor(getResources().getColor(R.color.white));
                        mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else if (gender_preference.equals("2")) {
                        mStrGender[0] = getResources().getString(R.string.female);
                        mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mTvFemale.setTextColor(getResources().getColor(R.color.white));
                        mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
                    } else {
                        mStrGender[0] = getResources().getString(R.string.other);
                        mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        mTvOther.setTextColor(getResources().getColor(R.color.white));
                        mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                        mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                        mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                }

            }
        });

        etRelationship.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectRelations();
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etFirstName.getText().toString().isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.please_enter_first_name));
                    return;
                } else if (etLastName.getText().toString().isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.please_enter_last_name));
                    return;
                } else if (etDob.getText().toString().isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.please_select_date_of_birth));
                    return;
                } else if (mStrGender[0].isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.please_select_gender));
                    return;
                } else if (etEmail.getText().toString().isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.please_enter_email_address));
                    return;
                } else if (!Utils.isValidEmail(etEmail.getText().toString())) {
                    Utils.showToast(actCon, getString(R.string.invalid_email));
                    return;
                } else if (etRelationship.getText().toString().isEmpty()) {
                    Utils.showToast(actCon, getString(R.string.error_please_choose_relationship));
                    return;
                }

                if (btnAdd.getText().toString().equals(getString(R.string.btn_add))) {
                    addNewMemberApi(etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etDob.getText().toString(),
                            mStrGender[0],
                            etEmail.getText().toString(),
                            etRelationship.getText().toString());
                } else {
                    editMemberApi(etFirstName.getText().toString(),
                            etLastName.getText().toString(),
                            etDob.getText().toString(),
                            mStrGender[0],
                            etEmail.getText().toString(),
                            etRelationship.getText().toString(), data.getId());
                }
            }
        });

        mPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withActivity(actCon)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        mTvMale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStrGender[0] = getResources().getString(R.string.male);
                mTvMale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvMale.setTextColor(getResources().getColor(R.color.white));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        mTvFemale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStrGender[0] = getResources().getString(R.string.female);
                mTvFemale.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvFemale.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvOther.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        mTvOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mStrGender[0] = getResources().getString(R.string.other);
                mTvOther.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                mTvOther.setTextColor(getResources().getColor(R.color.white));
                mTvMale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvMale.setTextColor(getResources().getColor(R.color.colorPrimary));
                mTvFemale.setBackground(getResources().getDrawable(R.drawable.bg_btn_default_stroke));
                mTvFemale.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });

        etDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment dFragment = new DatePickerFragment();
                dFragment.show(getSupportFragmentManager(), "Date Picker");
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

    }


    private void invokeDeleteMemberApi(String id) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("patient_id", Utils.getStr(id));
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<CommonResponse> service = apiInterface.deletePatient(jsonObj);
        service.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    CommonResponse responseCommon = response.body();
                    if (Utils.getStr(responseCommon.getStatus()).equals(ONE)) {
                        getPatientList();
                        Utils.showToast(actCon, responseCommon.getMessage());
                    } else if (Utils.getStr(responseCommon.getStatus()).equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, responseCommon.getMessage());
                    }
                }
            }


            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    private void editMemberApi(String firstName, String lastName, String dob, String strGender, String strEmail, String strRelationship, String patientID) {
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        RequestBody requestFile = null;
        MultipartBody.Part multiBody = null;
        if (profileImage) {
            Bitmap bitmap = ((BitmapDrawable) mPhoto.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            image = baos.toByteArray();
            requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image);
            multiBody = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());
        RequestBody patientId = RequestBody.create(MediaType.parse("text/plain"), patientID);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastname = RequestBody.create(MediaType.parse("text/plain"), lastName);
        String gender_type = "";
        if (strGender.equalsIgnoreCase("Hombre")) {
            gender_type = "Male";
        } else if (strGender.equalsIgnoreCase("Mujer")) {
            gender_type = "Female";
        } else if (strGender.equalsIgnoreCase("Otro")) {
            gender_type = "Non Binary";
        }
        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), gender_type);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), strEmail);
        String relation_type = "";
        if (strRelationship.equalsIgnoreCase("Cónyuge")) {
            relation_type = "Spouse";
        } else if (strRelationship.equalsIgnoreCase("Hijo")) {
            relation_type = "Child";
        } else if (strRelationship.equalsIgnoreCase("Pariente")) {
            relation_type = "Relative";
        } else if (strRelationship.equalsIgnoreCase("Amigo")) {
            relation_type = "Friend";
        } else if (strRelationship.equalsIgnoreCase("Otro")) {
            relation_type = "Other";
        }
        RequestBody relationship = RequestBody.create(MediaType.parse("text/plain"), relation_type);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), " ");
        RequestBody date = RequestBody.create(MediaType.parse("text/plain"), dateApi);
        RequestBody mobile_code = RequestBody.create(MediaType.parse("text/plain"), " ");
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);
        Call<AddPatientResponse> service = mApiService.editPatientImage(multiBody, patientId, name, lastname, gender, email, phone, mobile_code, date, language, relationship);
        service.enqueue(new Callback<AddPatientResponse>() {
            @Override
            public void onResponse(Call<AddPatientResponse> call, Response<AddPatientResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    AddPatientResponse addPatientResponse = response.body();
                    if (Utils.getStr(addPatientResponse.getStatus()).equals(ONE)) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        getPatientList();
                        Utils.showToast(actCon, Utils.getStr(getResources().getString(R.string.info_updated_success_message)));
                        // Utils.showToast(actCon, Utils.getStr(addPatientResponse.getMessage()));
                    } else if (Utils.getStr(addPatientResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, Utils.getStr(addPatientResponse.getMessage()));
                    }
                }
            }


            @Override
            public void onFailure(Call<AddPatientResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }


    private void addNewMemberApi(String firstName, String lastName, String dob, String strGender, String strEmail, String strRelationship) {
        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        RequestBody requestFile = null;
        MultipartBody.Part multiBody = null;
        if (profileImage) {
            Bitmap bitmap = ((BitmapDrawable) mPhoto.getDrawable()).getBitmap();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            image = baos.toByteArray();
            requestFile = RequestBody.create(MediaType.parse("image/jpeg"), image);
            multiBody = MultipartBody.Part.createFormData("image", "image.jpg", requestFile);
        }

        RequestBody user_id = RequestBody.create(MediaType.parse("text/plain"), appPreference.getUserId());
        RequestBody profileId = RequestBody.create(MediaType.parse("text/plain"), strPatientId);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), firstName);
        RequestBody lastname = RequestBody.create(MediaType.parse("text/plain"), lastName);

        String gender_type = "";
        if (strGender.equalsIgnoreCase("Hombre")) {
            gender_type = "Male";
        } else if (strGender.equalsIgnoreCase("Mujer")) {
            gender_type = "Female";
        } else if (strGender.equalsIgnoreCase("Otro")) {
            gender_type = "Non Binary";
        }

        RequestBody gender = RequestBody.create(MediaType.parse("text/plain"), gender_type);
        RequestBody email = RequestBody.create(MediaType.parse("text/plain"), strEmail);

        String relation_type = "";
        if (strRelationship.equalsIgnoreCase("Cónyuge")) {
            relation_type = "Spouse";
        } else if (strRelationship.equalsIgnoreCase("Hijo")) {
            relation_type = "Child";
        } else if (strRelationship.equalsIgnoreCase("Pariente")) {
            relation_type = "Relative";
        } else if (strRelationship.equalsIgnoreCase("Amigo")) {
            relation_type = "Friend";
        } else if (strRelationship.equalsIgnoreCase("Otro")) {
            relation_type = "Other";
        }
        RequestBody relationship = RequestBody.create(MediaType.parse("text/plain"), relation_type);
        RequestBody phone = RequestBody.create(MediaType.parse("text/plain"), " ");
        RequestBody date = RequestBody.create(MediaType.parse("text/plain"), dob);
        RequestBody mobile_code = RequestBody.create(MediaType.parse("text/plain"), " ");

        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        RequestBody language = RequestBody.create(MediaType.parse("text/plain"), currentLanguage);

        Call<AddPatientResponse> service = mApiService.addPatientImage(multiBody, user_id, name, lastname, gender, email, phone, mobile_code, date, language, relationship);
        service.enqueue(new Callback<AddPatientResponse>() {
            @Override
            public void onResponse(Call<AddPatientResponse> call, Response<AddPatientResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    AddPatientResponse addPatientResponse = response.body();
                    if (Utils.getStr(addPatientResponse.getStatus()).equals(ONE)) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        getPatientList();
                        Utils.showToast(actCon, Utils.getStr(addPatientResponse.getMessage()));
                    } else if (Utils.getStr(addPatientResponse.getStatus()).equals(FOUR_ZERO_ONE)) {
                        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, Utils.getStr(addPatientResponse.getMessage()));
                    }
                }
            }


            @Override
            public void onFailure(Call<AddPatientResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    private void invokeMyAccount() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<MyAccountResponse> service = apiInterface.myAccount(jsonObj);
        service.enqueue(new Callback<MyAccountResponse>() {
            @Override
            public void onResponse(Call<MyAccountResponse> call, Response<MyAccountResponse> response) {
                if (response.body() != null) {
                    MyAccountResponse myAccount = response.body();
                    if (Utils.getStr(myAccount.getStatus()).equals(ONE)) {
                        String gender_preference = Utils.getStr(myAccount.getFavDatas().getGenderPreferance());
                        if (gender_preference.equalsIgnoreCase("female")) {
                            mImvDentistFemale.setImageResource(R.drawable.ic_women_checked);
                        } else if (gender_preference.equalsIgnoreCase("male")) {
                            mImvDentistMale.setImageResource(R.drawable.ic_men_checked);
                        }
                    } else if (Utils.getStr(myAccount.getStatus()).equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(MyAccount.this);
                    }
                }
            }


            @Override
            public void onFailure(Call<MyAccountResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void invokeProfileApi() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ProfileResponse> service = apiInterface.profile(jsonObj);
        service.enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                Utils.closeProgressDialog();
                if (response.body() != null) {
                    ProfileResponse profile = response.body();
                    if (Utils.getStr(profile.getStatus()).equals(ONE)) {
                        bindDataToView(profile);
                    } else if (Utils.getStr(profile.getStatus()).equals(FOUR_ZERO_ONE)) {
                        Utils.showSessionAlert(actCon);
                    } else {
                        Utils.showToast(actCon, "" + Utils.getStr(profile.getMessage()));
                    }
                }
            }


            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    private void bindDataToView(ProfileResponse profile) {
        mUserName.setText(Utils.getStr(profile.getDatas().getFirstname()));
        mEmail.setText(Utils.getStr(profile.getDatas().getEmail()));
        mCode.setText(Utils.getStr(profile.getDatas().getProfileId()));
        strPatientId = Utils.getStr(profile.getDatas().getId());
        if (Utils.getStr(profile.getDatas().getImage()).isEmpty()) {
            mProfileImage.setImageResource(R.drawable.ic_profile_placeholder_side);
        } else {
            Picasso.get()
                    .load(Utils.getStr(profile.getDatas().getImage()))
                    .placeholder(R.drawable.ic_profile_placeholder_side)
                    .into(mProfileImage);
        }
        appPreference.setUserName(mUserName.getText().toString());
        appPreference.setEmail(mEmail.getText().toString());
        appPreference.setImage(profile.getDatas().getImage());

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imv_dentist_male:
                mImvDentistMale.setImageResource(R.drawable.ic_men_checked);
                mImvDentistFemale.setImageResource(R.drawable.ic_women_check);
                updateDentistGenderPreference("male");
                break;
            case R.id.imv_dentist_female:
                mImvDentistMale.setImageResource(R.drawable.ic_men_check);
                mImvDentistFemale.setImageResource(R.drawable.ic_women_checked);
                updateDentistGenderPreference("Female");
                break;
        }
    }

    public void getPatientList() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<PatientListResponse> mService = mApiService.getPatientList(jsonObj);
        mService.enqueue(new Callback<PatientListResponse>() {
            @Override
            public void onResponse(Call<PatientListResponse> call, Response<PatientListResponse> response) {
                PatientListResponse patientListPojo = response.body();
                isMemberAvailable = false;
                if (patientListPojo != null && Utils.getStr(patientListPojo.getStatus()).equals(ONE)) {
                    if (patientListPojo.getDatas() != null && !patientListPojo.getDatas().isEmpty()) {
                        //ArrayList<PatientListData> patientList = new ArrayList<>();
//                        PatientListData pojo = new PatientListData();
//                        pojo.setFirstName(getResources().getString(R.string.txt_new));
//                        patientList.add(0, pojo);
//                        patientList.addAll(patientListPojo.getDatas());
//                        myAccountMemberListAdapter.setCommonList(patientList);
                        myAccountMemberListAdapter.setCommonList(patientListPojo.getDatas());
                    }
                } else if (patientListPojo != null && Utils.getStr(patientListPojo.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, Utils.getStr(patientListPojo.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<PatientListResponse> call, Throwable t) {
                call.cancel();
            }
        });
    }

    private void updateDentistGenderPreference(String gender) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("type", "gender_preferance");
        jsonObj.addProperty("description", gender);
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<CommonResponse> mService = mApiService.updateDentistGenderPreference(jsonObj);
        mService.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                Utils.closeProgressDialog();
                CommonResponse FavoriteenterSubmit = response.body();
                if (Utils.getStr(FavoriteenterSubmit.getStatus()).equals(ONE)) {
                    Utils.showToast(actCon, FavoriteenterSubmit.getMessage());
                } else if (Utils.getStr(FavoriteenterSubmit.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, FavoriteenterSubmit.getMessage());
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (!actCon.isFinishing()) {
                    Utils.closeProgressDialog();
                }
                call.cancel();
            }
        });
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(actCon, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(actCon, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(actCon);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });

        builder.setNegativeButton(getString(R.string.cancel_caps), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void openSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    Picasso.get().load(uri.toString()).into(mPhoto);
                    mTvAddPhoto.setVisibility(View.GONE);
                    mPhoto.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    image = baos.toByteArray();
                    profileImage = true;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                invokeProfileApi();
            }
        }
    }

    public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dpd = new DatePickerDialog(getActivity(), this, year, month, day);
            dpd.getDatePicker().setMaxDate(System.currentTimeMillis());
            calendar.add(Calendar.YEAR, -70);
            dpd.getDatePicker().setMinDate(calendar.getTimeInMillis());
            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(0);
            cal.set(year, month, day, 0, 0, 0);
            Date chosenDate = cal.getTime();
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            DateFormat df2 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            dateApi = df.format(chosenDate);
            etDob.setText(df2.format(chosenDate));
        }
    }


}
