package com.app.fitsmile.appointment;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class AppointmentListActivity extends BaseActivity {
    TextView mTextFirstFilter, mTextSecFilter, mTextThirdFilter, mTextFourthFilter;
    Button mBtnApply;
    int mSelectFilter = 0;
    private BottomSheetBehavior bottomSheetFilterPopup;
    ViewPager viewPager;
    Adapter adapter;
    int openFrom;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_list);
        LocaleManager.setLocale(this);
        openFrom=getIntent().getIntExtra(AppConstants.OPEN_FROM,0);
        setUpToolBar();
        initView();
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.title_appointment_list));
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(Color.parseColor("#545454"), Color.parseColor("#545454"));
       /* LinearLayout bottomSheet = findViewById(R.id.ll_parent_add_address);
        bottomSheetFilterPopup = BottomSheetBehavior.from(bottomSheet);
        bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_HIDDEN);
        mTextFirstFilter = findViewById(R.id.textFirstFilter);
        mTextSecFilter = findViewById(R.id.textSecFilter);
        mTextThirdFilter = findViewById(R.id.textThirdFilter);
        mTextFourthFilter = findViewById(R.id.textFourthFilter);
        mBtnApply = findViewById(R.id.btn_apply);
        setFilterData();*/

        if (openFrom==2) {
            TabLayout.Tab tab = tabs.getTabAt(2);
            assert tab != null;
            tab.select();
        }


    }


    void setFilterData() {
        mTextFirstFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectFilter = 1;
            }
        });

        mTextSecFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectFilter = 2;
            }
        });

        mTextThirdFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectFilter = 3;
            }
        });

        mTextFourthFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSelectFilter = 4;
            }
        });

        mBtnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selected = "";
                if (mSelectFilter == 1) {
                    selected = mTextFirstFilter.getText().toString();
                } else if (mSelectFilter == 2) {
                    selected = mTextSecFilter.getText().toString();
                } else if (mSelectFilter == 3) {
                    selected = mTextThirdFilter.getText().toString();
                } else if (mSelectFilter == 4) {
                    selected = mTextFourthFilter.getText().toString();
                }
                int pos = viewPager.getCurrentItem();
                Fragment activeFragment = adapter.getItem(pos);
                Log.e("Tag",String.valueOf(pos));
                /*if (pos == 2)
                    ((PhotoConsultationFragmentList) activeFragment).setAdapterFilter(selected);*/
            }
        });
    }

    void showDialog() {
        if (bottomSheetFilterPopup.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetFilterPopup.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new TrayMinderListFragment(), getResources().getString(R.string.tray_minder_tab));
        adapter.addFragment(new VideoAppointmentFragmentList(), getResources().getString(R.string.tab_video_virtual));
        adapter.addFragment(new PhotoConsultationFragmentList(), getResources().getString(R.string.tab_photo_consultation));
        //adapter.addFragment(new TrayMinderListFragment(), getResources().getString(R.string.tray_minder_tab));
        //adapter.addFragment(new DentistASynchronousCaseReviewFragment(), "Photo Consultations");
        viewPager.setAdapter(adapter);
    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public Adapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
