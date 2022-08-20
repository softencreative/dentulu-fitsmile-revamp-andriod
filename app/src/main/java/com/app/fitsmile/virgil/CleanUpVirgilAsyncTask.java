package com.app.fitsmile.virgil;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;

import com.app.android.dentulu.virgil.VirgilDevice;

import org.jetbrains.annotations.NotNull;

public class CleanUpVirgilAsyncTask extends AsyncTask<Void, Void, Void> {
    private VirgilCleanupListener cleanupListener;
    private VirgilDevice senderDevice;

    public CleanUpVirgilAsyncTask(VirgilDevice senderDevice, @NotNull VirgilCleanupListener cleanupListener) {
        this.senderDevice = senderDevice;
        this.cleanupListener = cleanupListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        if (senderDevice == null) {
            new Handler(Looper.getMainLooper()).post(() -> cleanupListener.onCleanedUp());
            return null;
        }
        if (senderDevice.isInitialized()) {
            senderDevice.cleanup();
            new Handler(Looper.getMainLooper()).post(() -> cleanupListener.onCleanedUp());
        } else {
            new Handler(Looper.getMainLooper()).post(() -> cleanupListener.onError(""));
        }
        return null;
    }
}
