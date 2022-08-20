package com.app.fitsmile.virgil;

public interface EncryptionListener {
    void onEncryptionComplete(String encryptedMessage);

    void onError(String error);
}
