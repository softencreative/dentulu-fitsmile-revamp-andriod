package com.app.fitsmile.virgil;

public interface VirgilInitializeListener {
    void onInitialized();

    void onError(String message);
}
