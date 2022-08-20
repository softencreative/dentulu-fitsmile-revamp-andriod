package com.app.fitsmile.virgil;

public interface VirgilRestorePrivateKeyListener {
    void onRestoreSuccess();

    void onBackupNotPresentOnVirgil();

    void onPrivateKeyExists();

    void onWrongPassword();

    void onError(String message);
}
