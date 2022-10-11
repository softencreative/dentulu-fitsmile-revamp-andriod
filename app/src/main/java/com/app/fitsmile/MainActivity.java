package com.app.fitsmile;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.appointment.AppointmentListActivity;
import com.app.fitsmile.appointment.ReminderDetailsActivity;
import com.app.fitsmile.appointment.VideoCallStartActivity;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.book.SearchDentistList;
import com.app.fitsmile.common.CustomTypefaceSpan;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.contact.ContactUs;
import com.app.fitsmile.firebase_chat.IGetUnreadMessagesCount;
import com.app.fitsmile.fitsreminder.AlignerSmileProgressActivity;
import com.app.fitsmile.fitsreminder.FitsReminderActivity;
import com.app.fitsmile.fitsreminder.ImageUploadCameraActivity;
import com.app.fitsmile.home.FAQActivity;
import com.app.fitsmile.home.HomeFragment;
import com.app.fitsmile.home.TermsPrivacyActivity;
import com.app.fitsmile.myaccount.MyAccount;
import com.app.fitsmile.noti.NotificationList;
import com.app.fitsmile.photoConsultation.Constants;
import com.app.fitsmile.photoConsultation.DentistQuestionsActivity;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.SettingsResult;
import com.app.fitsmile.response.login.DentalOfficeResponse;
import com.app.fitsmile.response.login.LoginResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderDataResponse;
import com.app.fitsmile.response.trayMinder.TrayMinderResult;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.BuildConfig.BASE_IMAGEURL;
import static com.app.fitsmile.common.Utils.getChatIntent;
import static com.app.fitsmile.common.Utils.logOut;

public class MainActivity extends BaseActivity implements IGetUnreadMessagesCount {

    public static ImageView imvUserPhoto;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private TextView tvChatCount;
    TextView tvUserName;
    TextView tvUserEmail;
    int mOpenFrom = 0;
    int fitsSmileId;
    String alignNoUpload;
    HomeFragment homeFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LocaleManager.setLocale(this);
        Log.d(">>>Test>>>", ">>>>>>Main activity start>>");
        ((AppController) getApplication()).getUnreadMessagesCount(MainActivity.this);

        /*((AppController) getApplication()).initialiseVirgil(new VirgilInitializeListener() {
            @Override
            public void onInitialized() {
                closeProgressDialog();

            }

            @Override
            public void onError(String message) {
                closeProgressDialog();
                showToast(MainActivity.this, message);
            }
        });*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.side_menu);
        LocaleManager.setLocale(this);
        bottomNavigationView = findViewById(R.id.bottom_nav_view);
        bottomNavigationView.getMenu().clear();
        bottomNavigationView.inflateMenu(R.menu.bottom_navigation_menu);
        // Navigation view header
        navHeader = navigationView.getHeaderView(0);
        tvUserName = navHeader.findViewById(R.id.tv_username);
        tvUserEmail = navHeader.findViewById(R.id.tv_user_email);
        imvUserPhoto = (CircleImageView) navHeader.findViewById(R.id.profile_image);

        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    applyFontToMenuItem(subMenuItem);
                }
            }
            applyFontToMenuItem(mi);
        }
        Menu bottom = bottomNavigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            applyFontToMenuItem(mi);
        }
        setUpNavigationView();
        setBottomNavigationView();

        mOpenFrom = getIntent().getIntExtra(AppConstants.OPEN_FROM, 0);
        fitsSmileId = getIntent().getIntExtra(AppConstants.APPOINTMENT_ID, 0);
        alignNoUpload = getIntent().getStringExtra(AppConstants.ALIGNER_NO);

        // getReminderDetails();

        homeFragment = new HomeFragment();
        loadFragment(homeFragment);
        setUserInfo();
        ((AppController) getApplication()).notification();
        getNotificationSettings();
//        if (AppController.isVideoCallComing) {
//            Log.d(">>>Test>>>", ">>>>>>isVideoCallComing true>>");
//            Intent intent = new Intent(this, VideoCallStartActivity.class);
//            startActivity(intent);
//            finish();
//        } else {
//        if (mOpenFrom == 2) {
//            showPopUpAligner();
//        }
        if (mOpenFrom == 3) {
            if (!appPreference.getFitsReminderId().equals("")) {
                Intent intent = new Intent(this, AlignerSmileProgressActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, appPreference.getFitsReminderId());
                intent.putExtra(AppConstants.OPEN_FROM, mOpenFrom);
                intent.putExtra(AppConstants.ALIGNER_NO, alignNoUpload);
                intent.putExtra(AppConstants.APPOINTMENT_ID, fitsSmileId);
                startActivity(intent);
            }
            // showPopUpAlignerImages();
        } else if (mOpenFrom == 2) {
            homeFragment = new HomeFragment();
            loadFragment(homeFragment);
            showPopUpAligner();
        }
//        else if (mOpenFrom == 4) {
//            //for video call invitation
//            Intent intent = new Intent(this, VideoCallStartActivity.class);
//            startActivity(intent);
//            finish();
//        }
        else {
            homeFragment = new HomeFragment();
            loadFragment(homeFragment);
        }
        //}
    }

    void setUserInfo() {
        tvUserName.setText(Utils.getStr(appPreference.getUserName()));
        tvUserEmail.setText(Utils.getStr(appPreference.getEmail()));

        if (Utils.getStr(appPreference.getImage()).isEmpty()) {
            imvUserPhoto.setImageResource(R.drawable.ic_profile_placeholder_side);
        } else {
            Picasso.get()
                    .load(Utils.getStr(appPreference.getImage()))
                    .placeholder(R.drawable.ic_profile_placeholder_side)
                    .into(imvUserPhoto);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((AppController) getApplication()).stopListeningToAllMessages();
    }

    private void applyFontToMenuItem(MenuItem subMenuItem) {
        Typeface font = ResourcesCompat.getFont(MainActivity.this, R.font.proxima_nova_regular);
        SpannableString mNewTitle = new SpannableString(subMenuItem.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0, mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        subMenuItem.setTitle(mNewTitle);
    }

    private void setBottomNavigationView() {
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.bottom_home:
                        HomeFragment homeFragment = new HomeFragment();
                        loadFragment(homeFragment);
                        return true;
                    case R.id.bottom_my_appointments:
                        Intent intent = new Intent(actCon, AppointmentListActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.bottom_booking:
                        showPopUp();
                        return true;
                    case R.id.bottom_notification:
                        Intent intentNotification = new Intent(actCon, NotificationList.class);
                        startActivity(intentNotification);
//                        NotificationFragment notificationFragment = new NotificationFragment();
//                        loadFragment(notificationFragment);
                        return true;
                    case R.id.bottom_my_account:
                        Intent intentMyAccount = new Intent(actCon, MyAccount.class);
                        startActivity(intentMyAccount);
                        return true;
                }
                return false;
            }
        });
    }

    private void showPopUp() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_choose_appointment_type, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(true);
        final AlertDialog alert = dialogBuilder.create();
        alert.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        alert.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alert.show();

        ImageView imvClose = alert.findViewById(R.id.imv_close);
        Button btnVideo = alert.findViewById(R.id.btn_book_video_appointment);
        Button btnTrayMinder = alert.findViewById(R.id.tray_minder);
        Button btnPhoto = alert.findViewById(R.id.btn_photo_consultation);
        //  tvChatCount = (TextView) getActionView(bottomNavigationView.getMenu().findItem(R.id.my_chat));

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                startActivity(new Intent(MainActivity.this, DentistQuestionsActivity.class));
            }
        });

        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                startActivity(new Intent(actCon, SearchDentistList.class).putExtra("isFromBookAppointment", true));
            }
        });

        btnTrayMinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                startActivity(new Intent(MainActivity.this, FitsReminderActivity.class));
            }
        });

        imvClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();

            }
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_content, fragment);
        transaction.disallowAddToBackStack();
        transaction.commit();
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.home:
                        drawer.closeDrawers();
                        break;
//                    case R.id.myAccount:
//                        Intent intentMyAccount = new Intent(actCon, MyAccount.class);
//                        startActivity(intentMyAccount);
//                        drawer.closeDrawers();
//                        break;
                    case R.id.contact_us:
                        startActivity(new Intent(actCon, ContactUs.class));
                        drawer.closeDrawers();
                        break;
//                    case R.id.notification:
//                        startActivity(new Intent(actCon, NotificationList.class));
//                        drawer.closeDrawers();
//                        break;
                    case R.id.chatOffice:
                        getDentalOfficeData();
                        drawer.closeDrawers();
                        break;
//                    case R.id.addFitsReminder:
//                        startActivity(new Intent(MainActivity.this, FitsReminderActivity.class));
//                        drawer.closeDrawers();
//                        break;
                    case R.id.termsPolicy:
                        startActivity(new Intent(actCon, TermsPrivacyActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.help:
                        startActivity(new Intent(actCon, FAQActivity.class));
                        drawer.closeDrawers();
                        break;
                    case R.id.logout:
                        logOut(MainActivity.this);
                        break;
                }
                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        //After instantiating your ActionBarDrawerToggle
        actionBarDrawerToggle.setDrawerIndicatorEnabled(false);
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menu, this.getTheme());
        actionBarDrawerToggle.setHomeAsUpIndicator(drawable);
        actionBarDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawer.isDrawerVisible(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /*public void logOut() {
        appPreference.clearPreference();
        Intent intent = new Intent(actCon, SliderActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }*/

    @Override
    public void onUnreadMessageCountFetched(int count) {
       /* if (count > 0) {
            tvChatCount.setVisibility(View.VISIBLE);
            if (count > 99) {
                tvChatCount.setText("99+");
            } else {
                tvChatCount.setText(String.valueOf(count));
            }
        } else {
            tvChatCount.setVisibility(GONE);
        }*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUserInfo();
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
        Log.d(">>>Test>>>", ">>>>>>OnResume Main activity>>");
        if (AppController.isVideoCallComing) {
            Log.d(">>>Test>>>", ">>>>>>isVideoCallComing true>>");
            Intent intent = new Intent(this, VideoCallStartActivity.class);
            startActivity(intent);
        }

    }

    void getNotificationSettings() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, appPreference.getUserId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.fetchNotificationSettings(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    setNotificationSettings(mResponse.getData());
                }
                else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(MainActivity.this);
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {

            }
        });
    }

    void setNotificationSettings(SettingsResult mResult) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_ID, appPreference.getUserId());
        jsonObject.addProperty("aligner", mResult.getAligner());
        jsonObject.addProperty("sms", mResult.getAligner());

        if (appPreference.getTouchId().equals(true)) {
            jsonObject.addProperty("touch_id", "1");
        } else {
            jsonObject.addProperty("touch_id", "0");
        }
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.updateNotification(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                assert response.body() != null;
                if (response.body().getStatus().equals(AppConstants.ONE)) {
                    Log.e("settings", response.body().toString());
                } else if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    if (!isFinishing()) {
                        Utils.showSessionAlert(MainActivity.this);
                    }

                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Log.e("settings", t.getMessage());
            }
        });
    }


    void getDentalOfficeData() {
        Utils.openProgressDialog(MainActivity.this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<DentalOfficeResponse> mService = mApiService.fetchDentalOffice();
        mService.enqueue(new Callback<DentalOfficeResponse>() {
            @Override
            public void onResponse(Call<DentalOfficeResponse> call, Response<DentalOfficeResponse> response) {
                assert response.body() != null;
                Utils.closeProgressDialog();
                if (response.body().getStatus().equals(AppConstants.ONE)) {
                    LoginResponse loginResponse = response.body().getData();
                    Intent intent = getChatIntent(MainActivity.this, loginResponse.getFirebaseUid(), loginResponse.getName(), loginResponse.getImage());
                    startActivity(intent);
                } else if (response.body().getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(MainActivity.this);
                }

            }

            @Override
            public void onFailure(Call<DentalOfficeResponse> call, Throwable t) {
                Log.e("settings", t.getMessage());
            }
        });
    }

    private void showPopUpAligner() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_reminder_put, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setPositiveButton(R.string.put_aligner, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, ReminderDetailsActivity.class);
                intent.putExtra("id", fitsSmileId);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();


    }

    private void showPopUpAlignerImages() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(actCon);
        LayoutInflater inflater = actCon.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.popup_upload_aligner_image, null);
        dialogBuilder.setView(dialogView);

        TextView mTextDialog = dialogView.findViewById(R.id.textUploadAligner);
        String firstLine = getResources().getString(R.string.upload_image_of_aligner) + alignNoUpload + " ";
        mTextDialog.setText(firstLine + getResources().getString(R.string.for_provider_track_progress));

        dialogBuilder.setPositiveButton(R.string.upload_image, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(actCon, ImageUploadCameraActivity.class);
                intent.putExtra(AppConstants.TREATMENT_ID, fitsSmileId);
                intent.putExtra(AppConstants.ALIGNER_NO, alignNoUpload);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog alert = dialogBuilder.create();
        alert.show();
    }


    void getReminderDetails() {
        // Utils.openProgressDialog(this);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("id", appPreference.getFitsReminderId());
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<TrayMinderDataResponse> mService = mApiService.fetchFitsReminderDetails(jsonObject);
        mService.enqueue(new Callback<TrayMinderDataResponse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<TrayMinderDataResponse> call, Response<TrayMinderDataResponse> response) {
                TrayMinderDataResponse mResponse = response.body();
                assert mResponse != null;
                TrayMinderResult mBody = mResponse.getData();
                if (mResponse.getStatus() == 1) {
                    if (mBody.getStatus().equalsIgnoreCase("1") && mBody.getAssigned_id().equalsIgnoreCase(appPreference.getUserId())) {
                        HomeFragment homeFragment = new HomeFragment();
                        loadFragment(homeFragment);
                    } else {
                        Intent intent = new Intent(actCon, AppointmentListActivity.class);
                        startActivity(intent);
                    }
                }


            }

            @Override
            public void onFailure(Call<TrayMinderDataResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }
}