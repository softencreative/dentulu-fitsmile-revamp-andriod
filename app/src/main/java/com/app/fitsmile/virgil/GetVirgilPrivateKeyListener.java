package com.app.fitsmile.virgil;

public interface GetVirgilPrivateKeyListener {
    void onPrivateKeyFound();

    void onError(String error);
}
