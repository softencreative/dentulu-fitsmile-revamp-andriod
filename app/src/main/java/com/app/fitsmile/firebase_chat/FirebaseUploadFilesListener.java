package com.app.fitsmile.firebase_chat;

public interface FirebaseUploadFilesListener {
    void onUploadSuccess(String filename, boolean isFile);

    void onUploadError(String errorMessage, boolean isFile);
}
