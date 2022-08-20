package com.app.fitsmile.virgil;

public interface VirgilRegisterUserListener {
    void onRegistered();

    void onUserAlreadyRegistered();

    void onPrivateKeyExists();

    void onError(String message);
}
