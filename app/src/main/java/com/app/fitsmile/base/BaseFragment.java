package com.app.fitsmile.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.app.fitsmile.R;
import com.app.fitsmile.app.AppController;
import com.app.fitsmile.app.AppPreference;
import com.app.fitsmile.utils.LocaleManager;

import retrofit2.Retrofit;


public class BaseFragment extends Fragment {

    protected FragmentActivity mActivity;
    private ProgressDialog mProgressDialog;
    public AppPreference appPreference;
    public Retrofit retrofit;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity=getActivity();
        LocaleManager.setLocale(getActivity());
        appPreference = AppController.getInstance().getAppPreference();
        retrofit = AppController.getInstance().getClient();
    }

    public void hideSoftInput() {
        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getActivity().getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    protected void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    protected void showProgress(String msg) {
        if (mProgressDialog != null && mProgressDialog.isShowing())
            dismissProgress();

        mProgressDialog = ProgressDialog.show(mActivity, getResources().getString(R.string.app_name), msg);
    }
    protected void dismissProgress() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }
}
