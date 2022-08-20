package com.app.fitsmile.virgil;

public interface VirgilBackupPrivateKeyListener {
    void onBackupSuccess();

    void onBackupAlreadyExists();

    void onPrivateKeyMissing();

    void onError(String message);
}
