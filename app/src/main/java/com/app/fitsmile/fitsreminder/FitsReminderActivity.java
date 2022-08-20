package com.app.fitsmile.fitsreminder;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.fitsmile.MainActivity;
import com.app.fitsmile.R;
import com.app.fitsmile.app.ApiInterface;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.book.SearchDentistAdapter;
import com.app.fitsmile.common.Utils;
import com.app.fitsmile.fitsreminder.adapter.PatientListAdapter;
import com.app.fitsmile.fitsreminder.adapter.ProviderListAdapter;
import com.app.fitsmile.response.SettingsResponse;
import com.app.fitsmile.response.dentistlist.DoctorListDatas;
import com.app.fitsmile.response.dentistlist.DoctorListPojo;
import com.app.fitsmile.response.patientlist.PatientListData;
import com.app.fitsmile.response.patientlist.PatientListResponse;
import com.app.fitsmile.shop.inter.RecyclerItemClickListener;
import com.app.fitsmile.utils.EndlessRecyclerviewScrollListner;
import com.app.fitsmile.utils.LocaleManager;
import com.google.gson.JsonObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.app.fitsmile.app.AppConstants.FOUR_ZERO_ONE;
import static com.app.fitsmile.app.AppConstants.ONE;

public class FitsReminderActivity extends BaseActivity implements RecyclerItemClickListener , SearchDentistAdapter.ItemClickListener, TextWatcher {

    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private TextView[] dots;
    private int[] layouts;
    private TextView btnSkip, btnNext;
    LocalData localData;

    String strTreatmentType="";
    String strPatientId="";
    String strTotalAligners="";
    String strDaysEachAligner="";
    String strCurrentAligner="";
    String strDaysCurrentAligner="";
    String strDaysCurrentGoal="11 hours";
    String strReminderTime="";
    String strReminderTimeApi="";

    TextView tvQuestion1;
    TextView tvQuestionSelectPatient;
    TextView tvQuestion2;
    TextView tvQuestion3;
    TextView tvQuestion4;
    TextView tvQuestion5;
    TextView tvQuestionGoal;
    TextView tvQuestion6;

    private int mDay;
    private int mHour;
    private int mMinute;
    int checkedItems=-1;
    int checkedItemString=11;
    List<PatientListData> mPatientList=new ArrayList<>();
    AlertDialog alertDialogPatient;
    private RecyclerView recyclerSearchDentist;
    private ProviderListAdapter searchDentistAdapter;
    private TextView mTvNoData;
    private LinearLayoutManager layoutManager;
    ImageView mImageDentistSearch;
    String search;
    EditText mEditDentistSearch;
    String providerId="";


    ArrayList<DoctorListDatas> mDocList = new ArrayList<>();
    private DoctorListPojo doctorListPojo = null;
    int mOpenFrom=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_welcome);
        LocaleManager.setLocale(this);
        localData=new LocalData(this);

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        btnSkip = (TextView) findViewById(R.id.btn_skip);
        btnNext = (TextView) findViewById(R.id.btn_next);
        mOpenFrom=getIntent().getIntExtra(AppConstants.OPEN_FROM,0);

        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide_select_patient,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
                R.layout.welcome_slide5,
                R.layout.welcome_slide6,
                R.layout.welcome_slide_daily_goal,
                R.layout.welcome_slide7,
                R.layout.reminder_layout_select_provider};

        addBottomDots(0);

        // making notification bar transparent
        changeStatusBarColor();

        myViewPagerAdapter = new MyViewPagerAdapter();
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        btnSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchHomeScreen();
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking for last page
                // if last page home screen will be launched
                int current = getItem(+1);
                if (current < layouts.length) {
                    // move to next screen
                    viewPager.setCurrentItem(current);
                } else {
                    /*
                    List<TrayMinderResult> mList=new ArrayList<>();
                    Type type = new TypeToken<List<TrayMinderResult>>() {
                    }.getType();
                    Gson gson = new Gson();
                    String lastListReminders=appPreference.getReminderList();
                    if(!lastListReminders.equals("")){
                        mList=gson.fromJson(lastListReminders,type);
                    }

                    TrayMinderResult trayMinderResult=new TrayMinderResult();
                    trayMinderResult.Type =strTreatmentType;
                    trayMinderResult.Days = strDaysEachAligner;
                    trayMinderResult.Time =strReminderTime ;
                    mList.add(trayMinderResult);

                    String trayMinderJson = gson.toJson(mList);
                    appPreference.setReminderList(trayMinderJson);*/
                    if (providerId.equals("")){
                        Toast.makeText(actCon, R.string.select_provider_id, Toast.LENGTH_SHORT).show();
                    }else {
                        String[] timeSplit =strDaysCurrentGoal.split(" ");
                        String time=timeSplit[0];
                        String timeApi="";
                        if (time.length()==1){
                            timeApi="0"+time+":00:00";
                        }else {
                            timeApi=time+":00:00";
                        }
                        setBookReminders(timeApi);
                    }
               }
            }
        });
        getPatientList();

    }

    private void initView() {
        recyclerSearchDentist = findViewById(R.id.recycler_search_dentist);
        mTvNoData = findViewById(R.id.no_doctors);
        mImageDentistSearch=findViewById(R.id.iv_dentist_name);
        mEditDentistSearch=findViewById(R.id.et_dentist_name);
        mEditDentistSearch.addTextChangedListener(this);
        searchDentistList("","1");
        mImageDentistSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (search!=null &&search.length()>0) {
                    searchDentistBycode(search);
                }
            }
        });
    }

    public void getPatientList() {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        Utils.openProgressDialog(this);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id", appPreference.getUserId());
        jsonObj.addProperty("language", currentLanguage);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<PatientListResponse> mService = mApiService.getPatientList(jsonObj);
        mService.enqueue(new Callback<PatientListResponse>() {
            @Override
            public void onResponse(Call<PatientListResponse> call, Response<PatientListResponse> response) {
                Utils.closeProgressDialog();
                PatientListResponse patientListPojo = response.body();
                if (patientListPojo != null && Utils.getStr(patientListPojo.getStatus()).equals(ONE)) {
                    if (patientListPojo.getDatas() != null && !patientListPojo.getDatas().isEmpty()) {
                          mPatientList.addAll(patientListPojo.getDatas());
                    }
                } else if (patientListPojo != null && Utils.getStr(patientListPojo.getStatus()).equals(FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(actCon);
                } else {
                    Utils.showToast(actCon, Utils.getStr(patientListPojo.getMessage()));
                }
            }

            @Override
            public void onFailure(Call<PatientListResponse> call, Throwable t) {
                Utils.closeProgressDialog();
                call.cancel();
            }
        });
    }

    void setBookReminders(String time) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("patient_id", appPreference.getUserId());
        jsonObject.addProperty("type", strTreatmentType);
        jsonObject.addProperty("no_of_aligners", strTotalAligners);
        jsonObject.addProperty("no_of_days", strDaysEachAligner);
        jsonObject.addProperty("aligner_number", strCurrentAligner);
        jsonObject.addProperty("wear_current_aligner", strDaysCurrentAligner);
        jsonObject.addProperty("remind_time", strReminderTimeApi);
        jsonObject.addProperty("family_member_id", strPatientId);
        jsonObject.addProperty("doctor_id", providerId);
        jsonObject.addProperty("goal_time", time);

        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        jsonObject.addProperty("language", currentLanguage);

        Utils.openProgressDialog(this);
        ApiInterface mApiService = retrofit.create(ApiInterface.class);
        Call<SettingsResponse> mService = mApiService.bookFitsReminder(jsonObject);
        mService.enqueue(new Callback<SettingsResponse>() {
            @Override
            public void onResponse(Call<SettingsResponse> call, Response<SettingsResponse> response) {
                Utils.closeProgressDialog();
                SettingsResponse mResponse = response.body();
                assert mResponse != null;
                if (mResponse.getStatus().equals("1")) {
                    Utils.showToast(FitsReminderActivity.this,mResponse.getMessage());
                    if (appPreference.getFitsReminderId().equals("") ){
                        appPreference.setFitsReminderId(mResponse.getId());
                    }
                    if (mOpenFrom==1){
                        Intent intent = new Intent(FitsReminderActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }else {
                        NotificationScheduler.setReminder(FitsReminderActivity.this, AlarmReceiver.class, localData.get_hour(), localData.get_min());
                        finish();
                    }
                }else if (mResponse.getStatus().equals(AppConstants.FOUR_ZERO_ONE)) {
                    Utils.showSessionAlert(FitsReminderActivity.this);
                }  else {
                    Utils.showToast(FitsReminderActivity.this,mResponse.getMessage());
                }

            }

            @Override
            public void onFailure(Call<SettingsResponse> call, Throwable t) {
                Utils.closeProgressDialog();
            }
        });

    }
    private void addBottomDots(int currentPage) {
        dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0)
            dots[currentPage].setTextColor(colorsActive[currentPage]);
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    private void launchHomeScreen() {
        if (mOpenFrom==1){
            Intent intent = new Intent(FitsReminderActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }else {
            finish();
        }
    }

    //	viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            // changing the next button text 'NEXT' / 'GOT IT'
            if (position == layouts.length - 1) {
                // last page. make button text to GOT IT
                btnNext.setText(getString(R.string.start));
                btnSkip.setVisibility(View.GONE);
            } else {
                // still pages are left
                btnNext.setText(getString(R.string.nextt));
                btnSkip.setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    @Override
    public void setClicks(int pos, int open) {
        if (open==2){
            providerId=mDocList.get(pos).getId();
        }else {
            alertDialogPatient.dismiss();
            tvQuestionSelectPatient.setText(mPatientList.get(pos).getFirstName() + " " + mPatientList.get(pos).getLastName());
            if (!mPatientList.get(pos).getRelationshipType().equalsIgnoreCase("myself")) {
                strPatientId = mPatientList.get(pos).getId();
            }
        }

    }

    @Override
    public void onItemViewProfileClick(DoctorListDatas data) {

    }

    @Override
    public void onItemScheduleNowClick(DoctorListDatas data) {

    }

    /**
     * View pager adapter
     */
    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;

        public MyViewPagerAdapter() {
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            switch (layouts[position]) {
                case R.layout.welcome_slide_select_patient:
                    tvQuestionSelectPatient = view.findViewById(R.id.tvQuestionSelectPatient);
                    tvQuestionSelectPatient.setText(R.string.select_the_patient);
                    tvQuestionSelectPatient.setOnClickListener(v -> { showPopupPatient(); });
                    break;
                case R.layout.welcome_slide2:
                    tvQuestion1 = view.findViewById(R.id.tvQuestion1);
                    tvQuestion1.setText(R.string.fits_aligner);
                    strTreatmentType=getResources().getString(R.string.fits_aligner);
                    tvQuestion1.setOnClickListener(v -> { treatmentTypePopup(); });
                    break;
                case R.layout.welcome_slide3:
                    tvQuestion2 = view.findViewById(R.id.tvQuestion2);
                    strTotalAligners="15";
                    checkedItems=15;
                    tvQuestion2.setOnClickListener(v -> {
                        checkedItems=14;
                        alignerNumberPopup(getString(R.string.slide_3_title)); });
                    break;
                case R.layout.welcome_slide4:
                    tvQuestion3 = view.findViewById(R.id.tvQuestion3);
                    strDaysEachAligner="14";
                    tvQuestion3.setOnClickListener(v -> {
                        checkedItems=13;
                        alignerNumberPopup(getString(R.string.slide_4_title)); });
                    break;
                case R.layout.welcome_slide5:
                    tvQuestion4 = view.findViewById(R.id.tvQuestion4);
                    strCurrentAligner="1";
                    checkedItems=1;
                    tvQuestion4.setOnClickListener(v -> {
                        checkedItems=0;
                        alignerNumberPopup(getString(R.string.slide_5_title)); });
                    break;
                case R.layout.welcome_slide6:
                    strDaysCurrentAligner="0";

                    tvQuestion5 = view.findViewById(R.id.tvQuestion5);
                    tvQuestion5.setOnClickListener(v -> {
                        checkedItems=0;
                        alignerNumberPopup(getString(R.string.slide_6_title)); });
                    break;
                case R.layout.welcome_slide_daily_goal:
                    strDaysCurrentGoal="12 Hour";

                    tvQuestionGoal = view.findViewById(R.id.tvQuestionGoal);
                    tvQuestionGoal.setText("12 "+" "+ getResources().getString(R.string.hours));
                    tvQuestionGoal.setOnClickListener(v -> {
                        alignGoalPopup(); });
                    break;
                case R.layout.welcome_slide7:
                    tvQuestion6 = view.findViewById(R.id.tvQuestion6);
                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String currentTime = sdf.format(new Date());
                    strReminderTimeApi=sdf1.format(new Date());
                    tvQuestion6.setText(currentTime);
                    tvQuestion6.setOnClickListener(v -> { reminderPopup(); });
                    break;
                case R.layout.reminder_layout_select_provider:
                    initView();
                    break;

            }
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

    public void treatmentTypePopup() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FitsReminderActivity.this);
        alertDialog.setTitle(getString(R.string.slide_2_title));
        List<String> genders = new ArrayList<String>();
   /*     genders.add("3D Predict");
        genders.add("3D Smile");
        genders.add("3M Clarity");
        genders.add("Angel Align");
        genders.add("Candid");
        genders.add("Clear Correct");
        genders.add("Crystal Clear Aligners");
        genders.add("Easy Smile");
        genders.add("eCligner");
        genders.add("Insignia Clearguide");
        genders.add("Invisalign");
        genders.add("Myaligner");
        genders.add("Nu Smile Aligner");
        genders.add("Orthero");
        genders.add("Orthly");
        genders.add("OrthoAlign");
        genders.add("Othocaps");
        genders.add("SLX");
        genders.add("Smartee");
        genders.add("SmileDirectClub");
        genders.add("Smilelove");
        genders.add("Spark");
        genders.add("Sure Smile");
        genders.add("UGrin");
        genders.add("Uniform Teeth");
        genders.add("Others");*/
        genders.add(getResources().getString(R.string.fits_aligner));
        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        strTreatmentType = items[0].toString();
        alertDialog.setSingleChoiceItems(items, 0, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                strTreatmentType = items[which].toString();
                checkedItemString=which;
            }
        });
        alertDialog.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                tvQuestion1.setText(strTreatmentType);
            }
        });
        alertDialog.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void alignGoalPopup(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FitsReminderActivity.this);
        alertDialog.setTitle(R.string.select_daily_goal);
        List<String> genders = new ArrayList<String>();
        for(int i=1; i<25; i++) {
            if (i==1){
                genders.add(i+ " "+ getResources().getString(R.string.hour));
            }
            else {
                genders.add(i+ " "+ getResources().getString(R.string.hours));
            }

        }
        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        alertDialog.setSingleChoiceItems(items, 11, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 strDaysCurrentGoal=items[which].toString();
            }
        });
        alertDialog.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              tvQuestionGoal.setText(strDaysCurrentGoal);
            }
        });
        alertDialog.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                 dialog.dismiss();
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void alignerNumberPopup(String strType) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(FitsReminderActivity.this);
        alertDialog.setTitle(strType);
        List<String> genders = new ArrayList<String>();
        if(strType.equalsIgnoreCase(getString(R.string.slide_3_title)) || strType.equalsIgnoreCase(getString(R.string.slide_4_title))) {
            for(int i=1; i<151; i++) {
                genders.add(""+i);
            }
        } else if(strType.equalsIgnoreCase(getString(R.string.slide_5_title))) {
            for(int i=1; i<=Integer.parseInt(strTotalAligners); i++) {
                genders.add(""+i);
            }
        } else if(strType.equalsIgnoreCase(getString(R.string.slide_6_title))) {
            for(int i=0; i<=Integer.parseInt(strDaysEachAligner); i++) {
                genders.add(""+i);
            }
        }

        final CharSequence[] items = genders.toArray(new String[genders.size()]);
        alertDialog.setSingleChoiceItems(items, checkedItems, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(strType.equalsIgnoreCase(getString(R.string.slide_3_title))) {
                    strTotalAligners = items[which].toString();
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_4_title))) {
                    strDaysEachAligner = items[which].toString();
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_5_title))) {
                    strCurrentAligner = items[which].toString();
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_6_title))) {
                    strDaysCurrentAligner = items[which].toString();
                }
                checkedItems=which;
            }
        });
        alertDialog.setPositiveButton(R.string.capital_confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(strType.equalsIgnoreCase(getString(R.string.slide_3_title))) {
                    tvQuestion2.setText(strTotalAligners);
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_4_title))) {
                    tvQuestion3.setText(strDaysEachAligner);
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_5_title))) {
                    tvQuestion4.setText(strCurrentAligner);
                } else if(strType.equalsIgnoreCase(getString(R.string.slide_6_title))) {
                    tvQuestion5.setText(strDaysCurrentAligner);
                }
            }
        });
        alertDialog.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    public void reminderPopup() {
        Calendar calendar = Calendar.getInstance();

        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(FitsReminderActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int pHour, int pMinute) {
                        localData.set_hour(pHour);
                        localData.set_min(pMinute);
                        mHour = pHour;
                        mMinute = pMinute;
                        tvQuestion6.setText( getFormatedTime(mHour,mMinute));
                        strReminderTime=getFormatedTime(mHour,mMinute);
                        strReminderTimeApi=mHour+":"+mMinute;


                    }
                }, mHour, mMinute, false);

        timePickerDialog.show();
    }

    public String getFormatedTime(int h, int m) {
        final String OLD_FORMAT = "HH:mm";
        final String NEW_FORMAT = "hh:mm a";

        String oldDateString = h + ":" + m;
        String newDateString = "";

        try {
            SimpleDateFormat sdf = new SimpleDateFormat(OLD_FORMAT, getCurrentLocale());
            Date d = sdf.parse(oldDateString);
            sdf.applyPattern(NEW_FORMAT);
            newDateString = sdf.format(d);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return newDateString;
    }
    @TargetApi(Build.VERSION_CODES.N)
    public Locale getCurrentLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return getResources().getConfiguration().locale;
        }
    }

    void showPopupPatient(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_select_patient, null);
        dialogBuilder.setView(dialogView);
        RecyclerView mRecycler=dialogView.findViewById(R.id.recyclerPatient);
        mRecycler.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        PatientListAdapter mAdapter=new PatientListAdapter(this,mPatientList,this);
        mRecycler.setAdapter(mAdapter);
        dialogBuilder.setNegativeButton(R.string.capital_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              dialog.dismiss();
            }
        });
        alertDialogPatient = dialogBuilder.create();
        alertDialogPatient.setTitle(R.string.select_the_patient);
        alertDialogPatient.show();
    }

    void searchDentistBycode(String searchCode){
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
                if(doctorListPojo==null)
                    return;
                if (Utils.getStr(doctorListPojo.getStatus()).equals("1")) {
                    if(doctorListPojo.getDatas().size()>0) {
                        mTvNoData.setVisibility(View.GONE);
                        mDocList.addAll(doctorListPojo.getDatas());
                        setAdapter();
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

    void setAdapter(){
        layoutManager = new LinearLayoutManager(actCon, RecyclerView.VERTICAL, false);
        recyclerSearchDentist.setLayoutManager(layoutManager);
        searchDentistAdapter = new ProviderListAdapter(actCon,mDocList,this);
        recyclerSearchDentist.setAdapter(searchDentistAdapter);

        recyclerSearchDentist.addOnScrollListener(new EndlessRecyclerviewScrollListner(layoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if(doctorListPojo !=null){
                    if(doctorListPojo.getLimit_reach()!=null && doctorListPojo.getLimit_reach().equals("0")){
                        searchDentistList("",String.valueOf(current_page));
                    }
                }else{
                    searchDentistList("",String.valueOf(current_page));
                }
            }
        });



    }

    void searchDentistList(String str_value,String pageNo) {
        String currentLanguage = "";
        if (LocaleManager.getLanguagePref(this).equalsIgnoreCase("en")) {
            currentLanguage = "english";
        } else {
            currentLanguage = "spanish";
        }
        mDocList.clear();
        Utils.openProgressDialog(actCon);
        JsonObject jsonObj = new JsonObject();
        jsonObj.addProperty("user_id",appPreference.getUserId());
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
                doctorListPojo = response.body();
                if(doctorListPojo==null)
                    return;
                if (Utils.getStr(doctorListPojo.getStatus()).equals("1")) {
                    if(doctorListPojo.getDatas().size()>0) {
                        mTvNoData.setVisibility(View.GONE);
                        mDocList.addAll(doctorListPojo.getDatas());
                        setAdapter();
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

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        search=s.toString();
        if (s.length()==0){
            searchDentistList("","1");
        }
    }

}
