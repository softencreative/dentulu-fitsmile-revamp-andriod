package com.app.fitsmile.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.app.AppPreference;
import com.app.fitsmile.utils.LocaleManager;
import com.app.fitsmile.utils.LoggerUtils;

import java.util.List;

import retrofit2.Retrofit;

public class BaseActivity extends AppCompatActivity {

    public FragmentActivity actCon;
    private static final String TAG = LoggerUtils.makeLogTag(BaseActivity.class);
    private ProgressDialog mProgressDialog;
    public AppPreference appPreference;
    public Retrofit retrofit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        actCon = this;
        LocaleManager.setLocale(this);
        appPreference = AppController.getInstance().getAppPreference();
        retrofit = AppController.getInstance().getClient();

    }

    public void hideSoftInput() {
        try {
            InputMethodManager manager =
                    (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException e) {
            LoggerUtils.e(TAG, "Exception in hideSoftInput", e);
        }
    }

    public Fragment getVisibleFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        List<Fragment> fragments = fragmentManager.getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if (fragment != null && fragment.isVisible()) return fragment;
            }
        }
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        boolean userEvent = false;
        try {
            View v = getCurrentFocus();
            userEvent = super.dispatchTouchEvent(event);
            if (v instanceof EditText) {
                View w = getCurrentFocus();
                int scr[] = new int[2];
                if (w != null) {
                    w.getLocationOnScreen(scr);
                }
                assert w != null;
                float x = event.getRawX() + w.getLeft() - scr[0];
                float y = event.getRawY() + w.getTop() - scr[1];
                if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft()
                        || x >= w.getRight()
                        || y < w.getTop()
                        || y > w.getBottom())) {
                    hideSoftInput();
                }
            }
        } catch (Exception e) {
            LoggerUtils.e(TAG, "Exception in dispatchTouchEvent", e);
        }
        return userEvent;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setNewFragment(Fragment fragment, String title, boolean addbackstack) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
//        transaction.replace(R.id.frame_content, fragment);
        if (addbackstack) transaction.addToBackStack(title);
        transaction.commit();

    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(actCon, getResources().getString(R.string.app_name), msg);
    }

    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
