package com.app.fitsmile.fitsreminder;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.graphics.Color;
import android.os.Bundle;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppConstants;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;
import com.app.fitsmile.utils.NonSwipeableViewPager;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public class ReminderProgressActivity extends BaseActivity {
    NonSwipeableViewPager viewPager;
    Adapter adapter;
    String mFitSmileId;
    String mAlignerNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_progress);
        LocaleManager.setLocale(this);
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
        getSupportActionBar().setTitle(R.string.timeline);
    }

    private void initView() {
        mFitSmileId = getIntent().getStringExtra(AppConstants.TREATMENT_ID);
        mAlignerNo = getIntent().getStringExtra(AppConstants.ALIGNER_NO);
        viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        TabLayout tabs = findViewById(R.id.result_tabs);
        tabs.setupWithViewPager(viewPager);
        tabs.setTabTextColors(Color.parseColor("#545454"), Color.parseColor("#545454"));

    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new Adapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString(AppConstants.TREATMENT_ID, mFitSmileId);
        bundle.putString(AppConstants.ALIGNER_NO, mAlignerNo);
        TimelineFragment timelineFragment = new TimelineFragment();
        timelineFragment.setArguments(bundle);
        CalendarFragment calendarFragment = new CalendarFragment();
        calendarFragment.setArguments(bundle);
        adapter.addFragment(timelineFragment, getResources().getString(R.string.TIMELINE));
        adapter.addFragment(calendarFragment, getResources().getString(R.string.CALENDER));
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