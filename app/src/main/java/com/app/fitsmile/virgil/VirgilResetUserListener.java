package com.app.fitsmile.virgil;

public interface VirgilResetUserListener {
    void onResetCompleted();

    void onError(String message);
}
