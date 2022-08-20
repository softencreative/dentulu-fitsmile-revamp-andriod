package com.app.fitsmile.virgil;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.app.android.dentulu.virgil.VirgilDevice;
import com.app.fitsmile.app.AppPreference;

import org.jetbrains.annotations.NotNull;

public class InitializeVirgilAsyncTask extends AsyncTask<Void, Void, Void> {
    private VirgilInitializeListener initializeListener;
    private VirgilDevice senderDevice;
    private AppPreference appPreference;

    public InitializeVirgilAsyncTask(AppPreference preference, VirgilDevice senderDevice, @NotNull VirgilInitializeListener initializeListener) {
        this.appPreference = preference;
        this.senderDevice = senderDevice;
        this.initializeListener = initializeListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (!senderDevice.isInitialized()) {
            senderDevice.initialize(appPreference, new VirgilInitializeListener() {
                @Override
                public void onInitialized() {
                    new Handler(Looper.getMainLooper()).post(() -> initializeListener.onInitialized());
                }

                @Override
                public void onError(String message) {
                    new Handler(Looper.getMainLooper()).post(() -> initializeListener.onError(message));
                }
            });
        } else {
            new Handler(Looper.getMainLooper()).post(() -> initializeListener.onInitialized());
        }
        return null;
    }
}
