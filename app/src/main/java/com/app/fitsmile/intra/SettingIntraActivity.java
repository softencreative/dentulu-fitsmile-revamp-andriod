package com.app.fitsmile.intra;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.app.fitsmile.R;
import com.app.fitsmile.base.BaseActivity;
import com.app.fitsmile.utils.LocaleManager;


public class SettingIntraActivity extends BaseActivity {
	private static final String TAG = "SettingActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_intra);
		LocaleManager.setLocale(this);
		initUI();
	}

	private void initUI() {
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		if (getSupportActionBar() != null) {
			getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			getSupportActionBar().setDisplayShowHomeEnabled(true);
		}
		getSupportActionBar().setTitle(R.string.settings_intra);
		toolbar.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
	}


	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case android.R.id.home:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		super.onBackPressed();
		//overridePendingTransition(R.anim.left_out, R.anim.left_out);
	}
}
