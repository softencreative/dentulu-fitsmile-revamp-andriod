package com.app.fitsmile.myaccount;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.myaccount.adapter.RewardsPagerAdapter;
import com.app.fitsmile.utils.LocaleManager;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;


public class MyRewardPoints extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reward_points);
        LocaleManager.setLocale(this);
        setToolBar();
        initView();
    }

    private void initView() {
        TextView pointsTV = findViewById(R.id.pointsTV);
        pointsTV.setText(appPreference.getRewardPoint());
        ViewPager2 rewardsPointsVP = findViewById(R.id.rewardsPointsVP);
        TabLayout tabs = findViewById(R.id.tabs);
        rewardsPointsVP.setAdapter(new RewardsPagerAdapter(this,appPreference));
        TabLayoutMediator mediator = new TabLayoutMediator(tabs, rewardsPointsVP, true, (tab, position) -> {
            // position of the current tab and that tab
            switch (position) {
                case 0:
                    tab.setText(getResources().getString(R.string.earning_details));
                    break;
                case 1:
                    tab.setText(getResources().getString(R.string.recent_transactions));
                    break;
                default:
                    tab.setText(getResources().getString(R.string.invite_and_earn));
                    break;
            }

        });
        mediator.attach();
    }

    private void setToolBar() {
        //setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        getSupportActionBar().setTitle(getResources().getString(R.string.your_rewards_points));
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //handle arrow click
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
}
