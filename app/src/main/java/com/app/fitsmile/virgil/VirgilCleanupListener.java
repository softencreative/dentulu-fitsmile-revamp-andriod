package com.app.fitsmile.virgil;

public interface VirgilCleanupListener {
    void onCleanedUp();

    void onError(String message);
}
